package unit.model.task;

import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskPool;
import com.todolist.todo.Model.Task.TaskUpdateObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unit.model.task.mock.MockDataBaseDriver;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskPoolTest {
    private TaskPool taskPool;
    private MockDataBaseDriver mockDatabase;

    @BeforeEach
    public void setup() {
        mockDatabase = new MockDataBaseDriver();
    }

    void setupEmptyDB() {
        taskPool = new TaskPool(mockDatabase);
    }

    void setupDB() {
        MockDataBaseDriver.setupDB(mockDatabase);
        taskPool = new TaskPool(mockDatabase);
    }

    void setupOnlyDoneDB() {
        mockDatabase.add(new Task(
                mockDatabase.uuid(),
                "task a",
                LocalDate.now().atTime(23, 59),
                LocalDateTime.now(),
                false, "details", true
        ));

        mockDatabase.add(new Task(
                mockDatabase.uuid(),
                "task c",
                LocalDate.now().minusDays(1).atTime(23, 59),
                LocalDateTime.now(),
                false, "details", true
        ));

        taskPool = new TaskPool(mockDatabase);
    }

    void setupOnlyTodoDB() {
        mockDatabase.add(new Task(
                mockDatabase.uuid(),
                "task b",
                LocalDate.now().atTime(12, 0),
                LocalDateTime.now(),
                false, "details", false
        ));

        mockDatabase.add(new Task(
                mockDatabase.uuid(),
                "task d",
                LocalDate.now().minusDays(1).atTime(12, 0),
                LocalDateTime.now(),
                false, "details", false
        ));

        taskPool = new TaskPool(mockDatabase);
    }

    @Test
    void testSimpleAddNewTask() {
        setupEmptyDB();
        Task task = new Task(
                "Lab 5",
                LocalDate.now().atTime(23, 59),
                false,
                "software engineering lab 5");

        // Call addNewTask
        taskPool.addNewTask(task);

        // Verify the task is added to the database
        verify(mockDatabase.mock(), times(1)).addTask(task);

        // Verify the task is in the todoTaskSet
        List<Task> tasks = new ArrayList<>();
        taskPool.getAllTodoTasks(tasks);
        assertTrue(tasks.contains(task));
    }

    @Test
    void testSimpleDeleteTask() {
        setupEmptyDB();

        Task task = new Task(
                "Lab 5",
                LocalDate.now().atTime(23, 59),
                false,
                "software engineering lab 5");
        taskPool.addNewTask(task);

        taskPool.deleteTask(task);

        verify(mockDatabase.mock(), times(1)).deleteTask(task);

        List<Task> tasks = new ArrayList<>();
        taskPool.getAllTodoTasks(tasks);
        assertFalse(tasks.contains(task));
    }

    @Test
    void testMarkDoneTask1() {
        setupOnlyDoneDB();

        List<Task> doneTasks = new ArrayList<>();
        taskPool.getDoneTasks(doneTasks, LocalDate.now());

        assert (!doneTasks.isEmpty());
        Task t = doneTasks.get(0);
        t.markDone(false);

        verify(mockDatabase.mock(), times(1)).updateDoneMark(t);

        // Verify the task is moved from doneTaskMap to todoTaskSet
        List<Task> todoTasks = new ArrayList<>();
        taskPool.getAllTodoTasks(todoTasks);
        assertTrue(todoTasks.contains(t));

        doneTasks = new ArrayList<>();
        taskPool.getDoneTasks(doneTasks, t.getDdlDate());
        assertFalse(doneTasks.contains(t));
    }

    @Test
    void testMarkDoneTask2() {
        setupOnlyTodoDB();

        List<Task> todoTasks = new ArrayList<>();
        taskPool.getAllTodoTasks(todoTasks);

        assert (!todoTasks.isEmpty());
        Task t = todoTasks.get(0);
        t.markDone(true);

        verify(mockDatabase.mock(), times(1)).updateDoneMark(t);

        // Verify the task is moved from todoTaskSet to doneTaskMap
        todoTasks = new ArrayList<>();
        taskPool.getAllTodoTasks(todoTasks);
        assertFalse(todoTasks.contains(t));

        todoTasks = new ArrayList<>();
        taskPool.getDoneTasks(todoTasks, t.getDdlDate());
        assertTrue(todoTasks.contains(t));
    }

    @Test
    void testGetTodoTasks() {
        setupDB();

        List<Task> todoTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        taskPool.getTodoTasks(todoTasks, today);
        for (Task t : todoTasks) {
            assertTrue(!t.done() && t.withinDate(today));
        }

        todoTasks.clear();
        taskPool.getAllTodoTasks(todoTasks);
        for (Task t : todoTasks) {
            assertFalse(t.done());
        }
    }

    @Test
    void testGetDoneTasks() {
        setupDB();

        List<Task> taskList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        taskPool.getDoneTasks(taskList, today);
        for (Task t : taskList) {
            assertTrue(t.done() && t.withinDate(today));
        }
    }

    @Test
    void testGetRecentDoneTasks() {
        setupDB();

        List<Task> taskList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        taskPool.getRecentDoneTasks(taskList, yesterday, today);

        LocalDateTime todayTime = today.plusDays(1).atStartOfDay().minusSeconds(1);
        LocalDateTime yesterdayTime = yesterday.atStartOfDay();

        for (Task t : taskList) {
            assertTrue(t.getFinishTime().isAfter(yesterdayTime) && t.getFinishTime().isBefore(todayTime));
        }
    }

    @Test
    void testMultiGetTasks1() {
        setupDB();

        ConsistencyChecker checker = new ConsistencyChecker();
        List<Task> taskList = new ArrayList<>();
        LocalDate sevenDays = LocalDate.now().minusDays(6);
        LocalDate today = LocalDate.now();
        taskPool.getRecentDoneTasks(taskList, sevenDays, today);
        assertFalse (taskList.isEmpty());
        checker.check(taskList);

        taskList.clear();
        taskPool.getDoneTasks(taskList, today);
        checker.check(taskList);

        taskList.clear();
        taskPool.getDoneTasks(taskList, today.minusDays(1));
        checker.check(taskList);
    }

    @Test
    void testMultiGetTask2() {
        setupDB();

        ConsistencyChecker checker = new ConsistencyChecker();
        List<Task> taskList = new ArrayList<>();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        taskPool.getDoneTasks(taskList, LocalDate.now());
        checker.check(taskList);

        taskList.clear();
        taskPool.getRecentDoneTasks(taskList, yesterday, yesterday);
        checker.check(taskList);

        taskList.clear();
        taskPool.getDoneTasks(taskList, yesterday);
        checker.check(taskList);
    }

    @Test
    void testUpdateApproach() throws InterruptedException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        setupEmptyDB();

        Task approachTask = new Task("approach", LocalDateTime.now().plusSeconds(60 * 60 + 5), false, "detail");
        Task overdueTask = new Task("overdue", LocalDateTime.now().minusMinutes(10), false, "detail");
        Task normalTask = new Task("normal", LocalDateTime.now().plusDays(2), false, "detail");

        taskPool.addNewTask(approachTask);
        taskPool.addNewTask(overdueTask);
        taskPool.addNewTask(normalTask);

        assertFalse(approachTask.isApproach());
        assertTrue(overdueTask.isApproach());
        assertFalse(normalTask.isApproach());

        Thread.sleep(10000);

        java.lang.reflect.Method method = TaskPool.class.getDeclaredMethod("checkApproachingTasks");
        method.setAccessible(true);
        method.invoke(taskPool);

        assertTrue(approachTask.isApproach());
        assertTrue(overdueTask.isApproach());
        assertFalse(normalTask.isApproach());
        System.out.println("finish test");
    }

    @Test
    void testNotifyObserverOnNewTask() {
        setupEmptyDB();

        TaskUpdateObserver mockObserver = mock(TaskUpdateObserver.class);
        taskPool.registerObserver(mockObserver);

        Task newTask1 = mock(Task.class);
        when(mockObserver.isRelevant(newTask1)).thenReturn(true);
        taskPool.addNewTask(newTask1);
        verify(mockObserver, times(1)).updateNewTask(newTask1);

        Task newTask2 = mock(Task.class);
        when(mockObserver.isRelevant(newTask2)).thenReturn(false);
        taskPool.addNewTask(newTask2);
        verify(mockObserver, times(0)).updateNewTask(newTask2);
    }

    @Test
    void testNotifyObserverOnDeletedTask() {
        setupEmptyDB();
        TaskUpdateObserver mockObserver = mock(TaskUpdateObserver.class);

        Task taskToDelete = mock(Task.class);
        taskPool.addNewTask(taskToDelete);
        taskPool.registerObserver(mockObserver);
        when(mockObserver.isRelevant(taskToDelete)).thenReturn(true);
        taskPool.deleteTask(taskToDelete);
        verify(mockObserver, times(1)).updateDeletedTask(taskToDelete);
    }

    public static class ConsistencyChecker {
        Map<Task, Task> taskMap;

        ConsistencyChecker() {
            taskMap = new HashMap<>();
        }

        public void check(List<Task> taskList) {
            for (Task t : taskList) {
                if (taskMap.containsKey(t)) {
                    assertTrue(taskMap.get(t) == t);
                }
                else {
                    taskMap.put(t, t);
                }
            }
        }

        public Task getTask(int index) {
            if (taskMap.isEmpty()) return null;

            index = index % taskMap.size();
            int i = 0;
            for (Task t : taskMap.keySet()) {
                if (i == index) {
                    return t;
                }
                i += 1;
            }
            return null;
        }
    }
}