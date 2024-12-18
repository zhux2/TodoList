package com.todolist.todo.Model.Task.DB;

import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskTag;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

public class SQLiteDriver implements DataBaseDriver{
    private static final String URL = "jdbc:sqlite:jaw-todo.db";
    private Connection connection;

    public SQLiteDriver() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    ddl INTEGER NOT NULL,
                    finishTime INTEGER DEFAULT 0,
                    isImportant INTEGER DEFAULT 0,
                    detail TEXT,
                    isDone INTEGER DEFAULT 0,
                    tag TEXT
                );
                """;
        try {
            connection = DriverManager.getConnection(URL);
            Statement stmt = connection.createStatement();
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, ddl, isImportant, detail, tag) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.titleProperty().get());
            pstmt.setLong(2, task.getDdl().toEpochSecond(ZoneOffset.UTC));
            pstmt.setBoolean(3, task.importantProperty().get());
            pstmt.setString(4, task.detailsProperty().get());
            pstmt.setString(5, task.getTag().name());
            int id = pstmt.executeUpdate();
            task.setId(id);
//            System.out.println("Task added.");
        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
        }
    }

    @Override
    public Set<Task> selectAllTodoTasks() {
        String sql = "SELECT * FROM tasks WHERE isDone = 0";
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("Error selecting tasks: " + e.getMessage());
        }

        if (pstmt == null) {
            return new HashSet<>();
        }
        return loadTasks(pstmt);
    }

    @Override
    public Set<Task> selectDoneTasks(LocalDate date) {
        long startOfDay = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond() - 1;
        String sql = "SELECT * FROM tasks WHERE isDone = 1 AND ddl BETWEEN ? AND ?";
        return selectByTime(startOfDay, endOfDay, sql);
    }

    @Override
    public Set<Task> selectRecentDoneTasks(LocalDate startDate, LocalDate endDate) {
        long startTime = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long endTime = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond() - 1;
        String sql = "SELECT * FROM tasks WHERE isDone = 1 AND finishTime BETWEEN ? AND ?";
        return selectByTime(startTime, endTime, sql);
    }

    private Set<Task> selectByTime(long startTime, long endTime, String sql) {
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, startTime);
            pstmt.setLong(2, endTime);
        } catch (SQLException e) {
            System.out.println("Error selecting tasks: " + e.getMessage());
        }

        if (pstmt == null) {
            return new HashSet<>();
        }
        return loadTasks(pstmt);
    }

    private Set<Task> loadTasks(PreparedStatement pstmt) {
        Set<Task> taskSet = new HashSet<>();
        try {
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                Task task = new Task(
                        res.getInt("id"),
                        res.getString("title"),
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(res.getLong("ddl")), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(res.getLong("finishTime")), ZoneOffset.UTC),
                        res.getBoolean("isImportant"),
                        res.getString("detail"),
                        res.getBoolean("isDone"),
                        TaskTag.valueOf(res.getString("tag"))
                );

                taskSet.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return taskSet;
    }

    @Override
    public void updateDoneMark(Task task) {
        String sql = "UPDATE tasks SET isDone = ?, finishTime = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, task.done());
            pstmt.setLong(2, task.getFinishTime().toEpochSecond(ZoneOffset.UTC));
            pstmt.setInt(3, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating tasks: " + e.getMessage());
        }
    }

    @Override
    public void deleteTask(Task task) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting tasks: " + e.getMessage());
        }
    }
}
