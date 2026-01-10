package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;

import java.io.IOException;
import java.util.List;

/**
 * Este controlador hereda de BaseController para aplicar los principios de POO.
 */
public class DetalleTramiteController extends BaseController {

    @FXML private TextField txtBusquedaId;
    @FXML private Label lblNombre, lblCedula, lblEstadoActual;
    @FXML private Button btnGenerarLicencia;

    private TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    /**
     * Implementación obligatoria del método abstracto (Polimorfismo).
     * Limpia los labels y el campo de búsqueda.
     */
    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombre.setText("Nombre: -");
        lblCedula.setText("Cédula: -");
        lblEstadoActual.setText("Estado: -");
        btnGenerarLicencia.setDisable(true);
    }

    @FXML
    private void handleBuscar() {
        try {
            String cedula = txtBusquedaId.getText().trim();
            if (cedula.isEmpty()) {
                mostrarAlerta("Atención", "Ingrese una cédula para buscar", Alert.AlertType.WARNING);
                return;
            }

            // Consultamos al Service (Abstracción: la UI no sabe de SQL)
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado());

                // Lógica de validación de estado
                String estado = tramiteEncontrado.getEstado().toLowerCase();
                boolean puedePasar = estado.equals("aprobado") || estado.equals("licencia_emitida");

                btnGenerarLicencia.setDisable(!puedePasar);

            } else {
                tramiteEncontrado = null;
                btnGenerarLicencia.setDisable(true);
                // USAMOS EL MÉTODO HEREDADO DEL PADRE
                mostrarAlerta("No encontrado", "No existen trámites para la cédula: " + cedula, Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al consultar la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        if (tramiteEncontrado == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // Pasamos los datos al controlador de la siguiente vista
            // Nota: Aquí se usa el controlador que corresponda a tu paquete
            ui.admin.GenerarLicenciaController controller = loader.getController();
            controller.initData(tramiteEncontrado);

            // Cambiamos el contenido del área central (Navegación dinámica)
            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista de la licencia.", Alert.AlertType.ERROR);
        }
    }

    // El método mostrarAlerta() fue ELIMINADO de aquí porque ya lo tenemos en BaseController.
}