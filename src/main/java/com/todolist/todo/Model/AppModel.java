package com.todolist.todo.Model;

import com.todolist.todo.Model.Task.TaskPool;
import com.todolist.todo.Model.View.PaneModel;
import com.todolist.todo.View.ViewFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;

public class AppModel {

    public enum CenterPaneKind {
        CPANE_MYDAY,
        CPANE_MYWEEK,
        CPANE_IMPORTANT,
        CPANE_TIMELINE
    }

    private static AppModel appModel;

    private final ObjectProperty<CenterPaneKind> currentCenterPane;

    private final ViewFactory viewFactory;

    private final TaskPool taskPool;

    private final PaneModel paneModel;

    public static synchronized AppModel getInstance() {
        if (appModel == null) {
            appModel = new AppModel();
        }
        return appModel;
    }

    private AppModel() {
        taskPool = new TaskPool();
        viewFactory = new ViewFactory();
        currentCenterPane = new SimpleObjectProperty<>(CenterPaneKind.CPANE_MYDAY);
        paneModel = new PaneModel();
    }

    public ObjectProperty<CenterPaneKind> getCurrentCenterPane() {
        return currentCenterPane;
    }

    public void setCurrentCenterPane(CenterPaneKind newCenterPane) {
        currentCenterPane.set(newCenterPane);
    }

    public AnchorPane getSwitchedAnchorPane() {
        return switch (currentCenterPane.get()) {
            case CPANE_MYDAY -> viewFactory.getMyDayView();
            case CPANE_MYWEEK -> viewFactory.getMyDayView();
            case CPANE_IMPORTANT -> viewFactory.getMyDayView();
            case CPANE_TIMELINE -> viewFactory.getTimeLineView();
        };
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public TaskPool getTaskPool() {
        return taskPool;
    }

    public PaneModel getPaneModel() {
        return paneModel;
    }
}
