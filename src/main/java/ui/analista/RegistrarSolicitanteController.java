package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class RegistrarSolicitanteController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> comboLicencia;
    @FXML private DatePicker dateSolicitud;

    @FXML
    public void initialize() {
        // Llenamos los tipos de licencia
        comboLicencia.getItems().addAll("Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E");
        // Seteamos la fecha automática a hoy
        dateSolicitud.setValue(LocalDate.now());
    }

    @FXML
    private void handleGuardar() {
        String cedula = txtCedula.getText();
        String nombre = txtNombre.getText();
        String tipo = comboLicencia.getValue();

        if (cedula.isEmpty() || nombre.isEmpty() || tipo == null) {
            System.out.println("Error: Todos los campos son obligatorios.");
            return;
        }

        System.out.println("Guardando Solicitante: " + nombre);
        System.out.println("Creando Trámite: PENDIENTE");
        // Aquí llamarás a SolicitanteDao más adelante
    }

    @FXML
    private void handleLimpiar() {
        txtCedula.clear();
        txtNombre.clear();
        comboLicencia.setValue(null);
        dateSolicitud.setValue(LocalDate.now());
    }
}