package com.todolist.todo.Controller.ViewCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TimelineManager;
import com.todolist.todo.View.Cell.TimeStampCellFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class TimelineController implements Initializable {
    @FXML
    private ListView<Task> timelineListView;

    private final TimelineManager taskManager;

    public TimelineController() {
        taskManager = new TimelineManager(AppModel.getInstance().getTaskPool());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timelineListView.setItems(taskManager.getTaskList());
        timelineListView.setCellFactory(e -> new TimeStampCellFactory());
    }
}
