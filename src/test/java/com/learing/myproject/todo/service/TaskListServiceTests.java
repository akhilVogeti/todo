package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TaskListServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskListServiceImpl taskListService;

    private User testUser1;
    private TaskList testTaskList1;

    private TaskList testTaskList2;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testUser1 = User.builder()
                .id("testUser1")
                .username("testUser1")
                .taskListIds(new ArrayList<>(Arrays.asList("11", "12")))
                .build();

        testTaskList1 = TaskList.builder()
                .id("11")
                .userId("testUser1")
                .listName("testList1")
                .taskIds(new ArrayList<>(Arrays.asList("1", "2")))
                .build();
        testTaskList2 = TaskList.builder()
                .id("12")
                .userId("testUser1")
                .listName("testList2")
                .taskIds(new ArrayList<>(Arrays.asList("3", "4")))
                .build();
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        taskListRepository.deleteAll();
    }



    @Test
    public void testCreateTaskList_ReturnTaskList() {
        TaskList testTaskList3= TaskList.builder()
                .id("13")
                .listName("testList3")
                .build();

        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1","testList3"))
                .thenReturn(null);
        when(taskListRepository.save(testTaskList3)).thenReturn(testTaskList3);
        when(userRepository.save(testUser1)).thenReturn(testUser1);

        TaskList savedList = taskListService.createTaskList("testUser1", testTaskList3);

        Assert.assertEquals("testList3",savedList.getListName());
        Assert.assertEquals("testUser1",savedList.getUserId());
    }

    @Test
    public void testCreateTaskList_AlreadyExists() {

        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1","testList1"))
                .thenReturn(testTaskList1);

        Exception exception =  assertThrows(RuntimeException.class, () -> {
           taskListService.createTaskList("testUser1",testTaskList1);
        });

        assertTrue(exception.getMessage().contains("List already exists"));
    }

    @Test
    public void testDeleteTaskList_ReturnVoid() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1", testTaskList1.getListName()))
                .thenReturn(testTaskList1);
        taskListRepository.deleteById(testTaskList1.getId());
        verify(taskListRepository).deleteById("11");
    }

    @Test
    public void testDeleteTaskList_UserNotFound() {

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> {
            taskListService.deleteTaskList("nonexistentuser",anyString());
        });
    }

    @Test
    public void testDeleteTaskList_ListDoesNotExist() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1", "nonexistent"))
                .thenReturn(null);

        Exception exception =  assertThrows(RuntimeException.class, () -> {
            taskListService.deleteTaskList("testUser1","nonexistent");
        });

        assertTrue(exception.getMessage().contains("List does not exist"));

    }

    @Test
    public void testUpdateTaskList_ReturnTaskList() {
        TaskList testTaskList3= TaskList.builder()
                .listName("updatedList")
                .build();

        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1", testTaskList1.getListName()))
                .thenReturn(testTaskList1);

       when(taskListRepository.save(ArgumentMatchers.any(TaskList.class))).thenAnswer(invocation -> (TaskList) invocation.getArgument(0));
        TaskList taskListUpdated = taskListService.updateTaskList("testUser1", testTaskList1.getListName(), testTaskList3);
        Assertions.assertEquals("11",taskListUpdated.getId());
        Assertions.assertEquals("updatedList",taskListUpdated.getListName());

    }

    @Test
    void testUpdateTaskList_UserNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
            taskListService.updateTaskList("nonexistentuser","1", ArgumentMatchers.any(TaskList.class));
        });
    }

    @Test
    public void testUpdateTaskList_ListDoesNotExist() {
        String listName = "nonexistent";
        Exception exception =  assertThrows(RuntimeException.class, () -> {
            when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
            when(taskListRepository.findByUserIdAndListName("testUser1", listName))
                    .thenReturn(null);
            taskListService.updateTaskList("testUser1",listName, ArgumentMatchers.any(TaskList.class));
        });

        assertTrue(exception.getMessage().contains("List does not exist"));

    }

}
