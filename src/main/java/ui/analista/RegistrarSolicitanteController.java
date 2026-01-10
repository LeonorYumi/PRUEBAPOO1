package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Solicitante;
import service.SolicitanteService;

public class RegistrarSolicitanteController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> comboLicencia;
    @FXML private DatePicker dateNacimiento;

    // Conectamos con la capa de Servicio
    private SolicitanteService solicitanteService = new SolicitanteService();

    @FXML
    public void initialize() {
        // Llenamos el combo con los tipos de licencia disponibles
        comboLicencia.getItems().addAll("Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E");
    }

    @FXML
    private void handleGuardar() {
        // 1. VALIDACIÓN VISUAL: Que no haya cuadros vacíos
        if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                comboLicencia.getValue() == null || dateNacimiento.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Por favor, llene todos los campos.");
            return;
        }

        try {
            // 2. CREAR EL OBJETO: Pasamos los datos de la pantalla al modelo
            Solicitante nuevo = new Solicitante();
            nuevo.setCedula(txtCedula.getText().trim());
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setTipoLicencia(comboLicencia.getValue());
            nuevo.setFechaNacimiento(dateNacimiento.getValue());

            // Usuario que registra (Simulado como ID 1)
            nuevo.setCreatedBy(1);

            // 3. LLAMAR AL SERVICE: Aquí se validan los 10 dígitos y solo números
            // Usamos 'registrarSolicitante' para que coincida con el nombre en tu Service
            solicitanteService.registrarSolicitante(nuevo);

            // 4. ÉXITO
            mostrarAlerta("Éxito", "Solicitante registrado y trámite generado correctamente.");
            handleLimpiar();

        } catch (Exception e) {
            // Aquí se muestran los errores de "Cédula debe tener 10 dígitos", etc.
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        txtCedula.clear();
        txtNombre.clear();
        comboLicencia.setValue(null);
        dateNacimiento.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}