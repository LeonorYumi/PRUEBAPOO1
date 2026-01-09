package dao;

import model.Tramite;
import java.sql.*;

public class TramiteDao {

    public Tramite findById(int id) throws Exception {
        // Usamos LEFT JOIN para obtener el trámite y el nombre si existe
        String sql = "SELECT t.*, s.nombre AS nombre_solicitante " +
                "FROM tramites t " +
                "LEFT JOIN solicitantes s ON t.solicitante_id = s.id " +
                "WHERE t.id = ?";

        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tramite t = new Tramite();
                    t.setId(rs.getInt("id"));
                    t.setSolicitanteId(rs.getInt("solicitante_id"));
                    t.setEstado(rs.getString("estado"));

                    // Manejo de seguridad para la fecha
                    Timestamp ts = rs.getTimestamp("fecha_creacion");
                    t.setFecha(ts != null ? ts.toString() : "");

                    t.setCreatedBy(rs.getInt("created_by"));

                    // Mapeamos el nombre que viene del JOIN
                    t.setNombre(rs.getString("nombre_solicitante"));
                    return t;
                }
            }
        }
        return null; // Si no hay registros, devuelve null estrictamente
    }
}