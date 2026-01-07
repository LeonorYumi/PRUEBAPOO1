package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class RegistrarExamenController {

    @FXML private TextField txtNotaTeorica;
    @FXML private TextField txtNotaPractica;

    @FXML
    private void handleGuardarResultados() {
        try {
            // Convertir texto a números
            double notaTeorica = Double.parseDouble(txtNotaTeorica.getText());
            double notaPractica = Double.parseDouble(txtNotaPractica.getText());

            // Validación de rango (0-20)
            if (notaTeorica < 0 || notaTeorica > 20 || notaPractica < 0 || notaPractica > 20) {
                mostrarAlerta("Error", "Las notas deben estar entre 0 y 20.");
                return;
            }

            // Lógica de aprobación (≥ 14)
            if (notaTeorica >= 14 && notaPractica >= 14) {
                mostrarAlerta("Resultado", "ESTADO: APROBADO.\nEl solicitante puede obtener su licencia.");
            } else {
                mostrarAlerta("Resultado", "ESTADO: REPROBADO.\nDebe repetir los exámenes.");
            }

            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor, ingrese solo números válidos en las notas.");
        }
    }

    @FXML
    private void handleRegresar() {
        // Busca el StackPane central para limpiar la vista
        StackPane contentArea = (StackPane) txtNotaTeorica.getScene().lookup("#contentArea");
        if (contentArea != null) {
            contentArea.getChildren().clear();
        }
    }

    private void limpiarCampos() {
        txtNotaTeorica.clear();
        txtNotaPractica.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}