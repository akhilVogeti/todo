package com.learing.myproject.todo.repository;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TaskListRepository extends MongoRepository<TaskList, String> {

    @Query("{userId: ?0}")
    List<TaskList> findByUserId(String userId);

    @Query("{userId: ?0 , listName: ?1}")
    TaskList findByUserIdAndListName(String userId, String listName);
}
