package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import model.Tramite;
import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GenerarLicenciaController {

    // Vinculamos los elementos de la interfaz (FXML)
    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnExportar, btnRegresar, btnGenerar;
    @FXML private TextField txtBusquedaRapida;

    // Formato de fecha para Ecuador (día/mes/año)
    private final DateTimeFormatter formatoEcuador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Este método recibe los datos de la pantalla anterior.
     * Es 'public' para que DetalleTramiteController pueda enviarle el objeto 'tramite'.
     */
    public void initData(Tramite tramite) {
        if (tramite != null) {
            // Pasamos los datos del objeto a las etiquetas (Labels)
            lblNombreConductor.setText(tramite.getNombre().toUpperCase());
            lblNumeroLicencia.setText(tramite.getCedula());
            lblTipoLicencia.setText("TIPO " + tramite.getTipoLicencia());

            // Calculamos fechas: Hoy (2026) y en 5 años (2031)
            LocalDate hoy = LocalDate.now();
            lblFechaEmision.setText(hoy.format(formatoEcuador));
            lblFechaVencimiento.setText(hoy.plusYears(5).format(formatoEcuador));

            // Activamos el botón para poder guardar el PDF
            btnExportar.setDisable(false);
        }
    }

    /**
     * Método para el botón 'Generar'.
     * Soluciona el error de carga de la imagen
     */
    @FXML
    private void handleGenerar() {
        btnExportar.setDisable(false);
        Alert msj = new Alert(Alert.AlertType.INFORMATION);
        msj.setTitle("Aviso");
        msj.setHeaderText(null);
        msj.setContentText("Licencia generada con éxito. Ahora puede exportar a PDF.");
        msj.showAndWait();
    }

    /**
     * Método para el botón 'Regresar'.
     * Soluciona el error de carga de la imagen
     */
    @FXML
    private void handleRegresar() {
        try {
            // Cargamos la vista de Detalle
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetalleTramiteView.fxml"));
            Parent vistaAnterior = loader.load();

            // Reemplazamos el contenido en el panel principal
            StackPane contentArea = (StackPane) btnExportar.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(vistaAnterior);
            }
        } catch (IOException e) {
            System.out.println("No se pudo volver: " + e.getMessage());
        }
    }

    /**
     * Método para búsqueda rápida (Simulación).
     * Soluciona el error de la imagen
     */
    @FXML
    private void handleBuscarRapido() {
        if (!txtBusquedaRapida.getText().isEmpty()) {
            lblNombreConductor.setText("CAMILA BUENO");
            lblNumeroLicencia.setText(txtBusquedaRapida.getText());
            lblTipoLicencia.setText("TIPO A");

            LocalDate hoy = LocalDate.now();
            lblFechaEmision.setText(hoy.format(formatoEcuador));
            lblFechaVencimiento.setText(hoy.plusYears(5).format(formatoEcuador));
            btnExportar.setDisable(false);
        }
    }

    /**
     * EXPORTAR PDF: Dibuja la tarjeta con el cuadro de la foto.
     */
    @FXML
    private void handleExportarPDF() {
        // Obtenemos los textos de los Labels
        String cedula = lblNumeroLicencia.getText();
        String nombre = lblNombreConductor.getText();
        String tipo = lblTipoLicencia.getText();
        String fEmision = lblFechaEmision.getText();
        String fVence = lblFechaVencimiento.getText();

        // Ventana para elegir donde guardar el archivo
        FileChooser selector = new FileChooser();
        selector.setInitialFileName("Licencia_" + cedula + ".pdf");
        File destino = selector.showSaveDialog(btnExportar.getScene().getWindow());

        if (destino != null) {
            try (PrintWriter pw = new PrintWriter(destino)) {
                // Comienzo del archivo PDF
                pw.println("%PDF-1.4");
                pw.println("1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj");
                pw.println("2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj");
                pw.println("3 0 obj << /Type /Page /Parent 2 0 R /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Courier >> >> >> /MediaBox [0 0 612 792] /Contents 4 0 R >> endobj");

                // Contenido del dibujo y texto
                pw.println("4 0 obj << /Length 1500 >> stream");
                pw.println("BT");

                // --- DIBUJAR MARCO DE LA LICENCIA ---
                pw.println("1 w"); // Grosor de la línea
                pw.println("50 550 500 200 re S"); // Cuadro grande (x, y, ancho, alto)

                // --- DIBUJAR CUADRO DE LA FOTO ---
                pw.println("70 575 100 125 re S"); // Cuadro pequeño para la foto

                // --- ESCRIBIR DATOS ---
                pw.println("/F1 12 Tf"); // Fuente Courier tamaño 12
                pw.println("180 720 Td (REPUBLICA DEL ECUADOR) Tj");
                pw.println("0 -20 Td (LICENCIA NACIONAL DE CONDUCIR) Tj");

                pw.println("/F1 10 Tf");
                pw.println("0 -40 Td (TIPO: " + tipo + ") Tj");
                pw.println("0 -20 Td (CEDULA: " + cedula + ") Tj");
                pw.println("0 -20 Td (NOMBRE: " + nombre + ") Tj");

                // Posicionamos las fechas al final del cuadro
                pw.println("0 -40 Td (EMISION: " + fEmision + "      VENCE: " + fVence + ") Tj");

                pw.println("ET");
                pw.println("endstream");
                pw.println("endobj");

                // Cierre del PDF
                pw.println("xref");
                pw.println("0 5");
                pw.println("0000000000 65535 f");
                pw.println("trailer << /Size 5 /Root 1 0 R >>");
                pw.println("%%EOF");

                pw.flush();
                pw.close();

                // Abrimos el PDF automáticamente
                Desktop.getDesktop().open(destino);

            } catch (Exception e) {
                System.out.println("Error al crear el PDF: " + e.getMessage());
            }
        }
    }
}