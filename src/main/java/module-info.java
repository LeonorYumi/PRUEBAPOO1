module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;


// REQUIRES PARA PDFBOX Y MANEJO DE GRÁFICOS
    requires org.apache.pdfbox;
    requires java.desktop;

    // Paquete principal (Main)
    exports app;
    opens app to javafx.fxml;

    // Capa de Datos (DAO)
    exports dao;
    opens dao to javafx.fxml;

    // Modelos (POJOs/Beans)
    exports model;
    // CRÍTICO: opens model a javafx.base para TableView
    opens model to javafx.base, javafx.fxml;

    // Controladores de Interfaz (UI)
    exports ui.login;
    opens ui.login to javafx.fxml;

    exports ui.admin;
    opens ui.admin to javafx.fxml;

    exports ui.analista;
    opens ui.analista to javafx.fxml;
}