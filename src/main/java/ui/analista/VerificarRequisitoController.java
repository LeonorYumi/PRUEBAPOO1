package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import ui.base.BaseController;
import service.RequisitoService;
import service.TramiteService;
import model.Tramite;
import java.util.List;

/**
 * Controlador para la validación de documentos y requisitos.
 * Extiende de BaseController para cumplir con el pilar de Herencia.
 */
public class VerificarRequisitoController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private CheckBox chkCertificado, chkPago, chkSinMultas;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblNombreCliente;

    private RequisitoService requisitoService = new RequisitoService();
    private TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    /**
     * Implementación del método abstracto de BaseController (Polimorfismo).
     * Define cómo se limpia específicamente esta pantalla.
     */
    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombreCliente.setText("");
        chkCertificado.setSelected(false);
        chkPago.setSelected(false);
        chkSinMultas.setSelected(false);
        txtObservaciones.clear();
        tramiteEncontrado = null;
    }

    @FXML
    private void handleBuscar() {
        try {
            tramiteEncontrado = null;
            lblNombreCliente.setText("");
            String cedula = txtBusquedaId.getText().trim();

            // Validación de formato (Abstracción de reglas de negocio)
            if (cedula.length() != 10 || !cedula.matches("[0-9]+")) {
                mostrarAlerta("Error de Cédula", "Ingrese 10 dígitos numéricos.", Alert.AlertType.WARNING);
                return;
            }

            // Buscamos el trámite a través del Service
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            } else {
                lblNombreCliente.setText("CÉDULA NO ENCONTRADA");
                lblNombreCliente.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                mostrarAlerta("No encontrado", "No existe un trámite para la cédula: " + cedula, Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAprobar() {
        procesarTramite();
    }

    @FXML
    private void handleRechazar() {
        if (txtObservaciones.getText().trim().isEmpty()) {
            mostrarAlerta("Atención", "Debe ingresar una observación para el rechazo.", Alert.AlertType.WARNING);
            return;
        }
        procesarTramite();
    }

    /**
     * Lógica centralizada para procesar el trámite (Aprobar/Rechazar).
     */
    private void procesarTramite() {
        try {
            if (tramiteEncontrado == null) {
                mostrarAlerta("Error", "Primero debe encontrar un solicitante.", Alert.AlertType.ERROR);
                return;
            }

            // El Service se encarga de la persistencia (Abstracción)
            requisitoService.guardarRequisitos(
                    tramiteEncontrado.getId(),
                    chkCertificado.isSelected(),
                    chkPago.isSelected(),
                    chkSinMultas.isSelected(),
                    txtObservaciones.getText(),
                    null
            );

            mostrarAlerta("Éxito", "Requisitos procesados para: " + tramiteEncontrado.getNombre(), Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error de Proceso", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        if (txtBusquedaId.getScene() != null) {
            StackPane contentArea = (StackPane) txtBusquedaId.getScene().lookup("#contentArea");
            if (contentArea != null) contentArea.getChildren().clear();
        }
    }
}