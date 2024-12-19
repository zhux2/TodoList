package unit.model.task;

import com.todolist.todo.Model.Task.DashboardManager;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardManagerTest {
    private DashboardManager dashboardManager;
    private TaskPool mockTaskPool;
    private int taskID = 0;

    @BeforeEach
    void setUp() {
        dashboardManager = null;
        mockTaskPool = mock(TaskPool.class);
    }

    void setupTaskPool() {
        Task todoTask1 = new Task(taskID++, "Task 1", LocalDateTime.now().minusDays(1), LocalDateTime.now(), false, "Detail 1", false);
        Task todoTask2 = new Task(taskID++, "Task 2", LocalDateTime.now().plusDays(2), LocalDateTime.now(), false, "Detail 2", false);
        Task todoTask3 = new Task(taskID++, "Task 3", LocalDate.now().atTime(23, 59), LocalDateTime.now(), false, "Detail", false);
        Task doneTask1 = new Task(
                taskID++, "Task 4",
                LocalDate.now().atTime(12, 0),
                LocalDate.now().minusDays(3).atTime(12, 30),
                false, "Detail", true
        );
        Task doneTask2 = new Task(
                taskID++, "Task 5",
                LocalDate.now().minusDays(1).atTime(12, 0),
                LocalDate.now().minusDays(1).atTime(8, 0),
                false, "Detail", true
        );

        todoTask1.setTaskPool(mockTaskPool);
        todoTask2.setTaskPool(mockTaskPool);
        todoTask3.setTaskPool(mockTaskPool);
        doneTask1.setTaskPool(mockTaskPool);
        doneTask2.setTaskPool(mockTaskPool);

        doAnswer(invocationOnMock -> {
            List<Task> list = invocationOnMock.getArgument(0);
            list.add(todoTask1);
            list.add(todoTask2);
            list.add(todoTask3);
            return null;
        }).when(mockTaskPool).getAllTodoTasks(anyList());

        doAnswer(invocationOnMock -> {
            List<Task> list = invocationOnMock.getArgument(0);
            list.add(doneTask1);
            list.add(doneTask2);
            return null;
        }).when(mockTaskPool).getRecentDoneTasks(anyList(), eq(LocalDate.now().minusDays(6)), eq(LocalDate.now()));
    }

    @Test
    void testInitTaskData() {
        setupTaskPool();

        dashboardManager = new DashboardManager(mockTaskPool);

        List<Task> todoList = dashboardManager.getTodoTaskList();
        List<Task> doneList = dashboardManager.getDoneTaskList();

        assertEquals(3, todoList.size());
        assertEquals(2, doneList.size());
        assertEquals(3, dashboardManager.todoNumProperty().get());
        assertEquals(1, dashboardManager.dueTodayNumProperty().get());
    }

    @Test
    void testSimpleTaskCount() {
        Task todoTask1 = new Task(1, "Task 1", LocalDateTime.now().minusDays(1), LocalDateTime.now(), false, "Detail 1", false);
        Task todoTask2 = new Task(2, "Task 2", LocalDateTime.now().plusDays(2), LocalDateTime.now(), false, "Detail 2", false);
        Task todoTask3 = new Task(3, "Task 3", LocalDateTime.now(), LocalDateTime.now(), false, "Detail 2", false);

        doAnswer(invocationOnMock -> {
            List<Task> list = invocationOnMock.getArgument(0);
            list.add(todoTask1);
            list.add(todoTask2);
            list.add(todoTask3);
            return null;
        }).when(mockTaskPool).getAllTodoTasks(anyList());

        doNothing().when(mockTaskPool).getRecentDoneTasks(anyList(), any(LocalDate.class), any(LocalDate.class));

        dashboardManager = new DashboardManager(mockTaskPool);

        assertEquals(1, dashboardManager.dueTodayNumProperty().get());
        assertEquals(3, dashboardManager.todoNumProperty().get());
    }

    @Test
    void testIsRelevant() {
        Task recentDoneTask = new Task(
                taskID++, "Done Task",
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(1),
                true, "Detail", true);
        Task oldDoneTask = new Task(
                taskID++, "Old Task",
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(10),
                true, "Detail", true);
        Task newTask = new Task("New Task", LocalDate.now().atTime(23, 59), false, "detail");
        newTask.setId(taskID++);

        dashboardManager = new DashboardManager(mockTaskPool);

        assertTrue(dashboardManager.isRelevant(recentDoneTask));
        assertFalse(dashboardManager.isRelevant(oldDoneTask));
        assertTrue(dashboardManager.isRelevant(newTask));
    }

    @Test
    void testUpdateNewTask() {
        Task newTask = new Task(
                taskID++, "New Task",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                false, "Detail", false);

        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);
        int oldTodoNum = dashboardManager.todoNumProperty().get();
        int oldDueNum = dashboardManager.dueTodayNumProperty().get();

        if (dashboardManager.isRelevant(newTask)) {
            dashboardManager.updateNewTask(newTask);
        }

        List<Task> todoList = dashboardManager.getTodoTaskList();
        assertTrue(todoList.contains(newTask));
        assertEquals(oldTodoNum + 1, dashboardManager.todoNumProperty().get());
        assertEquals(oldDueNum, dashboardManager.dueTodayNumProperty().get());
    }

    @Test
    void testUpdateMarkDoneTask1() {
        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);

        Task modifiedTask = dashboardManager.getTodoTaskList()
                .stream()
                .filter(t -> !t.withinDate(LocalDate.now()))
                .findFirst().orElse(null);
        assert modifiedTask != null;
        modifiedTask.markDone(true);

        checkUpdateMarkDone(modifiedTask, -1, 0);

        modifiedTask = dashboardManager.getTodoTaskList()
                .stream()
                .filter(t -> t.withinDate(LocalDate.now()))
                .findFirst().orElse(null);
        assert modifiedTask != null;
        modifiedTask.markDone(true);

        checkUpdateMarkDone(modifiedTask, -1, -1);
    }

    @Test
    void testUpdateMarkDoneTask2() {
        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);

        Task modifiedTask = dashboardManager.getDoneTaskList()
                .stream()
                .filter(t -> !t.withinDate(LocalDate.now()))
                .findFirst().orElse(null);
        assert modifiedTask != null;
        modifiedTask.markDone(false);

        checkUpdateMarkDone(modifiedTask, 1, 0);

        modifiedTask = dashboardManager.getDoneTaskList()
                .stream()
                .filter(t -> t.withinDate(LocalDate.now()))
                .findFirst().orElse(null);
        assert modifiedTask != null;
        modifiedTask.markDone(false);

        checkUpdateMarkDone(modifiedTask, 1, 1);
    }

    @Test
    void testUpdateMarkDoneTask3() {
        Task newTask = new Task(
                taskID++, "New Task",
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(11),
                false, "Detail", false
        );
        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);

        checkUpdateMarkDone(newTask, 1, 1);
    }

    @Test
    void testUpdateDeletedTask1() {
        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);

        List<Task> taskList = dashboardManager.getDoneTaskList();
        assert (!taskList.isEmpty());
        Task deleteTask = taskList.get(0);

        checkUpdateDeletedTask(deleteTask, 0 ,0);
    }

    @Test
    void testUpdateDeletedTask2() {
        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);

        Task deleteTask = dashboardManager.getTodoTaskList()
                .stream()
                .filter(t -> !t.withinDate(LocalDate.now()))
                .findFirst().orElse(null);
        assert (deleteTask != null);
        checkUpdateDeletedTask(deleteTask, -1, 0);
    }

    @Test
    void testUpdateDeletedTask3() {
        setupTaskPool();
        dashboardManager = new DashboardManager(mockTaskPool);

        Task deleteTask = dashboardManager.getTodoTaskList()
                .stream()
                .filter(t -> t.withinDate(LocalDate.now()))
                .findFirst().orElse(null);
        assert (deleteTask != null);
        checkUpdateDeletedTask(deleteTask, -1, -1);
    }

    void checkUpdateMarkDone(Task task, int expectTodoDelta, int expectDueDelta) {
        int expectTodoNum = dashboardManager.todoNumProperty().get() + expectTodoDelta;
        int expectDueNum = dashboardManager.dueTodayNumProperty().get() + expectDueDelta;

        if (dashboardManager.isRelevant(task)) {
            dashboardManager.updateMarkDoneTask(task);
        }

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

        if (dashboardManager.isRelevant(deleteTask)) {
            dashboardManager.updateDeletedTask(deleteTask);
        }

        List<Task> todoList = dashboardManager.getTodoTaskList();
        List<Task> doneList = dashboardManager.getDoneTaskList();

        assertFalse(todoList.contains(deleteTask));
        assertFalse(doneList.contains(deleteTask));
        assertEquals(expectTodoNum, dashboardManager.todoNumProperty().get());
        assertEquals(expectDueNum, dashboardManager.dueTodayNumProperty().get());
    }
}
