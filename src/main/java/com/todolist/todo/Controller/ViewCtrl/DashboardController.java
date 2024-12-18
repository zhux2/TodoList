package com.todolist.todo.Controller.ViewCtrl;

import com.todolist.todo.Model.AppModel;
import com.todolist.todo.Model.Task.DashboardManager;
import com.todolist.todo.Model.Task.Task;
import com.todolist.todo.View.Cell.TaskCellFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ModifiableBaseController implements Initializable {
    @FXML
    private ListView<Task> allTodoListView;

    @FXML
    private ListView<Task> recentDoneListView;

    @FXML
    private Text todoNum;

    @FXML
    private Text dueTodayNum;

    private final DashboardManager taskManager;

    public DashboardController() {
        super(AppModel.CenterPaneKind.CPANE_DASHBOARD);
        taskManager = new DashboardManager(AppModel.getInstance().getTaskPool());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.init();

        todoNum.textProperty().bind(taskManager.todoNumProperty().asString());
        dueTodayNum.textProperty().bind(taskManager.dueTodayNumProperty().asString());
        allTodoListView.setItems(taskManager.getTodoTaskList());
        allTodoListView.setCellFactory(e -> new TaskCellFactory(model));
        recentDoneListView.setItems(taskManager.getDoneTaskList());
        recentDoneListView.setCellFactory(e -> new TaskCellFactory(model));
    }

}
