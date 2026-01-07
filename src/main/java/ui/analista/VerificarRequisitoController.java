package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

public class VerificarRequisitoController {

    @FXML private CheckBox chkCertificado;
    @FXML private CheckBox chkPago;
    @FXML private CheckBox chkSinMultas;
    @FXML private TextArea txtObservaciones;

    @FXML
    private void handleAprobar() {
        // Validación: Todos los checkboxes deben estar marcados
        if (chkCertificado.isSelected() && chkPago.isSelected() && chkSinMultas.isSelected()) {
            mostrarAlerta("Éxito", "Requisitos verificados. Estado: EN_EXÁMENES.");
            // Aquí iría la lógica para actualizar en Base de Datos
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "Debe cumplir todos los requisitos para aprobar.");
        }
    }

    @FXML
    private void handleRechazar() {
        if (txtObservaciones.getText().trim().isEmpty()) {
            mostrarAlerta("Atención", "Debe ingresar una observación para el rechazo.");
        } else {
            mostrarAlerta("Rechazado", "Trámite rechazado por falta de requisitos.");
            limpiarCampos();
        }
    }

    @FXML
    private void handleRegresar() {
        // Esta lógica depende de si quieres limpiar el área central
        StackPane contentArea = (StackPane) chkCertificado.getScene().lookup("#contentArea");
        if (contentArea != null) {
            contentArea.getChildren().clear();
        }
    }

    private void limpiarCampos() {
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