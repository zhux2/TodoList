package com.todolist.todo.Model.Task;

import com.todolist.todo.Model.AppModel;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private final StringProperty title;
    private final ObservableValue<LocalDateTime> ddl;
    private final BooleanProperty isImportant;
    private final StringProperty details;
    private final BooleanProperty isDone;

    private TaskPool taskPool;

    public Task(int id, String title, LocalDateTime ddl, boolean isImportant, String details, boolean isDone) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.ddl = new SimpleObjectProperty<>(ddl);
        this.isImportant = new SimpleBooleanProperty(isImportant);
        this.details = new SimpleStringProperty(details);
        this.isDone = new SimpleBooleanProperty(isDone);
    }

    public Task(String title, LocalDateTime ddl, boolean isImportant, String details) {
        this.title = new SimpleStringProperty(title);
        this.ddl = new SimpleObjectProperty<>(ddl);
        this.isImportant = new SimpleBooleanProperty(isImportant);
        this.details = new SimpleStringProperty(details);
        this.isDone = new SimpleBooleanProperty(false);
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
        taskPool.markDoneTask(this);
    }
}
