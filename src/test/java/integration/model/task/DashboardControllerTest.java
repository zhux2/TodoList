package integration.model.task;

import com.todolist.todo.HelloApplication;
import com.todolist.todo.Model.Task.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import unit.model.task.mock.MockDataBaseDriver;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardControllerTest extends ApplicationTest {
    MockDataBaseDriver mockDatabase;
    ListView<Task> allTodoListView;
    ListView<Task> recentDoneListView;
    Text todoNum;
    Text dueTodayNum;

    @BeforeEach
    public void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        mockDatabase = new MockDataBaseDriver();
        MockDataBaseDriver.setupDB(mockDatabase);
        FxToolkit.setupApplication(() -> new HelloApplication(mockDatabase));
        allTodoListView = lookup("#allTodoListView").queryListView();
        recentDoneListView = lookup("#recentDoneListView").queryListView();
        todoNum = lookup("#todoNum").queryText();
        dueTodayNum = lookup("#dueTodayNum").queryText();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
    }


    @Test
    void testAddTask() throws InterruptedException {
        int expectTodoNum = Integer.parseInt(todoNum.getText()) + 1;
        int expectDueNum = Integer.parseInt(dueTodayNum.getText()) + 1;

        Button addTaskBtn = lookup("#addTaskBtn").queryButton();
        clickOn(addTaskBtn);
        Thread.sleep(1000);

        TextField titleTextField = lookup("#titleTextField").queryAs(TextField.class);
        TextArea detailTextArea = lookup("#detailTextArea").queryAs(TextArea.class);

        String title = "New Task#testAddTask";
        clickOn(titleTextField).write(title);
        clickOn(detailTextArea).write("Details");

        Button createBtn = lookup("#createBtn").queryButton();
        clickOn(createBtn);
        Thread.sleep(1000);

        assertEquals(expectTodoNum, Integer.parseInt(todoNum.getText()));
        assertEquals(expectDueNum, Integer.parseInt(dueTodayNum.getText()));

        boolean taskFound = allTodoListView.getItems()
                .stream()
                .anyMatch(t -> t.getTitle().equals(title));

        assertTrue(taskFound, "Task not found in the list");
    }

    @Test
    void testDeleteTask() throws InterruptedException {
        int expectTodoNum = Integer.parseInt(todoNum.getText()) - 1;
        Thread.sleep(1000);

        Button deleteTaskBtn = lookup("#deleteTaskBtn").queryButton();
        clickOn(deleteTaskBtn);
        Thread.sleep(1000);

        interact(() -> allTodoListView.getSelectionModel().select(0));
        Task deleteTask = allTodoListView.getItems().get(0);
        Node selectedCell = allTodoListView.lookup(".list-cell:selected");
        Button deleteBtn = (Button) selectedCell.lookup("#deleteBtn");
        clickOn(deleteBtn);
        Thread.sleep(1000);

        AnchorPane overlay = lookup("#subPane").queryAs(AnchorPane.class);
        Button deleteCheck = (Button) overlay.lookup("#deleteBtn");
        clickOn(deleteCheck);
        Thread.sleep(1000);

        assertEquals(expectTodoNum, Integer.parseInt(todoNum.getText()));

        assertFalse(allTodoListView.getItems().contains(deleteTask));
    }

    @Test
    void testMarkDone() throws InterruptedException {
        Thread.sleep(1000);
        interact(() -> allTodoListView.getSelectionModel().select(0));
        Task doneTask = allTodoListView.getItems().get(0);
        Node selectedCell = allTodoListView.lookup(".list-cell:selected");
        CheckBox checkBox = (CheckBox) selectedCell.lookup("#checkBox");
        interact(checkBox::fire);

        Thread.sleep(1000);
        assertTrue(doneTask.done());
        boolean flag = false;
        for (Task t : recentDoneListView.getItems()) {
            if (t == doneTask) {
                flag = true;
                break;
            }
        }
        assertTrue(flag);

        Thread.sleep(1000);
        interact(() -> recentDoneListView.getSelectionModel().select(0));
        Node recentCell = recentDoneListView.lookup(".list-cell:selected");
        checkBox = (CheckBox) recentCell.lookup("#checkBox");
        doneTask = recentDoneListView.getItems().get(0);
        interact(checkBox::fire);


        Thread.sleep(1000);
        assertFalse(doneTask.done());
        flag = false;
        for (Task t : allTodoListView.getItems()) {
            if (t == doneTask) {
                flag = true;
                break;
            }
        }
        assertTrue(flag);
    }
}
