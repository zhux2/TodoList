package com.todolist.todo.Controller.PaneCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.TaskPool;
import com.todolist.todo.Model.View.PaneModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteCheckController implements Initializable {

    private final PaneModel model;
    private final TaskPool taskPool;

    @FXML
    Text promptText;
    @FXML
    Button cancelBtn;
    @FXML
    Button deleteBtn;

    public  DeleteCheckController() {
        model = AppModel.getInstance().getPaneModel();
        taskPool = AppModel.getInstance().getTaskPool();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPane();

        cancelBtn.setOnAction(actionEvent -> onBtnCancel());
        deleteBtn.setOnAction(actionEvent -> onBtnDelete());
    }

    private void setPane() {
        promptText.setText("Delete \"" + model.getTriggerTask().getTitle() + "\"?");
    }

    private void onBtnCancel() {
        model.setOverlayKind(PaneModel.PaneKind.PANE_NONE);
    }

    private void onBtnDelete() {
        taskPool.deleteTask(model.getTriggerTask());
        model.setOverlayKind(PaneModel.PaneKind.PANE_NONE);
    }
}