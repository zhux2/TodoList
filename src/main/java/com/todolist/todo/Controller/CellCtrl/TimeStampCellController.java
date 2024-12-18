package com.todolist.todo.Controller.CellCtrl;

import com.todolist.todo.Model.Task.Task;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TimeStampCellController implements Initializable {
    @FXML
    private Text ddlText;
    @FXML
    private Text titleText;

    private Task task;

    public TimeStampCellController(Task task) {
        this.task = task;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindTask();
    }

    /**
     * update controller when ListView reuse this cell for different task
     */
    public void bindNewTask(Task task) {
        if (task == this.task) {
            return;
        }

        this.task = task;
        bindTask();
    }

    private void bindTask() {
        titleText.textProperty().bind(task.titleProperty());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ddlText.textProperty().bind(
                Bindings.createStringBinding(
                        () -> task.ddlProperty().getValue() == null ? "" : task.ddlProperty().getValue().format(formatter),
                        task.ddlProperty()
                )
        );
    }
}
