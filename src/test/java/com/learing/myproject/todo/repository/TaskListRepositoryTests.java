package com.learing.myproject.todo.repository;
import com.learing.myproject.todo.entity.Task;
import com.learing.myproject.todo.entity.TaskList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

@DataMongoTest
public class TaskListRepositoryTests {
    // save, findByUserId, findById, findByUserIdAndListName
    @Autowired
    private TaskListRepository taskListRepository;

    @BeforeEach
    public void cleanUp() {
        taskListRepository.deleteAll();
    }


    @Test
    public void TaskListRepository_save_ReturnSavedTaskList() {
        TaskList testTaskList1 = TaskList.builder()
                .id("1")
                .userId("11")
                .listName("testList")
                .taskIds(List.of("1", "2"))
                .build();

        TaskList savedList = taskListRepository.save(testTaskList1);
        Assertions.assertThat(savedList.getId()).isEqualTo("1");
    }

    @Test
    public void TaskListRepository_findByUserId_ReturnListOfTaskList() {
        TaskList testTaskList1 = TaskList.builder()
                .id("1")
                .userId("11")
                .listName("testList1")
                .taskIds(List.of("1", "2"))
                .build();

        TaskList savedList1 = taskListRepository.save(testTaskList1);
        TaskList testTaskList2 = TaskList.builder()
                .id("2")
                .userId("11")
                .listName("testList2")
                .taskIds(List.of("3", "4"))
                .build();

        TaskList savedList2 = taskListRepository.save(testTaskList2);
        List<TaskList> fetchedLists = taskListRepository.findByUserId("11");

        Assertions.assertThat(fetchedLists.size()).isEqualTo(2);

    }

    @Test
    public void TaskListRepository_findById_ReturnTaskList() {
        TaskList testTaskList1 = TaskList.builder()
                .id("1")
                .userId("11")
                .listName("testList1")
                .taskIds(List.of("1", "2"))
                .build();

        TaskList savedTaskList = taskListRepository.save(testTaskList1);
        Optional<TaskList> optionalTaskList = taskListRepository.findById("1");
        TaskList fetchedTaskList = optionalTaskList.get();
        Assertions.assertThat(fetchedTaskList.getId()).isEqualTo(savedTaskList.getId());
    }

    @Test
    public void TaskListRepository_findByUserIdAndListName_ReturnTaskList() {
        TaskList testTaskList1 = TaskList.builder()
                .id("1")
                .userId("11")
                .listName("testList1")
                .taskIds(List.of("1", "2"))
                .build();

        TaskList savedList1 = taskListRepository.save(testTaskList1);
        TaskList testTaskList2 = TaskList.builder()
                .id("2")
                .userId("11")
                .listName("testList2")
                .taskIds(List.of("3", "4"))
                .build();

        TaskList savedList2 = taskListRepository.save(testTaskList2);
        TaskList fetchedList1 = taskListRepository.findByUserIdAndListName("11","testList1");
        TaskList fetchedList2 = taskListRepository.findByUserIdAndListName("11","testList2");
        Assertions.assertThat(fetchedList1.getId()).isEqualTo("1");
        Assertions.assertThat(fetchedList2.getId()).isEqualTo("2");
    }

}

