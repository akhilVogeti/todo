package com.learing.myproject.todo.repository;

import com.learing.myproject.todo.entity.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.util.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@DataMongoTest
public class TaskRepositoryTests {
    // findAllById, findById, save, delete.
    @Autowired
    private TaskRepository taskRepository;
    @BeforeEach
    public void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    public void TaskRepository_Save_ReturnSavedTask(){
        Task enteredTask = Task.builder()
                .id("1")
                .title("testTask1")
                .description("description for testTask1")
                .taskListId("10")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,8))
                .category("testing")
                .completed(false)
                .priority("2").build();

        Task savedTask = taskRepository.save(enteredTask);

        Assertions.assertThat(savedTask).isEqualTo(enteredTask);

    }

    @Test
    public void TaskRepository_findById_ReturnTaskById(){
        Task testTask1 = Task.builder()
                .id("1")
                .title("testTask1")
                .description("description for testTask1")
                .taskListId("10")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,8))
                .category("testing")
                .completed(false)
                .priority("2").build();
        Task savedTask = taskRepository.save(testTask1);
        Optional <Task> optionalTask = taskRepository.findById("1");
        Task fetchedTask = optionalTask.get();
        Assertions.assertThat(fetchedTask.getId()).isEqualTo(savedTask.getId());

        assertThrows(RuntimeException.class, () -> taskRepository.findById("10")
                .orElseThrow(() -> new RuntimeException("Task not found with this id")));

    }

    @Test
    public void TaskRepository_findAllById_ReturnListOfTasks() {
        Task testTask1 = Task.builder()
                .id("1")
                .title("testTask1")
                .description("description for testTask1")
                .taskListId("10")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,8))
                .category("testing")
                .completed(false)
                .priority("2").build();
        Task testTask2 = Task.builder()
                .id("2")
                .title("testTask2")
                .description("description for testTask2")
                .taskListId("11")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,9))
                .category("testing")
                .completed(false)
                .priority("2").build();
        testTask1 = taskRepository.save(testTask1);
        testTask2 = taskRepository.save(testTask2);
        List<Task> fetchedTasks = taskRepository.findAllById(Arrays.asList("1","2"));
        Assertions.assertThat(2).isEqualTo(fetchedTasks.size());
    }


    @Test
    public void TaskRepository_Delete_ReturnVoid(){
        Task testTask1 = Task.builder()
                .id("1")
                .title("testTask1")
                .description("description for testTask1")
                .taskListId("10")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,8))
                .category("testing")
                .completed(false)
                .priority("2").build();
        Task savedTask = taskRepository.save(testTask1);
        taskRepository.deleteById("1");
        Optional<Task> deletedTaskOptional = taskRepository.findById("1");
        Assertions.assertThat(deletedTaskOptional).isEmpty();
    }

}
