package ui.analista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Tramite;
import service.RequisitoService;
import service.TramiteService;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class GestionTramiteController {

    @FXML private TableView<Tramite> tablaTramites;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colCedula, colNombre, colTipo, colFecha, colEstado;
    @FXML private TableColumn<Tramite, Void> colAcciones;
    @FXML private ComboBox<String> comboFiltroEstado;

    private final RequisitoService requisitoService = new RequisitoService();
    private final TramiteService tramiteService = new TramiteService();
    private ObservableList<Tramite> listaMaster = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        agregarBotonesAccion();
        configurarFiltros();
        cargarDatosReales();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoLicencia"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    @FXML
    private void handleFiltrar() {
        if (comboFiltroEstado == null) return;
        String filtro = comboFiltroEstado.getValue();
        if (filtro == null || filtro.equals("Todos")) {
            tablaTramites.setItems(listaMaster);
        } else {
            ObservableList<Tramite> filtrada = listaMaster.filtered(t ->
                    t.getEstado() != null && t.getEstado().equalsIgnoreCase(filtro)
            );
            tablaTramites.setItems(filtrada);
        }
    }

    private void cargarDatosReales() {
        try {
            listaMaster.clear();
            List<Tramite> tramitesDesdeBD = tramiteService.listarTodosLosTramites();
            if (tramitesDesdeBD != null) {
                listaMaster.addAll(tramitesDesdeBD);
            }
            tablaTramites.setItems(listaMaster);
        } catch (Exception e) {
            mostrarAlerta("Error de Conexión", "No se pudieron obtener los datos: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerDetalle() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Detalle del Trámite",
                    "ID: " + seleccionado.getId() +
                            "\nSolicitante: " + seleccionado.getNombre() +
                            "\nEstado: " + seleccionado.getEstado());
        } else {
            mostrarAlerta("Atención", "Por favor, seleccione un trámite de la tabla.");
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, seleccione un trámite.");
            return;
        }

        if ("aprobado".equalsIgnoreCase(seleccionado.getEstado())) {
            abrirVentanaGenerar(seleccionado);
        } else {
            mostrarAlerta("Acción denegada", "Solo trámites con estado 'aprobado' pueden generar licencia.");
        }
    }

    private void abrirVentanaGenerar(Tramite seleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();

            // SOLUCIÓN AL ERROR DE CAST: Usamos reflexión para invocar initData
            // No importa en qué paquete esté el controlador, esto funcionará.
            Object controller = loader.getController();
            try {
                Method initMethod = controller.getClass().getMethod("initData", Tramite.class);
                initMethod.invoke(controller, seleccionado);
            } catch (Exception e) {
                System.out.println("Error al pasar datos: " + e.getMessage());
            }

            Stage stage = new Stage();
            stage.setTitle("Emisión de Licencia - " + seleccionado.getNombre());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarDatosReales();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
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
                        requisitoService.guardarRequisitos(t.getId(), true, true, true, "Validación Automática", 1);
                        cargarDatosReales();
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
                    setGraphic("pendiente".equalsIgnoreCase(t.getEstado()) ? btnValidar : null);
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}