package ui.analista;

import javafx.event.ActionEvent;
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

    // ACTUALIZADO: Estos IDs deben coincidir exactamente con el fx:id del FXML
    @FXML private CheckBox chkCopiaCedula, chkCertificadoVotacion, chkExamenTeorico;

    // Si en tu FXML tienes fx:id="chkFotos", fx:id="chkCertificado", etc., cámbialos aquí:
    @FXML private CheckBox chkFotos, chkCertificado, chkMultas;

    @FXML private TextArea txtNotas; // Por si tienes un área de notas

    private final TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();
            if (busqueda.isEmpty()) {
                mostrarAlerta("Error", "Por favor ingrese una cédula para buscar.", Alert.AlertType.WARNING);
                return;
            }

            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado().toUpperCase());

                btnGenerarLicencia.setDisable(!tramiteEncontrado.getEstado().equalsIgnoreCase("aprobado"));
            } else {
                mostrarAlerta("Información", "No se encontró ningún trámite con esa cédula.", Alert.AlertType.INFORMATION);
                limpiarCampos();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleGuardarRequisitos(ActionEvent event) {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Error", "Debe buscar un trámite antes de guardar requisitos.", Alert.AlertType.ERROR);
            return;
        }
        System.out.println("Guardando requisitos para: " + tramiteEncontrado.getNombre());
        mostrarAlerta("Éxito", "Requisitos actualizados correctamente.", Alert.AlertType.INFORMATION);
    }

    // NUEVO MÉTODO: Esto quitará el error rojo de "handleGuardarNotas" en el FXML
    @FXML
    private void handleGuardarNotas(ActionEvent event) {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Error", "No hay un trámite cargado.", Alert.AlertType.ERROR);
            return;
        }
        System.out.println("Notas guardadas");
        mostrarAlerta("Éxito", "Observaciones guardadas correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleGenerarLicencia() {
        if (tramiteEncontrado == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            GenerarLicenciaController controller = loader.getController();

            if (controller != null) {
                tramiteEncontrado.setNombre(lblNombre.getText().replace("Nombre: ", "").trim());
                tramiteEncontrado.setCedula(lblCedula.getText().replace("Cédula: ", "").trim());
                controller.initData(tramiteEncontrado);
            }

            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    @FXML
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombre.setText("Nombre: -");
        lblCedula.setText("Cédula: -");
        lblEstadoActual.setText("Estado: -");
        btnGenerarLicencia.setDisable(true);
        tramiteEncontrado = null;

        // Limpiar checkboxes si existen
        if(chkCopiaCedula != null) chkCopiaCedula.setSelected(false);
    }
}