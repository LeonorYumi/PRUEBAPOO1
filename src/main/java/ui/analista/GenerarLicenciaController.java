package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import model.Licencia;
import service.LicenciaService;
import service.TramiteService; // Importante para la búsqueda
import ui.base.BaseController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.List;

public class GenerarLicenciaController extends BaseController {

    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnGenerar, btnExportar, btnRegresar;
    @FXML private TextField txtBusquedaRapida; // Campo para búsqueda desde el menú

    private Tramite tramiteActivo;
    private final LicenciaService licenciaService = new LicenciaService();
    private final TramiteService tramiteService = new TramiteService();

    @Override
    public void limpiarCampos() {
        lblNumeroLicencia.setText("0000000000");
        lblNombreConductor.setText("---");
        lblTipoLicencia.setText("TIPO -");
        lblFechaEmision.setText("--");
        lblFechaVencimiento.setText("--");
        btnGenerar.setDisable(true);
        btnExportar.setDisable(true);
    }

    // Método para buscar si entras directamente desde el menú
    @FXML
    private void handleBuscarRapido() {
        String cedula = txtBusquedaRapida.getText().trim();

        if (!cedula.matches("\\d{10}")) {
            mostrarAlerta("Validación", "Ingrese una cédula válida de 10 dígitos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);
            if (resultados != null && !resultados.isEmpty()) {
                Tramite t = resultados.get(0);
                String estado = t.getEstado().toLowerCase();

                // Validamos que el trámite esté en un estado que permita ver la licencia
                if (estado.equals("aprobado") || estado.equals("licencia_emitida")) {
                    initData(t);
                } else {
                    limpiarCampos();
                    mostrarAlerta("Estado Inválido", "El trámite está " + estado.toUpperCase() + ". No se puede emitir licencia aún.", Alert.AlertType.WARNING);
                }
            } else {
                limpiarCampos();
                mostrarAlerta("No encontrado", "No existe trámite para la cédula: " + cedula, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;
        if (tramite != null) {
            lblNombreConductor.setText(tramite.getNombre().toUpperCase());

            // Limpieza de "TIPO TIPO"
            String tipoLimpio = tramite.getTipoLicencia().toUpperCase().replace("TIPO", "").trim();
            lblTipoLicencia.setText("TIPO " + tipoLimpio);

            lblNumeroLicencia.setText(tramite.getCedula());

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblFechaEmision.setText(LocalDate.now().format(fmt));
            lblFechaVencimiento.setText(LocalDate.now().plusYears(5).format(fmt));

            boolean emitida = "licencia_emitida".equalsIgnoreCase(tramite.getEstado());
            btnGenerar.setDisable(emitida);
            btnExportar.setDisable(!emitida);
        }
    }

    @FXML
    private void handleGenerar() {
        if (tramiteActivo == null) return;
        try {
            Licencia nueva = licenciaService.generarLicencia(tramiteActivo.getId(), 1);
            if (nueva != null) {
                lblNumeroLicencia.setText(nueva.getNumeroLicencia());
                btnExportar.setDisable(false);
                btnGenerar.setDisable(true);
                tramiteActivo.setEstado("licencia_emitida");
                mostrarAlerta("Éxito", "Licencia generada correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExportarPDF() {
        mostrarAlerta("PDF", "Generando PDF para " + lblNombreConductor.getText(), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRegresar() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/DetalleTramiteView.fxml"));
            StackPane contentArea = (StackPane) btnRegresar.getScene().lookup("#contentArea");
            if (contentArea != null) contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo regresar.", Alert.AlertType.ERROR);
        }
    }
}