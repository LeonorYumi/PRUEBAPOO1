package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import service.RequisitoService;
import ui.base.BaseController;
import java.io.IOException;
import java.util.List;

public class DetalleTramiteController extends BaseController {

    @FXML private TextField txtBusquedaId, txtNotaTeorica, txtNotaPractica;
    @FXML private Label lblNombre, lblCedula, lblEstadoActual;
    @FXML private CheckBox chkFotos, chkCertificado, chkMultas;
    @FXML private Button btnGenerarLicencia, btnGuardarReq, btnGuardarNotas;

    private final TramiteService tramiteService = new TramiteService();
    private final RequisitoService requisitoService = new RequisitoService();
    private Tramite tramiteEncontrado;

    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombre.setText("Nombre: -");
        lblCedula.setText("Cédula: -");
        lblEstadoActual.setText("Estado: -");
        txtNotaTeorica.clear();
        txtNotaPractica.clear();
        chkFotos.setSelected(false);
        chkCertificado.setSelected(false);
        chkMultas.setSelected(false);
        habilitarEdicion(true);
        btnGenerarLicencia.setDisable(true);
    }

    private void habilitarEdicion(boolean editable) {
        txtNotaTeorica.setEditable(editable);
        txtNotaPractica.setEditable(editable);
        chkFotos.setDisable(!editable);
        chkCertificado.setDisable(!editable);
        chkMultas.setDisable(!editable);
        btnGuardarReq.setDisable(!editable);
        btnGuardarNotas.setDisable(!editable);
    }

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();

            if (busqueda.isEmpty()) {
                mostrarAlerta("Campo Vacío", "Por favor, ingrese un número de cédula.", Alert.AlertType.WARNING);
                return;
            }

            if (!busqueda.matches("\\d{10}")) {
                mostrarAlerta("Formato Incorrecto", "La cédula debe contener exactamente 10 dígitos.", Alert.AlertType.ERROR);
                return;
            }

            // Consultar el trámite
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);

                // 1. Mostrar Datos Básicos
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado().toUpperCase());

                // 2. Cargar Requisitos y Notas
                boolean yaPasoRequisitos = !tramiteEncontrado.getEstado().equalsIgnoreCase("pendiente");
                chkFotos.setSelected(yaPasoRequisitos);
                chkCertificado.setSelected(yaPasoRequisitos);
                chkMultas.setSelected(yaPasoRequisitos);

                txtNotaTeorica.setText(tramiteEncontrado.getNotaTeorica() > 0 ? String.valueOf(tramiteEncontrado.getNotaTeorica()) : "");
                txtNotaPractica.setText(tramiteEncontrado.getNotaPractica() > 0 ? String.valueOf(tramiteEncontrado.getNotaPractica()) : "");

                // 3. Lógica de Bloqueo según estado
                String estado = tramiteEncontrado.getEstado().toLowerCase();

                if (estado.equals("licencia_emitida") || estado.equals("aprobado")) {
                    habilitarEdicion(false);
                    btnGenerarLicencia.setDisable(!estado.equals("aprobado"));
                } else if (estado.equals("en_examenes")) {
                    habilitarEdicion(true);
                    btnGuardarReq.setDisable(true);
                    btnGenerarLicencia.setDisable(true);
                } else {
                    habilitarEdicion(true);
                    btnGuardarNotas.setDisable(true);
                    btnGenerarLicencia.setDisable(true);
                }
            } else {
                limpiarCampos();
                tramiteEncontrado = null;
                mostrarAlerta("Sin Resultados", "No se encontró ningún trámite con esa cédula.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGuardarRequisitos() {
        if (tramiteEncontrado == null) return;
        try {
            requisitoService.guardarRequisitos(tramiteEncontrado.getId(),
                    chkFotos.isSelected(), chkCertificado.isSelected(), chkMultas.isSelected(),
                    "Validado desde Detalle", null);
            mostrarAlerta("Éxito", "Requisitos guardados. Avance a fase de exámenes.", Alert.AlertType.INFORMATION);
            handleBuscar();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGuardarNotas() {
        if (tramiteEncontrado == null) return;
        try {
            double nt = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double np = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            if (nt < 0 || nt > 20 || np < 0 || np > 20) {
                mostrarAlerta("Nota Inválida", "Las notas deben estar entre 0 y 20.", Alert.AlertType.WARNING);
                return;
            }

            tramiteService.registrarExamen(tramiteEncontrado.getId(), nt, np);
            handleBuscar();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Ingrese números válidos.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // Sincronizar con el controlador de la licencia
            ui.analista.GenerarLicenciaController controller = loader.getController();

            if (controller != null && tramiteEncontrado != null) {
                controller.initData(tramiteEncontrado);
            }

            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista de la licencia.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}