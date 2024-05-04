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
        private String taskListId;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Date createdOn;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Date dueDate;
        private String category;

        private boolean completed;
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

    public String getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(String taskListId) {
        this.taskListId = taskListId;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Task(String id, String title, String description, String taskListId, Date createdOn, Date dueDate, String category, boolean completed, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskListId = taskListId;
        this.createdOn = createdOn;
        this.dueDate = dueDate;
        this.category = category;
        this.completed = completed;
        this.priority = priority;
    }
}
