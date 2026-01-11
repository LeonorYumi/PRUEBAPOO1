package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import model.Usuario;
import service.UsuarioService;
import ui.base.BaseController;
import java.util.List;

public class GestionUsuarioController extends BaseController {

    @FXML private TextField txtNombre, txtCedula, txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol, cbEstado;
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre, colCedula, colUsername, colRol, colEstado;

    private final UsuarioService usuarioService = new UsuarioService();
    private Usuario usuarioSeleccionado;

    @FXML
    public void initialize() {
        cbRol.setItems(FXCollections.observableArrayList("analista", "admin"));
        cbEstado.setItems(FXCollections.observableArrayList("activo", "inactivo"));

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colEstado.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActivo() ? "activo" : "inactivo")
        );

        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarUsuario(newVal);
        });

        listarUsuarios();
    }

    private void cargarUsuario(Usuario u) {
        usuarioSeleccionado = u;
        txtNombre.setText(u.getNombre());
        txtCedula.setText(u.getCedula());
        txtUsername.setText(u.getUsername());
        txtPassword.clear();
        // Agregamos una validación para evitar nulls al cargar
        if (u.getRol() != null) cbRol.setValue(u.getRol().toLowerCase());
        cbEstado.setValue(u.isActivo() ? "activo" : "inactivo");
    }

    @FXML
    private void handleGuardar() {
        // VALIDACIÓN ANTES DE PREPARAR: Evita el error de NullPointerException
        if (validarCampos()) {
            try {
                Usuario u = prepararUsuario(new Usuario());
                usuarioService.crearUsuario(u);
                mostrarAlerta("Éxito", "Usuario creado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleActualizar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Aviso", "Seleccione un usuario de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (validarCampos()) { // Validamos que los combos tengan selección antes de actualizar
            try {
                prepararUsuario(usuarioSeleccionado);
                usuarioService.actualizarUsuario(usuarioSeleccionado);
                mostrarAlerta("Éxito", "Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // MÉTODO DE VALIDACIÓN: El cambio más importante
    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtCedula.getText().isEmpty() ||
                txtUsername.getText().isEmpty() || cbRol.getValue() == null || cbEstado.getValue() == null) {

            mostrarAlerta("Datos Incompletos", "Por favor, llene todos los campos y seleccione Rol y Estado.", Alert.AlertType.WARNING);
            return false;
        }

        // Si es un usuario nuevo, la contraseña es obligatoria
        if (usuarioSeleccionado == null && txtPassword.getText().isEmpty()) {
            mostrarAlerta("Contraseña Requerida", "Debe asignar una contraseña al nuevo usuario.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private Usuario prepararUsuario(Usuario u) {
        u.setNombre(txtNombre.getText().trim());
        u.setCedula(txtCedula.getText().trim());
        u.setUsername(txtUsername.getText().trim());

        if (!txtPassword.getText().isEmpty()) {
            u.setPassword(txtPassword.getText());
        }

        u.setRol(cbRol.getValue());
        u.setActivo("activo".equals(cbEstado.getValue()));
        return u;
    }

    @Override
    public void limpiarCampos() {
        usuarioSeleccionado = null;
        txtNombre.clear(); txtCedula.clear(); txtUsername.clear(); txtPassword.clear();
        cbRol.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();
        listarUsuarios();
    }

    private void listarUsuarios() {
        try {
            List<Usuario> lista = usuarioService.obtenerTodos();
            tableUsuarios.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            System.err.println("Error al cargar tabla: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Aviso", "Seleccione un usuario de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Ventana de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar a " + usuarioSeleccionado.getNombre() + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                // Llamamos al servicio para borrar
                usuarioService.eliminarUsuario(usuarioSeleccionado.getCedula());

                mostrarAlerta("Éxito", "Usuario eliminado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos(); // Esto refresca la tabla automáticamente
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}