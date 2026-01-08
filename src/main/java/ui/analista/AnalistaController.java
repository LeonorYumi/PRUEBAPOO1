package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Tramite;
import java.io.IOException;
import java.net.URL;

/**
 * Controlador principal de la interfaz del Analista.
 * Gestiona la navegación entre las diferentes funcionalidades del sistema.
 */
public class AnalistaController {

    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contentArea;

    // --- MÉTODOS DE NAVEGACIÓN ---

    @FXML
    private void handleIrARegistro() {
        cargarVista("/fxml/RegistrarSolicitanteView.fxml");
    }

    @FXML
    private void handleIrAVerificar() {
        cargarVista("/fxml/VerificarRequisitoView.fxml");
    }

    @FXML
    private void handleIrAExamenes() {
        cargarVista("/fxml/RegistrarExamenView.fxml");
    }

    @FXML
    private void handleIrAGestion() {
        cargarVista("/fxml/GestionTramiteView.fxml");
    }

    @FXML
    private void handleIrADetalleBusqueda() {
        cargarVista("/fxml/DetalleTramiteView.fxml");
    }

    @FXML
    private void handleIrAGenerarLicencia() {
        // Carga la vista sin datos previos (el usuario deberá buscar el trámite ahí)
        cargarVista("/fxml/GenerarLicenciaView.fxml");
    }

    /**
     * MÉTODO CLAVE: Permite cargar la pantalla de licencia pasando un trámite específico.
     * Útil cuando se viene desde la tabla de Gestión de Trámites.
     */
    public void cargarGenerarLicenciaConDatos(Tramite tramiteSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // Pasamos el objeto Trámite al controlador de la Licencia
            GenerarLicenciaController controllerHijo = loader.getController();
            controllerHijo.initData(tramiteSeleccionado);

            setContenido(root);
            System.out.println("✅ Generar Licencia cargado con trámite: " + tramiteSeleccionado.getId());
        } catch (IOException e) {
            mostrarAlertaError("Error de Navegación", "No se pudo cargar la vista de licencia.");
            e.printStackTrace();
        }
    }

    // --- LÓGICA DE CARGA Y AJUSTE ---

    /**
     * Carga un archivo FXML genérico y lo coloca en el área central.
     */
    private void cargarVista(String ruta) {
        try {
            URL fxmlUrl = getClass().getResource(ruta);
            if (fxmlUrl == null) {
                System.err.println("❌ FXML no encontrado en: " + ruta);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            setContenido(root);
            System.out.println("✅ Vista cargada: " + ruta);
        } catch (IOException e) {
            mostrarAlertaError("Error de Carga", "Error al abrir la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia el contenedor, añade la nueva vista y la ajusta al tamaño disponible.
     */
    private void setContenido(Parent nodo) {
        if (contentArea != null) {
            // Ajuste dinámico de tamaño (Responsive)
            if (nodo instanceof Region region) {
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }
            contentArea.getChildren().setAll(nodo);
        }
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.setScene(new Scene(root));
            stageActual.setTitle("Login - Sistema de Licencias");
            stageActual.centerOnScreen();
        } catch (IOException e) {
            mostrarAlertaError("Error", "No se pudo volver al login.");
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