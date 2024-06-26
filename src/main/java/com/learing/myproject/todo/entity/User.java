package com.learing.myproject.todo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document("users")
public class User {
    @Id
    private String id;
    @Indexed
    private String username;
    private String password;

    private List<String> taskListIds;

    public List<String> getTaskListIds() {
        return taskListIds;
    }

    public void setTaskListIds(List<String> taskListIds) {
        this.taskListIds = taskListIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getUsername() {
        return username;
    }

    public  void setUsername(String username) {
        this.username = username;
    }

    public  String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {}

    public User(String username, String password, List<String> taskListIds) {
        this.username = username;
        this.password = password;
        this.taskListIds = taskListIds;
    }

    public User(String id, String username, String password, List<String> taskListIds) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.taskListIds = taskListIds;
    }
}
