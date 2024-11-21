package com.todolist.todo.View;

import javafx.scene.layout.AnchorPane;

public class ViewFactory {
    private AnchorPane MyDayView;
    private AnchorPane TimeLineView;

    public ViewFactory() {}

    public AnchorPane getMyDayView() {
        if (MyDayView == null) {
            MyDayView = LoadPaneUtils.loadPane("/UI/View/TwoColumnView.fxml", null);
        }
        return MyDayView;
    }

    public AnchorPane getTimeLineView() {
        if (TimeLineView == null) {
            TimeLineView = LoadPaneUtils.loadPane("/UI/View/TimelineView.fxml", null);
        }
        return TimeLineView;
    }

}
