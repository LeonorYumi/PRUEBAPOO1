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
 * Diferencia entre trámites no encontrados y trámites ya procedentes.
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
            // Reset de estado previo
            tramiteEncontrado = null;
            lblNombreCliente.setText("");
            String cedula = txtBusquedaId.getText().trim();

            // 1. Validación de formato de entrada
            if (cedula.length() != 10 || !cedula.matches("[0-9]+")) {
                mostrarAlerta("Error de Formato", "Ingrese los 10 dígitos numéricos de la cédula.", Alert.AlertType.WARNING);
                return;
            }

            // 2. PRIMERA BÚSQUEDA: Trámites en estado 'pendiente' (Listos para validar)
            List<Tramite> pendientes = tramiteService.consultarTramitesReporte(null, null, "pendiente", "Todos", cedula);

            if (pendientes != null && !pendientes.isEmpty()) {
                // CASO EXITOSO: Trámite listo para procesar
                tramiteEncontrado = pendientes.get(0);
                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 14;");
            } else {
                // 3. SEGUNDA BÚSQUEDA: Si no hay pendientes, ver si la cédula existe en CUALQUIER otro estado
                List<Tramite> historial = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);

                if (historial != null && !historial.isEmpty()) {
                    // CASO: El trámite ya pasó esta fase (Ya es PROCEDENTE o RECHAZADO)
                    Tramite actual = historial.get(0);
                    String estadoActual = actual.getEstado().toUpperCase();

                    lblNombreCliente.setText("TRÁMITE YA PROCEDENTE (ESTADO: " + estadoActual + ")");
                    lblNombreCliente.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold; -fx-font-size: 14;");

                    mostrarAlerta("Trámite Procesado",
                            "El solicitante " + actual.getNombre() + " ya registra un trámite en estado: " + estadoActual,
                            Alert.AlertType.INFORMATION);
                } else {
                    // CASO: La cédula no existe en la base de datos
                    lblNombreCliente.setText("TRÁMITE NO ENCONTRADO EN EL SISTEMA");
                    lblNombreCliente.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14;");

                    mostrarAlerta("No Encontrado",
                            "No se encontró ningún registro para la cédula: " + cedula + ".\nVerifique que el solicitante haya sido registrado previamente.",
                            Alert.AlertType.ERROR);
                }

                // Limpiamos variables internas pero dejamos el mensaje en lblNombreCliente visible
                tramiteEncontrado = null;
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al consultar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAprobar() {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Atención", "Debe buscar y seleccionar un trámite pendiente primero.", Alert.AlertType.WARNING);
            return;
        }

        // Validación física de requisitos en la UI
        if (!chkCertificado.isSelected() || !chkPago.isSelected() || !chkSinMultas.isSelected()) {
            mostrarAlerta("Requisitos Incompletos", "No se puede aprobar si no se verifican todos los documentos físicos.", Alert.AlertType.WARNING);
            return;
        }

        procesarTramiteAdaptado();
    }

    @FXML
    private void handleRechazar() {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Atención", "Busque un trámite antes de intentar rechazarlo.", Alert.AlertType.WARNING);
            return;
        }

        if (txtObservaciones.getText().trim().isEmpty()) {
            mostrarAlerta("Observación Requerida", "Debe ingresar el motivo del rechazo en el campo de observaciones.", Alert.AlertType.WARNING);
            return;
        }

        procesarTramiteAdaptado();
    }

    /**
     * Lógica que conecta con el RequisitoService.
     */
    private void procesarTramiteAdaptado() {
        try {
            // El Service usa !multas para determinar procedencia
            boolean sinMultasParaService = !chkSinMultas.isSelected();

            requisitoService.guardarRequisitos(
                    tramiteEncontrado.getId(),
                    chkCertificado.isSelected(),
                    chkPago.isSelected(),
                    sinMultasParaService,
                    txtObservaciones.getText(),
                    null // creadoPor
            );

            mostrarAlerta("Éxito", "Gestión finalizada. El trámite ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error de Guardado", "No se pudo actualizar el trámite: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        limpiarCampos();
        // Lógica opcional para cerrar ventana o cambiar de panel
    }
}