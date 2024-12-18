package com.todolist.todo.Model;

import com.todolist.todo.Model.Task.DB.DataBaseDriver;
import com.todolist.todo.Model.Task.DB.SQLiteDriver;
import com.todolist.todo.Model.Task.TaskPool;
import com.todolist.todo.Model.View.PaneModel;
import com.todolist.todo.View.ViewFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;

public class AppModel {

    public enum CenterPaneKind {
        CPANE_MYDAY,
        CPANE_DASHBOARD,
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

    public static synchronized void setupAppModel(DataBaseDriver database) {
//        assert (appModel == null);
        appModel = new AppModel(database);
    }

    private AppModel(DataBaseDriver database) {
        taskPool = new TaskPool(database);
        viewFactory = new ViewFactory();
        currentCenterPane = new SimpleObjectProperty<>(CenterPaneKind.CPANE_DASHBOARD);
        paneModel = new PaneModel(this);
    }

    private AppModel() {
        taskPool = new TaskPool(new SQLiteDriver());
        viewFactory = new ViewFactory();
        currentCenterPane = new SimpleObjectProperty<>(CenterPaneKind.CPANE_DASHBOARD);
        paneModel = new PaneModel(this);
    }

    public ObjectProperty<CenterPaneKind> getCurrentCenterPane() {
        return currentCenterPane;
    }

    public void setCurrentCenterPane(CenterPaneKind newCenterPane) {
        if (paneModel.hasPane()) {
            paneModel.blinkPane();
            return;
        }
        currentCenterPane.set(newCenterPane);
    }

    public CenterPaneKind currentCenterPaneKind() {
        return currentCenterPane.get();
    }

    public AnchorPane getSwitchedAnchorPane() {
        return switch (currentCenterPane.get()) {
            case CPANE_MYDAY -> viewFactory.getMyDayView();
            case CPANE_DASHBOARD -> viewFactory.getDashboardView();
            case CPANE_IMPORTANT -> viewFactory.getMyDayView();
            case CPANE_TIMELINE -> viewFactory.getTimeLineView();
        };
    }

    public TaskPool getTaskPool() {
        return taskPool;
    }

    public PaneModel getPaneModel() {
        return paneModel;
    }
}
