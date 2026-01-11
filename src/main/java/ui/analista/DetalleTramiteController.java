package ui.analista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import model.Tramite;
import service.TramiteService;
import service.RequisitoService;
import ui.base.BaseController;
import java.io.IOException;
import java.util.List;

public class DetalleTramiteController extends BaseController {

    @FXML private TextField txtBusquedaId, txtNotaTeorica, txtNotaPractica;
    @FXML private Label lblNombre, lblCedula, lblEstadoActual;
    @FXML private CheckBox chkFotos, chkCertificado, chkMultas;
    @FXML private Button btnGenerarLicencia, btnGuardarReq, btnGuardarNotas;

    private final TramiteService tramiteService = new TramiteService();
    private final RequisitoService requisitoService = new RequisitoService();
    private Tramite tramiteEncontrado;

<<<<<<< HEAD
=======
    /**
     * Implementación obligatoria del método abstracto.
     */
>>>>>>> c2b06fcb9d299383a4c094a7d25472c038b5fcc4
    @Override
    public void limpiarCampos() {
        txtBusquedaId.clear();
        lblNombre.setText("Nombre: -");
        lblCedula.setText("Cédula: -");
        lblEstadoActual.setText("Estado: -");
        txtNotaTeorica.clear();
        txtNotaPractica.clear();
        chkFotos.setSelected(false);
        chkCertificado.setSelected(false);
        chkMultas.setSelected(false);
        habilitarEdicion(true);
        btnGenerarLicencia.setDisable(true);
    }

    private void habilitarEdicion(boolean editable) {
        txtNotaTeorica.setEditable(editable);
        txtNotaPractica.setEditable(editable);
        chkFotos.setDisable(!editable);
        chkCertificado.setDisable(!editable);
        chkMultas.setDisable(!editable);
        btnGuardarReq.setDisable(!editable);
        btnGuardarNotas.setDisable(!editable);
    }

