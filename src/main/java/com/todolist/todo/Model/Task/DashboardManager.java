package com.todolist.todo.Model.Task;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

public class DashboardManager implements TaskUpdateObserver{
    static LocalDate today = LocalDate.now();
    static LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);

    private final ObservableList<Task> doneTaskList;
    private final ObservableList<Task> todoTaskList;

    private final IntegerProperty todoNum;

    private final IntegerProperty dueTodayNum;

    private final TaskPool taskPool;

    public DashboardManager(TaskPool taskPool) {
        this.taskPool = taskPool;
        taskPool.registerObserver(this);
        this.doneTaskList = FXCollections.observableArrayList();
        this.todoTaskList = FXCollections.observableArrayList();
        dueTodayNum = new SimpleIntegerProperty();
        todoNum = new SimpleIntegerProperty();

        initTaskData();
    }

    private void initTaskData() {
        taskPool.getAllTodoTasks(todoTaskList);
        taskPool.getRecentDoneTasks(doneTaskList, sevenDaysAgo, today);

        todoNum.set(todoTaskList.size());

        int dueTodayCount = 0;
        for (Task t : todoTaskList) {
            if (t.withinDate(today)) {
                dueTodayCount += 1;
            }
        }
        dueTodayNum.set(dueTodayCount);
    }

    public ObservableList<Task> getTodoTaskList() {
        return new SortedList<>(todoTaskList, Comparator.comparing(Task::getDdl).thenComparing(Task::getTitle));
    }

    public ObservableList<Task> getDoneTaskList() {
        return new SortedList<>(doneTaskList, Comparator.comparing(Task::getDdl).thenComparing(Task::getTitle));
    }

    public IntegerProperty todoNumProperty() {
        return todoNum;
    }

    public IntegerProperty dueTodayNumProperty() {
        return dueTodayNum;
    }

    @Override
    public boolean isRelevant(Task task) {
        return !task.done() ||
                task.getFinishTime().toLocalDate().isAfter(sevenDaysAgo);
    }

    @Override
    public void updateNewTask(Task task) {
        System.out.println("dashboard manager: receive");
        todoTaskList.add(task);
        updateNumCount(1, task);
    }

    @Override
    public void updateMarkDoneTask(Task task) {
        if (task.done()) {
            todoTaskList.remove(task);
            doneTaskList.add(task);
            updateNumCount(-1, task);
        }
        else {
            todoTaskList.add(task);
            doneTaskList.remove(task);
            updateNumCount(1, task);
        }
    }

    @Override
    public void updateDeletedTask(Task task) {
        if (task.done()) {
            doneTaskList.remove(task);
        }
        else {
            updateNumCount(-1, task);
            todoTaskList.remove(task);
        }
    }

    private void updateNumCount(int i, Task task) {
        todoNum.set(todoNum.get() + i);
        if (task.withinDate(today)) {
            dueTodayNum.set(dueTodayNum.get() + i);
        }
    }

    @Override
    public void updateModifiedTask(Task task) {
        // TODO
    }

    @Override
    public void updateDdlChangeTask(Task task, LocalDateTime oldDdl) {
        // TODO
    }
}
