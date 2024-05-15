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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

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
        User user = findUserByUsername(username);
        List<String> allTaskIds = new ArrayList<>();
        List<TaskList> allTaskLists = taskListRepository.findByUserId(user.getId());
        for (TaskList taskList : allTaskLists) {
            allTaskIds.addAll(taskList.getTaskIds());
        }
        List<Task> allTasks = taskRepository.findAllById(allTaskIds);
        allTasks = sortByDueDateAndCompletion(allTasks);
        return allTasks;
    }


    @Override
    public Map<String, List<Task>> getTasksByLists(String username) {
        User user = findUserByUsername(username);
        List<String> taskListIds = user.getTaskListIds();
        Map<String, List<Task>> tasksByLists = new HashMap<>();

        for (String ListId : taskListIds) {
            Optional<TaskList> taskListOptional = taskListRepository.findById(ListId);
            TaskList taskList = taskListOptional.orElseThrow(() -> new RuntimeException("Task List not found"));
            List<Task> tasks = taskRepository.findAllById(taskList.getTaskIds());
            tasks = sortByDueDateAndCompletion(tasks);
            tasksByLists.put(taskList.getListName(), tasks);
        }
        return tasksByLists;
    }

    @Override
    public List<Task> getTasksByListName(String username, String listName) {
        User user = findUserByUsername(username);
        TaskList taskList = taskListRepository.findByUserIdAndListName(user.getId(), listName);
        List<String> taskIds = taskList.getTaskIds();
        List<Task> tasksByListName = taskRepository.findAllById(taskIds);
        tasksByListName = sortByDueDateAndCompletion(tasksByListName);
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
        Task existingTask = findById(id);
        if (!userHasAccess(user, id))
            throw new AccessDeniedException("Access Denied");


        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setCategory(task.getCategory());
        existingTask.setCompleted(task.isCompleted());
        existingTask.setPriority(task.getPriority());
        existingTask.setCreatedOn(task.getCreatedOn());
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(String username, String id) {
        User user = findUserByUsername(username);
        if (!userHasAccess(user, id))
            throw new AccessDeniedException("Access Denied");
        taskRepository.deleteById(id);
    }

    @Override
    public Task getTaskById(String username, String id) {
        User user = findUserByUsername(username);
        Task task = findById(id);
        if (!userHasAccess(user, id))
            throw new AccessDeniedException("Access Denied");
        return task;
    }

    @Override
    public List<Task> sortAndFilter(String username,  String sortBy, String filterField, String filterValue) {
        List<Task> tasks = findAllTasks(username);
        tasks = filterTasks(tasks, filterField, filterValue);
        sortTasks(tasks, sortBy);
        return tasks;
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
        if(tasks.isEmpty())
            throw new RuntimeException("No task matches the search query");
        return tasks;
    }

    private Task findById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
    }

    private boolean userHasAccess(User user, String id) {
        HashSet<String> taskIdSet = new HashSet<>();
        List<TaskList> allTaskLists = taskListRepository.findByUserId(user.getId());
        for (TaskList taskList : allTaskLists) {
            taskIdSet.addAll(taskList.getTaskIds());
        }
        return taskIdSet.contains(id);
    }

    private List<Task> sortByDueDateAndCompletion(List<Task> allTasks) {
        List<Task> completedTasks = new ArrayList<>();
        List<Task> incompleteTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            } else {
                incompleteTasks.add(task);
            }
        }
        Collections.sort(completedTasks, Comparator.comparing(Task::getDueDate));
        Collections.sort(incompleteTasks, Comparator.comparing(Task::getDueDate));
        incompleteTasks.addAll(completedTasks);
        return incompleteTasks;
    }




    private List<Task> filterTasks(List<Task> tasks, String filterField, String filterValue) {
        switch (filterField) {
            case "category":
                return tasks.stream().filter(task -> Objects.equals(task.getCategory(), filterValue)).collect(Collectors.toList());
            case "completed":
                return tasks.stream().filter(task -> Objects.equals(task.isCompleted(), Boolean.parseBoolean(filterValue))).collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid filterBy value. Supported values are: category, completed");
        }
    }

    private void sortTasks(List<Task> tasks, String sortBy) {
        Comparator<Task> comparator;
        switch (sortBy) {
            case "dueDate" -> comparator = Comparator.comparing(Task::getDueDate);
            case "priority" -> comparator = Comparator.comparing(Task::getPriority).reversed();
            default -> throw new IllegalArgumentException("Invalid sortBy value. Supported values are: dueDate, priority");
        }
        tasks.sort(comparator);
    }

}

