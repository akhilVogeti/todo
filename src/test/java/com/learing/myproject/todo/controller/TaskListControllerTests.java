package com.learing.myproject.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.service.TaskListService;
import com.learing.myproject.todo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(TaskListController.class)
public class TaskListControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskListService taskListService;


    @Autowired
    private WebApplicationContext webApplicationContext;

    private User testUser1;
    private TaskList testTaskList1;
    private TaskList testTaskList2;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser1 = User.builder()
                .id("testuser1")
                .username("testuser1")
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



    @Test
    public void testCreateTaskList_ReturnTaskList() throws Exception {

        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        TaskList testTaskList3= TaskList.builder()
                .id("14")
                .userId("testuser1")
                .listName("testList3")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String taskListJson = objectMapper.writeValueAsString(testTaskList3);

        when(taskListService.createTaskList(refEq("testuser1"),refEq( testTaskList3))).thenReturn(testTaskList3);

        MvcResult result = mockMvc.perform(
                        post("/tasks/lists")
                                .principal(testPrincipal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskListJson))
                .andExpect(jsonPath("$.listName").value("testList3"))
                .andReturn();
        System.out.println("Result: " + result.getResponse().getContentAsString());

    }

    @Test
    public void testUpdateTaskList_ReturnTaskList() throws Exception {

        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");
        TaskList testTaskList3= TaskList.builder()
                .listName("updatedList")
                .build();

        String username = "testuser1";
        String listName = "testList1";

        ObjectMapper objectMapper = new ObjectMapper();
        String taskListJson = objectMapper.writeValueAsString(testTaskList3);

        when(taskListService.updateTaskList(eq(username),eq(listName),refEq(testTaskList3)))
                .thenReturn(testTaskList3);

        mockMvc.perform(put("/tasks/lists/{listName}",listName)
                        .principal(testPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskListJson))
               .andExpect(jsonPath("$.listName").value("updatedList"));

        verify(taskListService).updateTaskList(eq(username),eq(listName),refEq(testTaskList3));

    }

    @Test
    public void testDeleteTaskList_VerifyArguments() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        String listName = "testList1";

        mockMvc.perform(delete("/tasks/lists/{listName}" , listName)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(testPrincipal));

        verify(taskListService).deleteTaskList("testuser1","testList1");

    }

}
