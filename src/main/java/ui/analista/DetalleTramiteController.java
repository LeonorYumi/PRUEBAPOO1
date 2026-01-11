package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;
import java.io.IOException;
import java.util.List;

public class DetalleTramiteController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private Label lblNombre, lblCedula, lblEstadoActual;
    @FXML private Button btnGenerarLicencia;

    private final TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado().toUpperCase());

                // Habilitar botón si el estado permite generar licencia
                btnGenerarLicencia.setDisable(!tramiteEncontrado.getEstado().equalsIgnoreCase("aprobado"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleGenerarLicencia() {
        if (tramiteEncontrado == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // OBTENEMOS EL CONTROLADOR DE LA SIGUIENTE VENTANA
            GenerarLicenciaController controller = loader.getController();

            if (controller != null) {
                // PASAMOS LOS DATOS LIMPIOS (Sin el texto "Nombre: ")
                tramiteEncontrado.setNombre(lblNombre.getText().replace("Nombre: ", "").trim());
                tramiteEncontrado.setCedula(lblCedula.getText().replace("Cédula: ", "").trim());

                // ENVIAR AL OTRO CONTROLADOR
                controller.initData(tramiteEncontrado);
            }

            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombre.setText("Nombre: -");
        lblCedula.setText("Cédula: -");
    }
}