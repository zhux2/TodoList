package com.todolist.todo.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowManager {
    public WindowManager() {}

    public void showApplicationWindow(Object controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/App.fxml"));
        loader.setController(controller);
        Stage stage = getStage(loader);
        stage.setResizable(false);
        stage.setTitle("Jaw To-do List");
        stage.show();
    }

    private Stage getStage(FXMLLoader loader) {
        Scene scene = null;

        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene(scene);

        return stage;
    }
}
