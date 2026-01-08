package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import service.RequisitoService;
import service.TramiteService; // Necesitarás este servicio
import model.Tramite;

public class VerificarRequisitoController {

    @FXML private TextField txtBusquedaId;
    @FXML private CheckBox chkCertificado;
    @FXML private CheckBox chkPago;
    @FXML private CheckBox chkSinMultas;
    @FXML private TextArea txtObservaciones;

    // Labels opcionales si quieres mostrar el nombre del cliente al buscar
    @FXML private Label lblNombreCliente;

    private RequisitoService requisitoService = new RequisitoService();
    private TramiteService tramiteService = new TramiteService(); // Para buscar datos

    /**
     * AÑADE ESTE MÉTODO: Es vital para cargar los datos antes de validar
     */
    @FXML
    private void handleBuscar() {
        try {
            if (txtBusquedaId.getText().trim().isEmpty()) {
                mostrarAlerta("Atención", "Ingrese un ID de trámite.");
                return;
            }
            int id = Integer.parseInt(txtBusquedaId.getText().trim());
            Tramite t = tramiteService.buscarTramitePorId(id);

            if (t != null) {
                // Aquí podrías marcar los checks si ya existen en la BD
                // O mostrar el nombre del cliente para confirmar
                System.out.println("Trámite encontrado: " + t.getNombre());
            } else {
                mostrarAlerta("Error", "No existe un trámite con ese ID.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "ID no válido.");
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
            // Este catch capturará el error de "Unknown column 'tipo_licencia'"
            // si aún no has corregido la base de datos.
            mostrarAlerta("Error de Base de Datos", e.getMessage());
        }
    }

    private void limpiarCampos() {
        if (txtBusquedaId != null) txtBusquedaId.clear();
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