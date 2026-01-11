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

        // Columna de estado formateada para mostrar texto claro
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
        cbRol.setValue(u.getRol().toLowerCase());
        cbEstado.setValue(u.isActivo() ? "activo" : "inactivo");
    }

    @FXML
    private void handleGuardar() {
        try {
            Usuario u = prepararUsuario(new Usuario());
            usuarioService.crearUsuario(u);
            mostrarAlerta("Éxito", "Usuario creado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleActualizar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Aviso", "Seleccione un usuario de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            prepararUsuario(usuarioSeleccionado);
            usuarioService.actualizarUsuario(usuarioSeleccionado);
            mostrarAlerta("Éxito", "Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Usuario prepararUsuario(Usuario u) {
        u.setNombre(txtNombre.getText());
        u.setCedula(txtCedula.getText());
        u.setUsername(txtUsername.getText());
        // Solo enviamos pass si se escribió algo
        if (!txtPassword.getText().isEmpty()) u.setPassword(txtPassword.getText());
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
}