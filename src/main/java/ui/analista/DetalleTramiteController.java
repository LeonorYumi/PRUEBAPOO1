package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Tramite;

public class DetalleTramiteController {

    // Secciones de la interfaz (fx:id)
    @FXML private TextField txtBusquedaId;
    @FXML private Label lblNombre;
    @FXML private Label lblCedula;
    @FXML private Label lblTipoLicencia;
    @FXML private Label lblEstadoActual;

    @FXML private CheckBox chkCopiaCedula;
    @FXML private CheckBox chkCertificadoSalud;
    @FXML private CheckBox chkTipoSangre;
    @FXML private CheckBox chkPagoBanco;

    @FXML private TextField txtNotaTeorica;
    @FXML private TextField txtNotaPractica;

    @FXML private Button btnGuardarRequisitos;
    @FXML private Button btnGuardarNotas;
    @FXML private Button btnGenerarLicencia;

    @FXML
    public void initialize() {
        System.out.println("DetalleTramiteController inicializado correctamente.");
    }

    @FXML
    private void handleBuscar() {
        String idStr = txtBusquedaId.getText();
        if (idStr.isEmpty()) {
            mostrarAlerta("Atención", "Por favor ingrese un ID de trámite.");
            return;
        }
        System.out.println("Buscando trámite ID: " + idStr);
        // Aquí simulamos una carga. Luego conectarás con tu DAO.
        lblNombre.setText("Juan Pérez (Simulado)");
        lblCedula.setText("1234567890");
        lblTipoLicencia.setText("Tipo B");
        lblEstadoActual.setText("Pendiente");
    }

    @FXML
    private void handleGuardarRequisitos() {
        System.out.println("Requisitos guardados.");
        mostrarAlerta("Éxito", "Requisitos actualizados.");
    }

    @FXML
    private void handleGuardarNotas() {
        System.out.println("Notas guardadas.");
        mostrarAlerta("Éxito", "Notas registradas correctamente.");
    }

    @FXML
    private void handleGenerarLicencia() {
        System.out.println("Generando licencia...");
        mostrarAlerta("Proceso", "Generando documento legal...");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}