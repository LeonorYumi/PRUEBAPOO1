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

    @FXML private CheckBox chkCopiaCedula, chkCertificadoVotacion, chkExamenTeorico;
    @FXML private CheckBox chkFotos, chkCertificado, chkMultas;
    @FXML private TextArea txtNotas;

    private final TramiteService tramiteService = new TramiteService();
    private Tramite tramiteEncontrado;

    /**
     * Inicializa el controlador y configura el filtro de entrada para la cédula.
     */
    @FXML
    public void initialize() {
        // MEJORA: Solo permite números y máximo 10 caracteres mientras el usuario escribe
        txtBusquedaId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtBusquedaId.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtBusquedaId.getText().length() > 10) {
                txtBusquedaId.setText(txtBusquedaId.getText().substring(0, 10));
            }
        });
    }

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();

            // 1. VALIDACIÓN: Ingrese algo
            if (busqueda.isEmpty()) {
                mostrarAlerta("Atención", "Por favor ingrese una cédula para buscar.", Alert.AlertType.WARNING);
                return;
            }

            // 2. VALIDACIÓN: Exactamente 10 dígitos
            if (busqueda.length() != 10) {
                mostrarAlerta("Cédula Incompleta",
                        "Para realizar la consulta, debe ingresar los 10 dígitos de la cédula correctamente.",
                        Alert.AlertType.WARNING);
                return;
            }

            // 3. BÚSQUEDA: Solo llegamos aquí si pasó la validación de 10 dígitos
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado().toUpperCase());

                // Habilitar botón solo si está aprobado
                btnGenerarLicencia.setDisable(!tramiteEncontrado.getEstado().equalsIgnoreCase("aprobado"));
            } else {
                // Ahora este mensaje solo sale cuando la cédula es de 10 dígitos pero no existe
                mostrarAlerta("Información", "No se encontró ningún trámite registrado con la cédula: " + busqueda, Alert.AlertType.INFORMATION);
                limpiarCampos();
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al consultar la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGuardarRequisitos(ActionEvent event) {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Error", "Debe buscar un trámite antes de guardar requisitos.", Alert.AlertType.ERROR);
            return;
        }
        mostrarAlerta("Éxito", "Requisitos actualizados correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleGuardarNotas(ActionEvent event) {
        if (tramiteEncontrado == null) {
            mostrarAlerta("Error", "No hay un trámite cargado.", Alert.AlertType.ERROR);
            return;
        }
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
                controller.initData(tramiteEncontrado);
            }

            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el módulo de licencias.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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

        if(chkCopiaCedula != null) chkCopiaCedula.setSelected(false);
        if(chkCertificadoVotacion != null) chkCertificadoVotacion.setSelected(false);
        if(chkExamenTeorico != null) chkExamenTeorico.setSelected(false);
        if(chkFotos != null) chkFotos.setSelected(false);
        if(chkCertificado != null) chkCertificado.setSelected(false);
        if(chkMultas != null) chkMultas.setSelected(false);
        if(txtNotas != null) txtNotas.clear();
    }
}