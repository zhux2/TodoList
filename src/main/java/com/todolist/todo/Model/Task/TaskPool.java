package com.todolist.todo.Model.Task;

import com.todolist.todo.Model.Task.DB.DataBaseDriver;
import com.todolist.todo.Model.Task.DB.SQLiteDriver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TaskPool {
    private final Set<Task> todoTaskSet;
    private final Map<LocalDate, Boolean> doneTaskBitmap;
    private final Map<LocalDate, Map<Task, Task>> doneTaskMap;
    private final DataBaseDriver database;

    private final Set<TaskUpdateObserver> updateObservers;

    public TaskPool(DataBaseDriver dataBaseDriver) {
        todoTaskSet = new HashSet<>();
        doneTaskMap = new HashMap<>();
        doneTaskBitmap = new HashMap<>();
        database = dataBaseDriver;
        todoTaskSet.addAll(database.selectAllTodoTasks());
        for (Task t: todoTaskSet) {
            t.setTaskPool(this);
        }

        updateObservers = new HashSet<>();

        startApproachChecker();
    }

    public void registerObserver(TaskUpdateObserver observer) {
        updateObservers.add(observer);
    }

    public void getAllTodoTasks(List<Task> tasks) {
        tasks.addAll(todoTaskSet);
    }

    public void getTodoTasks(List<Task> tasks, LocalDate date) {
        for (Task t: todoTaskSet) {
            if (t.withinDate(date)) {
                tasks.add(t);
            }
        }
    }

    public void getDoneTasks(List<Task> tasks, LocalDate date) {
        if (doneTaskBitmap.get(date) == null) {
            Set<Task> doneTasks = database.selectDoneTasks(date);
            Map<Task, Task> newDoneMap = new HashMap<>();
            for (Task t: doneTasks) {
                t.setTaskPool(this);
                newDoneMap.put(t, t);
            }
            doneTaskMap.put(date, newDoneMap);
            doneTaskBitmap.put(date, true);
        }
        else if (!doneTaskBitmap.get(date)) {
            Set<Task> doneTasks = database.selectDoneTasks(date);
            for (Task t: doneTasks) {
                if (!doneTaskMap.get(date).containsKey(t)) {
                    t.setTaskPool(this);
                    doneTaskMap.get(date).put(t, t);
                }
            }
            doneTaskBitmap.put(date, true);
        }

        tasks.addAll(doneTaskMap.get(date).keySet());
    }

    public void getRecentDoneTasks(List<Task> tasks, LocalDate startDate, LocalDate endDate) {
        Set<Task> recentDoneTasks = database.selectRecentDoneTasks(startDate, endDate);
        for (Task t : recentDoneTasks) {
            LocalDate ddlDate = t.getDdlDate();
            if (doneTaskBitmap.get(ddlDate) == null) {
                doneTaskMap.put(ddlDate, new HashMap<>());
                doneTaskBitmap.put(ddlDate, false);
            }

            if (!doneTaskMap.get(ddlDate).containsKey(t)) {
                t.setTaskPool(this);
                doneTaskMap.get(ddlDate).put(t, t);
            }
            tasks.add(doneTaskMap.get(ddlDate).get(t));
        }
    }

    public void addNewTask(Task task) {
        database.addTask(task);
        task.setTaskPool(this);
        todoTaskSet.add(task);

        for (TaskUpdateObserver observer : updateObservers) {
            if (observer.isRelevant(task))
                observer.updateNewTask(task);
        }
    }

    public void deleteTask(Task task) {
        assert task != null;

        if (task.done()) {
            doneTaskMap.get(task.getDdlDate()).remove(task);
        }
        else {
            todoTaskSet.remove(task);
        }

        for (TaskUpdateObserver observer: updateObservers) {
            if (observer.isRelevant(task))
                observer.updateDeletedTask(task);
        }

        database.deleteTask(task);
    }

    public void markDoneTask(Task task) {
        database.updateDoneMark(task);

        if (task.done()) {
            todoTaskSet.remove(task);
            // may have problem
            doneTaskMap.get(task.getDdlDate()).put(task, task);
        }
        else {
            doneTaskMap.get(task.getDdlDate()).remove(task);
            todoTaskSet.add(task);
        }

        for (TaskUpdateObserver observer: updateObservers) {
            if (observer.isRelevant(task))
                observer.updateMarkDoneTask(task);
        }
    }

    public void changeDdlTask(Task task) {
        // TODO - implement me
    }

    public void modifiedTask(Task task) {
        // TODO - implement me
    }

    private void startApproachChecker() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkApproachingTasks();
            }
        }, 0, 60000);
    }

    private void checkApproachingTasks() {
        LocalDateTime now = LocalDateTime.now();
        for (Task task : todoTaskSet) {
            task.updateApproachState(now);
        }
    }
}
