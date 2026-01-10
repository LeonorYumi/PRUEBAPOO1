package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Tramite;
import model.Licencia;
import service.LicenciaService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.awt.Desktop;

// Importaciones para PDFBox
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class GenerarLicenciaController {

    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnGenerar, btnExportar;

    private Tramite tramiteActivo;
    private final LicenciaService licenciaService = new LicenciaService();

    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;
        if (tramite != null) {
            lblNombreConductor.setText(tramite.getNombre().toUpperCase());
            lblTipoLicencia.setText("TIPO " + tramite.getTipoLicencia());

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblFechaEmision.setText(LocalDate.now().format(fmt));
            lblFechaVencimiento.setText(LocalDate.now().plusYears(5).format(fmt));

            // --- FUERZA BRUTA PARA ACTIVAR EL BOTÓN ---
            btnGenerar.setDisable(false); // Lo activa
            btnGenerar.setOpacity(1.0);   // Lo hace brillar (que no se vea gris)
            btnGenerar.setText("Generar y Guardar");

            // Si ya está emitida, permitimos exportar PDF desde el inicio
            if ("licencia_emitida".equalsIgnoreCase(tramite.getEstado())) {
                btnExportar.setDisable(false);
                btnExportar.setOpacity(1.0);
            }
        }
    }

    @FXML
    private void handleGenerar() {
        if (tramiteActivo == null) return;
        try {
            // Llamada al servicio (Asegúrate que LicenciaService reciba 2 parámetros: int, int)
            Licencia nueva = licenciaService.generarLicencia(tramiteActivo.getId(), 1);

            if (nueva != null) {
                lblNumeroLicencia.setText(nueva.getNumeroLicencia());
                btnExportar.setDisable(false);
                btnExportar.setOpacity(1.0);
                mostrarAlerta("Éxito", "Licencia generada con éxito.");
            }
        } catch (Exception e) {
            // Si sale error de "Duplicate entry", igual habilitamos el PDF
            mostrarAlerta("Información", "Nota: " + e.getMessage());
            btnExportar.setDisable(false);
            btnExportar.setOpacity(1.0);
        }
    }

    @FXML
    private void handleExportarPDF() {
        if (tramiteActivo == null) return;
        String nombreArchivo = "Licencia_" + tramiteActivo.getCedula() + ".pdf";
        File file = new File(nombreArchivo);

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contenido.newLineAtOffset(150, 750);
                contenido.showText("REPUBLICA DEL ECUADOR");
                contenido.endText();

                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA, 12);
                contenido.newLineAtOffset(100, 680);
                contenido.setLeading(25f);
                contenido.showText("N. LICENCIA: " + lblNumeroLicencia.getText());
                contenido.newLine();
                contenido.showText("CONDUCTOR: " + lblNombreConductor.getText());
                contenido.newLine();
                contenido.showText("FECHA VENCIMIENTO: " + lblFechaVencimiento.getText());
                contenido.endText();
            }
            documento.save(file);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
        } catch (Exception e) {
            mostrarAlerta("Error PDF", "Error al abrir el PDF: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegresar() {
        if (lblNombreConductor.getScene() != null) {
            lblNombreConductor.getScene().getWindow().hide();
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}