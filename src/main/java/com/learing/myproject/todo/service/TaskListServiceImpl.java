package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.TaskRepository;
import com.learing.myproject.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskListServiceImpl implements TaskListService{

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TaskList createTaskList(String username, TaskList taskList) {
        User user = findUserByUsername(username);
        TaskList fetchedTaskList = taskListRepository.findByUserIdAndListName(user.getId(), taskList.getListName());

        if(fetchedTaskList != null)
            throw new RuntimeException("List already exists");

        taskList.setUserId(user.getId());
        taskList.setTaskIds(new ArrayList<>());
        taskList = taskListRepository.save(taskList);
        user.getTaskListIds().add(taskList.getId());
        user = userRepository.save(user);
        return taskList;
    }

    @Override
    public void deleteTaskList(String username, String listName) {
        User user = findUserByUsername(username);
        TaskList taskList = taskListRepository.findByUserIdAndListName(user.getId(),listName);

        if(taskList == null)
            throw new RuntimeException("List does not exist");
        user.getTaskListIds().remove(taskList.getId());
        userRepository.save(user);
        deleteTasksInList(taskList);
        taskListRepository.deleteById(taskList.getId());
    }



    @Override
    public TaskList updateTaskList(String username, String listName, TaskList taskListUpdate) {
        User user = findUserByUsername(username);
        TaskList existingTaskList = taskListRepository.findByUserIdAndListName(user.getId(),listName);
        if(existingTaskList == null)
            throw new RuntimeException("List does not exist");

        existingTaskList.setListName(taskListUpdate.getListName());
        return taskListRepository.save(existingTaskList);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
    }

    private void deleteTasksInList(TaskList taskList) {
        List<String> taskIds = taskList.getTaskIds();
        taskRepository.deleteAllById(taskIds);
    }


}
