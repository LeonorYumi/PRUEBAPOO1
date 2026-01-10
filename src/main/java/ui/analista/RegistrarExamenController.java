package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;
import java.util.List;

/**
 * Controlador para registrar las notas de los exámenes.
 * Hereda de BaseController para estandarizar el manejo de la interfaz.
 */
public class RegistrarExamenController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private TextField txtNotaTeorica;
    @FXML private TextField txtNotaPractica;
    @FXML private Label lblNombreCliente;

    private final TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    /**
     * Implementación obligatoria (Polimorfismo).
     * El padre dice qué hacer (limpiar), el hijo decide cómo (estos campos).
     */
    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        txtNotaTeorica.clear();
        txtNotaPractica.clear();
        lblNombreCliente.setText("");
        tramiteEncontrado = null;
    }

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();
            lblNombreCliente.setText("");
            tramiteEncontrado = null;

            if (busqueda.isEmpty()) {
                mostrarAlerta("Atención", "Ingrese una cédula o ID para buscar.", Alert.AlertType.WARNING);
                return;
            }

            // Lógica Dual: Busca por cédula (10 dígitos) o por ID directamente
            if (busqueda.length() == 10 && busqueda.matches("[0-9]+")) {
                List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);
                if (resultados != null && !resultados.isEmpty()) tramiteEncontrado = resultados.get(0);
            } else if (busqueda.matches("[0-9]+")) {
                tramiteEncontrado = tramiteService.buscarTramitePorId(Integer.parseInt(busqueda));
            }

            if (tramiteEncontrado != null) {
                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                lblNombreCliente.setText("No se encontró el trámite.");
                lblNombreCliente.setStyle("-fx-text-fill: #c0392b;");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGuardarResultados() {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Error", "Debe encontrar un trámite primero.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Conversión de datos (Encapsulamiento de lógica de negocio en el Service)
            double notaT = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double notaP = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            tramiteService.registrarExamen(tramiteEncontrado.getId(), notaT, notaP);

            mostrarAlerta("Éxito", "Notas guardadas para: " + tramiteEncontrado.getNombre(), Alert.AlertType.INFORMATION);
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Asegúrese de ingresar números válidos en las notas.", Alert.AlertType.WARNING);
        } catch (Exception e) {
            mostrarAlerta("Error al Guardar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        // Navegación para limpiar el área central
        StackPane contentArea = (StackPane) txtNotaTeorica.getScene().lookup("#contentArea");
        if (contentArea != null) contentArea.getChildren().clear();
    }
}