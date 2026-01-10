package ui.base;

import javafx.scene.control.Alert;

public abstract class BaseController {

    // Método con TRES parámetros para permitir diferentes tipos de alertas (Polimorfismo)
    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método abstracto: cada controlador hijo DEBE decir cómo se limpia su pantalla
    public abstract void limpiarCampos();
}