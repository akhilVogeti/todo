package com.learing.myproject.todo.repository;

import com.learing.myproject.todo.entity.User;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@DataMongoTest

public class UserRepositoryTests {

    //save, findByUsername
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void UserRepository_Save_ReturnSavedUser(){
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setTaskListIds(Arrays.asList("1", "2", "3"));

        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getUsername()).isEqualTo("testUser");
        Assertions.assertThat(passwordEncoder.matches("password",savedUser.getPassword())).isTrue();
        Assertions.assertThat(savedUser.getTaskListIds()).isEqualTo(Arrays.asList("1", "2", "3"));
    }

    @Test
    public void UserRepository_findByUsername_ReturnUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setTaskListIds(Arrays.asList("1", "2", "3"));
        User savedUser = userRepository.save(user);

        Optional<User> optionalUser = userRepository.findByUsername("testUser");
        User foundUser = optionalUser.get();
        Assertions.assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    public void UserRepository_findByUsername_ReturnException() {
        assertThrows(UsernameNotFoundException.class, () -> userRepository.findByUsername("nonExistentUser")
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this username")));

    }

}
