package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import model.Licencia;
import service.LicenciaService;
import ui.base.BaseController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// PDFBOX
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

public class GenerarLicenciaController extends BaseController { // <--- EXTENDS

    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnGenerar, btnExportar;

    private Tramite tramiteActivo;
    private Licencia licenciaGenerada;
    private final LicenciaService licenciaService = new LicenciaService();

    @Override
    public void limpiarCampos() {
        // Implementación requerida por BaseController
        lblNumeroLicencia.setText("");
        btnExportar.setDisable(true);
    }

    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;
        lblNombreConductor.setText(tramite.getNombre());
        lblNumeroLicencia.setText("PENDIENTE GENERAR");
        lblTipoLicencia.setText(tramite.getTipoLicencia());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFechaEmision.setText(LocalDate.now().format(fmt));
        lblFechaVencimiento.setText(LocalDate.now().plusYears(5).format(fmt));

        btnGenerar.setDisable(!"aprobado".equalsIgnoreCase(tramite.getEstado()));
        btnExportar.setDisable(true);
    }

    @FXML
    private void handleGenerar() {
        try {
            // Auditoría: Usamos el ID del trámite y el usuario 1 (ajustar según sesión)
            this.licenciaGenerada = licenciaService.generarLicencia(tramiteActivo.getId(), 1);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblNumeroLicencia.setText(licenciaGenerada.getNumeroLicencia());
            lblFechaEmision.setText(licenciaGenerada.getFechaEmision().format(fmt));
            lblFechaVencimiento.setText(licenciaGenerada.getFechaVencimiento().format(fmt));

            // USAMOS EL MÉTODO HEREDADO
            mostrarAlerta("Éxito", "Licencia generada oficialmente: " + licenciaGenerada.getNumeroLicencia(), Alert.AlertType.INFORMATION);

            btnGenerar.setDisable(true);
            btnExportar.setDisable(false);
        } catch (Exception e) {
            mostrarAlerta("Error de Emisión", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExportarPDF() {
        if (licenciaGenerada == null) return;
        String nombreArchivo = "Licencia_" + tramiteActivo.getCedula() + ".pdf";

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);

            try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                escribirTexto(contenido, "REPUBLICA DEL ECUADOR", 150, 750, 18, true);
                escribirTexto(contenido, "SISTEMA NACIONAL DE TRANSITO", 180, 730, 12, false);

                int startY = 650;
                escribirTexto(contenido, "NUMERO DE LICENCIA: " + licenciaGenerada.getNumeroLicencia(), 100, startY, 12, true);
                escribirTexto(contenido, "CONDUCTOR: " + tramiteActivo.getNombre(), 100, startY - 30, 12, false);
                escribirTexto(contenido, "CEDULA: " + tramiteActivo.getCedula(), 100, startY - 60, 12, false);
                escribirTexto(contenido, "TIPO: " + tramiteActivo.getTipoLicencia(), 100, startY - 90, 12, false);
                escribirTexto(contenido, "EMISION: " + lblFechaEmision.getText(), 100, startY - 120, 12, false);
                escribirTexto(contenido, "VENCIMIENTO: " + lblFechaVencimiento.getText(), 100, startY - 150, 12, true);
            }

            documento.save(nombreArchivo);
            mostrarAlerta("PDF Generado", "Guardado como: " + nombreArchivo, Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            mostrarAlerta("Error PDF", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegresar() {
        if (lblNombreConductor.getScene() != null) {
            StackPane contentArea = (StackPane) lblNombreConductor.getScene().lookup("#contentArea");
            if (contentArea != null) contentArea.getChildren().clear();
        }
    }

    private void escribirTexto(PDPageContentStream cs, String texto, float x, float y, int size, boolean negrita) throws IOException {
        cs.beginText();
        cs.setFont(negrita ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }
}