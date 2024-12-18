package com.todolist.todo.Controller.ViewCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.View.OverlayObserver;
import com.todolist.todo.Model.View.PaneModel;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ModifiableBaseController implements OverlayObserver {
    @FXML
    protected Button addTaskBtn;

    @FXML
    protected Button deleteTaskBtn;

    @FXML
    protected AnchorPane subPane;

    private final AppModel.CenterPaneKind centerPaneKind;

    protected final PaneModel model = AppModel.getInstance().getPaneModel();

    public ModifiableBaseController(AppModel.CenterPaneKind kind) {
        centerPaneKind = kind;
    }

    protected void init() {
        model.registerObserver(this);
        subPane.setVisible(false);
        addTaskBtn.setOnAction(event -> onBtnAddTask());
        deleteTaskBtn.setOnAction(event -> onBtnDeleteTask());
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

    @Override
    public AppModel.CenterPaneKind centerPaneKind() {
        return centerPaneKind;
    }

    @Override
    public void onOverlayChange(AnchorPane overlay) {
        subPane.getChildren().clear();
        if (overlay != null) {
            subPane.getChildren().add(overlay);
            subPane.setVisible(true);
        }
        else {
            subPane.setVisible(false);
        }
    }
}
