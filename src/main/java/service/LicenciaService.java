package service;

import dao.Conexion;
import model.Licencia;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LicenciaService {

    public LicenciaService() {}

    // Método para generar la licencia (Transaccional)
    public Licencia generarLicencia(int idTramite, Integer creadoPor) throws Exception {
        // ... (Tu código actual de generarLicencia está PERFECTO, mantenlo así)
        // Solo asegúrate de que el SQLUpdate coincida con tu tabla:
        // "UPDATE tramites SET estado = 'licencia_emitida', updated_at = NOW() WHERE id = ?"
        return null; // (Aquí va tu código funcional)
    }

    // --- NUEVO MÉTODO PARA EL DASHBOARD DEL ADMIN ---
    public Map<String, Integer> obtenerTotalesPorEstado() throws Exception {
        Map<String, Integer> totales = new HashMap<>();
        String sql = "SELECT estado, COUNT(*) as total FROM tramites GROUP BY estado";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                totales.put(rs.getString("estado"), rs.getInt("total"));
            }
        }
        return totales;
    }
}