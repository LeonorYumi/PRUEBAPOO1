package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Solicitante;
import service.SolicitanteService;
import ui.base.BaseController; // Asegúrate que la ruta sea ui.BaseController según tu código previo
import java.time.LocalDate;

/**
 * Controlador para el registro de nuevos solicitantes.
 * Vinculado con RegistrarSolicitanteView.fxml
 */
public class RegistrarSolicitanteController extends BaseController {

    // Los IDs deben ser EXACTAMENTE iguales al FXML
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private DatePicker dateNacimiento;
    @FXML private ComboBox<String> comboLicencia;
    @FXML private DatePicker dateSolicitud; // Agregado para coincidir con el FXML

    private final SolicitanteService solicitanteService = new SolicitanteService();

    @FXML
    public void initialize() {
        // Llenar el combo con las opciones
        comboLicencia.getItems().addAll("Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E");

        // Seteamos la fecha actual por defecto en el campo de solicitud
        if (dateSolicitud != null) {
            dateSolicitud.setValue(LocalDate.now());
        }
    }

    /**
     * Implementación de BaseController (Polimorfismo)
     */
    @Override
    public void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        dateNacimiento.setValue(null);
        comboLicencia.setValue(null);
        if (dateSolicitud != null) dateSolicitud.setValue(LocalDate.now());
    }

    /**
     * Vinculado a onAction="#handleLimpiar" en el FXML
     */
    @FXML
    private void handleLimpiar() {
        limpiarCampos();
    }

    /**
     * Vinculado a onAction="#handleGuardar" en el FXML
     */
    @FXML
    private void handleGuardar() {
        // Validación de campos
        if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                dateNacimiento.getValue() == null || comboLicencia.getValue() == null) {

            mostrarAlerta("Atención", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Solicitante nuevo = new Solicitante();
            nuevo.setCedula(txtCedula.getText().trim());
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setFechaNacimiento(dateNacimiento.getValue());
            nuevo.setTipoLicencia(comboLicencia.getValue());
            nuevo.setCreatedBy(1); // ID del usuario logueado (simulado)

            // El Service se encarga de la persistencia y el trámite
            solicitanteService.registrarSolicitante(nuevo);

            mostrarAlerta("Éxito", "Solicitante guardado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}