package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import service.RequisitoService;
import service.TramiteService;
import model.Tramite;

public class VerificarRequisitoController {

    @FXML private TextField txtBusquedaId;
    @FXML private CheckBox chkCertificado;
    @FXML private CheckBox chkPago;
    @FXML private CheckBox chkSinMultas;
    @FXML private TextArea txtObservaciones;

    // Label para mostrar el nombre del cliente al buscar
    @FXML private Label lblNombreCliente;

    private RequisitoService requisitoService = new RequisitoService();
    private TramiteService tramiteService = new TramiteService();

    /**
     * Busca el trámite por ID y muestra el nombre del solicitante en pantalla.
     * Si el ID no existe, muestra un error de "No encontrado".
     */
    @FXML
    private void handleBuscar() {
        try {
            // 1. Limpiar el nombre anterior antes de iniciar una nueva búsqueda
            if (lblNombreCliente != null) {
                lblNombreCliente.setText("");
            }

            String textoId = txtBusquedaId.getText().trim();
            if (textoId.isEmpty()) {
                mostrarAlerta("Atención", "Ingrese un ID de trámite.");
                return;
            }

            int id = Integer.parseInt(textoId);
            Tramite t = tramiteService.buscarTramitePorId(id);

            // 2. VALIDACIÓN: ¿El trámite existe en la base de datos?
            if (t == null) {
                if (lblNombreCliente != null) {
                    lblNombreCliente.setText("ID NO ENCONTRADO");
                    lblNombreCliente.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
                mostrarAlerta("Error", "El trámite #" + id + " no existe en la base de datos.");
                return;
            }

            // 3. VALIDACIÓN: El trámite existe, verificar si tiene nombre vinculado
            if (t.getNombre() != null && !t.getNombre().isEmpty()) {
                if (lblNombreCliente != null) {
                    lblNombreCliente.setText("Solicitante: " + t.getNombre());
                    lblNombreCliente.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 14px;");
                }
                System.out.println("Trámite encontrado: " + t.getNombre());
            } else {
                if (lblNombreCliente != null) {
                    lblNombreCliente.setText("Trámite #" + id + " sin nombre vinculado.");
                    lblNombreCliente.setStyle("-fx-text-fill: #f39c12;");
                }
                mostrarAlerta("Aviso", "El trámite existe pero no tiene un solicitante válido asignado.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID debe ser un número válido.");
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

    @FXML
    private void handleRegresar() {
        if (txtBusquedaId.getScene() != null) {
            StackPane contentArea = (StackPane) txtBusquedaId.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
            }
        }
    }

    private void procesarTramite() {
        try {
            if (txtBusquedaId.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Debe ingresar o buscar un ID de trámite primero.");
                return;
            }

            int idTramiteActual = Integer.parseInt(txtBusquedaId.getText().trim());

            requisitoService.guardarRequisitos(
                    idTramiteActual,
                    chkCertificado.isSelected(),
                    chkPago.isSelected(),
                    chkSinMultas.isSelected(),
                    txtObservaciones.getText(),
                    null
            );

            mostrarAlerta("Éxito", "Requisitos procesados correctamente.");
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID del trámite debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error de Base de Datos", e.getMessage());
        }
    }

    private void limpiarCampos() {
        if (txtBusquedaId != null) txtBusquedaId.clear();
        if (lblNombreCliente != null) lblNombreCliente.setText("");
        chkCertificado.setSelected(false);
        chkPago.setSelected(false);
        chkSinMultas.setSelected(false);
        txtObservaciones.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}