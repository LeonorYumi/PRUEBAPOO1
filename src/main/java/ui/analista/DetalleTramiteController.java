package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import java.io.IOException;
import java.util.List;

public class DetalleTramiteController {

    @FXML private TextField txtBusquedaId;
    @FXML private Label lblNombre, lblCedula, lblEstadoActual;
    @FXML private Button btnGenerarLicencia;

    private TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    @FXML
    private void handleBuscar() {
        try {
            String cedula = txtBusquedaId.getText().trim();
            if (cedula.isEmpty()) return;

            // Busca el tramite en la base de datos
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado());

                // --- SOLUCIÓN AL BOTÓN DESHABILITADO ---
                // Permitimos el botón si está 'aprobado' O si ya es 'licencia_emitida'
                // Esto permite entrar a la siguiente pantalla aunque ya se haya generado antes
                String estado = tramiteEncontrado.getEstado().toLowerCase();
                boolean puedePasar = estado.equals("aprobado") || estado.equals("licencia_emitida");

                btnGenerarLicencia.setDisable(!puedePasar);

            } else {
                tramiteEncontrado = null;
                btnGenerarLicencia.setDisable(true);
                mostrarAlerta("No encontrado", "No existen trámites para la cédula: " + cedula);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al consultar la base de datos.");
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        if (tramiteEncontrado == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));

            // Configuración del controlador para evitar errores de casteo
            loader.setControllerFactory(type -> {
                if (type == ui.analista.GenerarLicenciaController.class || type == ui.admin.GenerarLicenciaController.class) {
                    return new ui.analista.GenerarLicenciaController();
                }
                try { return type.getDeclaredConstructor().newInstance(); } catch (Exception e) { throw new RuntimeException(e); }
            });

            Parent root = loader.load();

            // Pasamos el tramite al controlador de la siguiente vista
            ui.analista.GenerarLicenciaController controller = loader.getController();
            controller.initData(tramiteEncontrado);

            // Cambiamos el contenido del área central
            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de la licencia.");
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}