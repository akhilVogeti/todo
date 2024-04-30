package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {
    List<Task> findAllTasks(String username);

    Map<String, List<String>> getTasksByLists(String username);

    List<Task> getTasksByListName(String username, String ListName);
    Task findById(String id);
    Task createTask(String username, Task task);

    Task updateTask (String id, Task task);

    void deleteTask(String id);
}
