package com.learing.myproject.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(TaskController.class)
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;


    @Autowired
    private WebApplicationContext webApplicationContext;

    private User testUser1;
    private List<TaskList> testLists;
    private List<Task> testTasks;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser1 = User.builder()
                .id("testuser1")
                .username("testuser1")
                .taskListIds(new ArrayList<>(Arrays.asList("11","12")))
                .build();


        TaskList testTaskList1 = TaskList.builder()
                .id("11")
                .userId("testUser1")
                .listName("testList1")
                .taskIds(new ArrayList<>(Arrays.asList("1", "2")))
                .build();
        TaskList testTaskList2 = TaskList.builder()
                .id("12")
                .userId("testUser1")
                .listName("testList2")
                .taskIds(new ArrayList<>(Arrays.asList("3", "4")))
                .build();
        TaskList testTaskList3 = TaskList.builder()
                .id("13")
                .userId("testUser1")
                .listName("inbox")
                .taskIds(new ArrayList<>(Arrays.asList("5")))
                .build();

        Task testTask1 = Task.builder()
                .id("1")
                .title("testTask1")
                .description("description for testTask1")
                .taskListId("11")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,8))
                .category("testing")
                .completed(false)
                .priority("2").build();

        Task testTask2 = Task.builder()
                .id("2")
                .title("testTask2")
                .description("talk to ice cream vendor")
                .taskListId("11")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,9))
                .category("testing")
                .completed(false)
                .priority("1").build();

        Task testTask3 = Task.builder()
                .id("3")
                .title("testTask3")
                .description("description for testTask3")
                .taskListId("12")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,10))
                .category("testing")
                .completed(true)
                .priority("3").build();

        Task testTask4 = Task.builder()
                .id("4")
                .title("testTask4")
                .description("description for testTask4")
                .taskListId("12")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,11))
                .category("sample")
                .completed(true)
                .priority("4").build();

        Task testTask5 = Task.builder()
                .id("5")
                .title("taskInInbox")
                .description("description for testTask5")
                .taskListId("13")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,11))
                .category("sample")
                .completed(true)
                .priority("4").build();
        testLists = new ArrayList<>(Arrays.asList(testTaskList1,testTaskList2,testTaskList3));
        testTasks = new ArrayList<>(Arrays.asList(testTask1,testTask2,testTask3,testTask4,testTask5));

    }


    @Test
    public void testGetAllTasks_ReturnTasksList () throws Exception {

        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testUser1", "password");

        when(taskService.findAllTasks("testUser1")).thenReturn(testTasks);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/")
                        .principal(testPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("testTask1"))
                .andExpect(jsonPath("$[1].title").value("testTask2"));
    }

    @Test
    public void testCreateTask_ReturnTask() throws Exception {
        Task task = testTasks.get(0);
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        when(taskService.createTask(refEq("testuser1"),refEq(task),refEq("inbox"))).thenReturn(task);

        ObjectMapper objectMapper = new ObjectMapper();
        String taskJson = objectMapper.writeValueAsString(task);

       mockMvc.perform(
               post("/tasks/")
                       .principal(testPrincipal)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(taskJson))
               .andExpect(jsonPath("$.title").value("testTask1"))
               .andReturn();

    }

    @Test
    public void testGetAllTasksByLists_ReturnMap() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        Map<String,List<Task>> testMap = new HashMap<>();
        testMap.put("inbox",new ArrayList<>(List.of(testTasks.get(4))));
        testMap.put("testList1", new ArrayList<>(List.of(testTasks.get(0),testTasks.get(1))));
        testMap.put("testList2", new ArrayList<>(List.of(testTasks.get(2),testTasks.get(3))));

        when(taskService.getTasksByLists("testuser1")).thenReturn(testMap);

         mockMvc.perform(get("/tasks/lists")
                .principal(testPrincipal))
                .andExpect(jsonPath("$.testList1" ,hasSize(2)))
                .andExpect(jsonPath("$.testList2" ,hasSize(2)))
                .andExpect(jsonPath("$.inbox" ,hasSize(1)));

    }

    @Test
    public void testCreateTaskList_ReturnTaskList() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        TaskList testTaskList4= TaskList.builder()
                .id("14")
                .userId("testuser1")
                .listName("testList4")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String taskListJson = objectMapper.writeValueAsString(testTaskList4);

        when(taskService.createTaskList(refEq("testuser1"), refEq(testTaskList4))).thenReturn(testTaskList4);

        MvcResult result = mockMvc.perform(
                        post("/tasks/lists")
                                .principal(testPrincipal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskListJson))
                .andExpect(jsonPath("$.listName").value("testList4"))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void testCreateTaskInList_ReturnTask() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        Task task = Task.builder()
                .id("6")
                .title("testTask6")
                .description("description for testTask6")
                .taskListId("14")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,11))
                .category("sample")
                .completed(true)
                .priority("4").build();

        TaskList testTaskList4= TaskList.builder()
                .id("14")
                .userId("testuser1")
                .listName("testList4")
                .taskIds(new ArrayList<>())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String taskJson = objectMapper.writeValueAsString(task);


        when(taskService.createTask(refEq("testuser1"),refEq(task),refEq("testList4"))).thenReturn(task);

        MvcResult result = mockMvc.perform(
                        post("/tasks/lists/{listName}", "testList4")
                                .principal(testPrincipal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskJson))
                .andExpect(jsonPath("$.id").value("6"))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());


    }

    @Test
    public void testGetTasksByListName_ReturnTasks() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        when(taskService.getTasksByListName(refEq("testuser1"),refEq("testList1")))
                .thenReturn(List.of(testTasks.get(0),testTasks.get(1)));

        mockMvc.perform(get("/tasks/lists/{listName}","testList1")
                        .principal(testPrincipal))
                .andExpect(jsonPath("$" ,hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("testTask1"))
                .andExpect(jsonPath("$[1].title").value("testTask2"));

    }

    @Test
    public void testGetTaskById_ReturnTask() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        when(taskService.getTaskById("testuser1","1")).thenReturn(testTasks.get(0));

       mockMvc.perform(get("/tasks/{id}","1")
                        .principal(testPrincipal))
              .andExpect(jsonPath("$.title").value("testTask1"));

    }

    @Test
    public void testDeleteTask_VerifyParameters () throws Exception {

        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");


        mockMvc.perform(delete("/tasks/" + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                .principal(testPrincipal));

        verify(taskService).deleteTask("testuser1","1");

    }

    @Test
    public void testSortAndFilter_ReturnSortedFilteredTasks () throws Exception {

        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        String username = "testuser1";
        String sortBy = "dueDate";
        String filterBy = "category";
        String filterValue = "testing";

        List <Task> sampleTasks = new ArrayList<>(List.of(testTasks.get(0),testTasks.get(1)
        , testTasks.get(2)));


        when(taskService.sortAndFilter(username,sortBy,filterBy,filterValue)).thenReturn(sampleTasks);

       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks")
                .param("sortBy", sortBy)
                .param("filterBy", filterBy)
                .param("filterValue", filterValue)
                .principal(testPrincipal))
                .andExpect(jsonPath("$" ,hasSize(3)))
               .andReturn();

       verify(taskService).sortAndFilter(username,sortBy,filterBy,filterValue);

    }

    @Test
    public void testSearchTasks_ReturnTasksFound() throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        String testQuery = "ice";
        String username = "testuser1";

        when(taskService.searchTasks(eq(username),eq(testQuery))).thenReturn(new ArrayList<>(Arrays.asList(testTasks.get(1))));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks/search")
                        .param("query", testQuery)
                        .principal(testPrincipal))
                .andExpect(jsonPath("$" ,hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(testTasks.get(1).getTitle()))
                .andReturn();


        verify(taskService).searchTasks(username,testQuery);
    }

    @Test
    public void testUpdateTask_ReturnUpdatedTask () throws Exception {
        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");

        Task testTaskUpdate= Task.builder()
                .id("1")
                .title("testTaskUpdate")
                .description("description for updated testTask")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,11))
                .category("testing")
                .completed(false)
                .priority("2").build();

        String username = "testuser1";
        String taskId = "1";

        ObjectMapper objectMapper = new ObjectMapper();
        String taskJson = objectMapper.writeValueAsString(testTaskUpdate);

        when(taskService.updateTask(eq(username),eq(taskId),refEq(testTaskUpdate) )).thenReturn(testTaskUpdate);

        mockMvc.perform(put("/tasks/{id}",taskId)
                .principal(testPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(jsonPath("$.title").value("testTaskUpdate"));

        verify(taskService).updateTask(eq(username),eq(taskId),refEq(testTaskUpdate));


    }




















































































//    @Test
//    public void testUpdateTask_ReturnUpdatedTask() throws Exception {
//        Principal testPrincipal = new UsernamePasswordAuthenticationToken("testuser1", "password");
//
//        Task testTaskUpdate= Task.builder()
//                .id("1")
//                .title("testTaskUpdate")
//                .description("description for updated testTask")
//                .createdOn(new Date(2000,1,1))
//                .dueDate(new Date(2000,1,11))
//                .category("testing")
//                .completed(false)
//                .priority("2").build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String taskJson = objectMapper.writeValueAsString(testTaskUpdate);
//
//        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
//        ArgumentCaptor<String> taskIdCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
//
//
//        when(taskService.updateTask(refEq("testuser1"),refEq("1"),refEq(testTaskUpdate))).thenReturn(testTaskUpdate);
//
//
//        MvcResult result = mockMvc.perform(
//                        put("/tasks/{id}", "1")
//                                .principal(testPrincipal)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(taskJson))
//                .andReturn();
//
//        verify(taskService).updateTask(usernameCaptor.getValue(), taskIdCaptor.getValue(),taskCaptor.getValue());
//
//        System.out.println(usernameCaptor.getValue());
//        System.out.println(taskIdCaptor.getValue());
//        System.out.println(taskCaptor.getValue().getTitle());
//        System.out.println("result: " + result.getResponse().getContentAsString());
//    }














}
