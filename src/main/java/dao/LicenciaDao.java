package dao;

import model.Licencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LicenciaDao {

    public Integer create(Licencia l) throws Exception {
        String sql = "INSERT INTO licencias (tramite_id, numero_licencia, fecha_emision, fecha_vencimiento, created_by, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getTramiteId());
            ps.setString(2, l.getNumeroLicencia());
            ps.setDate(3, Date.valueOf(l.getFechaEmision()));
            ps.setDate(4, Date.valueOf(l.getFechaVencimiento()));
            if (l.getCreatedBy() != null) ps.setInt(5, l.getCreatedBy()); else ps.setNull(5, java.sql.Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }

    public Licencia findByTramite(int tramiteId) throws Exception {
        String sql = "SELECT id, tramite_id, numero_licencia, fecha_emision, fecha_vencimiento, created_by, created_at FROM licencias WHERE tramite_id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tramiteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Licencia l = new Licencia();
                    l.setId(rs.getInt("id"));
                    l.setTramiteId(rs.getInt("tramite_id"));
                    l.setNumeroLicencia(rs.getString("numero_licencia"));
                    l.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
                    l.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
                    l.setCreatedBy(rs.getInt("created_by"));
                    l.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
                    return l;
                }
            }
        }
        return null;
    }

    public Licencia findByNumero(String numero) throws Exception {
        String sql = "SELECT id, tramite_id, numero_licencia, fecha_emision, fecha_vencimiento, created_by, created_at FROM licencias WHERE numero_licencia = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Licencia l = new Licencia();
                    l.setId(rs.getInt("id"));
                    l.setTramiteId(rs.getInt("tramite_id"));
                    l.setNumeroLicencia(rs.getString("numero_licencia"));
                    l.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
                    l.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
                    l.setCreatedBy(rs.getInt("created_by"));
                    l.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
                    return l;
                }
            }
        }
        return null;
    }
}
