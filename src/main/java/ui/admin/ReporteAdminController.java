package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import model.Tramite;
import service.TramiteService;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ReporteAdminController {

    @FXML private DatePicker dpInicio, dpFin;
    @FXML private ComboBox<String> cbEstado, cbTipoLicencia;
    @FXML private TextField txtCedulaFiltro;
    @FXML private TableView<Tramite> tableReportes;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colNombre, colCedula, colEstado, colFecha;
    @FXML private Label lblTotalTramites, lblTotalAprobados, lblTotalRechazados;

    private TramiteService tramiteService = new TramiteService();

    @FXML
    public void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "pendiente", "aprobado", "reprobado"));
        cbTipoLicencia.setItems(FXCollections.observableArrayList("Todos", "Tipo A", "Tipo B", "Tipo C"));

        // Valores por defecto
        cbEstado.setValue("Todos");
        cbTipoLicencia.setValue("Todos");

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
            mostrarAlerta("Error", "No se pudo realizar la búsqueda: " + e.getMessage());
        }
    }

    private void actualizarTotales(List<Tramite> lista) {
        long aprobados = lista.stream().filter(t -> "aprobado".equalsIgnoreCase(t.getEstado())).count();
        long rechazados = lista.stream().filter(t -> "reprobado".equalsIgnoreCase(t.getEstado())).count();
        lblTotalTramites.setText(String.valueOf(lista.size()));
        lblTotalAprobados.setText(String.valueOf(aprobados));
        lblTotalRechazados.setText(String.valueOf(rechazados));
    }

    @FXML
    private void handleExportar() {
        List<Tramite> lista = tableReportes.getItems();
        if (lista.isEmpty()) {
            mostrarAlerta("Atención", "No hay datos para exportar.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar Reporte CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = chooser.showSaveDialog(tableReportes.getScene().getWindow());

        if (file != null) {
            try {
                tramiteService.exportarA_CSV(lista, file);
                mostrarAlerta("Éxito", "Archivo guardado en: " + file.getAbsolutePath());
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al exportar: " + e.getMessage());
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