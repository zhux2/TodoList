package com.todolist.todo;

import com.todolist.todo.Controller.AppController;
import com.todolist.todo.View.WindowManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        WindowManager wm = new WindowManager();
        wm.showApplicationWindow(new AppController());
    }

    public static void main(String[] args) {
        launch();
    }
}


//import javafx.scene.text.Text;
//
//public class HelloApplication extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        // 创建 TextFlow 和内容
//        TextFlow textFlow = new TextFlow();
//        textFlow.setPrefWidth(300);  // 设置宽度限制
//        textFlow.setPrefHeight(100); // 设置高度限制
//        textFlow.setMaxWidth(300);  // 最大宽度
//        textFlow.setMaxHeight(100); // 最大高度
//        textFlow.setClip(new javafx.scene.shape.Rectangle(300, 100)); // 添加裁剪区域
//
//
//        // 添加一些长文本
//        Text text = new Text("这是一个非常长的段落，内容超出宽度时会自动换行，"
//                + "而如果内容超出高度，会被限制在最大高度内，除非启用滚动或裁剪。");
//        textFlow.getChildren().add(text);
//
//        // 场景和窗口
//        Scene scene = new Scene(textFlow, 400, 200);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("限制 TextFlow 的宽度和高度");
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
