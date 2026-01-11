package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;
import java.util.List;

public class RegistrarExamenController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private TextField txtNotaTeorica;
    @FXML private TextField txtNotaPractica;
    @FXML private Label lblNombreCliente;

    private final TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

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
            if (busqueda.isEmpty()) {
                mostrarAlerta("Atención", "Ingrese una cédula o ID para buscar.", Alert.AlertType.WARNING);
                return;
            }

            // Búsqueda robusta
            if (busqueda.length() == 10 && busqueda.matches("[0-9]+")) {
                List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);
                if (resultados != null && !resultados.isEmpty()) tramiteEncontrado = resultados.get(0);
            } else if (busqueda.matches("[0-9]+")) {
                tramiteEncontrado = tramiteService.buscarTramitePorId(Integer.parseInt(busqueda));
            }

            if (tramiteEncontrado != null) {
                // Validación de seguridad: Solo permitir exámenes si el trámite está en el estado correcto
                if (!tramiteEncontrado.getEstado().equalsIgnoreCase("en_examenes") &&
                        !tramiteEncontrado.getEstado().equalsIgnoreCase("pendiente")) {
                    mostrarAlerta("Estado no válido", "Este trámite ya está " + tramiteEncontrado.getEstado().toUpperCase(), Alert.AlertType.INFORMATION);
                }

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
            // 1. Obtención y validación de notas
            double notaT = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double notaP = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            if (notaT < 0 || notaT > 20 || notaP < 0 || notaP > 20) {
                mostrarAlerta("Rango excedido", "Las notas deben estar entre 0 y 20.", Alert.AlertType.WARNING);
                return;
            }

            // 2. Aplicación de la Regla de Negocio 4.3
            // "si notas ≥ 14 → estado = aprobado, sino reprobado"
            String resultadoFinal = (notaT >= 14 && notaP >= 14) ? "APROBADO" : "REPROBADO";

            // 3. Persistencia
            tramiteService.registrarExamen(tramiteEncontrado.getId(), notaT, notaP);

            // 4. Feedback visual al usuario
            String mensaje = String.format("Notas: T:%.2f, P:%.2f\nResultado: %s", notaT, notaP, resultadoFinal);

            if (resultadoFinal.equals("APROBADO")) {
                mostrarAlerta("Trámite Aprobado", mensaje, Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Trámite Reprobado", mensaje + "\nEl solicitante debe repetir el proceso.", Alert.AlertType.WARNING);
            }

            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Ingrese números válidos (ej: 15.50).", Alert.AlertType.WARNING);
        } catch (Exception e) {
            mostrarAlerta("Error al Guardar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        StackPane contentArea = (StackPane) txtNotaTeorica.getScene().lookup("#contentArea");
        if (contentArea != null) contentArea.getChildren().clear();
    }
}