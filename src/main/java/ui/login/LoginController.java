package ui.login;

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
        // Limpiamos y cargamos los roles exactos del script SQL
        comboUsuario.getItems().clear();
        comboUsuario.getItems().addAll("ADMIN", "ANALISTA");
    }

    @FXML
    private void handleLogin() {
        String usuarioStr = txtUsuario.getText().trim();
        String passwordStr = txtPassword.getText().trim();
        String rolSeleccionado = comboUsuario.getValue();

        // 1. Validación de campos vacíos
        if (usuarioStr.isEmpty() || passwordStr.isEmpty() || rolSeleccionado == null) {
            mostrarError("Por favor, complete todos los campos y seleccione su rol.");
            return;
        }


        try {
            // Llamada al servicio que usa SHA2 en el DAO
            Usuario u = usuarioService.iniciarSesion(usuarioStr, passwordStr);

            // 2. Validación de credenciales y rol (Regla de negocio 2 y 6.5)
            if (u == null || !u.getRol().equalsIgnoreCase(rolSeleccionado)) {
                intentos++;
                if (intentos >= 3) {
                    mostrarError("Has superado el límite de 3 intentos. El sistema se cerrará.");
                    System.exit(0);
                }
                mostrarError("Credenciales o rol incorrectos.\nIntento " + intentos + " de 3.");
                return;
            }

            // 3. Login exitoso: Redirección según rol
            intentos = 0;

            // Comparamos con los valores que vienen del JOIN en la BD (ADMIN/ANALISTA)
            if (u.getRol().equalsIgnoreCase("ADMIN")) {
                cargarVentana("/fxml/MenuAdminView.fxml", "Sistema de Licencias - Administrador");
            } else if (u.getRol().equalsIgnoreCase("ANALISTA")) {
                cargarVentana("/fxml/MenuAnalistaView.fxml", "Sistema de Licencias - Analista");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error crítico al iniciar sesión: " + e.getMessage());
        }
    }

    private void cargarVentana(String ruta, String titulo) {
        try {
            // Cargamos el recurso usando la ruta absoluta dentro de resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();

            // Configuramos la nueva escena
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));

            // Centrar ventana (opcional)
            stage.show();

            // Cerramos la ventana de Login actual
            Stage actual = (Stage) btnLogin.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            System.err.println("Error cargando FXML: " + ruta);
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista del menú. Verifique la consola.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Acceso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void handleExit() {
        // Esto cierra la aplicación por completo
        System.exit(0);
    }
}