package dao;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    // Tu método findByLogin se queda EXACTAMENTE igual
    public Usuario findByLogin(String user, String pass) {
        String sql = "SELECT u.id, u.nombre, u.username, r.nombre AS rol_nombre " +
                "FROM usuarios u " +
                "JOIN roles r ON r.id = u.rol_id " +
                "WHERE u.username = ? AND u.password_hash = SHA2(?, 256) AND u.activo = TRUE";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (cn == null) return null;
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol_nombre"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en UsuarioDao: " + e.getMessage());
        }
        return null;
    }

    // NUEVO: Para guardar cumpliendo con tu seguridad (password_hash)
    public void save(Usuario u) throws SQLException {
        // Asumimos: rol_id 1 para Admin, 2 para Analista (ajusta según tu BD)
        int rolId = u.getRol().equalsIgnoreCase("admin") ? 1 : 2;

        String sql = "INSERT INTO usuarios (nombre, cedula, username, password_hash, rol_id, activo) " +
                "VALUES (?, ?, ?, SHA2(?, 256), ?, ?)";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCedula());
            ps.setString(3, u.getUsername());
            ps.setString(4, u.getPassword());
            ps.setInt(5, rolId);
            ps.setBoolean(6, u.isActivo());
            ps.executeUpdate();
        }
    }

    // NUEVO: Para llenar la tabla del Administrador
    public List<Usuario> findAll() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCedula(rs.getString("cedula"));
                u.setUsername(rs.getString("username"));
                u.setRol(rs.getString("rol_nombre"));
                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }
        }
        return lista;
    }
}