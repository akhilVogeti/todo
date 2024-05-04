package com.learing.myproject.todo.controller;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.repository.TaskRepository;
import com.learing.myproject.todo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/")
    public List<Task> getAllTasks(Principal user) {
        return taskService.findAllTasks(user.getName());
    }

    @PostMapping("/")
    public Task createTaskInDefaultList(Principal user, @RequestBody Task task) {
        return taskService.createTask(user.getName(), task, "inbox");
    }



    @GetMapping("/lists")
    public  Map<String, List<String>> getAllTasksByLists(Principal user) {
        return taskService.getTasksByLists(user.getName());
    }

    @PostMapping("/lists")
    public TaskList createTaskList(Principal user, @RequestBody TaskList taskList) {
        return taskService.createTaskList (user.getName(), taskList);
    }


    @GetMapping("/lists/{listName}")
    public List<Task> getTaskByListName(Principal user, @PathVariable String listName){
        return taskService.getTasksByListName(user.getName(),listName);
    }

    @PostMapping("/lists/{listName}")
    public Task createTaskInList(Principal user, @RequestBody Task task, @PathVariable String listName) {
        return taskService.createTask(user.getName(), task, listName);
    }

    @GetMapping("/{id}")
    public Task getTaskById(Principal user, @PathVariable String id) {
        return taskService.getTaskById(user.getName(), id);
    }

    @PutMapping("/{id}")
    public Task updateTask(Principal user, @PathVariable String id, @RequestBody Task taskDetails) {
       return taskService.updateTask(user.getName(), id, taskDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(Principal user, @PathVariable String id) {
        taskService.deleteTask(user.getName(), id);
    }

    @GetMapping("?sortBy={sortField}&filterBy={filterField}")
    public List<Task> sortAndFilter(Principal user, String sortField, String filterField) {
        return taskService.sortAndFilter(user.getName(), sortField, filterField);
    }

    @GetMapping("/search?query={searchText}")
    public List<Task> searchTasks(Principal user, String searchText) {
        return taskService.searchTasks(user.getName(), searchText);
    }

}

