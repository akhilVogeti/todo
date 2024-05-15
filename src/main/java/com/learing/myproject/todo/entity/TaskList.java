package com.learing.myproject.todo.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Document(collection = "taskLists")
public class TaskList {
    @Id
    private String id;
    private String userId;
    private String listName;
    private List<String> taskIds;

    public TaskList(String id, String userId, String listName, List<String> taskIds) {
        this.id = id;
        this.userId = userId;
        this.listName = listName;
        this.taskIds = taskIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    public TaskList() {}
}
