package com.todolist.todo.Model.Task;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private StringProperty title;
    private ObjectProperty<LocalDateTime> ddl;
    private ObjectProperty<LocalDateTime> finishTime;
    private BooleanProperty isImportant;
    private StringProperty details;
    private BooleanProperty isDone;

    private BooleanProperty isApproach;

    private TaskPool taskPool;

    public Task(int id, String title, LocalDateTime ddl, LocalDateTime finishTime, boolean isImportant, String details, boolean isDone) {
        init(title, ddl, isImportant, details);
        this.id = id;
        this.isDone.set(isDone);
        this.finishTime.set(finishTime);
        updateApproachState(LocalDateTime.now());
    }

    public Task(String title, LocalDateTime ddl, boolean isImportant, String details) {
        init(title, ddl, isImportant, details);
        updateApproachState(LocalDateTime.now());
    }

    private void init(String title, LocalDateTime ddl, boolean isImportant, String details) {
        this.title = new SimpleStringProperty(title);
        this.ddl = new SimpleObjectProperty<>(ddl);
        this.isImportant = new SimpleBooleanProperty(isImportant);
        this.details = new SimpleStringProperty(details);
        this.isDone = new SimpleBooleanProperty(false);
        this.isApproach = new SimpleBooleanProperty(false);
        this.finishTime = new SimpleObjectProperty<>();
    }
    
    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty detailsProperty() {
        return details;
    }

    public ObservableValue<LocalDateTime> ddlProperty() {
        return ddl;
    }

    public BooleanProperty importantProperty() {
        return isImportant;
    }

    public BooleanProperty doneProperty() {
        return isDone;
    }

    public boolean withinDate(LocalDate date) {
        return ddl.getValue().toLocalDate().equals(date);
    }

    public boolean done() {
        return isDone.get();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDdl() {
        return ddl.getValue();
    }

    public LocalDateTime getFinishTime() {
        return finishTime.getValue();
    }

    public String getTitle() {
        return title.get();
    }

    public LocalDate getDdlDate() {
        return ddl.getValue().toLocalDate();
    }

    public void setTaskPool(TaskPool taskPool) {
        this.taskPool = taskPool;
    }

    public void markDone(boolean done) {
        if (done == isDone.get()) return;

        isDone.set(done);
        if (done) {
            finishTime.set(LocalDateTime.now());
        }
        updateApproachState(LocalDateTime.now());

        taskPool.markDoneTask(this);
    }

    public boolean isApproach() {
        return isApproach.get();
    }

    public BooleanProperty approachProperty() {
        return isApproach;
    }

    public void updateApproachState(LocalDateTime curTime) {
        if (isDone.get()) {
            isApproach.set(false);
        }
        else {
            isApproach.set(curTime.isAfter(ddl.getValue().minusHours(1)));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Task) o).id;
    }
}
