package com.todolist.todo.Controller.ViewCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskManager;
import com.todolist.todo.Model.View.PaneModel;
import com.todolist.todo.View.Cell.TaskCellFactory;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TwoColumnController implements Initializable {
    @FXML
    private DatePicker calendar;
    @FXML
    private Button addTaskBtn;
    @FXML
    private Button deleteTaskBtn;
    @FXML
    private ListView<Task> finishedListView;
    @FXML
    private ListView<Task> unfinishedListView;

    @FXML
    private AnchorPane subPane;

    private final TaskManager taskManager;

    private final PaneModel model = AppModel.getInstance().getPaneModel();

    public TwoColumnController() {
        taskManager = new TaskManager(AppModel.getInstance().getTaskPool());
//        System.out.println("create TwoColumnController");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initMainPane();

        model.overlayKindProperty().addListener(this::onOverlayChange);

        taskManager.setDate(calendar.getValue());

        unfinishedListView.setItems(taskManager.getTodoTaskList());
        unfinishedListView.setCellFactory(e -> new TaskCellFactory(model));
        finishedListView.setItems(taskManager.getDoneTaskList());
        finishedListView.setCellFactory(e -> new TaskCellFactory(model));
    }

    private void initMainPane() {
        subPane.setVisible(false);
        addTaskBtn.setOnAction(event -> onBtnAddTask());
        deleteTaskBtn.setOnAction(event -> onBtnDeleteTask());
        calendar.setValue(LocalDate.now());
        calendar.valueProperty().addListener(this::onCalendarChangeDate);
    }

    /**
     * Listener of overlay-kind.
     */
    private void onOverlayChange(Observable observable) {
        subPane.getChildren().clear();
        AnchorPane overlay = model.getOverlay();
        if (overlay != null) {
            subPane.getChildren().add(overlay);
            subPane.setVisible(true);
        }
        else {
            subPane.setVisible(false);
        }
    }

    /**
     * Event handler for delete task button press.
     */
    private void onBtnDeleteTask() {
        if (model.isDeleting()) {
            model.setOverlayKind(PaneModel.PaneKind.PANE_NONE);
        }
        else {
            model.setOverlayKind(PaneModel.PaneKind.PANE_DELETE);
        }
    }

    /**
     * Event handler for add task button press.
     */
    private void onBtnAddTask() {
        model.setOverlayKind(PaneModel.PaneKind.PANE_ADDTASK);
    }

    /**
     * Listener for changes in currently selected date.
     */
    private void onCalendarChangeDate(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {
        if (oldDate != null && oldDate.equals(newDate)) return;

        taskManager.setDate(newDate);
    }
}
