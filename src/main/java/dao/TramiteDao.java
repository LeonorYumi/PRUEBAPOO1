package dao;

import model.Tramite;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TramiteDao {

    public Integer create(Tramite t) throws Exception {
        String sql = "INSERT INTO tramites (solicitante_id, estado, fecha_creacion, created_by, updated_at) VALUES (?, ?, NOW(), ?, NOW())";
        try (Connection c = Conexion.getConexion()) {
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, t.getSolicitanteId());
            ps.setString(2, t.getEstado());

            if (t.getCreatedBy() > 0) {
                ps.setInt(3, t.getCreatedBy());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }

    /**
     * Obtiene el conteo de trámites agrupados por su estado.
     * Esencial para el PieChart del Dashboard.
     */
    public Map<String, Integer> obtenerConteoPorEstado() throws SQLException {
        Map<String, Integer> conteo = new HashMap<>();
        String sql = "SELECT estado, COUNT(*) as total FROM tramites GROUP BY estado";

        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Guardamos el estado (ej: "pendiente") y su cantidad (ej: 10)
                conteo.put(rs.getString("estado"), rs.getInt("total"));
            }
        }
        return conteo;
    }

    public void updateEstado(int tramiteId, String nuevoEstado) throws Exception {
        String sql = "UPDATE tramites SET estado = ?, updated_at = NOW() WHERE id = ?";
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, tramiteId);
            ps.executeUpdate();
        }
    }

    public Tramite findById(int id) throws Exception {
        String sql = "SELECT id, solicitante_id, estado, fecha_creacion, created_by FROM tramites WHERE id = ?";
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearTramite(rs);
                }
            }
        }
        return null;
    }

    public List<Tramite> listAll() throws Exception {
        String sql = "SELECT id, solicitante_id, estado, fecha_creacion, created_by FROM tramites";
        List<Tramite> list = new ArrayList<>();
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapearTramite(rs));
            }
        }
        return list;
    }

    private Tramite mapearTramite(ResultSet rs) throws SQLException {
        Tramite t = new Tramite();
        t.setId(rs.getInt("id"));
        t.setSolicitanteId(rs.getInt("solicitante_id"));
        t.setEstado(rs.getString("estado"));
        t.setFecha(rs.getTimestamp("fecha_creacion").toString());
        t.setCreatedBy(rs.getInt("created_by"));
        return t;
    }
}