package com.todolist.todo.Controller.ViewCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskManager;
import com.todolist.todo.Model.Task.TaskTag;
import com.todolist.todo.View.Cell.TaskCellFactory;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TwoColumnController extends ModifiableBaseController implements Initializable {
    @FXML
    private DatePicker calendar;
    @FXML
    private ComboBox<TaskTag> tagComboBox;

    @FXML
    private ListView<Task> finishedListView;
    @FXML
    private ListView<Task> unfinishedListView;

    private final TaskManager taskManager;

    public TwoColumnController() {
        super(AppModel.CenterPaneKind.CPANE_MYDAY);
        taskManager = new TaskManager(AppModel.getInstance().getTaskPool());
//        System.out.println("create TwoColumnController");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.init();
        initMainPane();

        taskManager.setDate(calendar.getValue());

        unfinishedListView.setItems(taskManager.getTodoShowList());
        unfinishedListView.setCellFactory(e -> new TaskCellFactory(model));
        finishedListView.setItems(taskManager.getDoneShowList());
        finishedListView.setCellFactory(e -> new TaskCellFactory(model));
    }

    private void initMainPane() {
        calendar.setValue(LocalDate.now());
        calendar.valueProperty().addListener(this::onCalendarChangeDate);

        tagComboBox.getItems().addAll(TaskTag.values());
        tagComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TaskTag item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.displayName());
            }
        });

        tagComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TaskTag item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.displayName());
            }
        });

        tagComboBox.setValue(TaskTag.NONE);

        tagComboBox.setOnAction(actionEvent -> {
            taskManager.setTag(tagComboBox.getValue());
        });
    }

    /**
     * Listener for changes in currently selected date.
     */
    private void onCalendarChangeDate(ObservableValue<? extends LocalDate> observable, LocalDate oldDate, LocalDate newDate) {
        if (oldDate != null && oldDate.equals(newDate)) return;

        taskManager.setDate(newDate);
    }
}
