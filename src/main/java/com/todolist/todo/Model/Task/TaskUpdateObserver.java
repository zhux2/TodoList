package com.todolist.todo.Model.Task;

import java.time.LocalDateTime;

public interface TaskUpdateObserver {
    /**
     * Check whether the observer is interested in the task before every update.
     *
     * @return true if observer is interested in the updating task
     */
    public boolean isRelevant(Task task);

    /**
     * @param task a newly created task
     */
    public void updateNewTask(Task task);

    /**
     * @param task a task whose isDone field changes
     */
    public void updateMarkDoneTask(Task task);

    /**
     * @param task a deleted task
     */
    public void updateDeletedTask(Task task);

    /**
     * @param task a task with one or more fields modified
     *             the modified fields do not contain ddl or isDone
     */
    public void updateModifiedTask(Task task);

    /**
     * @param task a task whose ddl field changes
     * @param oldDdl the ddl before changing
     */
    public void updateDdlChangeTask(Task task, LocalDateTime oldDdl);
}