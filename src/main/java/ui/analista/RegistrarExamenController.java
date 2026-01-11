package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    @FXML
    public void initialize() {
        // MEJORA: Solo permitir números y máximo 10 caracteres en el buscador
        txtBusquedaId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtBusquedaId.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtBusquedaId.getText().length() > 10) {
                txtBusquedaId.setText(txtBusquedaId.getText().substring(0, 10));
            }
        });
    }

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

            // VALIDACIÓN DE 10 DÍGITOS (Requerimiento solicitado)
            if (busqueda.length() != 10 || !busqueda.matches("[0-9]+")) {
                lblNombreCliente.setText("INGRESE LOS 10 DÍGITOS CORRECTAMENTE");
                lblNombreCliente.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                mostrarAlerta("Atención", "La cédula debe tener exactamente 10 dígitos numéricos.", Alert.AlertType.WARNING);
                return;
            }

            // Búsqueda robusta tras pasar la validación
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);

                // Validación de seguridad: Solo permitir exámenes si el trámite está en el estado correcto
                if (!tramiteEncontrado.getEstado().equalsIgnoreCase("en_examenes") &&
                        !tramiteEncontrado.getEstado().equalsIgnoreCase("pendiente")) {
                    mostrarAlerta("Estado no válido", "Este trámite ya está " + tramiteEncontrado.getEstado().toUpperCase(), Alert.AlertType.INFORMATION);
                }

                lblNombreCliente.setText("Solicitante: " + tramiteEncontrado.getNombre());
                lblNombreCliente.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                lblNombreCliente.setText("TRÁMITE NO ENCONTRADO");
                lblNombreCliente.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
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
            if (txtNotaTeorica.getText().isEmpty() || txtNotaPractica.getText().isEmpty()) {
                mostrarAlerta("Campos vacíos", "Debe ingresar ambas notas para calificar.", Alert.AlertType.WARNING);
                return;
            }

            double notaT = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double notaP = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            if (notaT < 0 || notaT > 20 || notaP < 0 || notaP > 20) {
                mostrarAlerta("Rango excedido", "Las notas deben estar entre 0 y 20.", Alert.AlertType.WARNING);
                return;
            }

            String resultadoFinal = (notaT >= 14 && notaP >= 14) ? "APROBADO" : "REPROBADO";
            tramiteService.registrarExamen(tramiteEncontrado.getId(), notaT, notaP);

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
        try {
            // Ajusta la ruta a tu vista de bienvenida analista
            String ruta = "/ui/analista/BienvenidaAnalistaView.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) txtBusquedaId.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            // Si falla la carga del FXML (por si estás usando un StackPane principal)
            System.err.println("Error al navegar: " + e.getMessage());
        }
    }
}