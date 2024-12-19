package unit.model.task;

import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskPool;
import fuzz.FuzzEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unit.model.task.mock.MockDataBaseDriver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FuzzTaskPoolTest {
    private TaskPool taskPool;
    private MockDataBaseDriver mockDatabase;
    private FuzzEngine fuzzEngine;
    private TaskPoolTest.ConsistencyChecker checker;

    @BeforeEach
    void setup() {
        mockDatabase = new MockDataBaseDriver();
        MockDataBaseDriver.setupDB(mockDatabase);
        taskPool = new TaskPool(mockDatabase);
        fuzzEngine = new FuzzEngine("output.txt");
        checker = new TaskPoolTest.ConsistencyChecker();
    }

    @Test
    void testWrapper() {
        fuzzEngine.fuzz(this::fuzzTestTaskPool, 1000);
    }

    void fuzzTestTaskPool() {
        int operationNum = fuzzEngine.randInt(5, 40);

        while (operationNum > 0) {
            operationNum -= 1;
            switch (fuzzEngine.randInt(0, 6)) {
                case 0: opAddNewTask(); break;
                case 1: opDeleteTask(); break;
                case 2: opMarkDoneTask(); break;
                case 3: opGetAllTodoTask(); break;
                case 4: opGetTodoTasks(); break;
                case 5: opGetDoneTasks(); break;
                case 6: opGetRecentDoneTasks(); break;
            }
        }
    }

    void opAddNewTask() {
        Task newTask = new Task(
                fuzzEngine.randString(50),
                fuzzEngine.randLocalDateTime(),
                false,
                fuzzEngine.randString(100)
        );
        fuzzEngine.log("AddNewTask - New Task title[" + newTask.getTitle() + "] ddl[" + newTask.getDdl().toString() + "]");
        taskPool.addNewTask(newTask);
    }

    void opDeleteTask() {
        Task deleteTask = checker.getTask(fuzzEngine.randInt(0, 1000));
        fuzzEngine.log("DeleteTask - Delete Task title[" + deleteTask.getTitle() + "] ddl[" + deleteTask.getDdl().toString() + "]");
        taskPool.deleteTask(deleteTask);
    }

    void opMarkDoneTask() {
        Task modifiedTask = checker.getTask(fuzzEngine.randInt(0, 1000));
        fuzzEngine.log("MarkDoneTask - Modified Task title[" + modifiedTask.getTitle() + "] ddl[" + modifiedTask.getDdl().toString() + "]");
        modifiedTask.markDone(!modifiedTask.done());
        taskPool.markDoneTask(modifiedTask);
    }

    void opGetAllTodoTask() {
        List<Task> taskList = new ArrayList<>();
        fuzzEngine.log("GetAllTodoTasks");
        taskPool.getAllTodoTasks(taskList);
        checker.check(taskList);
    }

    void opGetTodoTasks() {
        List<Task> taskList = new ArrayList<>();
        LocalDate date = fuzzEngine.randLocalDate();
        fuzzEngine.log("GetTodoTasks - date[" + date.toString() + "]");
        taskPool.getTodoTasks(taskList, date);
        checker.check(taskList);
    }

    void opGetDoneTasks() {
        List<Task> taskList = new ArrayList<>();
        LocalDate date = fuzzEngine.randLocalDate();
        fuzzEngine.log("GetDoneTasks - date[" + date.toString() + "]");
        taskPool.getDoneTasks(taskList, date);
        checker.check(taskList);
    }

    void opGetRecentDoneTasks() {
        List<Task> taskList = new ArrayList<>();
        LocalDate start = fuzzEngine.randLocalDate();
        LocalDate end = fuzzEngine.randLocalDate();
        fuzzEngine.log("GetRecentDoneTasks - start[" + start.toString() + "] end[" + end.toString() + "]");
        taskPool.getRecentDoneTasks(taskList, start, end);
        checker.check(taskList);
    }
}
