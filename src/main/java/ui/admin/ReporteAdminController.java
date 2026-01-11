package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import model.Tramite;
import service.TramiteService;
import ui.base.BaseController;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;

public class ReporteAdminController extends BaseController {

    @FXML private DatePicker dpInicio, dpFin;
    @FXML private ComboBox<String> cbEstado, cbTipoLicencia;
    @FXML private TextField txtCedulaFiltro;

    @FXML private TableView<Tramite> tableReportes;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colNombre, colCedula, colEstado, colFecha;

    @FXML private Label lblTotalTramites, lblTotalAprobados, lblTotalLicencias;
    @FXML private PieChart chartEstados;

    private final TramiteService tramiteService = new TramiteService();

    @FXML
    public void initialize() {
        // Configuración de Filtros
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "pendiente", "en_examenes", "aprobado", "reprobado", "licencia_emitida"));
        cbTipoLicencia.setItems(FXCollections.observableArrayList("Todos", "Tipo A", "Tipo B", "Tipo C", "Tipo D", "Tipo E"));
        cbEstado.setValue("Todos");
        cbTipoLicencia.setValue("Todos");

        // Configuración de Tabla
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
            actualizarTotalesYGrafico(resultados);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los reportes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarTotalesYGrafico(List<Tramite> lista) {
        // Totales numéricos
        long total = lista.size();
        long aprobados = lista.stream().filter(t -> "aprobado".equalsIgnoreCase(t.getEstado())).count();
        long licencias = lista.stream().filter(t -> "licencia_emitida".equalsIgnoreCase(t.getEstado())).count();

        lblTotalTramites.setText(String.valueOf(total));
        lblTotalAprobados.setText(String.valueOf(aprobados));
        lblTotalLicencias.setText(String.valueOf(licencias));

        // Gráfico Estadístico
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        long p = lista.stream().filter(t -> "pendiente".equalsIgnoreCase(t.getEstado())).count();
        long e = lista.stream().filter(t -> "en_examenes".equalsIgnoreCase(t.getEstado())).count();
        long r = lista.stream().filter(t -> "reprobado".equalsIgnoreCase(t.getEstado())).count();

        if (p > 0) pieData.add(new PieChart.Data("Pendientes", p));
        if (e > 0) pieData.add(new PieChart.Data("En Examen", e));
        if (aprobados > 0) pieData.add(new PieChart.Data("Aprobados", aprobados));
        if (licencias > 0) pieData.add(new PieChart.Data("Emitidas", licencias));
        if (r > 0) pieData.add(new PieChart.Data("Reprobados", r));

        chartEstados.setData(pieData);
    }

    @FXML
    private void handleExportar() {
        List<Tramite> lista = tableReportes.getItems();
        if (lista.isEmpty()) {
            mostrarAlerta("Aviso", "No hay datos para exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        fc.setInitialFileName("Reporte_Gestion_ANT.csv");
        File file = fc.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("ID;Nombre;Cedula;Estado;Fecha");
                for (Tramite t : lista) {
                    pw.println(t.getId() + ";" + t.getNombre() + ";" + t.getCedula() + ";" + t.getEstado() + ";" + t.getFecha());
                }
                mostrarAlerta("Éxito", "Reporte exportado correctamente.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo exportar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleVerDetalle() {
        Tramite t = tableReportes.getSelectionModel().getSelectedItem();
        if (t == null) {
            mostrarAlerta("Aviso", "Seleccione un trámite de la lista.", Alert.AlertType.WARNING);
            return;
        }
        mostrarAlerta("Detalle de Trámite",
                "Solicitante: " + t.getNombre() + "\nCédula: " + t.getCedula() +
                        "\nTipo: " + t.getTipoLicencia() + "\nEstado Actual: " + t.getEstado(),
                Alert.AlertType.INFORMATION);
    }

    @Override
    public void limpiarCampos() {
        dpInicio.setValue(null);
        dpFin.setValue(null);
        cbEstado.setValue("Todos");
        cbTipoLicencia.setValue("Todos");
        txtCedulaFiltro.clear();
        tableReportes.getItems().clear();
        chartEstados.getData().clear();
        lblTotalTramites.setText("0");
        lblTotalAprobados.setText("0");
        lblTotalLicencias.setText("0");
    }
}