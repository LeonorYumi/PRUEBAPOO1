package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import service.TramiteService;

public class RegistrarExamenController {

    @FXML private TextField txtBusquedaId;
    @FXML private TextField txtNotaTeorica;
    @FXML private TextField txtNotaPractica;

    private final TramiteService tramiteService = new TramiteService();

    @FXML
    private void handleGuardarResultados() {
        try {
            // Validación mínima de UI: campos no vacíos
            if (txtBusquedaId.getText().trim().isEmpty() ||
                    txtNotaTeorica.getText().trim().isEmpty() ||
                    txtNotaPractica.getText().trim().isEmpty()) {
                mostrarAlerta("Campos Requeridos", "Por favor, complete todos los campos.");
                return;
            }

            // Captura de datos
            int id = Integer.parseInt(txtBusquedaId.getText().trim());
            double notaT = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double notaP = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));


            tramiteService.registrarExamen(id, notaT, notaP, null);

            mostrarAlerta("Éxito", "Los resultados han sido procesados por el sistema.");
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El ID y las notas deben ser valores numéricos.");
        } catch (Exception e) {
            // Aquí el Service lanzará sus propias excepciones (ej: "Nota fuera de rango")
            // y el Controller simplemente las muestra al usuario.
            mostrarAlerta("Validación del Sistema", e.getMessage());
        }
    }


    @FXML
    private void handleRegresar() {
        if (txtNotaTeorica.getScene() != null) {
            StackPane contentArea = (StackPane) txtNotaTeorica.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
            }
        }
    }

    private void limpiarCampos() {
        if (txtBusquedaId != null) txtBusquedaId.clear();
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