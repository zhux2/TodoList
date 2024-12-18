package com.todolist.todo.Model.View;

import com.todolist.todo.Model.AppModel;
import javafx.scene.layout.AnchorPane;

public interface OverlayObserver {
    /**
     * @return the center pane kind of the observer
     */
    public AppModel.CenterPaneKind centerPaneKind();

    /**
     * @param overlay new overlay
     */
    public void onOverlayChange(AnchorPane overlay);
}
