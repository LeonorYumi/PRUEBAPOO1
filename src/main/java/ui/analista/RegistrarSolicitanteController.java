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
    @FXML private DatePicker dateNacimiento; // Necesario para el Service

    private SolicitanteService solicitanteService = new SolicitanteService();

    @FXML
    public void initialize() {
        comboLicencia.getItems().addAll("Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E");
    }

    @FXML
    private void handleGuardar() {
        try {
            // 1. Empaquetamos los datos en el Modelo
            Solicitante nuevoSolicitante = new Solicitante();
            nuevoSolicitante.setCedula(txtCedula.getText());
            nuevoSolicitante.setNombre(txtNombre.getText());
            nuevoSolicitante.setTipoLicencia(comboLicencia.getValue());
            nuevoSolicitante.setFechaNacimiento(dateNacimiento.getValue());
            // nuevoSolicitante.setCreatedBy(idUsuarioLogueado); // Si lo tienes

            // 2. LLAMADA AL SERVICIO (Aquí ocurren todas las validaciones)
            solicitanteService.crearSolicitanteConTramite(nuevoSolicitante);

            // 3. Si llega aquí, es que no hubo excepciones
            mostrarAlerta("Éxito", "Solicitante y Trámite creados correctamente.");
            handleLimpiar();

        } catch (Exception e) {
            mostrarAlerta("Error de Validación", e.getMessage());
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