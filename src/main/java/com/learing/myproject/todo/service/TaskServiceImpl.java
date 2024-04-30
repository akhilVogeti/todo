package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.TaskRepository;
import com.learing.myproject.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

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
        List<TaskList> allTaskLists = taskListRepository.findByOwner(username);
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
        Optional<User> userOptional = userRepository.findByUsername(username);
        User userEntity = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

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
        TaskList taskList = taskListRepository.findByOwnerAndListName(username, listName);
        List<String> taskIds = taskList.getTaskIds();
        List<Task> tasksByListName = new ArrayList<>();
        for(String taskId: taskIds){
            tasksByListName.add(findById(taskId));
        }
        tasksByListName.sort(Comparator.comparing(Task::getDueDate).reversed());
        return tasksByListName;
    }


    @Override
    public Task findById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @Override
    public Task createTask(String username, Task task) {
        
        return null;
    }

    @Override
    public Task updateTask(String id, Task task) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        Task theTask = optionalTask.orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        theTask.setTitle(task.getTitle());
        theTask.setDescription(task.getDescription());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }
}
