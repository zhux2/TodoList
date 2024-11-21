package com.todolist.todo.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public final class LoadPaneUtils {
    private LoadPaneUtils() {}

    public static AnchorPane loadPane(String path, Object controller) {
        AnchorPane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(LoadPaneUtils.class.getResource(path));
            if (controller != null)
                loader.setController(controller);
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }
}
