package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.TaskList;

public interface TaskListService {
    TaskList createTaskList(String username, TaskList taskList);

    void deleteTaskList(String username, String listName);

    TaskList updateTaskList(String username, String listName, TaskList taskList);
}
