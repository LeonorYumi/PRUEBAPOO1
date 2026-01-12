package ui.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Usuario;
import service.UsuarioService;

import java.util.Optional; // Necesario para la confirmación

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private ComboBox<String> comboUsuario;

    private final UsuarioService usuarioService = new UsuarioService();
    private int intentos = 0;

    @FXML
    public void initialize() {
        comboUsuario.getItems().clear();
        comboUsuario.getItems().addAll("Administrador", "Analista");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String usuarioStr = txtUsuario.getText().trim();
        String passwordStr = txtPassword.getText().trim();
        String rolSeleccionadoLabel = comboUsuario.getValue();

        if (usuarioStr.isEmpty() || passwordStr.isEmpty() || rolSeleccionadoLabel == null) {
            mostrarError("Por favor, complete todos los campos y seleccione su rol.");
            return;
        }

        try {
            Usuario u = usuarioService.iniciarSesion(usuarioStr, passwordStr);

            if (u == null) {
                intentos++;
                if (intentos >= 3) {
                    mostrarError("Has superado el límite de 3 intentos. El sistema se cerrará.");
                    Platform.exit();
                } else {
                    mostrarError("Credenciales incorrectas.\nIntento " + intentos + " de 3.");
                }
                return;
            }

            if (!u.isActivo()) {
                mostrarError("Usuario inactivo. Contacte al administrador.");
                return;
            }

            String rolSeleccionado;
            if ("Administrador".equalsIgnoreCase(rolSeleccionadoLabel)) {
                rolSeleccionado = "ADMIN";
            } else if ("Analista".equalsIgnoreCase(rolSeleccionadoLabel)) {
                rolSeleccionado = "ANALISTA";
            } else {
                rolSeleccionado = rolSeleccionadoLabel.toUpperCase();
            }

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

            intentos = 0;
            if (u.getRol().equalsIgnoreCase("ADMIN")) {
                cargarVentana("/fxml/MenuAdminView.fxml", "Sistema de Licencias - Administrador");
            } else if (u.getRol().equalsIgnoreCase("ANALISTA")) {
                cargarVentana("/fxml/MenuAnalistaView.fxml", "Sistema de Licencias - Analista");
            } else {
                mostrarError("Rol no soportado: " + u.getRol());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error crítico al iniciar sesión: " + e.getMessage());
        }
    }

    // Manejador para el botón SALIR con confirmación
    @FXML
    private void handleExit(ActionEvent event) {
        // Crear alerta de tipo confirmación
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Salida");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Está seguro de que desea salir del sistema?");

        // Personalizar botones para que sean claros
        ButtonType btnSi = new ButtonType("Salir");
        ButtonType btnNo = new ButtonType("Regresar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSi, btnNo);

        // Mostrar y capturar respuesta
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnSi) {
            Platform.exit(); // Cierra JavaFX
            System.exit(0);  // Cierra el proceso por completo
        }
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