    @FXML
    private void handleBuscar() {
        try {
            String busqueda = txtBusquedaId.getText().trim();

            // --- VALIDACIÓN DE CÉDULA ---
            if (busqueda.isEmpty()) {
                mostrarAlerta("Campo Vacío", "Por favor, ingrese un número de cédula.", Alert.AlertType.WARNING);
                return;
            }

<<<<<<< HEAD
            // Validar que sean exactamente 10 dígitos numéricos
            if (!busqueda.matches("\\d{10}")) {
                mostrarAlerta("Formato Incorrecto",
                        "La cédula debe contener exactamente 10 dígitos numéricos.\nEvite ingresar nombres o caracteres especiales.",
                        Alert.AlertType.ERROR);
                return;
            }

            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", busqueda);
=======
            List<Tramite> resultados = tramiteService.consultarTramitesReporte(null, null, "Todos", "Todos", cedula);
>>>>>>> c2b06fcb9d299383a4c094a7d25472c038b5fcc4

            if (resultados != null && !resultados.isEmpty()) {
                tramiteEncontrado = resultados.get(0);

                // 1. Mostrar Datos Básicos
                lblNombre.setText("Nombre: " + tramiteEncontrado.getNombre());
                lblCedula.setText("Cédula: " + tramiteEncontrado.getCedula());
                lblEstadoActual.setText("Estado: " + tramiteEncontrado.getEstado().toUpperCase());

<<<<<<< HEAD
                // 2. Cargar Requisitos (Visualización histórica)
                // Si el trámite ya pasó de 'pendiente', los requisitos están aprobados
                boolean yaPasoRequisitos = !tramiteEncontrado.getEstado().equalsIgnoreCase("pendiente");
                chkFotos.setSelected(yaPasoRequisitos);
                chkCertificado.setSelected(yaPasoRequisitos);
                chkMultas.setSelected(yaPasoRequisitos);

                // 3. Cargar Notas (Visualización histórica)
                txtNotaTeorica.setText(tramiteEncontrado.getNotaTeorica() > 0 ? String.valueOf(tramiteEncontrado.getNotaTeorica()) : "");
                txtNotaPractica.setText(tramiteEncontrado.getNotaPractica() > 0 ? String.valueOf(tramiteEncontrado.getNotaPractica()) : "");

                // 4. Lógica de Bloqueo según estado (Control de flujo)
=======
>>>>>>> c2b06fcb9d299383a4c094a7d25472c038b5fcc4
                String estado = tramiteEncontrado.getEstado().toLowerCase();

                if (estado.equals("licencia_emitida") || estado.equals("aprobado")) {
                    habilitarEdicion(false); // Bloquear cambios si ya finalizó
                    // El botón se habilita solo si es 'aprobado' para permitir imprimir
                    btnGenerarLicencia.setDisable(!estado.equals("aprobado"));
                } else if (estado.equals("en_examenes")) {
                    habilitarEdicion(true);
                    btnGuardarReq.setDisable(true); // Ya no se editan requisitos
                    btnGenerarLicencia.setDisable(true);
                } else {
                    // Estado: Pendiente
                    habilitarEdicion(true);
                    btnGuardarNotas.setDisable(true); // No puede rendir examen sin requisitos
                    btnGenerarLicencia.setDisable(true);
                }

            } else {
<<<<<<< HEAD
                limpiarCampos();
                mostrarAlerta("Sin Resultados", "No se encontró ningún trámite con la cédula ingresada.", Alert.AlertType.INFORMATION);
=======
                tramiteEncontrado = null;
                btnGenerarLicencia.setDisable(true);
                mostrarAlerta("No encontrado", "No existen trámites para la cédula: " + cedula, Alert.AlertType.INFORMATION);
>>>>>>> c2b06fcb9d299383a4c094a7d25472c038b5fcc4
            }
        } catch (Exception e) {
            mostrarAlerta("Error de Sistema", "Ocurrió un error al consultar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGuardarRequisitos() {
        if (tramiteEncontrado == null) return;
        try {
            requisitoService.guardarRequisitos(tramiteEncontrado.getId(),
                    chkFotos.isSelected(), chkCertificado.isSelected(), chkMultas.isSelected(),
                    "Validado desde Detalle", null);
            mostrarAlerta("Éxito", "Requisitos validados correctamente. El trámite avanza a fase de exámenes.", Alert.AlertType.INFORMATION);
            handleBuscar(); // Refrescar vista
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar los requisitos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGuardarNotas() {
        if (tramiteEncontrado == null) return;
        try {
            double nt = Double.parseDouble(txtNotaTeorica.getText().replace(",", "."));
            double np = Double.parseDouble(txtNotaPractica.getText().replace(",", "."));

            // Regla de negocio: Notas deben ser positivas
            if (nt < 0 || nt > 20 || np < 0 || np > 20) {
                mostrarAlerta("Nota Inválida", "Las notas deben estar entre 0 y 20.", Alert.AlertType.WARNING);
                return;
            }

            tramiteService.registrarExamen(tramiteEncontrado.getId(), nt, np);
            String res = (nt >= 14 && np >= 14) ? "APROBADO" : "REPROBADO";
            mostrarAlerta("Evaluación Registrada", "Resultado: " + res, Alert.AlertType.INFORMATION);
            handleBuscar(); // Refrescar vista
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Asegúrese de ingresar números válidos en las notas.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar exámenes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerarLicencia() {
        try {
            // 1. Cargar el FXML de la vista Generar Licencia
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarLicenciaView.fxml"));
            Parent root = loader.load();
<<<<<<< HEAD
            ui.analista.GenerarLicenciaController controller = loader.getController();
            controller.initData(tramiteEncontrado);

=======

            // 2. OBTENER EL CONTROLADOR (Corregido a ui.analista para evitar ClassCastException)
            ui.analista.GenerarLicenciaController controller = loader.getController();

            // 3. Pasar los datos al nuevo controlador
            if (controller != null) {
                controller.initData(tramiteEncontrado);
            }

            // 4. Cambiamos el contenido del área central
>>>>>>> c2b06fcb9d299383a4c094a7d25472c038b5fcc4
            StackPane contentArea = (StackPane) btnGenerarLicencia.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }

        } catch (ClassCastException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Tipo", "El controlador del FXML no coincide con la clase esperada.", Alert.AlertType.ERROR);
        } catch (IOException e) {
<<<<<<< HEAD
            mostrarAlerta("Navegación Fallida", "No se pudo abrir el módulo de emisión: " + e.getMessage(), Alert.AlertType.ERROR);
=======
            e.printStackTrace();
            mostrarAlerta("Error de Carga", "No se pudo cargar la vista de la licencia.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
>>>>>>> c2b06fcb9d299383a4c094a7d25472c038b5fcc4
        }
    }
}