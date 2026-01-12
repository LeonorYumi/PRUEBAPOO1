package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Solicitante;
import service.SolicitanteService;
import ui.base.BaseController;
import java.time.LocalDate;

public class RegistrarSolicitanteController extends BaseController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private DatePicker dateNacimiento;
    @FXML private ComboBox<String> comboLicencia;
    @FXML private DatePicker dateSolicitud;

    private final SolicitanteService solicitanteService = new SolicitanteService();

    @FXML
    public void initialize() {
        comboLicencia.getItems().addAll("Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E");
        if (dateSolicitud != null) {
            dateSolicitud.setValue(LocalDate.now());
        }
    }

    @Override
    public void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        dateNacimiento.setValue(null);
        comboLicencia.setValue(null);
        if (dateSolicitud != null) dateSolicitud.setValue(LocalDate.now());
    }

    @FXML private void handleLimpiar() { limpiarCampos(); }

    @FXML
    private void handleGuardar() {
        // Validación de campos vacíos
        if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                dateNacimiento.getValue() == null || comboLicencia.getValue() == null) {
            mostrarAlerta("Atención", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        // Validación de cédula (10 dígitos)
        if (txtCedula.getText().trim().length() != 10) {
            mostrarAlerta("Error", "La cédula debe tener exactamente 10 dígitos.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Solicitante nuevo = new Solicitante();
            nuevo.setCedula(txtCedula.getText().trim());
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setFechaNacimiento(dateNacimiento.getValue());
            nuevo.setTipoLicencia(comboLicencia.getValue());
            nuevo.setCreatedBy(1);

            solicitanteService.registrarSolicitante(nuevo);

            mostrarAlerta("Éxito", "Solicitante y Trámite 'Pendiente' creados correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}