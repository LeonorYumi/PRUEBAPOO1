package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.RequisitoService;
import service.TramiteService;

public class DetalleTramiteController {

    @FXML private TextField txtBusquedaId;
    @FXML private Label lblNombre, lblCedula, lblTipoLicencia, lblEstadoActual;
    @FXML private CheckBox chkCertificadoSalud, chkPagoBanco, chkSinMultas;
    @FXML private TextField txtNotaTeorica, txtNotaPractica;

    private final RequisitoService requisitoService = new RequisitoService();
    private final TramiteService tramiteService = new TramiteService();

    @FXML
    private void handleBuscar() {
        String idStr = txtBusquedaId.getText().trim();
        if (idStr.isEmpty()) {
            mostrarAlerta("Atención", "Por favor ingrese un ID de trámite para buscar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Tramite tramiteEncontrado = tramiteService.buscarTramitePorId(id);

            if (tramiteEncontrado != null) {
                lblNombre.setText(tramiteEncontrado.getNombre());
                lblCedula.setText(tramiteEncontrado.getCedula());
                lblTipoLicencia.setText(tramiteEncontrado.getTipo());
                lblEstadoActual.setText(tramiteEncontrado.getEstado().toUpperCase());
            } else {
                mostrarAlerta("No encontrado", "No se encontró ningún trámite con el ID: " + id);
                limpiarLabels();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID debe ser un número entero.");
        } catch (Exception e) {
            mostrarAlerta("Error de Conexión", "No se pudo consultar la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardarRequisitos() {
        try {
            if (txtBusquedaId.getText().isEmpty()) throw new Exception("Busque un trámite primero.");

            int id = Integer.parseInt(txtBusquedaId.getText());
            requisitoService.guardarRequisitos(
                    id,
                    chkCertificadoSalud.isSelected(),
                    chkPagoBanco.isSelected(),
                    chkSinMultas.isSelected(),
                    "Actualización desde detalle",
                    null
            );
            mostrarAlerta("Éxito", "Requisitos actualizados.");
            handleBuscar();
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void handleGuardarNotas() {
        try {
            if (txtBusquedaId.getText().isEmpty()) throw new Exception("Busque un trámite primero.");

            int id = Integer.parseInt(txtBusquedaId.getText());
            double nt = Double.parseDouble(txtNotaTeorica.getText());
            double np = Double.parseDouble(txtNotaPractica.getText());

            tramiteService.registrarExamen(id, nt, np, null);
            mostrarAlerta("Éxito", "Notas registradas correctamente.");
            handleBuscar();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese notas válidas (números).");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }


    @FXML
    private void handleGenerarLicencia() {
        if (lblEstadoActual.getText().equalsIgnoreCase("APROBADO")) {
            // Lógica para navegar a la pantalla de generación
            AnalistaController main = (AnalistaController) txtBusquedaId.getScene().getRoot().getUserData();
            if (main != null) {
                // Aquí podrías buscar el trámite actual y pasarlo
                mostrarAlerta("Navegación", "Cambiando a pantalla de Generar Licencia...");
            }
        } else {
            mostrarAlerta("Atención", "El trámite debe estar en estado APROBADO para generar la licencia.");
        }
    }


    @FXML
    private void handleRegresar() {
        if (txtBusquedaId.getScene() != null) {
            StackPane contentArea = (StackPane) txtBusquedaId.getScene().lookup("#contentArea");
            if (contentArea != null) contentArea.getChildren().clear();
        }
    }

    private void limpiarLabels() {
        lblNombre.setText("---");
        lblCedula.setText("---");
        lblTipoLicencia.setText("---");
        lblEstadoActual.setText("---");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}