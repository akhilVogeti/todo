package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> findByUsername(String username);
    Task findById(String id);
    Task create(Task);

    Task update



}
