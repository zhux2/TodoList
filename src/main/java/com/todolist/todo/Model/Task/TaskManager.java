package com.todolist.todo.Model.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskManager implements TaskUpdateObserver{
    private LocalDate date;
    private final ObservableList<Task> doneTaskList;
    private final ObservableList<Task> todoTaskList;

    private final TaskPool taskPool;

    public TaskManager(TaskPool taskPool) {
        this.taskPool = taskPool;
        taskPool.registerObserver(this);
        this.doneTaskList = FXCollections.observableArrayList();
        this.todoTaskList = FXCollections.observableArrayList();
    }

    public void setDate(LocalDate date) {
        this.date = date;

        todoTaskList.clear();
        taskPool.getTodoTasks(todoTaskList, date);

        doneTaskList.clear();
        taskPool.getDoneTasks(doneTaskList, date);
    }

    public ObservableList<Task> getTodoTaskList() {
        return new SortedList<>(todoTaskList, Comparator.comparing(Task::getDdl).thenComparing(Task::getTitle));
    }

    public ObservableList<Task> getDoneTaskList() {
        return new SortedList<>(doneTaskList, Comparator.comparing(Task::getDdl).thenComparing(Task::getTitle));
    }

//    public void addNewTask(Task task) {
//        taskPool.addNewTask(task);
//    }

    @Override
    public boolean isRelevant(Task task) {
        return task.withinDate(date);
    }

    @Override
    public void updateNewTask(Task task) {
        System.out.println("today manager: receive");
        todoTaskList.add(task);
    }

    @Override
    public void updateMarkDoneTask(Task task) {
        if (task.done()) {
            todoTaskList.remove(task);
            doneTaskList.add(task);
            System.out.println(task.titleProperty().get() + ": move to doneTaskList");
        }
        else {
            doneTaskList.remove(task);
            todoTaskList.add(task);
            System.out.println(task.titleProperty().get() + ": move to todoTaskList");
        }
    }

    @Override
    public void updateDeletedTask(Task task) {
        if (task.done()) {
            doneTaskList.remove(task);
        }
        else {
            todoTaskList.remove(task);
        }
    }

    @Override
    public void updateModifiedTask(Task task) {
        // TODO - implement me
    }

    @Override
    public void updateDdlChangeTask(Task task, LocalDateTime oldDdl) {
        // TODO - implement me
    }

}
