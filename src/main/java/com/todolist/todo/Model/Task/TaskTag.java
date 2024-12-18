package com.todolist.todo.Model.Task;

public enum TaskTag {
    WORK("Work"),
    STUDY("Study"),
    PERSONAL("Play"),
    LIFE("Life"),
    NONE("No-Tag");

    private final String displayName;

    TaskTag(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
