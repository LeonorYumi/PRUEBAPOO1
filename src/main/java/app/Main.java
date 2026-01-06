package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(loader.load());
        //scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        primaryStage.setTitle("SISTEMA DE ENTREGA DE LICENCIAS");
        // icon.png must be placed in resources/images/
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        } catch (Exception ignored) {}
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}