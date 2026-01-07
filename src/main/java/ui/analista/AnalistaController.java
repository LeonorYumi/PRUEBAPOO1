package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class AnalistaController {

    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contentArea;

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sistema de Licencias - Login");
            stage.setScene(new Scene(root));
            stage.show();

            if (btnCerrarSesion != null && btnCerrarSesion.getScene() != null) {
                ((Stage) btnCerrarSesion.getScene().getWindow()).close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }

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
        cargarVista("/fxml/GenerarLicenciaView.fxml");
    }

    /**
     * Método mejorado para cargar vistas.
     * Se añade lógica para que la vista cargada se ajuste al tamaño del contenedor.
     */
    private void cargarVista(String ruta) {
        try {
            System.out.println("Intentando cargar: " + ruta);
            URL fxmlUrl = getClass().getResource(ruta);

            if (fxmlUrl == null) {
                System.err.println("❌ ERROR: No se encontró el archivo FXML en: " + ruta);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent fxml = loader.load();

            if (contentArea != null) {
                // Mejora: Si la vista cargada es una Region (Pane, VBox, etc.),
                // forzamos que use el ancho/alto del StackPane
                if (fxml instanceof Region) {
                    ((Region) fxml).prefWidthProperty().bind(contentArea.widthProperty());
                    ((Region) fxml).prefHeightProperty().bind(contentArea.heightProperty());
                }

                contentArea.getChildren().setAll(fxml);
                System.out.println("✅ Vista cargada exitosamente: " + ruta);
            } else {
                System.err.println("❌ ERROR: El contenedor 'contentArea' es NULL.");
            }
        } catch (IOException e) {
            System.err.println("❌ ERROR CRÍTICO al cargar la vista: " + ruta);
            e.printStackTrace();
        }
    }
}