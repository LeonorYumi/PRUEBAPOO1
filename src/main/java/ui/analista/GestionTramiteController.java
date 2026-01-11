package ui.analista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.RequisitoService;
import service.TramiteService;
import ui.base.BaseController;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GestionTramiteController extends BaseController {

    @FXML private TableView<Tramite> tablaTramites;
    @FXML private TableColumn<Tramite, Integer> colId;
    @FXML private TableColumn<Tramite, String> colCedula, colNombre, colTipo, colFecha, colEstado;
    @FXML private TableColumn<Tramite, Void> colAcciones;
    @FXML private ComboBox<String> comboFiltroEstado;

    private final RequisitoService requisitoService = new RequisitoService();
    private final TramiteService tramiteService = new TramiteService();
    private ObservableList<Tramite> listaMaster = FXCollections.observableArrayList();

    @Override
    public void limpiarCampos() {
        if (comboFiltroEstado != null) comboFiltroEstado.setValue("Todos");
        cargarDatosReales();
    }

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

    private void agregarBotonesAccion() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            // Se cambian los botones para que tengan el texto solicitado
            private final Button btnValidar = new Button("Req OK");
            private final Button btnExamen = new Button("Registrar Examen");
            private final HBox container = new HBox(8, btnValidar, btnExamen);

            {
                // Estilos para que se vean como botones de acción claros
                btnValidar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btnExamen.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");

                container.setAlignment(Pos.CENTER);

                btnValidar.setOnAction(event -> {
                    Tramite t = getTableView().getItems().get(getIndex());
                    manejarValidacionRapida(t);
                });

                btnExamen.setOnAction(event -> {
                    Tramite t = getTableView().getItems().get(getIndex());
                    manejarRegistroExamen(t);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Tramite t = getTableView().getItems().get(getIndex());
                    String estado = t.getEstado().toLowerCase();

                    // Lógica de visibilidad según el requerimiento
                    btnValidar.setVisible("pendiente".equalsIgnoreCase(estado));
                    btnExamen.setVisible("en_examenes".equalsIgnoreCase(estado));

                    setGraphic(container);
                }
            }
        });
    }

    private void manejarValidacionRapida(Tramite t) {
        try {
            requisitoService.guardarRequisitos(t.getId(), true, true, true, "Validación rápida", null);
            tramiteService.actualizarEstado(t.getId(), "en_examenes");
            cargarDatosReales();
            mostrarAlerta("Éxito", "Requisitos marcados como OK. El trámite pasó a EN EXÁMENES.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo validar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void manejarRegistroExamen(Tramite t) {
        Dialog<double[]> dialog = new Dialog<>();
        dialog.setTitle("Registro de Notas");
        dialog.setHeaderText("Evaluación para: " + t.getNombre());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        TextField txtTeorica = new TextField(); txtTeorica.setPromptText("0 - 20");
        TextField txtPractica = new TextField(); txtPractica.setPromptText("0 - 20");

        grid.add(new Label("Nota Teórica:"), 0, 0); grid.add(txtTeorica, 1, 0);
        grid.add(new Label("Nota Práctica:"), 0, 1); grid.add(txtPractica, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == btnGuardar) {
                try {
                    return new double[]{
                            Double.parseDouble(txtTeorica.getText().replace(",", ".")),
                            Double.parseDouble(txtPractica.getText().replace(",", "."))
                    };
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<double[]> result = dialog.showAndWait();
        result.ifPresent(notas -> {
            try {
                if (notas[0] < 0 || notas[0] > 20 || notas[1] < 0 || notas[1] > 20) {
                    mostrarAlerta("Error", "Las notas deben estar entre 0 y 20.", Alert.AlertType.ERROR);
                    return;
                }

                // Aprobación según regla 14/20
                String resultado = (notas[0] >= 14 && notas[1] >= 14) ? "aprobado" : "reprobado";
                tramiteService.registrarNotas(t.getId(), notas[0], notas[1], resultado);
                cargarDatosReales();
                mostrarAlerta("Registro Completo", "Estado final: " + resultado.toUpperCase(), Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al guardar notas: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleFiltrar() {
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
            mostrarAlerta("Error", "Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccione un trámite aprobado.", Alert.AlertType.WARNING);
            return;
        }

        if ("aprobado".equalsIgnoreCase(seleccionado.getEstado())) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
                Parent root = loader.load();

                ui.analista.GenerarLicenciaController controller = loader.getController();
                controller.initData(seleccionado);

                StackPane contentArea = (StackPane) tablaTramites.getScene().lookup("#contentArea");
                if (contentArea != null) {
                    contentArea.getChildren().setAll(root);
                }
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo abrir el módulo de licencias.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Denegado", "Solo trámites aprobados pueden generar licencia.", Alert.AlertType.ERROR);
        }
    }

    private void configurarFiltros() {
        if (comboFiltroEstado != null) {
            comboFiltroEstado.setItems(FXCollections.observableArrayList(
                    "Todos", "pendiente", "en_examenes", "aprobado", "reprobado", "licencia_emitida"
            ));
            comboFiltroEstado.setValue("Todos");
        }
    }

    @FXML
    private void handleVerDetalle() {
        Tramite seleccionado = tablaTramites.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetalleTramiteView.fxml"));
                Parent root = loader.load();
                StackPane contentArea = (StackPane) tablaTramites.getScene().lookup("#contentArea");
                if (contentArea != null) contentArea.getChildren().setAll(root);
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo cargar el detalle.", Alert.AlertType.ERROR);
            }
        }
    }
}