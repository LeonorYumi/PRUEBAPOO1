package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    public void initialize() {
        // Este método se ejecuta cuando se carga la vista
        System.out.println("Controlador de Login inicializado correctamente.");
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String pass = txtPassword.getText();
        System.out.println("Intento de login con: " + usuario);
    }
}