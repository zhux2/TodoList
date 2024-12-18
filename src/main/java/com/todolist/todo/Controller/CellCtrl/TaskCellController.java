package com.todolist.todo.Controller.CellCtrl;

import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.View.PaneModel;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TaskCellController implements Initializable {
    @FXML
    private AnchorPane taskCellPane;
    @FXML
    private Text title;
    @FXML
    private Text ddlString;
    @FXML
    private Text details;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Button deleteBtn;

    private Task task;

    private ChangeListener<Boolean> importantListener = null;

    private ChangeListener<Boolean> approachListener = null;

    private final PaneModel model;

    public TaskCellController(Task task, PaneModel model) {
        this.task = task;
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setStaticData();
        bindTask();

        checkBox.setOnAction(event -> onCheckBoxSelected());
        taskCellPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                onDoubleClick();
            }
        });

        deleteBtn.setVisible(false);
        deleteBtn.setOnAction(event -> onBtnDelete());
        model.deletingProperty().addListener(this::onDeletingChange);
    }

    private void onDeletingChange(Observable observable) {
        if (model.isDeleting()) {
            deleteBtn.setVisible(true);
            checkBox.setVisible(false);
        }
        else {
            deleteBtn.setVisible(false);
            checkBox.setVisible(true);
        }
    }

    public Task getTask() {
        return task;
    }

    /**
     * JavaFX ListView will reuse Task Cell by binding different task to the
     * same Task Cell
     * update controller when ListView reuse this cell for different task
     */
    public void bindNewTask(Task task) {
        if (task == this.task) {
            setStaticData();
            return;
        }
        // remove old listeners
        assert (this.task != null && importantListener != null && approachListener != null);
        this.task.importantProperty().removeListener(importantListener);
        this.task.approachProperty().removeListener(approachListener);

        this.task = task;
        setStaticData();
        bindTask();
    }

    private void setStaticData() {
        checkBox.setSelected(task.done());
    }

    private void bindTask() {
        title.textProperty().bind(task.titleProperty());
        details.textProperty().bind(task.detailsProperty());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ddlString.textProperty().bind(
                Bindings.createStringBinding(
                        () -> task.ddlProperty().getValue() == null ? "" : task.ddlProperty().getValue().format(formatter),
                        task.ddlProperty()
                )
        );

        updateCheckBox(task.importantProperty().get());
        updateHighlight(task.isApproach());

        // add listeners
        importantListener = this::onImportantChange;
        task.importantProperty().addListener(importantListener);

        approachListener = this::onApproachChange;
        task.approachProperty().addListener(approachListener);
    }

    private void onImportantChange(ObservableValue<? extends Boolean> isImportant, Boolean oldVal, Boolean newVal) {
        if (newVal == oldVal) return;
        updateCheckBox(newVal);
    }

    private void onApproachChange(ObservableValue<? extends Boolean> isApproach, Boolean oldVal, Boolean newVal) {
        if (newVal == oldVal) return;
        updateHighlight(newVal);
    }

    private void updateHighlight(boolean isApproach) {
        if (isApproach) {
            taskCellPane.getStyleClass().remove("taskCellNormal");
            taskCellPane.getStyleClass().add("taskCellHighlight");
        }
        else {
            taskCellPane.getStyleClass().remove("taskCellHighlight");
            taskCellPane.getStyleClass().add("taskCellNormal");
        }
    }

    private void updateCheckBox(boolean isImportant) {
        if (isImportant) {
            checkBox.getStyleClass().remove("checkBox");
            checkBox.getStyleClass().add("checkBoxImportant");
        }
        else {
            checkBox.getStyleClass().remove("checkBoxImportant");
            checkBox.getStyleClass().add("checkBox");
        }
    }

    private void onCheckBoxSelected() {
        System.out.println("check box selected");
        task.markDone(checkBox.isSelected());
    }

    private void onDoubleClick() {
        // TODO - show Modify Task Pane
        System.out.println(task.titleProperty().getValue() + ": double click");
    }

    private void onBtnDelete() {
        model.setTriggerTask(task);
        model.setOverlayKind(PaneModel.PaneKind.PANE_DELETECHECK);
    }
}
