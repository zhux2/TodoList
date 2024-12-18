package com.todolist.todo.View.Cell;

import com.todolist.todo.Controller.CellCtrl.TimeStampCellController;
import com.todolist.todo.Model.Task.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class TimeStampCellFactory extends ListCell<Task> {
    private FXMLLoader loader;
    private Node graphic;
    private TimeStampCellController controller;

    public TimeStampCellFactory() { }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/UI/Cell/TimeStampCell.fxml"));
                controller = new TimeStampCellController(task);
                loader.setController(controller);
                try {
                    graphic = loader.load();
                } catch (IOException e) {
                    System.out.println("Error loading timestamp-cell fxml: " + e.getMessage());
                }
            }
            assert controller != null;
            controller.bindNewTask(task);

            setText(null);
            setGraphic(graphic);
        }
    }
}
