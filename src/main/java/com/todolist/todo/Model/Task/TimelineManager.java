package com.todolist.todo.Model.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TimelineManager implements TaskUpdateObserver {

    private final ObservableList<Task> taskList;

    private final TaskPool taskPool;

    public TimelineManager(TaskPool taskPool) {
        this.taskPool = taskPool;
        taskPool.registerObserver(this);
        taskList = FXCollections.observableArrayList();

        initTaskData();
    }

    void initTaskData() {
        taskPool.getAllTodoTasks(taskList);
    }

    public ObservableList<Task> getTaskList() {
        return new SortedList<>(taskList, Comparator
                .comparing(Task::getDdl, Comparator.reverseOrder())
                .thenComparing(Task::getTitle));
    }

    @Override
    public boolean isRelevant(Task task) {
        return true;
    }

    @Override
    public void updateNewTask(Task task) {
        System.out.println("timeline manager: receive");
        taskList.add(task);
    }

    @Override
    public void updateMarkDoneTask(Task task) {
        if (task.done()) {
            taskList.remove(task);
        }
        else {
            taskList.add(task);
        }
    }

    @Override
    public void updateDeletedTask(Task task) {
        taskList.remove(task);
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
