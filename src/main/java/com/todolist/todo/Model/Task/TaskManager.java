package com.todolist.todo.Model.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskManager implements TaskUpdateObserver{
    private LocalDate date;
    private TaskTag tag;
    private final ObservableList<Task> doneShowList;
    private final ObservableList<Task> todoShowList;
    private final List<Task> todoList;
    private final List<Task> doneList;


    private final TaskPool taskPool;

    public TaskManager(TaskPool taskPool) {
        this.taskPool = taskPool;
        taskPool.registerObserver(this);
        this.doneShowList = FXCollections.observableArrayList();
        this.todoShowList = FXCollections.observableArrayList();

        this.todoList = new ArrayList<>();
        this.doneList = new ArrayList<>();
        this.tag = TaskTag.NONE;
    }

    public void setDate(LocalDate date) {
        this.date = date;

        todoShowList.clear();
        taskPool.getTodoTasks(todoList, date);

        doneShowList.clear();
        taskPool.getDoneTasks(doneList, date);
        updateShowList();
    }

    public void setTag(TaskTag tag) {
        if (this.tag != tag) {
            this.tag = tag;
            updateShowList();
        }
    }

    private void updateShowList() {
        if (tag == TaskTag.NONE) {
            todoShowList.clear();
            todoShowList.addAll(todoList);

            doneShowList.clear();
            doneShowList.addAll(doneList);
            return;
        }

        todoShowList.clear();
        for (Task t : todoList) {
            if (t.getTag() == tag) {
                todoShowList.add(t);
            }
        }

        doneShowList.clear();
        for (Task t : doneList) {
            if (t.getTag() == tag) {
                doneShowList.add(t);
            }
        }
    }

    public ObservableList<Task> getTodoShowList() {
        return new SortedList<>(todoShowList, Comparator.comparing(Task::getDdl).thenComparing(Task::getTitle));
    }

    public ObservableList<Task> getDoneShowList() {
        return new SortedList<>(doneShowList, Comparator.comparing(Task::getDdl).thenComparing(Task::getTitle));
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
        todoList.add(task);
        if (tag == TaskTag.NONE || task.getTag() == tag) {
            todoShowList.add(task);
        }
    }

    @Override
    public void updateMarkDoneTask(Task task) {
        if (task.done()) {
            todoList.remove(task);
            doneList.add(task);
            todoShowList.remove(task);
            doneShowList.add(task);
        }
        else {
            todoList.add(task);
            doneList.remove(task);
            doneShowList.remove(task);
            todoShowList.add(task);
        }
    }

    @Override
    public void updateDeletedTask(Task task) {
        if (task.done()) {
            doneList.remove(task);
            doneShowList.remove(task);
        }
        else {
            todoList.remove(task);
            todoShowList.remove(task);
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
