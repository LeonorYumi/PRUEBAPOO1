package ui.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private ComboBox<String> comboUsuario;

    @FXML
    public void initialize() {
        System.out.println("Controlador de Login inicializado correctamente.");
    }

    @FXML
    private void handleLogin() {
        String rol = (comboUsuario != null) ? comboUsuario.getValue() : null;
        String usuario = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (rol == null) {
            System.out.println("Error: Debe seleccionar un rol");
            return;
        }

        if (usuario.isEmpty() || pass.isEmpty()) {
            System.out.println("Error: Campos vacíos");
            return;
        }

        // --- LÓGICA DE NAVEGACIÓN ---
        // Por ahora, como es un prototipo, entramos directo según el rol
        if (rol.equals("Administrador")) {
            cargarVentana("/fxml/MenuAdminView.fxml", "Panel de Administrador");
        } else if (rol.equals("Analista")) {
            cargarVentana("/fxml/MenuAnalistaView.fxml", "Panel de Analista");
        }
    }

    // Este es el método que usa getClass().getResource()
    private void cargarVentana(String rutaFxml, String titulo) {
        try {
            // 1. Cargamos el archivo FXML desde la carpeta resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();

            // 2. Creamos la nueva escena
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

            // 3. Cerramos la ventana de Login actual para que no queden dos abiertas
            Stage loginStage = (Stage) btnLogin.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }
}