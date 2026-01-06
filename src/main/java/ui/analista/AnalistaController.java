package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class AnalistaController {

    @FXML
    private Button btnCerrarSesion; // Debes ponerle este fx:id a tu botón en el FXML

    @FXML
    private void handleCerrarSesion() {
        try {
            System.out.println("Cerrando sesión...");

            // 1. Cargar la vista del Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            // 2. Crear el nuevo Stage para el Login
            Stage stage = new Stage();
            stage.setTitle("Sistema de Licencias - Login");
            stage.setScene(new Scene(root));
            stage.show();

            // 3. Cerrar la ventana actual del Analista
            // Usamos btnCerrarSesion para obtener la ventana actual
            Stage currentStage = (Stage) btnCerrarSesion.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println("Error al regresar al Login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}