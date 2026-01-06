package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1. CARGAR EL FXML: Nota que la ruta empieza con /fxml/
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));

        // 2. CREAR LA ESCENA
        Scene scene = new Scene(fxmlLoader.load());

        // 3. CARGAR EL CSS: Ruta /css/
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        stage.setTitle("SISTEMA DE LICENCIAS - LOGIN");
        stage.setResizable(false); // Recomendado para el Login
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}