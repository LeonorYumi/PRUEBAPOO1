package ui.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.base.BaseController;
import java.io.IOException;

public class AdminController extends BaseController {

    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contentArea;

    @Override
    public void limpiarCampos() {
        // Método requerido por la herencia de BaseController
    }

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("No se encontró el archivo FXML: " + fxmlPath);
            }
            Parent view = loader.load();

            contentArea.getChildren().setAll(view);
            contentArea.setFocusTraversable(true);
            contentArea.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath, Alert.AlertType.ERROR);
        }
    }

    // --- OPERACIONES DE TRÁMITE ---
    @FXML private void handleRegistrarSolicitante() { cargarVista("/fxml/RegistrarSolicitanteView.fxml"); }
    @FXML private void handleVerificarRequisitos() { cargarVista("/fxml/VerificarRequisitoView.fxml"); }
    @FXML private void handleRegistrarExamenes() { cargarVista("/fxml/RegistrarExamenView.fxml"); }
    @FXML private void handleGestionTramites() { cargarVista("/fxml/GestionTramiteView.fxml"); }
    @FXML private void handleGenerarLicencia() { cargarVista("/fxml/GenerarLicenciaView.fxml"); }

    // --- CONTROL ADMINISTRATIVO ---
    @FXML private void handleGestionUsuarios() { cargarVista("/fxml/GestionUsuarioView.fxml"); }
    @FXML private void handleReportes() { cargarVista("/fxml/ReporteAdminView.fxml"); }

    // El método handleDashboard ha sido eliminado correctamente.

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login - Sistema de Licencias");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) btnCerrarSesion.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cerrar sesión", Alert.AlertType.ERROR);
        }
    }
}