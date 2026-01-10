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
            // 1. Validación de campos vacíos
            if (txtBusquedaId.getText().trim().isEmpty() ||
                    txtNotaTeorica.getText().trim().isEmpty() ||
                    txtNotaPractica.getText().trim().isEmpty()) {
                mostrarAlerta("Campos Requeridos", "Por favor, complete todos los campos.");
                return;
            }

            // 2. Captura y conversión de datos
            int id = Integer.parseInt(txtBusquedaId.getText().trim());
            double notaT = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double notaP = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            // 3. Llamada al servicio (Corregido: 3 parámetros coincidiendo con el Service)
            tramiteService.registrarExamen(id, notaT, notaP);

            mostrarAlerta("Éxito", "Los resultados han sido procesados y el estado del trámite actualizado.");
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El ID debe ser entero y las notas valores numéricos.");
        } catch (Exception e) {
            // Muestra errores de lógica (ej: notas fuera de rango o ID inexistente)
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