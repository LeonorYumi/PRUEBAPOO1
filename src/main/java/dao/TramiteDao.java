package dao;

import model.Tramite;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TramiteDao {

    public Integer create(Tramite t) throws Exception {
        String sql = "INSERT INTO tramites (solicitante_id, estado, fecha_creacion, created_by, updated_at) VALUES (?, ?, NOW(), ?, NOW())";
        try (Connection c = new Conexion().getConexion()) {
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, t.getSolicitanteId());
            ps.setString(2, t.getEstado());

            // CORRECCIÓN: Un int no puede ser null. Verificamos si tiene un valor válido.
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

    public void updateEstado(int tramiteId, String nuevoEstado) throws Exception {
        String sql = "UPDATE tramites SET estado = ?, updated_at = NOW() WHERE id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, tramiteId);
            ps.executeUpdate();
        }
    }

    public Tramite findById(int id) throws Exception {
        String sql = "SELECT id, solicitante_id, estado, fecha_creacion, created_by FROM tramites WHERE id = ?";
        try (Connection c = new Conexion().getConexion();
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
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapearTramite(rs));
            }
        }
        return list;
    }

    // Método auxiliar para no repetir código de mapeo
    private Tramite mapearTramite(ResultSet rs) throws SQLException {
        Tramite t = new Tramite();
        t.setId(rs.getInt("id"));
        t.setSolicitanteId(rs.getInt("solicitante_id"));
        t.setEstado(rs.getString("estado"));
        // Simplificamos la fecha a String para evitar errores de zona horaria
        t.setFecha(rs.getTimestamp("fecha_creacion").toString());
        t.setCreatedBy(rs.getInt("created_by"));
        return t;
    }
}