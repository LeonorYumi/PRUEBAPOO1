package ui.analista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Tramite;
import service.RequisitoService;
import java.io.IOException;

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
        if (comboFiltroEstado == null) return;
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
            mostrarAlerta("Detalle del Trámite",
                    "ID: " + seleccionado.getId() + "\nSolicitante: " + seleccionado.getNombre());
        } else {
            mostrarAlerta("Atención", "Por favor, seleccione un trámite de la tabla.");
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, seleccione un trámite de la tabla.");
            return;
        }

        if ("aprobado".equalsIgnoreCase(seleccionado.getEstado())) {
            try {
                // 1. Cargamos la vista de generación de licencia
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
                Parent root = loader.load();

                // 2. Pasamos los datos al controlador correspondiente
                Object ctrl = loader.getController();

                // Verificamos el paquete del controlador para hacer el cast correcto
                if (ctrl instanceof ui.admin.GenerarLicenciaController) {
                    ((ui.admin.GenerarLicenciaController) ctrl).initData(seleccionado);
                } else if (ctrl instanceof ui.analista.GenerarLicenciaController) {
                    ((ui.analista.GenerarLicenciaController) ctrl).initData(seleccionado);
                }

                // 3. Abrimos como ventana POP-UP independiente
                Stage stage = new Stage();
                stage.setTitle("Emisión de Licencia Física - " + seleccionado.getNombre());

                // Bloquea la ventana principal hasta que se cierre esta
                stage.initModality(Modality.APPLICATION_MODAL);

                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.showAndWait();

                // Refrescamos la tabla por si el estado cambió a 'licencia_emitida'
                // cargarDatosReales();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir la ventana de emisión.");
            }
        } else {
            mostrarAlerta("Acción denegada",
                    "Solo se puede generar la licencia si el trámite está en estado 'aprobado'.\n" +
                            "Estado actual: " + seleccionado.getEstado());
        }
    }

    private void configurarFiltros() {
        if (comboFiltroEstado != null) {
            comboFiltroEstado.setItems(FXCollections.observableArrayList(
                    "Todos", "pendiente", "en_examenes", "aprobado", "licencia_emitida"
            ));
            comboFiltroEstado.setValue("Todos");
        }
    }

    private void agregarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnValidar = new Button("Validar");
            {
                btnValidar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnValidar.setOnAction(event -> {
                    Tramite t = getTableView().getItems().get(getIndex());
                    try {
                        // Simula la validación de requisitos
                        requisitoService.guardarRequisitos(t.getId(), true, true, true, "Validación Automática", null);
                        t.setEstado("en_examenes");
                        getTableView().refresh();
                    } catch (Exception e) {
                        mostrarAlerta("Error", "No se pudo validar: " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Tramite t = getTableView().getItems().get(getIndex());
                    // Solo mostramos validar si está pendiente
                    setGraphic("pendiente".equalsIgnoreCase(t.getEstado()) ? btnValidar : null);
                }
            }
        });
    }

    private void cargarDatosReales() {
        listaMaster.clear();
        // Datos de ejemplo (Sustituir por llamada a BD si es necesario)
        listaMaster.addAll(
                new Tramite(15, "1104587234", "Juan Pérez", "B", "2025-01-10", "pendiente"),
                new Tramite(11, "0923478902", "María López", "A", "2025-01-09", "aprobado"),
                new Tramite(22, "1712345678", "Carlos Ruiz", "C", "2025-01-08", "en_examenes")
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