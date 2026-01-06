package dao;

import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO concreto para usuarios (MySQL).
 * Métodos: findByUsername, create, update, findById.
 */
public class UsuarioDao {

    public Usuario findByUsername(String username) throws Exception {
        String sql = "SELECT id, nombre, cedula, username, password_hash, rol, activo, created_at FROM usuarios WHERE username = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCedula(rs.getString("cedula"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        }
        return null;
    }

    public Usuario findById(int id) throws Exception {
        String sql = "SELECT id, nombre, cedula, username, password_hash, rol, activo, created_at FROM usuarios WHERE id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCedula(rs.getString("cedula"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        }
        return null;
    }

    public Integer create(Usuario usuario) throws Exception {
        String sql = "INSERT INTO usuarios (nombre, cedula, username, password_hash, rol, activo, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCedula());
            ps.setString(3, usuario.getUsername());
            ps.setString(4, usuario.getPasswordHash());
            ps.setString(5, usuario.getRol());
            ps.setBoolean(6, usuario.isActivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }

    public void update(Usuario usuario) throws Exception {
        String sql = "UPDATE usuarios SET nombre = ?, cedula = ?, username = ?, password_hash = ?, rol = ?, activo = ? WHERE id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCedula());
            ps.setString(3, usuario.getUsername());
            ps.setString(4, usuario.getPasswordHash());
            ps.setString(5, usuario.getRol());
            ps.setBoolean(6, usuario.isActivo());
            ps.setInt(7, usuario.getId());
            ps.executeUpdate();
        }
    }
}