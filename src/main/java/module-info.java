module com.todolist.todo {
    requires javafx.controls;
    requires javafx.fxml;

    requires de.jensd.fx.glyphs.fontawesome;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires jdk.compiler;

    opens com.todolist.todo to javafx.fxml;
    opens com.todolist.todo.Controller to javafx.fxml;
    exports com.todolist.todo;
    exports com.todolist.todo.Controller;
    exports com.todolist.todo.Controller.ViewCtrl;
    exports com.todolist.todo.Model.Task;
}