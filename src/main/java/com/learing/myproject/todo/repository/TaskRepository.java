package com.learing.myproject.todo.repository;


import com.learing.myproject.todo.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String>{

    @Query("{owner:'?0'}")
    List<Task> findByOwner(String ownerName);

}
