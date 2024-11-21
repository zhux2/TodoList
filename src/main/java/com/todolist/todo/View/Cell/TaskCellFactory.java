package com.todolist.todo.View.Cell;

import com.todolist.todo.Controller.CellCtrl.TaskCellController;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.View.PaneModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class TaskCellFactory extends ListCell<Task> {
    /**
     * lazy load: avoid loading fxml every time
     */
    private FXMLLoader loader;
    private Node graphic;
    private TaskCellController controller;

    private PaneModel model;

    public TaskCellFactory(PaneModel model) {
        this.model = model;
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setText(null);
            setGraphic(null);
        }
        else {
//            if (controller != null && task == controller.getTask()) {
////                System.out.println("no need to update");
//                return;
//            }
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/UI/Cell/TaskCell.fxml"));
                controller = new TaskCellController(task, model);
                loader.setController(controller);
                try {
                    graphic = loader.load();
                } catch (IOException e) {
                    System.out.println("Error loading task-cell fxml: " + e.getMessage());
                }
            }
            assert controller != null;
            controller.updateTask(task);

            setText(null);
            setGraphic(graphic);
        }
    }
}
