package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;

public class ReporteAdminController extends BaseController { // <--- EXTENDS

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

        // 2. Vincular Columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }

    /**
     * Implementación obligatoria por Herencia (Polimorfismo)
     */
    @Override
    public void limpiarCampos() {
        dpInicio.setValue(null);
        dpFin.setValue(null);
        cbEstado.setValue("Todos");
        cbTipoLicencia.setValue("Todos");
        txtCedulaFiltro.clear();
        tableReportes.getItems().clear();
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
            // USAMOS EL MÉTODO HEREDADO DEL PADRE
            mostrarAlerta("Error de Búsqueda", "No se pudieron cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
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
        List<Tramite> lista = tableReportes.getItems();

        if (lista == null || lista.isEmpty()) {
            mostrarAlerta("Sin Datos", "La tabla está vacía.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Reporte a Excel (CSV)");
        chooser.setInitialFileName("Reporte_Licencias.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        File file = chooser.showSaveDialog(tableReportes.getScene().getWindow());

        if (file != null) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                // Cabecera para Excel
                pw.println("ID Tramite;Nombre Solicitante;Cedula;Estado;Fecha");

                for (Tramite t : lista) {
                    pw.println(String.format("%d;%s;%s;%s;%s",
                            t.getId(),
                            t.getNombre() != null ? t.getNombre() : "N/A",
                            t.getCedula() != null ? t.getCedula() : "N/A",
                            t.getEstado() != null ? t.getEstado() : "N/A",
                            t.getFecha() != null ? t.getFecha() : "N/A"
                    ));
                }
                pw.flush();
                mostrarAlerta("Exportación Exitosa", "Se han guardado los registros.", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                mostrarAlerta("Error al Guardar", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}