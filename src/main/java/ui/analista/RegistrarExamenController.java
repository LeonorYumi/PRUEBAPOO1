package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import java.util.List;

public class RegistrarExamenController {

    @FXML private TextField txtBusquedaId;
    @FXML private TextField txtNotaTeorica;
    @FXML private TextField txtNotaPractica;
    @FXML private Label lblNombreCliente;

    private final TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();
            lblNombreCliente.setText(""); // Limpiar previo
            tramiteEncontrado = null;

            if (busqueda.isEmpty()) {
                mostrarAlerta("Atención", "Ingrese una cédula o ID para buscar.");
                return;
            }

            // Lógica Dual: Cédula (10) o ID (menos de 10)
            if (busqueda.length() == 10 && busqueda.matches("[0-9]+")) {
                List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);
                if (resultados != null && !resultados.isEmpty()) tramiteEncontrado = resultados.get(0);
            } else if (busqueda.matches("[0-9]+")) {
                tramiteEncontrado = tramiteService.buscarTramitePorId(Integer.parseInt(busqueda));
            }

            if (tramiteEncontrado != null) {
                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #27ae60;");
            } else {
                lblNombreCliente.setText("No se encontró el trámite.");
                lblNombreCliente.setStyle("-fx-text-fill: #c0392b;");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error en la búsqueda: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardarResultados() {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Error", "Debe buscar y encontrar un trámite primero.");
            return;
        }

        try {
            double notaT = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double notaP = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            tramiteService.registrarExamen(tramiteEncontrado.getId(), notaT, notaP);

            mostrarAlerta("Éxito", "Notas guardadas para: " + tramiteEncontrado.getNombre());
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta("Error", "Verifique las notas: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegresar() {
        StackPane contentArea = (StackPane) txtNotaTeorica.getScene().lookup("#contentArea");
        if (contentArea != null) contentArea.getChildren().clear();
    }

    private void limpiarCampos() {
        txtBusquedaId.clear();
        txtNotaTeorica.clear();
        txtNotaPractica.clear();
        lblNombreCliente.setText("");
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