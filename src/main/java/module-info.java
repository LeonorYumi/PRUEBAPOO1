module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;

    opens app to javafx.fxml;
    exports app;

    // Esto es vital para que JavaFX encuentre tus archivos FXML y controladores
    opens fxml to javafx.fxml;
    exports ui.login;
    opens ui.login to javafx.fxml;
    exports ui.admin;
    opens ui.admin to javafx.fxml;
    exports ui.analista;
    opens ui.analista to javafx.fxml;
}