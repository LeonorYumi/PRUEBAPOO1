module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;

    // Esto permite que JavaFX lea tus clases y el controlador
    opens app to javafx.fxml;
    exports app;
}