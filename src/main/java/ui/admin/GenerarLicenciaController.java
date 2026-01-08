package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Tramite;
import model.Licencia;
import service.LicenciaService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

public class GenerarLicenciaController {

    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnGenerar, btnExportar, btnRegresar;

    private Tramite tramiteActivo;
    private Licencia licenciaGenerada;
    private final LicenciaService licenciaService = new LicenciaService();

    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;

        // Nota: Asegúrate que los getters coincidan con tu clase Tramite
        lblNombreConductor.setText(tramite.getNombre());
        lblNumeroLicencia.setText("PENDIENTE GENERAR");
        lblTipoLicencia.setText(tramite.getTipo());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFechaEmision.setText(LocalDate.now().format(fmt));
        lblFechaVencimiento.setText(LocalDate.now().plusYears(5).format(fmt));

        // Solo habilitar si el trámite ya fue aprobado
        btnGenerar.setDisable(!"aprobado".equalsIgnoreCase(tramite.getEstado()));
        btnExportar.setDisable(true);
    }

    @FXML
    private void handleGenerar() {
        try {
            // Esto genera el registro en la BD y cambia el estado a 'licencia_emitida'
            this.licenciaGenerada = licenciaService.generarLicencia(tramiteActivo.getId(), null);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblNumeroLicencia.setText(licenciaGenerada.getNumeroLicencia());
            lblFechaEmision.setText(licenciaGenerada.getFechaEmision().format(fmt));
            lblFechaVencimiento.setText(licenciaGenerada.getFechaVencimiento().format(fmt));

            mostrarAlerta("Éxito", "Licencia generada correctamente con número: " + licenciaGenerada.getNumeroLicencia());

            btnGenerar.setDisable(true);
            btnExportar.setDisable(false);
        } catch (Exception e) {
            mostrarAlerta("Error de Emisión", e.getMessage());
            e.printStackTrace();
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
                escribirTexto(contenido, "REPÚBLICA DEL ECUADOR", 150, 750, 18, true);
                escribirTexto(contenido, "SISTEMA NACIONAL DE TRÁNSITO", 180, 730, 12, false);
                escribirTexto(contenido, "CONDUCTOR: " + lblNombreConductor.getText(), 100, 620, 12, false);
                escribirTexto(contenido, "TIPO: " + lblTipoLicencia.getText(), 100, 590, 12, false);
            }
            documento.save(nombreArchivo);
            mostrarAlerta("PDF Generado", "El archivo se guardó como: " + nombreArchivo);
        } catch (IOException e) {
            mostrarAlerta("Error PDF", e.getMessage());
        }
    }

    @FXML
    private void handleRegresar() {
        // Esta es la forma más limpia de cerrar el pop-up
        Stage stage = (Stage) lblNombreConductor.getScene().getWindow();
        stage.close();
    }

    private void escribirTexto(PDPageContentStream cs, String texto, float x, float y, int size, boolean negrita) throws IOException {
        cs.beginText();
        cs.setFont(negrita ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}