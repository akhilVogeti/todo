package com.learing.myproject.todo.repository;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TaskListRepository extends MongoRepository<TaskList, String> {

    @Query("{owner:'?0'}")
    List<TaskList> findByOwner(String ownerName);

    TaskList findByOwnerAndListName(String owner, String listName);
}
