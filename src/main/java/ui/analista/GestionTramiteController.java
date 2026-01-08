package ui.analista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.Tramite;
import service.RequisitoService;

public class GestionTramiteController {

    @FXML private TableView<Tramite> tablaTramites;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colCedula, colNombre, colTipo, colFecha, colEstado;
    @FXML private TableColumn<Tramite, Void> colAcciones;

    @FXML private ComboBox<String> comboFiltroEstado;

    private final RequisitoService requisitoService = new RequisitoService();
    private ObservableList<Tramite> listaMaster = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        agregarBotonesAccion();
        configurarFiltros();
        cargarDatosReales();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colCedula.setCellValueFactory(cellData -> cellData.getValue().cedulaProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
    }


    @FXML
    private void handleFiltrar() {
        String filtro = comboFiltroEstado.getValue();
        if (filtro == null || filtro.equals("Todos")) {
            tablaTramites.setItems(listaMaster);
        } else {
            ObservableList<Tramite> filtrada = listaMaster.filtered(t ->
                    t.getEstado().equalsIgnoreCase(filtro)
            );
            tablaTramites.setItems(filtrada);
        }
    }


    @FXML
    private void handleVerDetalle() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Información", "Detalle del trámite ID: " + seleccionado.getId() + "\nSolicitante: " + seleccionado.getNombre());
        } else {
            mostrarAlerta("Atención", "Por favor, seleccione un trámite de la tabla.");
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            if ("aprobado".equalsIgnoreCase(seleccionado.getEstado())) {
                // Mensaje corregido y útil
                mostrarAlerta("Trámite Aprobado",
                        "Para emitir la licencia física de " + seleccionado.getNombre() +
                                ":\n1. Diríjase a la sección 'Detalle de Trámite'.\n" +
                                "2. Ingrese el ID: " + seleccionado.getId() + "\n" +
                                "3. Presione el botón 'Generar Licencia' al final del formulario.");
            } else {
                mostrarAlerta("Atención", "Este trámite aún no ha sido aprobado.");
            }
        } else {
            mostrarAlerta("Atención", "Por favor, seleccione un trámite de la tabla.");
        }
    }

    private void agregarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnValidar = new Button("Validar");
            private final HBox pane = new HBox(5, btnValidar);

            {
                btnValidar.setOnAction(event -> {
                    Tramite t = getTableView().getItems().get(getIndex());
                    try {
                        // Marcamos requisitos básicos como completados
                        requisitoService.guardarRequisitos(t.getId(), true, true, true, "Validación desde gestión", null);
                        t.setEstado("en_examenes");
                        getTableView().refresh();
                        mostrarAlerta("Éxito", "Requisitos validados. El trámite pasó a 'en_examenes'.");
                    } catch (Exception e) {
                        mostrarAlerta("Error", e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void configurarFiltros() {
        if (comboFiltroEstado != null) {
            comboFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "pendiente", "requisitos", "en_examenes", "aprobado", "licencia_emitida"));
            comboFiltroEstado.setValue("Todos");
        }
    }

    private void cargarDatosReales() {
        listaMaster.clear();
        // Datos de ejemplo para que la tabla no esté vacía
        listaMaster.addAll(
                new Tramite(15, "1104587234", "Juan Pérez", "B", "2025-01-10", "pendiente"),
                new Tramite(11, "0923478902", "María López", "A", "2025-01-09", "aprobado")
        );
        tablaTramites.setItems(listaMaster);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}