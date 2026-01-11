package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import model.Tramite;
import service.TramiteService;
import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GenerarLicenciaController {

    // Vinculamos los elementos de la interfaz (FXML)
    @FXML private Label lblNumeroLicencia, lblNombreConductor, lblTipoLicencia, lblFechaEmision, lblFechaVencimiento;
    @FXML private Button btnExportar, btnRegresar, btnGenerar;
    @FXML private TextField txtBusquedaRapida;

    // Formato de fecha para Ecuador (día/mes/año)
    private final DateTimeFormatter formatoEcuador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Servicio para buscar trámites reales
    private final TramiteService tramiteService = new TramiteService();

    // Mantenemos el tramite cargado para usar sus datos en PDF y mostrar
    private Tramite tramiteActivo;

    public GenerarLicenciaController() {
        // Constructor vacío
    }

    @FXML
    private void initialize() {
        System.out.println("GenerarLicenciaController initialized. txtBusquedaRapida = " + (txtBusquedaRapida != null));
        if (btnExportar != null) btnExportar.setDisable(true);
        if (btnGenerar != null) btnGenerar.setDisable(true);
    }

    /**
     * Recibe el trámite seleccionado desde la pantalla anterior.
     */
    public void initData(Tramite tramite) {
        this.tramiteActivo = tramite;
        if (tramite != null) {
            // Nombre y cédula tal como vienen del trámite
            lblNombreConductor.setText(tramite.getNombre() != null ? tramite.getNombre().toUpperCase() : "---");
            lblNumeroLicencia.setText(tramite.getCedula() != null ? tramite.getCedula() : "---");
            lblTipoLicencia.setText("TIPO " + (tramite.getTipoLicencia() != null ? tramite.getTipoLicencia() : "-"));

            // Usamos la fecha que está en el trámite si existe
            LocalDate fechaEmisionLD;
            String fechaTramite = tramite.getFecha(); // es String en el modelo
            if (fechaTramite != null && !fechaTramite.isBlank()) {
                try {
                    // Intentamos parsear ISO (yyyy-MM-dd)
                    fechaEmisionLD = LocalDate.parse(fechaTramite);
                    lblFechaEmision.setText(fechaEmisionLD.format(formatoEcuador));
                } catch (DateTimeParseException ex) {
                    // Si no es ISO, mostramos el string tal cual y tomamos hoy como fallback para vencimiento
                    lblFechaEmision.setText(fechaTramite);
                    fechaEmisionLD = LocalDate.now();
                }
            } else {
                // Si no hay fecha en el trámite, caemos a hoy (pero ideal: guardar la fecha en BD correctamente)
                fechaEmisionLD = LocalDate.now();
                lblFechaEmision.setText(fechaEmisionLD.format(formatoEcuador));
            }

            // Vencimiento = emisión + 5 años
            lblFechaVencimiento.setText(fechaEmisionLD.plusYears(5).format(formatoEcuador));

            // Activamos el botón de exportar y, si aplica, habilitamos generar
            btnExportar.setDisable(false);
            btnGenerar.setDisable(!"aprobado".equalsIgnoreCase(tramite.getEstado()));
        } else {
            // No hay tramite: limpiamos
            lblNombreConductor.setText("---");
            lblNumeroLicencia.setText("---");
            lblTipoLicencia.setText("TIPO -");
            lblFechaEmision.setText("--");
            lblFechaVencimiento.setText("--");
            btnExportar.setDisable(true);
            btnGenerar.setDisable(true);
        }
    }

    @FXML
    private void handleGenerar() {
        btnExportar.setDisable(false);
        Alert msj = new Alert(Alert.AlertType.INFORMATION);
        msj.setTitle("Aviso");
        msj.setHeaderText(null);
        msj.setContentText("Licencia generada con éxito. Ahora puede exportar a PDF.");
        msj.showAndWait();
    }

    @FXML
    private void handleRegresar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetalleTramiteView.fxml"));
            Parent vistaAnterior = loader.load();

            StackPane contentArea = (StackPane) btnExportar.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(vistaAnterior);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo volver: " + e.getMessage());
        }
    }

    /**
     * Búsqueda rápida: ahora consulta el servicio para traer datos reales y llama initData(tramite).
     */
    @FXML
    private void handleBuscarRapido() {
        try {
            if (txtBusquedaRapida == null) {
                throw new IllegalStateException("Control txtBusquedaRapida no inyectado. Revisa fx:id y fx:controller.");
            }

            String cedula = txtBusquedaRapida.getText().trim();
            if (cedula.isEmpty()) {
                mostrarAlerta("Campo vacío", "Ingrese una cédula para buscar.", Alert.AlertType.WARNING);
                return;
            }

            if (!cedula.matches("\\d{10}")) {
                mostrarAlerta("Formato incorrecto", "La cédula debe tener 10 dígitos.", Alert.AlertType.ERROR);
                return;
            }

            // Llamamos al servicio para buscar el trámite REAL por cédula
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);
            if (resultados != null && !resultados.isEmpty()) {
                Tramite encontrado = resultados.get(0);
                // Cargamos los datos reales en la vista
                initData(encontrado);
            } else {
                mostrarAlerta("No encontrado", "No se encontró ningún trámite con la cédula indicada.", Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error búsqueda", "Ocurrió un error al buscar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * EXPORTAR PDF: restaurado al método que tenía antes (escribe PDF con PrintWriter).
     * Usa los textos actuales de las Labels para generar el archivo.
     */
    @FXML
    private void handleExportarPDF() {
        // Si hay un tramiteActivo, preferimos sus datos
        String cedula = (tramiteActivo != null && tramiteActivo.getCedula() != null) ? tramiteActivo.getCedula() : lblNumeroLicencia.getText();
        String nombre = (tramiteActivo != null && tramiteActivo.getNombre() != null) ? tramiteActivo.getNombre() : lblNombreConductor.getText();
        String tipo = (tramiteActivo != null && tramiteActivo.getTipoLicencia() != null) ? tramiteActivo.getTipoLicencia() : lblTipoLicencia.getText();
        String fEmision = lblFechaEmision.getText();
        String fVence = lblFechaVencimiento.getText();

        // Ventana para elegir donde guardar el archivo
        FileChooser selector = new FileChooser();
        selector.setInitialFileName("Licencia_" + cedula + ".pdf");
        File destino = selector.showSaveDialog(btnExportar.getScene().getWindow());

        if (destino != null) {
            try (PrintWriter pw = new PrintWriter(destino)) {
                // Comienzo del archivo PDF (estructura mínima)
                pw.println("%PDF-1.4");
                pw.println("1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj");
                pw.println("2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj");
                pw.println("3 0 obj << /Type /Page /Parent 2 0 R /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Courier >> >> >> /MediaBox [0 0 612 792] /Contents 4 0 R >> endobj");
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
                pw.println("0 -40 Td (" +  ") Tj");
                pw.println("0 -20 Td (CEDULA: " + cedula + ") Tj");
                pw.println("0 -20 Td (NOMBRE: " + nombre + ") Tj");

                // Posicionamos las fechas al final del cuadro
                pw.println("0 -40 Td (EMISION: " + fEmision + "      VENCE: " + fVence + ") Tj");

                pw.println("ET");
                pw.println("endstream");
                pw.println("endobj");

                // Cierre del PDF (tarea mínima)
                pw.println("xref");
                pw.println("0 5");
                pw.println("0000000000 65535 f");
                pw.println("trailer << /Size 5 /Root 1 0 R >>");
                pw.println("%%EOF");

                pw.flush();

                // Intentamos abrirlo
                try {
                    Desktop.getDesktop().open(destino);
                } catch (UnsupportedOperationException | IOException ex) {
                    mostrarAlerta("Hecho", "PDF guardado en: " + destino.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error al crear el PDF", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // Método auxiliar para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}