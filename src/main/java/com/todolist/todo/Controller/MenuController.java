package com.todolist.todo.Controller;

import com.todolist.todo.Model.AppModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    private Button MyDayBtn;

    @FXML
    private Button DashBoardBtn;

    @FXML
    private Button ImportantBtn;

    @FXML
    private Button TimelineBtn;

    public MenuController() {
        System.out.println("create MenuController");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAddListeners();
    }

    private void initAddListeners() {
        MyDayBtn.setOnAction(event -> onBtnMyDay());
        TimelineBtn.setOnAction(event -> onBtnTimeline());
        ImportantBtn.setOnAction(event -> onBtnImportant());
        DashBoardBtn.setOnAction(event -> onBtnMyWeek());
    }

    private void onBtnMyDay() {
        AppModel.getInstance().setCurrentCenterPane(AppModel.CenterPaneKind.CPANE_MYDAY);
    }

    private void onBtnTimeline() {
        AppModel.getInstance().setCurrentCenterPane(AppModel.CenterPaneKind.CPANE_TIMELINE);
    }

    private void onBtnMyWeek() {
        AppModel.getInstance().setCurrentCenterPane(AppModel.CenterPaneKind.CPANE_DASHBOARD);
    }

    private void onBtnImportant() {
        AppModel.getInstance().setCurrentCenterPane(AppModel.CenterPaneKind.CPANE_IMPORTANT);
    }
}
