package com.learing.myproject.todo.controller;


import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User user){
        try {
            if (userRepository.findByUsername(user.getUsername()).isPresent())
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken. Please try again");

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setTaskListIds(new ArrayList<>());
            User savedUser = userRepository.save(user);

            TaskList defaultTaskList = new TaskList();
            defaultTaskList.setUserId(savedUser.getId());
            defaultTaskList.setListName("inbox");
            defaultTaskList.setTaskIds(new ArrayList<>());
            defaultTaskList = taskListRepository.save(defaultTaskList);
            savedUser.getTaskListIds().add(defaultTaskList.getId());
            defaultTaskList.setUserId(savedUser.getId());
            userRepository.save(savedUser);
            taskListRepository.save(defaultTaskList);

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e){
           return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


}
