package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.TaskRepository;
import com.learing.myproject.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Task> findAllTasks(String username) {
        List<Task> allTasks = new ArrayList<>();
        List<String> allTaskIds = new ArrayList<>();
        User user = findUserByUsername(username);
        List<TaskList> allTaskLists = taskListRepository.findByUserId(user.getId());
        for(TaskList taskList : allTaskLists) {
            allTaskIds.addAll(taskList.getTaskIds());
        }
        for(String taskId : allTaskIds) {
            Task theTask = findById(taskId);
            allTasks.add(theTask);
        }
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
        user.getTaskListIds().add(taskList.getId());
        user = userRepository.save(user);
        TaskList newTaskList = taskListRepository.save(taskList);
        return newTaskList;
    }

    @Override
    public Task updateTask(String id, Task task) {
        Task theTask = findById(id);
        
        theTask.setTitle(task.getTitle());
        theTask.setDescription(task.getDescription());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    @Override
    public Task findById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username " + username));
    }

    public boolean userHasAccess ()
}
