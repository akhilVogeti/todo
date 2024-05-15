package com.learing.myproject.todo.controller;

import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(UserController.class)
public class UserControllerTests {

    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskListRepository taskListRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;



    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup()
    {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
    }
    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        taskListRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        User testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setTaskListIds(new ArrayList<>());

        TaskList testDefaultList = new TaskList();
        testDefaultList.setId("11");
        testDefaultList.setListName("inbox");
        testDefaultList.setTaskIds(new ArrayList<>());

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(testUser);
        when(taskListRepository.save(ArgumentMatchers.any(TaskList.class))).thenReturn(testDefaultList);
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"password\"}"))
                .andExpect(status().isCreated());
        Assert.assertEquals(List.of("11"),testUser.getTaskListIds());
        Assert.assertEquals("1",testDefaultList.getUserId());
    }

    @Test
    public void testRegisterUser_ReturnConflictStatus () throws Exception {
        User testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testUser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setTaskListIds(new ArrayList<>());

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already taken. Please try again"));
    }
}
