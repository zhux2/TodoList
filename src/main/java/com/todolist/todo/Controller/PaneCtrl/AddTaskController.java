package com.todolist.todo.Controller.PaneCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.Model.Task.TaskPool;
import com.todolist.todo.Model.View.PaneModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddTaskController implements Initializable {

    private final PaneModel model;
    private final TaskPool taskPool;

    @FXML
    private Button backFromAddBtn;
    @FXML
    private Button createBtn;

    @FXML
    private TextField titleTextField;
    @FXML
    private DatePicker ddlDate;
    @FXML
    private Spinner<Integer> ddlHour;
    @FXML
    private Spinner<Integer> ddlMinute;
    @FXML
    private CheckBox tagImportant;
    @FXML
    private TextArea detailTextArea;

    public AddTaskController() {
        this.model = AppModel.getInstance().getPaneModel();
        this.taskPool = AppModel.getInstance().getTaskPool();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initPane();
        resetPane();

        backFromAddBtn.setOnAction(actionEvent -> onBtnBackFromAdd());
        createBtn.setOnAction(actionEvent -> onBtnCreate());
    }

    private void initPane() {
        backFromAddBtn.setOnAction(event -> onBtnBackFromAdd());
        createBtn.setOnAction(event -> onBtnCreate());

        /**
         * IntegerSpinnerValueFactory(i1, i2, i3, i4)
         *  i1: min val
         *  i2: max val
         *  i3: default val
         *  i4: step
         */
        ddlHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
        ddlMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 59, 1));
    }

    private void resetPane() {
        ddlDate.setValue(LocalDate.now());
        ddlHour.getValueFactory().setValue(23);
        ddlMinute.getValueFactory().setValue(59);
        titleTextField.setText("");
        detailTextArea.setText("");
        tagImportant.setSelected(false);
    }

    private void onBtnBackFromAdd() {
        model.setOverlayKind(PaneModel.PaneKind.PANE_NONE);
    }

    private void onBtnCreate() {
        Task createdTask = new Task(
                titleTextField.getText(),
                ddlDate.getValue().atTime(ddlHour.getValue(), ddlMinute.getValue()),
                tagImportant.isSelected(),
                detailTextArea.getText()
        );

        taskPool.addNewTask(createdTask);

        resetPane();

        model.setOverlayKind(PaneModel.PaneKind.PANE_NONE);
    }
}
