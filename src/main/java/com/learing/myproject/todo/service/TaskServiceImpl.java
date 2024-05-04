package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.TaskRepository;
import com.learing.myproject.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Task> findAllTasks(String username) {
        List<Task> allTasks = new ArrayList<>();
        List<String> allTaskIds = new ArrayList<>();
        User user = findUserByUsername(username);
        List<TaskList> allTaskLists = taskListRepository.findByUserId(user.getId());
        for(TaskList taskList : allTaskLists) {
            allTaskIds.addAll(taskList.getTaskIds());
        }
        allTasks = taskRepository.findAllById(allTaskIds);
        allTasks.sort(Comparator.comparing(Task::getDueDate).reversed());
        return allTasks;
    }

    @Override
    public Map<String, List<String>> getTasksByLists(String username) {
        User userEntity = findUserByUsername(username);
        List<String> taskListIds = userEntity.getTaskListIds();
        Map<String, List<String>> tasksByLists = new HashMap<>();

        for(String ListId: taskListIds){
            Optional <TaskList> taskListOptional = taskListRepository.findById(ListId);
            TaskList taskList = taskListOptional.orElseThrow(()->new RuntimeException("Task List not found"));
            List<String> taskNames = taskList.getTaskIds().stream()
                    .map(taskId -> findById(taskId).getTitle()) // Get only the name of the task
                    .toList();
            tasksByLists.put(taskList.getListName(), taskNames);
        }
        return tasksByLists;
    }

    @Override
    public List<Task> getTasksByListName(String username, String listName) {
        User user = findUserByUsername(username);
        TaskList taskList = taskListRepository.findByUserIdAndListName(user.getId(), listName);
        List<String> taskIds = taskList.getTaskIds();
        List<Task> tasksByListName = new ArrayList<>();
        for(String taskId: taskIds){
            tasksByListName.add(findById(taskId));
        }
        tasksByListName.sort(Comparator.comparing(Task::getDueDate).reversed());
        return tasksByListName;
    }

    @Override
    public Task createTask(String username, Task task, String listName) {
        User user = findUserByUsername(username);
        TaskList taskList = taskListRepository.findByUserIdAndListName(user.getId(), listName);
        task.setTaskListId(taskList.getId());
        Task newTask = taskRepository.save(task);
        taskList.getTaskIds().add(newTask.getId());
        taskList = taskListRepository.save(taskList);
        return newTask;
    }

    @Override
    public TaskList createTaskList(String username, TaskList taskList) {
        User user = findUserByUsername(username);
        taskList.setUserId(user.getId());
        taskList.setTaskIds(new ArrayList<>());
        taskList = taskListRepository.save(taskList);
        user.getTaskListIds().add(taskList.getId());
        user = userRepository.save(user);
        return taskList;
    }

    @Override
    public Task updateTask(String username, String id, Task task) {
        User user = findUserByUsername(username);
        if(!userHasAccess(user, id))
            throw new AccessDeniedException("Access Denied");
        Task theTask = findById(id);
        theTask.setTitle(task.getTitle());
        theTask.setDescription(task.getDescription());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(String username, String id) {
        User user = findUserByUsername(username);
        if(!userHasAccess(user, id))
            throw new AccessDeniedException("Access Denied");
        Task task = findById(id);
        taskRepository.delete(task);
    }

    @Override
    public Task getTaskById(String username, String id) {
        User user = findUserByUsername(username);
        if(!userHasAccess(user, id))
            throw new AccessDeniedException("Access Denied");
        Task task = findById(id);
        return task;
    }

    @Override
    public List<Task> sortAndFilter(String username, String sortField, String filterField) {
       List<Task> allTasks = findAllTasks(username);
       return groupAndSort(allTasks, sortField, filterField);
    }

    @Override
    public List<Task> searchTasks(String username, String searchText) {
        User user = findUserByUsername(username);
        List<String> taskListIds = user.getTaskListIds();
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("taskListId").in(taskListIds),
                new Criteria().orOperator(
                        Criteria.where("title").regex(searchText),
                        Criteria.where("description").regex(searchText)
                )
        );
        Query query = new Query(criteria);
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        return tasks;
    }

    public Task findById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username " + username));
    }

    public boolean userHasAccess (User user, String id){
        HashSet<String> taskIdSet = new HashSet<>();
        List<TaskList> allTaskLists = taskListRepository.findByUserId(user.getId());
        for(TaskList taskList : allTaskLists) {
            taskIdSet.addAll(taskList.getTaskIds());
        }
        return taskIdSet.contains(id);
    }

    public List<Task> groupAndSort(List<Task> tasks, String sortField, String filterField) {
        Map<Object, List<Task>> groupedTasks = new HashMap<>();
        switch (filterField) {
            case "dueDate":
                groupedTasks = tasks.stream().collect(Collectors.groupingBy(Task::getDueDate));
                break;
            case "category":
                groupedTasks = tasks.stream().collect(Collectors.groupingBy(Task::getCategory));
                break;
            case "completed":
                groupedTasks = tasks.stream().collect(Collectors.groupingBy(Task::isCompleted));
                break;
            case "priority":
                groupedTasks = tasks.stream().collect(Collectors.groupingBy(Task::getPriority));
                break;
            default:
                System.out.println("Invalid groupBy value. Supported values are: dueDate, category, completed, priority");
                return tasks;
        }

        // Sorting logic based on the value of sortBy
        Comparator<Task> comparator;
        switch (sortField) {
            case "dueDate" -> comparator = Comparator.comparing(Task::getDueDate);
            case "priority" -> comparator = Comparator.comparing(Task::getPriority);
            default -> {
                System.out.println("Invalid sortBy value. Supported values are: dueDate, priority");
                return tasks;
            }
        }

        // Sorting each group
        groupedTasks.forEach((key, value) -> value.sort(comparator));

        // Flattening the map to retrieve sorted tasks
        List<Task> sortedAndGroupedTasks = groupedTasks.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return sortedAndGroupedTasks;
    }
}
