package com.learing.myproject.todo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "lists")
public class TaskList {
    @Id
    private String id;
    private String owner;
    private String listName;
    private List<String> taskIds;

    public TaskList(String id, String owner, String listName, List<String> taskIds) {
        this.id = id;
        this.owner = owner;
        this.listName = listName;
        this.taskIds = taskIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
}
