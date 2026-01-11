package dao;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public Usuario findByLogin(String user, String pass) {
        // Ya no filtramos por activo en la consulta: queremos saber si existe y su estado
        String sql = "SELECT u.id, u.nombre, u.username, r.nombre AS rol_nombre, u.activo " +
                "FROM usuarios u " +
                "JOIN roles r ON r.id = u.rol_id " +
                "WHERE u.username = ? AND u.password_hash = SHA2(?, 256)";
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
                    // establecer el flag activo según la columna de la BD
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en UsuarioDao: " + e.getMessage());
        }
        return null;
    }

    public void save(Usuario u) throws SQLException {
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

    public void update(Usuario u) throws SQLException {
        int rolId = u.getRol().equalsIgnoreCase("admin") ? 1 : 2;
        boolean actualizaPass = u.getPassword() != null && !u.getPassword().trim().isEmpty();

        String sql = actualizaPass
                ? "UPDATE usuarios SET nombre=?, cedula=?, username=?, rol_id=?, activo=?, password_hash=SHA2(?, 256) WHERE id=?"
                : "UPDATE usuarios SET nombre=?, cedula=?, username=?, rol_id=?, activo=? WHERE id=?";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCedula());
            ps.setString(3, u.getUsername());
            ps.setInt(4, rolId);
            ps.setBoolean(5, u.isActivo());

            if (actualizaPass) {
                ps.setString(6, u.getPassword());
                ps.setInt(7, u.getId());
            } else {
                ps.setInt(6, u.getId());
            }
            ps.executeUpdate();
        }
    }

    /**
     * ELIMINAR: Borra físicamente un registro basado en la cédula.
     */
    public void delete(String cedula) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE cedula = ?";

        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cedula);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró ningún usuario con la cédula proporcionada.");
            }
        }
    }
}