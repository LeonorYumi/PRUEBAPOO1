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
import ui.base.BaseController;
import ui.admin.GenerarLicenciaController;
import java.io.IOException;
import java.net.URL;


public class AnalistaController extends BaseController {

    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contentArea;

    // Implementación obligatoria del metodo abstracto de BaseController (Polimorfismo)
    @Override
    public void limpiarCampos() {
        // El panel principal no tiene campos de texto
    }

    // --- MÉTODOS DE NAVEGACIÓN ---
    // Usamos el mismo metodo cargarVista
    @FXML private void handleIrARegistro() { cargarVista("/fxml/RegistrarSolicitanteView.fxml"); }
    @FXML private void handleIrAVerificar() { cargarVista("/fxml/VerificarRequisitoView.fxml"); }
    @FXML private void handleIrAExamenes() { cargarVista("/fxml/RegistrarExamenView.fxml"); }
    @FXML private void handleIrAGestion() { cargarVista("/fxml/GestionTramiteView.fxml"); }
    @FXML private void handleIrADetalleBusqueda() { cargarVista("/fxml/DetalleTramiteView.fxml"); }
    @FXML private void handleIrAGenerarLicencia() { cargarVista("/fxml/GenerarLicenciaView.fxml"); }


    public void cargarGenerarLicenciaConDatos(Tramite tramiteSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // Obtenemos el controlador de la vista cargada para pasarle el objeto
            GenerarLicenciaController controllerHijo = loader.getController();
            controllerHijo.initData(tramiteSeleccionado);

            setContenido(root);
        } catch (IOException e) {
            // USAMOS EL METODO HEREDADO: mostrarAlerta(titulo, mensaje, tipo)
            mostrarAlerta("Error de Navegación", "No se pudo cargar la vista de licencia.", Alert.AlertType.ERROR);
        }
    }

    private void cargarVista(String ruta) {
        try {
            URL fxmlUrl = getClass().getResource(ruta);
            if (fxmlUrl == null) {
                mostrarAlerta("Error", "Archivo no encontrado: " + ruta, Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            setContenido(root);
        } catch (IOException e) {
            mostrarAlerta("Error de Carga", "Error al abrir la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void setContenido(Parent nodo) {
        if (contentArea != null) {
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
            mostrarAlerta("Error", "No se pudo volver al login.", Alert.AlertType.ERROR);
        }
    }
}