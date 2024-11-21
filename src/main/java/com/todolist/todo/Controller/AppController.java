package com.todolist.todo.Controller;

import com.todolist.todo.Model.AppModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    private BorderPane ApplicationPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ApplicationPane.setCenter(AppModel.getInstance().getSwitchedAnchorPane());
        AppModel.getInstance().getCurrentCenterPane().addListener(observable -> {
            ApplicationPane.setCenter(AppModel.getInstance().getSwitchedAnchorPane());
        });
    }
}
