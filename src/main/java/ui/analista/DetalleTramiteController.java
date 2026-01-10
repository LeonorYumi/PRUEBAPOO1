package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;

import java.io.IOException;
import java.util.List;

/**
 * Este controlador hereda de BaseController para aplicar los principios de POO.
 */
public class DetalleTramiteController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private Label lblNombre, lblCedula, lblEstadoActual;
    @FXML private Button btnGenerarLicencia;

    private TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    /**
     * Implementación obligatoria del método abstracto.
     */
    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombre.setText("Nombre: -");
        lblCedula.setText("Cédula: -");
        lblEstadoActual.setText("Estado: -");
        btnGenerarLicencia.setDisable(true);
    }

    @FXML
    private void handleBuscar() {
        try {
            String cedula = txtBusquedaId.getText().trim();
            if (cedula.isEmpty()) {
                mostrarAlerta("Atención", "Ingrese una cédula para buscar", Alert.AlertType.WARNING);
                return;
            }

            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado());

                String estado = tramiteEncontrado.getEstado().toLowerCase();
                boolean puedePasar = estado.equals("aprobado") || estado.equals("licencia_emitida");

                btnGenerarLicencia.setDisable(!puedePasar);

            } else {
                tramiteEncontrado = null;
                btnGenerarLicencia.setDisable(true);
                mostrarAlerta("No encontrado", "No existen trámites para la cédula: " + cedula, Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al consultar la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        if (tramiteEncontrado == null) return;

        try {
            // 1. Cargar el FXML de la vista Generar Licencia
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // 2. OBTENER EL CONTROLADOR (Corregido a ui.analista para evitar ClassCastException)
            ui.analista.GenerarLicenciaController controller = loader.getController();

            // 3. Pasar los datos al nuevo controlador
            if (controller != null) {
                controller.initData(tramiteEncontrado);
            }

            // 4. Cambiamos el contenido del área central
            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }

        } catch (ClassCastException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Tipo", "El controlador del FXML no coincide con la clase esperada.", Alert.AlertType.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Carga", "No se pudo cargar la vista de la licencia.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}