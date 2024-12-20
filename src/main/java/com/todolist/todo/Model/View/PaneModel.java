package com.todolist.todo.Model.View;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.View.PaneFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class PaneModel {

    public enum PaneKind {
        PANE_ADDTASK,
        PANE_DELETECHECK,
        PANE_DELETE,
        PANE_MODIFY,
        PANE_NONE
    }

    private final PaneFactory paneFactory;

    private PaneKind overlayKind;
    private final BooleanProperty deleting;
    private final BooleanProperty deleteChecking;

    private final List<OverlayObserver> observers;

    private Task triggerTask;

    private Timeline blinkEffect;

    private final AppModel appModel;

    public PaneModel(AppModel appModel) {
        this.appModel = appModel;
        overlayKind = PaneKind.PANE_NONE;
        paneFactory = new PaneFactory();
        deleting = new SimpleBooleanProperty(false);
        deleteChecking = new SimpleBooleanProperty(false);
        observers = new ArrayList<>();
    }

    public void registerObserver(OverlayObserver observer) {
        observers.add(observer);
    }

    public void setOverlayKind(PaneKind paneKind) {
        if (paneKind == overlayKind) return;

        overlayKind = paneKind;
        deleting.set(paneKind == PaneKind.PANE_DELETE || paneKind == PaneKind.PANE_DELETECHECK);
        deleteChecking.set(paneKind == PaneKind.PANE_DELETECHECK);

        AppModel.CenterPaneKind cur = appModel.currentCenterPaneKind();
        for (OverlayObserver observer : observers) {
            if (observer.centerPaneKind() == cur) {
                observer.onOverlayChange(getOverlay());
                break;
            }
        }
    }

    public AnchorPane getOverlay() {
        return switch (overlayKind) {
            case PANE_ADDTASK -> paneFactory.getAddTaskPane();
            case PANE_DELETECHECK -> paneFactory.getDeleteCheckPane();
            default -> null;
        };
    }

    public BooleanProperty deletingProperty() {
        return deleting;
    }

    public boolean isDeleting() {
        return deleting.get();
    }

    public BooleanProperty deleteCheckingProperty() {
        return deleteChecking;
    }

    public boolean isDeleteChecking() {
        return deleteChecking.get();
    }

    public void setTriggerTask(Task task) {
        this.triggerTask = task;
    }

    public Task getTriggerTask() {
        return triggerTask;
    }

    public boolean hasPane() {
        return overlayKind != PaneKind.PANE_NONE;
    }

    public void blinkPane() {
        if (blinkEffect == null) {
            blinkEffect = new Timeline(
                    new KeyFrame(Duration.seconds(0), e -> getOverlay().setStyle("-fx-background-color: rgba(223, 232, 232, 0.5)")),
                    new KeyFrame(Duration.seconds(0.2), e -> getOverlay().setStyle("-fx-background-color: rgba(193, 202, 202, 0.5);"))
            );
            blinkEffect.setCycleCount(1);
        }

        blinkEffect.play();
    }
}
