package integration.model.task;

import com.todolist.todo.Model.Task.DashboardManager;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unit.model.task.mock.MockDataBaseDriver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardTaskTest {
    private MockDataBaseDriver mockDatabase;
    private TaskPool taskPool;
    private DashboardManager dashboardManager;
    static private final LocalDate sevenDays = LocalDate.now().minusDays(6);
    static private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        mockDatabase = new MockDataBaseDriver();
    }

    void setupEmptyDB() {
        taskPool = new TaskPool(mockDatabase);
        dashboardManager = new DashboardManager(taskPool);
    }

    void setupDB() {
        MockDataBaseDriver.setupDB(mockDatabase);
        taskPool = new TaskPool(mockDatabase);
        dashboardManager = new DashboardManager(taskPool);
    }

    @Test
    void testInitLoad() {
        setupDB();

        List<Task> doneList = dashboardManager.getDoneTaskList();
        for (Task t : doneList) {
            assertTrue(t.done());
            assertTrue(t.getFinishTime().isAfter(sevenDays.atStartOfDay()));
        }

        List<Task> todoList = dashboardManager.getTodoTaskList();
        int dueTodayCount = 0;
        for (Task t : todoList) {
            assertFalse(t.done());
            if (t.withinDate(today)) {
                dueTodayCount += 1;
            }
        }

        assertEquals(dueTodayCount, dashboardManager.dueTodayNumProperty().get());
        assertEquals(todoList.size(), dashboardManager.todoNumProperty().get());
    }

    @Test
    void testAddAndDeleteTask() {
        setupEmptyDB();
        Task task1 = new Task("Task 1", LocalDateTime.now().plusDays(1), false, "Detail 1");
        Task task2 = new Task("Task 2", LocalDateTime.now().plusDays(2), false, "Detail 2");

        taskPool.addNewTask(task1);
        taskPool.addNewTask(task2);

        // 验证任务被添加到 TaskPool 和 DashboardManager
        List<Task> todoList = dashboardManager.getTodoTaskList();
        assertEquals(2, todoList.size());
        assertTrue(todoList.contains(task1));
        assertTrue(todoList.contains(task2));

        // Step 2: 删除任务
        taskPool.deleteTask(task1);

        // 验证任务已被删除
        todoList = dashboardManager.getTodoTaskList();
        assertEquals(1, todoList.size());
        assertFalse(todoList.contains(task1));
        assertTrue(todoList.contains(task2));
    }

    @Test
    void testSimpleUpdateTask() {
        setupEmptyDB();
        Task task1 = new Task("Task 1", LocalDateTime.now().plusDays(1), false, "Detail 1");
        Task task2 = new Task("Task 2", LocalDateTime.now().plusDays(2), false, "Detail 2");

        taskPool.addNewTask(task1);
        taskPool.addNewTask(task2);

        assertTrue(dashboardManager.getTodoTaskList().contains(task1));
        assertEquals(2, dashboardManager.todoNumProperty().get());

        task1.markDone(true);

        // 验证任务已移动到完成列表
        List<Task> doneList = dashboardManager.getDoneTaskList();
        List<Task> todoList = dashboardManager.getTodoTaskList();

        assertTrue(doneList.contains(task1));
        assertFalse(todoList.contains(task1));

        // 验证统计信息
        assertEquals(1, dashboardManager.todoNumProperty().get());
    }

    @Test
    void testUpdateTask() {
        setupDB();

        Task taskToday = new Task("Due Today", LocalDateTime.now(), false, "Detail Today");
        Task taskFuture = new Task("Future Task", LocalDateTime.now().plusDays(3), false, "Detail Future");

        checkNewTask(taskToday, 1);
        checkNewTask(taskFuture, 0);

        Task modifiedTask = mockDatabase.delayTodoTask;
        checkUpdateMarkDone(modifiedTask, true, -1, 0);

        taskPool.getDoneTasks(new ArrayList<>(), LocalDate.now());
        modifiedTask = mockDatabase.longDoneTask;
        checkUpdateMarkDone(modifiedTask, false, 1, 1);

        checkUpdateMarkDone(modifiedTask, true, -1 ,-1);

        checkUpdateDeletedTask(taskToday, -1, -1);
        checkUpdateDeletedTask(mockDatabase.recentDoneTask1, 0, 0);
    }

    void checkNewTask(Task newTask, int expectDurDelta) {
        int expectTodoNum = dashboardManager.todoNumProperty().get() + 1;
        int expectDueNum = dashboardManager.dueTodayNumProperty().get() + expectDurDelta;

        taskPool.addNewTask(newTask);

        List<Task> todoList = dashboardManager.getTodoTaskList();
        List<Task> doneList = dashboardManager.getDoneTaskList();

        assertTrue(todoList.contains(newTask));
        assertFalse(doneList.contains(newTask));
        assertEquals(expectTodoNum, dashboardManager.todoNumProperty().get());
        assertEquals(expectDueNum, dashboardManager.dueTodayNumProperty().get());
    }

    void checkUpdateMarkDone(Task task, boolean newDoneMark, int expectTodoDelta, int expectDueDelta) {
        int expectTodoNum = dashboardManager.todoNumProperty().get() + expectTodoDelta;
        int expectDueNum = dashboardManager.dueTodayNumProperty().get() + expectDueDelta;

        task.markDone(newDoneMark);

        List<Task> todoList = dashboardManager.getTodoTaskList();
        List<Task> doneList = dashboardManager.getDoneTaskList();

        assertEquals(!task.done(), todoList.contains(task));
        assertEquals(task.done(), doneList.contains(task));
        assertEquals(expectTodoNum, dashboardManager.todoNumProperty().get());
        assertEquals(expectDueNum, dashboardManager.dueTodayNumProperty().get());
    }

    void checkUpdateDeletedTask(Task deleteTask, int expectTodoDelta, int expectDueDelta) {
        int expectTodoNum = dashboardManager.todoNumProperty().get() + expectTodoDelta;
        int expectDueNum = dashboardManager.dueTodayNumProperty().get() + expectDueDelta;

        taskPool.deleteTask(deleteTask);

        List<Task> todoList = dashboardManager.getTodoTaskList();
        List<Task> doneList = dashboardManager.getDoneTaskList();

        assertFalse(todoList.contains(deleteTask));
        assertFalse(doneList.contains(deleteTask));
        assertEquals(expectTodoNum, dashboardManager.todoNumProperty().get());
        assertEquals(expectDueNum, dashboardManager.dueTodayNumProperty().get());
    }

}