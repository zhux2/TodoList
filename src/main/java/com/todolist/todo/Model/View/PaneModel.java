package com.todolist.todo.Model.View;

import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.View.PaneFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;

public class PaneModel {

    public enum PaneKind {
        PANE_ADDTASK,
        PANE_DELETECHECK,
        PANE_DELETE,
        PANE_MODIFY,
        PANE_NONE
    }

    private final PaneFactory paneFactory;

    private final ObjectProperty<PaneKind> overlayKind;
    private final BooleanProperty deleting;

    private Task triggerTask;



    public PaneModel() {
        overlayKind = new SimpleObjectProperty<>(PaneKind.PANE_NONE);
        paneFactory = new PaneFactory();
        deleting = new SimpleBooleanProperty(false);
    }

    public ObjectProperty<PaneKind> overlayKindProperty() {
        return overlayKind;
    }

    public void setOverlayKind(PaneKind paneKind) {
        overlayKind.set(paneKind);
        deleting.set(paneKind == PaneKind.PANE_DELETE || paneKind == PaneKind.PANE_DELETECHECK);
    }

    public AnchorPane getOverlay() {
        return switch (overlayKind.getValue()) {
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

    public void setTriggerTask(Task task) {
        this.triggerTask = task;
    }

    public Task getTriggerTask() {
        return triggerTask;
    }
}
