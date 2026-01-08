package ui.admin;

import dao.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DashboardTotalesController {

    @FXML private PieChart chartEstados;

    @FXML
    public void initialize() {
        // Se ejecuta automáticamente al cargar la vista
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        String sql = "SELECT estado, COUNT(*) as total FROM tramites GROUP BY estado";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean tieneDatos = false;
            while (rs.next()) {
                tieneDatos = true;
                String estado = rs.getString("estado");
                int total = rs.getInt("total");

                // Agregamos la rebanada al gráfico (Ej: "APROBADO (5)")
                pieData.add(new PieChart.Data(estado.toUpperCase() + " (" + total + ")", total));
            }

            if (!tieneDatos) {
                chartEstados.setTitle("No hay trámites registrados aún");
            } else {
                chartEstados.setData(pieData);
                chartEstados.setTitle("Distribución Global de Trámites");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar el dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}