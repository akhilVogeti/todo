package com.learing.myproject.todo.repository;

import com.learing.myproject.todo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{username: ?0}")
    Optional<User> findByUsername(String username);
}
