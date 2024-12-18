package com.todolist.todo;

import com.todolist.todo.Controller.AppController;
import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.DB.DataBaseDriver;
import com.todolist.todo.View.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public HelloApplication() {}

    public HelloApplication(DataBaseDriver database) {
        AppModel.setupAppModel(database);
    }

    @Override
    public void start(Stage stage) throws IOException {
        WindowManager wm = new WindowManager();
        wm.showApplicationWindow(new AppController());
    }

    public static void main(String[] args) {
        launch();
    }
}