package ui.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Necesario para el tipo de alerta
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.base.BaseController;
import java.io.IOException;

// AHORA TU CLASE HEREDA DE BASECONTROLLER
public class AdminController extends BaseController {

    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contentArea;

    // Implementamos el método abstracto que definimos en la base
    @Override
    public void limpiarCampos() {
        // En este controlador no hay campos de texto que limpiar,
        // pero el requisito de POO nos obliga a tener el método.
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
            // USAMOS EL MÉTODO HEREDADO DEL PADRE
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath, Alert.AlertType.ERROR);
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN (Se quedan igual) ---
    @FXML private void handleRegistrarSolicitante() { cargarVista("/fxml/RegistrarSolicitanteView.fxml"); }
    @FXML private void handleVerificarRequisitos() { cargarVista("/fxml/VerificarRequisitoView.fxml"); }
    @FXML private void handleRegistrarExamenes() { cargarVista("/fxml/RegistrarExamenView.fxml"); }
    @FXML private void handleGestionTramites() { cargarVista("/fxml/GestionTramiteView.fxml"); }
    @FXML private void handleGenerarLicencia() { cargarVista("/fxml/GenerarLicenciaView.fxml"); }
    @FXML private void handleGestionUsuarios() { cargarVista("/fxml/GestionUsuarioView.fxml"); }
    @FXML private void handleReportes() { cargarVista("/fxml/ReporteAdminView.fxml"); }
    @FXML private void handleDashboard() { cargarVista("/fxml/DashboardTotalesView.fxml"); }

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