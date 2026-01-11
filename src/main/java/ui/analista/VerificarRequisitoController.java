package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.base.BaseController;
import service.RequisitoService;
import service.TramiteService;
import model.Tramite;
import java.util.List;

/**
 * Controlador para la validación de documentos y requisitos.
 * Adaptado para funcionar con RequisitoService sin modificar su firma.
 */
public class VerificarRequisitoController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private CheckBox chkCertificado, chkPago, chkSinMultas;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblNombreCliente;

    private RequisitoService requisitoService = new RequisitoService();
    private TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

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

            if (cedula.length() != 10 || !cedula.matches("[0-9]+")) {
                mostrarAlerta("Error", "Ingrese 10 dígitos numéricos.", Alert.AlertType.WARNING);
                return;
            }

            // Buscamos trámites que estén en estado 'pendiente'
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "pendiente", "Todos", cedula);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            } else {
                lblNombreCliente.setText("TRÁMITE NO ENCONTRADO O YA PROCESADO");
                lblNombreCliente.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAprobar() {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Atención", "Busque un solicitante primero.", Alert.AlertType.WARNING);
            return;
        }

        // REGLA: Para aprobar manualmente, los 3 checks DEBEN estar marcados en la UI
        if (!chkCertificado.isSelected() || !chkPago.isSelected() || !chkSinMultas.isSelected()) {
            mostrarAlerta("Incompleto", "No se puede aprobar si faltan requisitos físicos.", Alert.AlertType.WARNING);
            return;
        }

        procesarTramiteAdaptado();
    }

    @FXML
    private void handleRechazar() {
        if (tramiteEncontrado == null) return;

        if (txtObservaciones.getText().trim().isEmpty()) {
            mostrarAlerta("Atención", "Debe ingresar una observación para el rechazo.", Alert.AlertType.WARNING);
            return;
        }

        // Al llamar a procesar con algún checkbox desmarcado,
        // la lógica interna del Service pondrá el estado "rechazado".
        procesarTramiteAdaptado();
    }

    /**
     * Lógica que conecta con el Service respetando los tipos de datos originales.
     */
    private void procesarTramiteAdaptado() {
        try {
            // Invertimos el valor de chkSinMultas para que el Service (que usa !multas)
            // funcione correctamente con el texto de nuestra interfaz.
            boolean valorParaService = !chkSinMultas.isSelected();

            requisitoService.guardarRequisitos(
                    tramiteEncontrado.getId(),
                    chkCertificado.isSelected(),
                    chkPago.isSelected(),
                    valorParaService,
                    txtObservaciones.getText(),
                    null // Se envía null para el parámetro 'creadoPor' (Integer)
            );

            mostrarAlerta("Éxito", "Proceso completado. El estado del trámite se ha actualizado.", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        limpiarCampos();
        // Aquí puedes añadir la lógica para volver al panel de bienvenida si lo deseas
    }
}