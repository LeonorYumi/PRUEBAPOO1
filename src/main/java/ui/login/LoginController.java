package ui.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;
import service.UsuarioService;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private ComboBox<String> comboUsuario;

    private final UsuarioService usuarioService = new UsuarioService();

    // 🔒 Control de intentos (Regla de negocio 2)
    private int intentos = 0;

    @FXML
    public void initialize() {
        // Cargar exactamente las etiquetas que tienes en el FXML
        comboUsuario.getItems().clear();
        comboUsuario.getItems().addAll("Administrador", "Analista");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String usuarioStr = txtUsuario.getText().trim();
        String passwordStr = txtPassword.getText().trim();
        String rolSeleccionadoLabel = comboUsuario.getValue();

        // 1. Validación de campos vacíos
        if (usuarioStr.isEmpty() || passwordStr.isEmpty() || rolSeleccionadoLabel == null) {
            mostrarError("Por favor, complete todos los campos y seleccione su rol.");
            return;
        }

        try {
            // Llamada al servicio
            Usuario u = usuarioService.iniciarSesion(usuarioStr, passwordStr);

            if (u == null) {
                // Credenciales incorrectas
                intentos++;
                if (intentos >= 3) {
                    mostrarError("Has superado el límite de 3 intentos. El sistema se cerrará.");
                    Platform.exit();
                } else {
                    mostrarError("Credenciales incorrectas.\nIntento " + intentos + " de 3.");
                }
                return;
            }

            // Verificar si el usuario está activo
            if (!u.isActivo()) {
                mostrarError("Usuario inactivo. Contacte al administrador.");
                return;
            }

            // Normalizar la selección del combo a los códigos del rol en BD
            String rolSeleccionado;
            if ("Administrador".equalsIgnoreCase(rolSeleccionadoLabel)) {
                rolSeleccionado = "ADMIN";
            } else if ("Analista".equalsIgnoreCase(rolSeleccionadoLabel)) {
                rolSeleccionado = "ANALISTA";
            } else {
                // Fallback por seguridad
                rolSeleccionado = rolSeleccionadoLabel.toUpperCase();
            }

            // Validación de rol
            if (!u.getRol().equalsIgnoreCase(rolSeleccionado)) {
                intentos++;
                if (intentos >= 3) {
                    mostrarError("Has superado el límite de 3 intentos. El sistema se cerrará.");
                    Platform.exit();
                } else {
                    mostrarError("Credenciales o rol incorrectos.\nIntento " + intentos + " de 3.");
                }
                return;
            }

            // Login exitoso: Redirección según rol
            intentos = 0;
            if (u.getRol().equalsIgnoreCase("ADMIN")) {
                cargarVentana("/fxml/MenuAdminView.fxml", "Sistema de Licencias - Administrador");
            } else if (u.getRol().equalsIgnoreCase("ANALISTA")) {
                cargarVentana("/fxml/MenuAnalistaView.fxml", "Sistema de Licencias - Analista");
            } else {
                // Por si hubiera otros roles
                mostrarError("Rol no soportado: " + u.getRol());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error crítico al iniciar sesión: " + e.getMessage());
        }
    }

    // Manejador para el botón SALIR (referenciado por onAction="#handleExit")
    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }

    private void cargarVentana(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

            Stage actual = (Stage) btnLogin.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}