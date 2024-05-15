package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;
import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.TaskListRepository;
import com.learing.myproject.todo.repository.TaskRepository;
import com.learing.myproject.todo.repository.UserRepository;
import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TaskServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskListRepository taskListRepository;
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser1;
    private List<TaskList> testLists;
    private List<Task> testTasks;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
         testUser1 = User.builder()
                .id("testUser1")
                .username("testUser1")
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
                .completed(true)
                .priority("1").build();

        Task testTask3 = Task.builder()
                .id("3")
                .title("testTask3")
                .description("description for testTask3")
                .taskListId("12")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,10))
                .category("testing")
                .completed(false)
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
         testLists = new ArrayList<>(Arrays.asList(testTaskList1,testTaskList2));
         testTasks = new ArrayList<>(Arrays.asList(testTask1,testTask2,testTask3,testTask4));

    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        taskListRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    public void testFindAllTasks_ReturnAllTasks() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserId("testUser1")).thenReturn(testLists);
        when(taskRepository.findAllById(Arrays.asList("1","2","3","4"))).thenReturn(testTasks);

        List<Task> allTasks = taskService.findAllTasks("testUser1");

        Assert.assertEquals(4, allTasks.size());
        Assert.assertEquals(false,allTasks.get(0).isCompleted());
        Assert.assertEquals(false,allTasks.get(1).isCompleted());
        Assert.assertEquals(true,allTasks.get(2).isCompleted());
        Assert.assertEquals(true,allTasks.get(3).isCompleted());
        Assert.assertTrue(allTasks.get(0).getDueDate().before(allTasks.get(1).getDueDate()));
    }

    @Test
    public void testGetTasksByList_ReturnTasksByLists() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findById("11")).thenReturn(Optional.ofNullable(testLists.get(0)));
        when(taskListRepository.findById("12")).thenReturn(Optional.ofNullable(testLists.get(1)));
        when(taskRepository.findAllById(Arrays.asList("1","2"))).thenReturn(Arrays.asList(testTasks.get(0), testTasks.get(1)));
        when(taskRepository.findAllById(Arrays.asList("3","4"))).thenReturn(Arrays.asList(testTasks.get(2), testTasks.get(3)));

        Map<String, List<Task>> tasksByLists = taskService.getTasksByLists("testUser1");


        Assert.assertEquals(2, tasksByLists.get("testList1").size());
        Assert.assertEquals(Arrays.asList(testTasks.get(2),testTasks.get(3)),tasksByLists.get("testList2"));
    }

    @Test
    public void testGetTasksByListName_ReturnTasksByListName() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1", "testList1"))
                .thenReturn(testLists.get(0));
        when(taskRepository.findAllById(Arrays.asList("1","2"))).thenReturn(Arrays.asList(testTasks.get(0), testTasks.get(1)));

        List<Task> tasks = taskService.getTasksByListName("testUser1","testList1");
        Assert.assertTrue(tasks.size()==2);
        Assert.assertEquals(testTasks.get(0).getTitle(),tasks.get(0).getTitle());
        Assert.assertEquals(true, tasks.get(1).isCompleted());
    }

    @Test
    public void testCreateTaskInList_ReturnTask(){
        Task testTask5 = Task.builder()
                .id("5")
                .title("testTask5")
                .description("description for testTask5")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,11))
                .category("testing")
                .completed(false)
                .priority("2").build();

        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserIdAndListName("testUser1", "testList1"))
                .thenReturn(testLists.get(0));
        when(taskRepository.save(testTask5)).thenReturn(testTask5);
        when(taskListRepository.save(testLists.get(0))).thenReturn(testLists.get(0));

        Task savedTask = taskService.createTask("testUser1",testTask5,"testList1");
        Assert.assertEquals("11",savedTask.getTaskListId());
        Assert.assertEquals("5",savedTask.getId());
        Assert.assertEquals(List.of("1","2","5"),testLists.get(0).getTaskIds());

    }

    @Test
    public void testCreateTaskList_ReturnTaskList() {
        TaskList testTaskList3= TaskList.builder()
                .id("13")
                .listName("testList3")
                .build();

        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.save(testTaskList3)).thenReturn(testTaskList3);
        when(userRepository.save(testUser1)).thenReturn(testUser1);

        TaskList savedList = taskService.createTaskList("testUser1", testTaskList3);

        Assert.assertEquals("testList3",savedList.getListName());
        Assert.assertEquals("testUser1",savedList.getUserId());
    }

    @Test
    public void testUpdateTask_ReturnUpdatedTask() {
        Task testTaskUpdate= Task.builder()
                .id("1")
                .title("testTaskUpdate")
                .description("description for updated testTask")
                .createdOn(new Date(2000,1,1))
                .dueDate(new Date(2000,1,11))
                .category("testing")
                .completed(false)
                .priority("2").build();
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserId("testUser1")).thenReturn(testLists);
        when(taskRepository.findById("1")).thenReturn(Optional.ofNullable(testTasks.get(0)));
        when(taskRepository.save(any(Task.class))).thenReturn(testTaskUpdate);

        Task updatedTask = taskService.updateTask("testUser1","1",testTaskUpdate);

        Assert.assertEquals("1",updatedTask.getId());
        Assert.assertEquals("testTaskUpdate",updatedTask.getTitle());
    }

    @Test
    public void testDeleteTask_ReturnVoid(){
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserId("testUser1")).thenReturn(testLists);
        when(taskRepository.findById("1")).thenReturn(Optional.ofNullable(testTasks.get(0)));

        taskService.deleteTask("testUser1", "1");

        verify(taskRepository).deleteById("1");

    }

    @Test
    public void testGetTaskById_ReturnTask(){
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskListRepository.findByUserId("testUser1")).thenReturn(testLists);
        when(taskRepository.findById("1")).thenReturn(Optional.ofNullable(testTasks.get(0)));

        Task fetchedTask = taskService.getTaskById("testUser1","1");

        Assert.assertEquals("testTask1", fetchedTask.getTitle());
        Assert.assertEquals("11", fetchedTask.getTaskListId());
    }

    @Test
    public void testSortAndFilter_ReturnTaskList() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        when(taskService.findAllTasks("testUser1")).thenReturn(testTasks);

        List<Task> tasksByDueDateAndCategory = taskService.sortAndFilter("testUser1", "dueDate", "category","testing");
        List<Task> tasksByPriorityAndCategory = taskService.sortAndFilter("testUser1", "priority","category","testing");

        Assert.assertEquals(3, tasksByDueDateAndCategory.size());
        Assert.assertTrue(tasksByDueDateAndCategory.get(0).getDueDate()
                .before(tasksByDueDateAndCategory.get(1).getDueDate()));

        Assert.assertEquals(3, tasksByPriorityAndCategory.size());
        Assert.assertTrue(tasksByPriorityAndCategory.get(0).getPriority()
                .compareTo(tasksByPriorityAndCategory.get(1).getPriority()) >= 0);

    }

    @Test
    public void testSearch_ReturnTasksList() {
        when(userRepository.findByUsername("testUser1")).thenReturn(Optional.ofNullable(testUser1));
        Criteria expectedCriteria = new Criteria().andOperator(
                Criteria.where("taskListId").in(List.of("11","12")),
                new Criteria().orOperator(
                        Criteria.where("title").regex("ice"),
                        Criteria.where("description").regex("ice")
                )
        );
        Query expectedQuery = new Query(expectedCriteria);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);


        when(mongoTemplate.find(queryCaptor.capture(), eq(Task.class))).thenReturn(Arrays.asList(testTasks.get(1)));


        List<Task> fetchedTasks = taskService.searchTasks("testUser1", "ice");
        Query capturedQuery = queryCaptor.getValue();

        Assert.assertEquals(expectedQuery,capturedQuery);

        verify(userRepository).findByUsername("testUser1");
        verify(mongoTemplate).find(eq(capturedQuery), eq(Task.class));
        Assert.assertEquals(1, fetchedTasks.size());
        Assert.assertEquals("testTask2", fetchedTasks.get(0).getTitle());

    }

}
