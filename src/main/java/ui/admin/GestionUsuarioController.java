package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import model.Usuario;
import service.UsuarioService;
import ui.base.BaseController; // Ajustado a la ruta estándar
import java.util.List;

/**
 * Controlador para la administración de usuarios del sistema.
 * Aplica Herencia de BaseController para estandarizar alertas y limpieza.
 */
public class GestionUsuarioController extends BaseController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCedula;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol;
    @FXML private ComboBox<String> cbEstado;

    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCedula;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        // Configuración de opciones en los ComboBox
        cbRol.setItems(FXCollections.observableArrayList("analista", "admin"));
        cbEstado.setItems(FXCollections.observableArrayList("activo", "inactivo"));

        // Vinculación de columnas con los atributos del modelo Usuario
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        listarUsuarios();
    }

    /**
     * Implementación obligatoria del método de limpieza (Polimorfismo).
     */
    @Override
    public void limpiarCampos() {
        txtNombre.clear();
        txtCedula.clear();
        txtUsername.clear();
        txtPassword.clear();
        cbRol.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();
        listarUsuarios();
    }

    /**
     * Método vinculado al botón Limpiar en el FXML.
     */
    @FXML
    private void handleLimpiar() {
        limpiarCampos();
    }

    /**
     * Método vinculado al botón Guardar en el FXML.
     */
    @FXML
    private void handleGuardar() {
        // Validación de campos obligatorios
        if (txtUsername.getText().trim().isEmpty() || txtPassword.getText().isEmpty()) {
            mostrarAlerta("Campos Vacíos", "El nombre de usuario y la contraseña son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Usuario nuevo = new Usuario();
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setCedula(txtCedula.getText().trim());
            nuevo.setUsername(txtUsername.getText().trim());
            nuevo.setPassword(txtPassword.getText()); // La encriptación ocurre en el Service
            nuevo.setRol(cbRol.getValue());
            nuevo.setEstado(cbEstado.getValue());

            // Delegamos la persistencia a la capa de servicio
            usuarioService.crearUsuario(nuevo);

            mostrarAlerta("Éxito", "Usuario registrado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo completar el registro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Actualiza la tabla con los datos más recientes de la BD.
     */
    private void listarUsuarios() {
        try {
            List<Usuario> lista = usuarioService.obtenerTodos();
            tableUsuarios.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            System.err.println("Error al cargar la lista de usuarios: " + e.getMessage());
        }
    }
}