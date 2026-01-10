package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import model.Tramite;
import service.TramiteService;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;

public class ReporteAdminController {

    @FXML private DatePicker dpInicio, dpFin;
    @FXML private ComboBox<String> cbEstado, cbTipoLicencia;
    @FXML private TextField txtCedulaFiltro;

    @FXML private TableView<Tramite> tableReportes;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colNombre, colCedula, colEstado, colFecha;

    @FXML private Label lblTotalTramites, lblTotalAprobados, lblTotalRechazados;

    private final TramiteService tramiteService = new TramiteService();

    @FXML
    public void initialize() {
        // 1. Configuración de Combos
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "pendiente", "aprobado", "reprobado"));
        cbTipoLicencia.setItems(FXCollections.observableArrayList("Todos", "Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E"));
        cbEstado.setValue("Todos");
        cbTipoLicencia.setValue("Todos");

        // 2. Vincular Columnas (Asegúrate que estos nombres coincidan con los Getters de tu clase Tramite)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }

    @FXML
    private void handleBuscar() {
        try {
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(
                    dpInicio.getValue(),
                    dpFin.getValue(),
                    cbEstado.getValue(),
                    cbTipoLicencia.getValue(),
                    txtCedulaFiltro.getText()
            );

            tableReportes.setItems(FXCollections.observableArrayList(resultados));
            actualizarTotales(resultados);

        } catch (Exception e) {
            mostrarAlerta("Error de Búsqueda", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void actualizarTotales(List<Tramite> lista) {
        int total = lista.size();
        int aprobados = 0;
        int rechazados = 0;

        for (Tramite t : lista) {
            if ("aprobado".equalsIgnoreCase(t.getEstado())) aprobados++;
            else if ("reprobado".equalsIgnoreCase(t.getEstado()) || "rechazado".equalsIgnoreCase(t.getEstado())) rechazados++;
        }

        lblTotalTramites.setText(String.valueOf(total));
        lblTotalAprobados.setText(String.valueOf(aprobados));
        lblTotalRechazados.setText(String.valueOf(rechazados));
    }

    @FXML
    private void handleExportar() {
        // Obtenemos los datos que la tabla está mostrando actualmente
        List<Tramite> lista = tableReportes.getItems();

        if (lista == null || lista.isEmpty()) {
            mostrarAlerta("Sin Datos", "La tabla está vacía. Realice una búsqueda antes de exportar.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Reporte a Excel (CSV)");
        chooser.setInitialFileName("Reporte_Sistema_Licencias.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        File file = chooser.showSaveDialog(tableReportes.getScene().getWindow());

        if (file != null) {
            // Usamos PrintWriter para una escritura más directa y evitar archivos en blanco
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

                // CABECERA (Punto y coma para Excel en español)
                pw.println("ID Tramite;Nombre Solicitante;Cedula;Estado;Fecha");

                // DATOS
                for (Tramite t : lista) {
                    pw.print(t.getId() + ";");
                    pw.print((t.getNombre() != null ? t.getNombre() : "N/A") + ";");
                    pw.print((t.getCedula() != null ? t.getCedula() : "N/A") + ";");
                    pw.print((t.getEstado() != null ? t.getEstado() : "N/A") + ";");
                    pw.println(t.getFecha() != null ? t.getFecha().toString() : "N/A");
                }

                // Forzamos el guardado de los datos en el archivo
                pw.flush();

                mostrarAlerta("Exportación Exitosa", "Se han guardado " + lista.size() + " registros correctamente.");

            } catch (Exception e) {
                mostrarAlerta("Error al Guardar", "No se pudo generar el archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}