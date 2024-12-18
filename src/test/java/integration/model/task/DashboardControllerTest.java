package integration.model.task;

import com.todolist.todo.HelloApplication;
import com.todolist.todo.Model.Task.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
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
        FxToolkit.cleanupStages();
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
    void testAddTask() {
        int expectTodoNum = Integer.parseInt(todoNum.getText()) + 1;
        int expectDueNum = Integer.parseInt(dueTodayNum.getText()) + 1;

        Button addTaskBtn = lookup("#addTaskBtn").queryButton();
        clickOn(addTaskBtn);

        TextField titleTextField = lookup("#titleTextField").queryAs(TextField.class);
        TextArea detailTextArea = lookup("#detailTextArea").queryAs(TextArea.class);

        String title = "New Task#testAddTask";
        clickOn(titleTextField).write(title);
        clickOn(detailTextArea).write("Details");

        Button createBtn = lookup("#createBtn").queryButton();
        clickOn(createBtn);

        assertEquals(expectTodoNum, Integer.parseInt(todoNum.getText()));
        assertEquals(expectDueNum, Integer.parseInt(dueTodayNum.getText()));

        boolean taskFound = allTodoListView.getItems()
                .stream()
                .anyMatch(t -> t.getTitle().equals(title));

        assertTrue(taskFound, "Task not found in the list");
    }

//    @Test
//    void testDeleteTaskIntegration() {
//        // Step 1: 添加两个任务
//        clickOn(taskInputField).write("Task 1");
//        clickOn(addButton);
//        clickOn(taskInputField).write("Task 2");
//        clickOn(addButton);
//
//        // Step 2: 选择第一个任务并删除
//        interact(() -> taskListView.getSelectionModel().select(0)); // 选择第一个任务
//        Button deleteButton = lookup("#deleteButton").queryAs(Button.class);
//        clickOn(deleteButton);
//
//        // Step 3: 验证任务是否从列表中删除
//        assertEquals(1, taskListView.getItems().size());
//        assertEquals("Task 2", taskListView.getItems().get(0));
//    }
//
    @Test
    void testMarkDone() throws InterruptedException {
        Thread.sleep(1000);
        interact(() -> allTodoListView.getSelectionModel().select(0)); // 选择任务
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
