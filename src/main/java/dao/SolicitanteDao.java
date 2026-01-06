package dao;

import model.Solicitante;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO concreto para solicitantes.
 * Métodos: create, findById, findByCedula.
 */
public class SolicitanteDao {

    public Integer create(Solicitante s) throws Exception {
        String sql = "INSERT INTO solicitantes (cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getCedula());
            ps.setString(2, s.getNombre());
            ps.setDate(3, Date.valueOf(s.getFechaNacimiento()));
            ps.setString(4, s.getTipoLicencia());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            if (s.getCreatedBy() != null) ps.setInt(6, s.getCreatedBy());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }

    public Solicitante findById(int id) throws Exception {
        String sql = "SELECT id, cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by FROM solicitantes WHERE id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Solicitante s = new Solicitante();
                    s.setId(rs.getInt("id"));
                    s.setCedula(rs.getString("cedula"));
                    s.setNombre(rs.getString("nombre"));
                    s.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                    s.setTipoLicencia(rs.getString("tipo_licencia"));
                    s.setFechaSolicitud(rs.getTimestamp("fecha_solicitud").toInstant().atOffset(java.time.ZoneOffset.UTC));
                    s.setCreatedBy(rs.getInt("created_by"));
                    return s;
                }
            }
        }
        return null;
    }

    public Solicitante findByCedula(String cedula) throws Exception {
        String sql = "SELECT id, cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by FROM solicitantes WHERE cedula = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Solicitante s = new Solicitante();
                    s.setId(rs.getInt("id"));
                    s.setCedula(rs.getString("cedula"));
                    s.setNombre(rs.getString("nombre"));
                    s.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                    s.setTipoLicencia(rs.getString("tipo_licencia"));
                    s.setFechaSolicitud(rs.getTimestamp("fecha_solicitud").toInstant().atOffset(java.time.ZoneOffset.UTC));
                    s.setCreatedBy(rs.getInt("created_by"));
                    return s;
                }
            }
        }
        return null;
    }
}