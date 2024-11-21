package com.todolist.todo.View;

import com.todolist.todo.Controller.PaneCtrl.AddTaskController;
import com.todolist.todo.Controller.PaneCtrl.DeleteCheckController;
import javafx.scene.layout.AnchorPane;

public class PaneFactory {
    private AnchorPane AddTaskPane;
    private AnchorPane DeleteCheckPane;

    public AnchorPane getAddTaskPane() {
        if (AddTaskPane == null) {
            AddTaskPane = LoadPaneUtils.loadPane("/UI/View/AddTaskPane.fxml", new AddTaskController());
        }
        return AddTaskPane;
    }

    public AnchorPane getDeleteCheckPane() {
        if (DeleteCheckPane == null) {
            DeleteCheckPane = LoadPaneUtils.loadPane("/UI/View/DeleteCheckPane.fxml", new DeleteCheckController());
        }
        return DeleteCheckPane;
    }

    public AnchorPane getModifyTaskPane() {
        // TODO - implement me
        return null;
    }
}
