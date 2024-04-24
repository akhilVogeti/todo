package com.learing.myproject.todo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "tasks")
public class Task {
        @Id
        private String id;
        private String title;
        private String description;
        private String owner;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Date createdOn;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Date dueDate;
        private String category;

        private String priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Task(String id, String title, String description, String owner, Date createdOn, Date dueDate, String category, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.createdOn = createdOn;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
    }
}
