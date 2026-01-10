package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Tramite;
import model.Licencia;
import service.LicenciaService;
import ui.base.BaseController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.awt.Desktop;

// Importaciones para PDFBox (Generación de documentos)
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Controlador para la emisión de licencias en formato PDF.
 * Aplica Herencia al extender de BaseController.
 */
public class GenerarLicenciaController extends BaseController {

    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnGenerar, btnExportar;

    private Tramite tramiteActivo;
    private final LicenciaService licenciaService = new LicenciaService();

    /**
     * Implementación obligatoria del método de limpieza (Polimorfismo).
     */
    @Override
    public void limpiarCampos() {
        lblNumeroLicencia.setText("---");
        btnExportar.setDisable(true);
        btnExportar.setOpacity(0.5);
    }

    /**
     * Recibe los datos del trámite desde la pantalla anterior.
     */
    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;
        if (tramite != null) {
            lblNombreConductor.setText(tramite.getNombre().toUpperCase());
            lblTipoLicencia.setText("TIPO " + tramite.getTipoLicencia());

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblFechaEmision.setText(LocalDate.now().format(fmt));
            lblFechaVencimiento.setText(LocalDate.now().plusYears(5).format(fmt));

            // Activamos el botón para permitir la generación
            btnGenerar.setDisable(false);
            btnGenerar.setOpacity(1.0);

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
            // Llamada al servicio (Abstracción: la UI no sabe cómo se guarda en la DB)
            Licencia nueva = licenciaService.generarLicencia(tramiteActivo.getId(), 1);

            if (nueva != null) {
                lblNumeroLicencia.setText(nueva.getNumeroLicencia());
                btnExportar.setDisable(false);
                btnExportar.setOpacity(1.0);
                // USAMOS EL MÉTODO HEREDADO DEL PADRE
                mostrarAlerta("Éxito", "Licencia generada con éxito.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Información", e.getMessage(), Alert.AlertType.INFORMATION);
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
                // Título del PDF
                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contenido.newLineAtOffset(150, 750);
                contenido.showText("REPUBLICA DEL ECUADOR");
                contenido.endText();

                // Datos de la licencia
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

            // Intenta abrir el archivo automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

            mostrarAlerta("PDF", "Documento generado: " + nombreArchivo, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            mostrarAlerta("Error PDF", "No se pudo crear el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        if (lblNombreConductor.getScene() != null) {
            // Método sencillo para cerrar o esconder la ventana actual
            lblNombreConductor.getScene().getWindow().hide();
        }
    }

    // El método mostrarAlerta() anterior se borra porque ya se hereda de BaseController
}