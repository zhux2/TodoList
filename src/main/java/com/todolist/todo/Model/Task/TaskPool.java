package com.todolist.todo.Model.Task;

import com.todolist.todo.Model.Task.DB.DataBaseDriver;
import com.todolist.todo.Model.Task.DB.SQLiteDriver;

import java.time.LocalDate;
import java.util.*;

public class TaskPool {
    private final Set<Task> todoTaskSet;
    private final Map<LocalDate, Set<Task>> doneTaskMap;
    private final DataBaseDriver database;

    private final Set<TaskUpdateObserver> updateObservers;

    public TaskPool() {
        todoTaskSet = new HashSet<>();
        doneTaskMap = new HashMap<>();
        database = new SQLiteDriver();
        todoTaskSet.addAll(database.selectAllTodoTasks());
        for (Task t: todoTaskSet) {
            t.setTaskPool(this);
        }

        updateObservers = new HashSet<>();
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
        if (doneTaskMap.get(date) == null) {
            Set<Task> doneTasks = database.selectDoneTasks(date);
            for (Task t: doneTasks) {
                t.setTaskPool(this);
            }
            doneTaskMap.put(date, doneTasks);
        }

        tasks.addAll(doneTaskMap.get(date));
    }

    public void addNewTask(Task task) {
        task.setTaskPool(this);
        todoTaskSet.add(task);
        database.addTask(task);

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
            doneTaskMap.get(task.getDdlDate()).add(task);
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
}
