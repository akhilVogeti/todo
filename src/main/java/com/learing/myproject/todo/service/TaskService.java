package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface TaskService {
    List<Task> findAllTasks(String username);

    Map<String, List<String>> getTasksByLists(String username);

    List<Task> getTasksByListName(String username, String ListName);
    Task getTaskById(String username, String id);
    Task createTask(String username, Task task, String listName);

    TaskList createTaskList(String username, TaskList taskList);

    Task updateTask (String username,String id, Task task);

    void deleteTask(String username, String id);

    List<Task> sortAndFilter(String username, String sortField, String filterField);

    List<Task> searchTasks(String username, String searchText);
}
