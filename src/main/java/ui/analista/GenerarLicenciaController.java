package ui.analista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Tramite;
import dao.TramiteDao;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// IMPORTACIONES PARA PDFBOX
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

public class GenerarLicenciaController {

    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnGenerar, btnExportar;

    private Tramite tramiteActivo;
    private TramiteDao tramiteDao = new TramiteDao();

    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;

        lblNombreConductor.setText(tramite.getNombre());
        lblNumeroLicencia.setText(tramite.getCedula());
        lblTipoLicencia.setText(tramite.getTipo());

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = hoy.plusYears(5);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFechaEmision.setText(hoy.format(fmt));
        lblFechaVencimiento.setText(vencimiento.format(fmt));

        // Habilitar botón generar solo si está aprobado
        btnGenerar.setDisable(!"aprobado".equalsIgnoreCase(tramite.getEstado()));
        // El botón exportar se mantiene apagado hasta que se guarde en DB
        btnExportar.setDisable(true);
    }

    @FXML
    private void handleGenerar() {
        try {
            // 1. Cambiar estado en la base de datos
            tramiteDao.updateEstado(tramiteActivo.getId(), "licencia_emitida");

            mostrarAlerta("Éxito", "Licencia registrada en el sistema. Ahora puede exportar el PDF.");

            btnGenerar.setDisable(true);
            btnExportar.setDisable(false); // Activamos el botón de PDF
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al procesar: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportarPDF() {
        if (tramiteActivo == null) return;

        String nombreArchivo = "Licencia_" + tramiteActivo.getCedula() + ".pdf";

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);

            try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                // Título
                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contenido.newLineAtOffset(150, 750);
                contenido.showText("REPÚBLICA DEL ECUADOR");
                contenido.endText();

                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contenido.newLineAtOffset(180, 730);
                contenido.showText("LICENCIA DE CONDUCIR");
                contenido.endText();

                // Datos de la licencia
                contenido.setFont(PDType1Font.HELVETICA, 12);

                int startY = 680;
                escribirLinea(contenido, "NÚMERO DE LICENCIA: " + lblNumeroLicencia.getText(), 100, startY);
                escribirLinea(contenido, "CONDUCTOR: " + lblNombreConductor.getText(), 100, startY - 30);
                escribirLinea(contenido, "TIPO DE LICENCIA: " + lblTipoLicencia.getText(), 100, startY - 60);
                escribirLinea(contenido, "FECHA EMISIÓN: " + lblFechaEmision.getText(), 100, startY - 90);
                escribirLinea(contenido, "FECHA VENCIMIENTO: " + lblFechaVencimiento.getText(), 100, startY - 120);

                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contenido.newLineAtOffset(100, 500);
                contenido.showText("Documento generado electrónicamente por el Sistema de Licencias.");
                contenido.endText();
            }

            documento.save(nombreArchivo);
            mostrarAlerta("PDF Creado", "Se ha generado el archivo: " + nombreArchivo);

        } catch (IOException e) {
            mostrarAlerta("Error PDF", "No se pudo generar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para escribir texto más fácil en PDFBox
    private void escribirLinea(PDPageContentStream cs, String texto, float x, float y) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }

    @FXML
    private void handleRegresar() {
        // Aquí deberías llamar a la función de navegación de tu AnalistaController
        // o simplemente limpiar el contentArea.
        btnExportar.getScene().lookup("#contentArea"); // Esto es una referencia, depende de tu navegación
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}