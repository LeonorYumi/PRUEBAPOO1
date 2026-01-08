package ui.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminController {

    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contentArea;

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("No se encontró el archivo FXML: " + fxmlPath);
            }
            Parent view = loader.load();

            // 1. Limpiamos el área y cargamos la nueva vista
            contentArea.getChildren().setAll(view);

            // 2. LA SOLUCIÓN DEFINITIVA:
            // Forzamos el foco a un elemento que no sea un botón del menú.
            // Esto quita automáticamente el resaltado azul de "Gestión de Usuarios".
            contentArea.setFocusTraversable(true);
            contentArea.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo cargar la vista.");
        }
    }

    // --- OPERACIONES COMPARTIDAS (Analista + Admin) ---
    @FXML private void handleRegistrarSolicitante() { cargarVista("/fxml/RegistrarSolicitanteView.fxml"); }
    @FXML private void handleVerificarRequisitos() { cargarVista("/fxml/VerificarRequisitoView.fxml"); }
    @FXML private void handleRegistrarExamenes() { cargarVista("/fxml/RegistrarExamenView.fxml"); }
    @FXML private void handleGestionTramites() { cargarVista("/fxml/GestionTramiteView.fxml"); }

    /**
     * Acceso directo a la cola de impresión de licencias (Aprobados).
     */
    @FXML private void handleGenerarLicencia() {
        cargarVista("/fxml/GenerarLicenciaView.fxml");
    }

    // --- OPERACIONES EXCLUSIVAS DE ADMINISTRADOR ---
    @FXML private void handleGestionUsuarios() { cargarVista("/fxml/GestionUsuarioView.fxml"); }
    @FXML private void handleReportes() { cargarVista("/fxml/ReporteAdminView.fxml"); }

    /**
     * Carga el Dashboard de estadísticas con el gráfico de pastel.
     */
    @FXML
    private void handleDashboard() {
        cargarVista("/fxml/DashboardTotalesView.fxml");
    }

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
            e.printStackTrace();
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}