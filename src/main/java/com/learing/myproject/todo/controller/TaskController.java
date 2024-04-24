package com.learing.myproject.todo.controller;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/")
    public List<Task> getAllTasks(Principal user) {
        String username = user.getName();
        return taskRepository.findByOwner(username);
    }

    @PostMapping("/")
    public Task createTask(Principal user, @RequestBody Task task) {
        task.setOwner(user.getName());
        return taskRepository.save(task);

    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task taskDetails) {
        System.out.println("in update method put mapping");
        Optional<Task> optionalTask = taskRepository.findById(id);
        System.out.println(optionalTask);
        Task task = optionalTask.orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        // Update other fields as needed

        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }
}

