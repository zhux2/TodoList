package unit.model.task.mock;

import com.todolist.todo.Model.Task.DB.DataBaseDriver;
import com.todolist.todo.Model.Task.Task;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockDataBaseDriver implements DataBaseDriver {
    private final Set<Task> mockSet;
    private final DataBaseDriver mockDatabase;
    private int taskID = 0;

    /**
     * due today, done 10 days ago
     */
    public Task longDoneTask;
    /**
     * due today
     */
    public Task recentDoneTask1;
    public Task todayTodoTask;
    public Task delayTodoTask;

    /**
     * due yesterday
     */
    public Task recentDoneTask2;

    public MockDataBaseDriver() {
        mockSet = new HashSet<>();

        mockDatabase = Mockito.mock(DataBaseDriver.class);

        when(mockDatabase.selectAllTodoTasks()).then(invocationOnMock -> {
            Set<Task> taskSet = new HashSet<>();
            for (Task t : mockSet) {
                if (!t.done()) {
                    taskSet.add(t);
                }
            }
            return taskSet;
        });

        when(mockDatabase.selectDoneTasks(any(LocalDate.class))).then(invocationOnMock -> {
            LocalDate date = invocationOnMock.getArgument(0); // 获取传入的日期参数
            Set<Task> doneTaskSet = new HashSet<>();

            for (Task t : mockSet) {
                if (t.done() && t.withinDate(date)) { // 过滤出符合条件的任务
                    doneTaskSet.add(t);
                }
            }
            return doneTaskSet;
        });

        when(mockDatabase.selectRecentDoneTasks(any(LocalDate.class), any(LocalDate.class))).then(invocationOnMock -> {
            LocalDate startDate = invocationOnMock.getArgument(0);
            LocalDate endDate   = invocationOnMock.getArgument(1);
            long startTime = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            long endTime = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond() - 1;

            Set<Task> taskSet = new HashSet<>();
            for (Task t : mockSet) {
                if (!t.done()) continue;
                long finishTime = t.getFinishTime().toEpochSecond(ZoneOffset.UTC);
                if (finishTime >= startTime && finishTime <= endTime) {
                    taskSet.add(t);
                }
            }
            return taskSet;
        });

        doAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(taskID++);
            mockSet.add(task);
            return null;
        }).when(mockDatabase).addTask(any(Task.class));

        doAnswer(invocationOnMock -> {
            Task task = invocationOnMock.getArgument(0);
            mockSet.remove(task);
            return null;
        }).when(mockDatabase).deleteTask(any(Task.class));

        doNothing().when(mockDatabase).updateDoneMark(any(Task.class));
    }

    @Override
    public void addTask(Task task) {
        mockDatabase.addTask(task);
    }

    @Override
    public Set<Task> selectAllTodoTasks() {
        return mockDatabase.selectAllTodoTasks();
    }

    @Override
    public Set<Task> selectDoneTasks(LocalDate date) {
        return mockDatabase.selectDoneTasks(date);
    }

    @Override
    public Set<Task> selectRecentDoneTasks(LocalDate startDate, LocalDate endDate) {
        return mockDatabase.selectRecentDoneTasks(startDate, endDate);
    }

    @Override
    public void updateDoneMark(Task task) {
        mockDatabase.updateDoneMark(task);
    }

    @Override
    public void deleteTask(Task task) {
        mockDatabase.deleteTask(task);
    }

    public void add(Task task) {
        mockSet.add(task);
    }

    public int uuid() {
        return taskID++;
    }

    public DataBaseDriver mock() {
        return mockDatabase;
    }

    public static void setupDB(MockDataBaseDriver mockDatabase) {
        mockDatabase.recentDoneTask1 = new Task(
                mockDatabase.uuid(),
                "Done Today",
                LocalDate.now().atTime(23, 59),
                LocalDate.now().atTime(12, 0),
                false, "details", true
        );
        mockDatabase.add(mockDatabase.recentDoneTask1);

        mockDatabase.longDoneTask = new Task(
                mockDatabase.uuid(),
                "Done 10 Days Ago",
                LocalDate.now().atTime(23, 59),
                LocalDate.now().minusDays(10).atTime(12, 0),
                false, "details", true
        );
        mockDatabase.add(mockDatabase.longDoneTask);

        mockDatabase.todayTodoTask = new Task(
                mockDatabase.uuid(),
                "Due Today",
                LocalDate.now().atTime(12, 0),
                LocalDateTime.now(),
                false, "details", false
        );
        mockDatabase.add(mockDatabase.todayTodoTask);

        mockDatabase.recentDoneTask2 = new Task(
                mockDatabase.uuid(),
                "Done 2 Days Ago",
                LocalDate.now().minusDays(1).atTime(23, 59),
                LocalDate.now().minusDays(2).atTime(12, 0),
                false, "details", true
        );
        mockDatabase.add(mockDatabase.recentDoneTask2);

        mockDatabase.delayTodoTask = new Task(
                mockDatabase.uuid(),
                "Delay",
                LocalDate.now().minusDays(1).atTime(12, 0),
                LocalDateTime.now(),
                false, "details", false
        );
        mockDatabase.add(mockDatabase.delayTodoTask);
    }
}
