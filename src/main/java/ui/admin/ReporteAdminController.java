package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import model.Tramite;
import service.TramiteService; // Usamos el servicio que ya tienes
import java.util.List;

public class ReporteAdminController {

    @FXML private DatePicker dpInicio, dpFin;
    @FXML private ComboBox<String> cbEstado, cbTipoLicencia;
    @FXML private TextField txtCedulaFiltro;
    @FXML private TableView<Tramite> tableReportes;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colNombre, colCedula, colEstado, colFecha;
    @FXML private Label lblTotalTramites, lblTotalAprobados, lblTotalRechazados;

    // Cambiamos a TramiteService
    private TramiteService tramiteService = new TramiteService();

    @FXML
    public void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "pendiente", "aprobado", "reprobado"));
        cbTipoLicencia.setItems(FXCollections.observableArrayList("Todos", "Tipo A", "Tipo B", "Tipo C"));

        // Vincular columnas con los atributos de la clase Tramite
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
    }

    @FXML
    private void handleBuscar() {
        try {
            // Llamamos al nuevo método en TramiteService
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
            mostrarAlerta("Error", "Error al consultar: " + e.getMessage());
        }
    }

    private void actualizarTotales(List<Tramite> lista) {
        long aprobados = lista.stream().filter(t -> t.getEstado().equalsIgnoreCase("aprobado")).count();
        long rechazados = lista.stream().filter(t -> t.getEstado().equalsIgnoreCase("reprobado")).count();

        lblTotalTramites.setText(String.valueOf(lista.size()));
        lblTotalAprobados.setText(String.valueOf(aprobados));
        lblTotalRechazados.setText(String.valueOf(rechazados));
    }

    @FXML
    private void handleExportar() {
        // Lógica simple de exportación por consola o puedes implementar un FileChooser
        if(tableReportes.getItems().isEmpty()) {
            mostrarAlerta("Atención", "No hay datos para exportar.");
            return;
        }
        System.out.println("Exportando datos a CSV...");
        mostrarAlerta("Éxito", "Datos listos para exportar (simulado).");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}