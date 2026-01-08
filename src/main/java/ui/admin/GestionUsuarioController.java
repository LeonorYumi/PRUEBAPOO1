package ui.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import model.Usuario;
import service.UsuarioService;
import java.util.List;

public class GestionUsuarioController {

    // Campos de texto (FX:ID coinciden con el requerimiento 4.7)
    @FXML private TextField txtNombre, txtCedula, txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol, cbEstado;

    // Tabla y columnas
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre, colCedula, colUsername, colRol, colEstado;

    private UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        // 1. Llenar opciones de los ComboBox
        cbRol.setItems(FXCollections.observableArrayList("analista", "admin"));
        cbEstado.setItems(FXCollections.observableArrayList("activo", "inactivo"));

        // 2. Configurar las columnas para que lean del modelo Usuario
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // 3. Cargar datos iniciales de la base de datos
        listarUsuarios();
    }

    @FXML
    private void handleGuardar() {
        try {
            // Creamos el objeto con los datos de los campos
            Usuario nuevo = new Usuario();
            nuevo.setNombre(txtNombre.getText());
            nuevo.setCedula(txtCedula.getText());
            nuevo.setUsername(txtUsername.getText());
            nuevo.setPassword(txtPassword.getText());
            nuevo.setRol(cbRol.getValue());
            nuevo.setEstado(cbEstado.getValue());

            usuarioService.crearUsuario(nuevo);
            mostrarAlerta("Éxito", "Usuario guardado en la base de datos.");
            handleLimpiar();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        txtNombre.clear();
        txtCedula.clear();
        txtUsername.clear();
        txtPassword.clear();
        cbRol.getSelectionModel().selectFirst();
        cbEstado.getSelectionModel().selectFirst();
        listarUsuarios();
    }

    private void listarUsuarios() {
        try {
            List<Usuario> lista = usuarioService.obtenerTodos();
            tableUsuarios.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}