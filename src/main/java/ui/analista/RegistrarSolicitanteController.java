package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Solicitante;
import service.SolicitanteService;
import java.time.LocalDate;

public class RegistrarSolicitanteController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> comboLicencia;
    @FXML private DatePicker dateNacimiento;

    private SolicitanteService solicitanteService = new SolicitanteService();

    @FXML
    public void initialize() {
        // Asegúrate de que los nombres coincidan con lo que espera tu lógica de negocio
        comboLicencia.getItems().addAll("Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E");
    }

    @FXML
    private void handleGuardar() {
        // Validaciones básicas antes de enviar al Service
        if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                comboLicencia.getValue() == null || dateNacimiento.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Por favor, complete todos los campos del formulario.");
            return;
        }

        try {
            // 1. Empaquetamos los datos en el Modelo
            Solicitante nuevoSolicitante = new Solicitante();
            nuevoSolicitante.setCedula(txtCedula.getText().trim());
            nuevoSolicitante.setNombre(txtNombre.getText().trim());
            nuevoSolicitante.setTipoLicencia(comboLicencia.getValue());
            nuevoSolicitante.setFechaNacimiento(dateNacimiento.getValue());

            // Asignamos un ID de usuario por defecto (1) para evitar el error de created_by en la BD
            // En un sistema real, aquí pondrías el ID del usuario que inició sesión.
            nuevoSolicitante.setCreatedBy(1);

            // 2. LLAMADA AL SERVICIO
            // Este método ahora funcionará porque ya agregamos las columnas a la tabla 'solicitantes'
            solicitanteService.crearSolicitanteConTramite(nuevoSolicitante);

            // 3. Feedback al usuario
            mostrarAlerta("Éxito", "Solicitante y Trámite creados correctamente.");
            handleLimpiar();

        } catch (Exception e) {
            // Aquí capturamos errores de la BD o de lógica (ej: cédula duplicada)
            mostrarAlerta("Error de Validación", "No se pudo guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLimpiar() {
        txtCedula.clear();
        txtNombre.clear();
        comboLicencia.setValue(null);
        if(dateNacimiento != null) dateNacimiento.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}