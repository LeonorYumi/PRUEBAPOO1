package ui.analista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.Tramite;

public class GestionTramiteController {

    @FXML private TableView<Tramite> tablaTramites;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colCedula, colNombre, colTipo, colFecha, colEstado;
    @FXML private TableColumn<Tramite, Void> colAcciones;
    @FXML private ComboBox<String> comboFiltroEstado;

    private ObservableList<Tramite> listaMaster = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        agregarBotonesAccion(); // Aquí se crean los botones de Req OK y Examen
        cargarDatosPrueba();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colCedula.setCellValueFactory(cellData -> cellData.getValue().cedulaProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
    }

    private void agregarBotonesAccion() {
        Callback<TableColumn<Tramite, Void>, TableCell<Tramite, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnReq = new Button("Req OK");
            private final Button btnExamen = new Button("Examen");
            private final HBox pane = new HBox(5, btnReq, btnExamen);

            {
                btnReq.setOnAction(event -> {
                    Tramite t = getTableView().getItems().get(getIndex());
                    System.out.println("Marcando Requisitos OK para: " + t.getId());
                    t.setEstado("en_examenes"); // Simulación de cambio de estado
                });

                btnExamen.setOnAction(event -> {
                    Tramite t = getTableView().getItems().get(getIndex());
                    System.out.println("Abriendo Registro de Examen para: " + t.getId());
                    // Aquí podrías llamar a un método del AnalistaController para cambiar de vista
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void cargarDatosPrueba() {
        listaMaster.addAll(
                new Tramite(15, "1104587234", "Juan Pérez", "B", "2025-01-10 09:20", "pendiente"),
                new Tramite(14, "0923478902", "María López", "A", "2025-01-09 14:33", "requisitos"),
                new Tramite(11, "1109872341", "Rosa Gualán", "B", "2025-01-08 16:45", "aprobado")
        );
        tablaTramites.setItems(listaMaster);
        comboFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "pendiente", "requisitos", "en_examenes", "aprobado"));
    }

    @FXML private void handleFiltrar() { /* lógica de filtrado */ }

    @FXML private void handleVerDetalle() {
        Tramite sel = tablaTramites.getSelectionModel().getSelectedItem();
        if (sel != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalle del Trámite");
            alert.setHeaderText("Trámite #" + sel.getId());
            alert.setContentText("Solicitante: " + sel.nombreProperty().get() + "\nEstado actual: " + sel.getEstado());
            alert.showAndWait();
        }
    }

    @FXML private void handleGenerarLicencia() {
        Tramite sel = tablaTramites.getSelectionModel().getSelectedItem();
        if (sel != null && sel.getEstado().equals("aprobado")) {
            System.out.println("Generando licencia para " + sel.getId());
        } else {
            System.out.println("El trámite debe estar en estado 'aprobado'");
        }
    }
}