package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import service.RequisitoService;
import service.TramiteService;
import model.Tramite;
import java.util.List;

public class VerificarRequisitoController {

    @FXML private TextField txtBusquedaId; // Ahora lo usaremos para la Cédula
    @FXML private CheckBox chkCertificado;
    @FXML private CheckBox chkPago;
    @FXML private CheckBox chkSinMultas;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblNombreCliente;

    private RequisitoService requisitoService = new RequisitoService();
    private TramiteService tramiteService = new TramiteService();

    // Variable para guardar el trámite encontrado y usarlo al aprobar/rechazar
    private Tramite tramiteEncontrado;

    @FXML
    private void handleBuscar() {
        try {
            // 1. Limpiar datos previos
            tramiteEncontrado = null;
            lblNombreCliente.setText("");

            String cedula = txtBusquedaId.getText().trim();

            // 2. VALIDACIÓN BÁSICA (Igual que en el Service)
            if (cedula.length() != 10 || !cedula.matches("[0-9]+")) {
                mostrarAlerta("Error de Cédula", "Ingrese una cédula válida de 10 dígitos numéricos.");
                return;
            }

            // 3. BUSCAR POR CÉDULA
            // Usamos el método que ya tienes en el service para reportes que filtra por cédula
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);

                // Mostrar el nombre del solicitante en el Label
                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");

            } else {
                lblNombreCliente.setText("CÉDULA NO ENCONTRADA");
                lblNombreCliente.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                mostrarAlerta("No encontrado", "No existe un trámite activo para la cédula: " + cedula);
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void handleAprobar() {
        procesarTramite();
    }

    @FXML
    private void handleRechazar() {
        if (txtObservaciones.getText().trim().isEmpty()) {
            mostrarAlerta("Atención", "Debe ingresar una observación para el rechazo.");
            return;
        }
        procesarTramite();
    }

    private void procesarTramite() {
        try {
            // Validar que primero se haya buscado a alguien
            if (tramiteEncontrado == null) {
                mostrarAlerta("Error", "Primero debe buscar y encontrar un solicitante por cédula.");
                return;
            }

            // Usamos el ID del trámite que encontramos en la búsqueda por cédula
            requisitoService.guardarRequisitos(
                    tramiteEncontrado.getId(),
                    chkCertificado.isSelected(),
                    chkPago.isSelected(),
                    chkSinMultas.isSelected(),
                    txtObservaciones.getText(),
                    null
            );

            mostrarAlerta("Éxito", "Requisitos de " + tramiteEncontrado.getNombre() + " procesados.");
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void handleRegresar() {
        if (txtBusquedaId.getScene() != null) {
            StackPane contentArea = (StackPane) txtBusquedaId.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
            }
        }
    }

    private void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombreCliente.setText("");
        chkCertificado.setSelected(false);
        chkPago.setSelected(false);
        chkSinMultas.setSelected(false);
        txtObservaciones.clear();
        tramiteEncontrado = null;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}