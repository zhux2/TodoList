package com.todolist.todo.Model.Task.DB;

import com.todolist.todo.Model.Task.Task;

import java.time.LocalDate;
import java.util.Set;

public interface DataBaseDriver {
    /**
     * Add a new task to database.
     */
    public void addTask(Task task);

    /**
     * @return a set of all not done tasks
     */
    public Set<Task> selectAllTodoTasks();

    /**
     * @return a set of already been done tasks whose ddl is within date
     */
    public Set<Task> selectDoneTasks(LocalDate date);

    /**
     * Update the isDone field of database.
     * @param task a task whose isDone field changes
     */
    public void updateDoneMark(Task task);

    /**
     * Delete a task from database.
     */
    public void deleteTask(Task task);
}
