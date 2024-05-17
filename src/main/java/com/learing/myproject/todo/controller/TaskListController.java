package com.learing.myproject.todo.controller;

import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/tasks/lists")
public class TaskListController {

    @Autowired
    private TaskListService taskListService;

    @PostMapping()
    public TaskList createTaskList(Principal user, @RequestBody TaskList taskList) {
        System.out.println(user + taskList.getListName());
        return taskListService.createTaskList(user.getName(), taskList);
    }

    @PutMapping("/{listName}")
    public TaskList updateTaskList(Principal user, @PathVariable String listName, @RequestBody TaskList taskListUpdate){
        System.out.println(user + listName + taskListUpdate.getListName());
        return taskListService.updateTaskList(user.getName(),listName,taskListUpdate);
    }

    @DeleteMapping("/{listName}")
    public void deleteTaskList(Principal user, @PathVariable String listName){
         taskListService.deleteTaskList(user.getName(), listName);
    }

}
