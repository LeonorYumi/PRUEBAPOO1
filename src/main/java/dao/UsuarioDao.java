package dao;

import model.Usuario;
import java.sql.*;

public class UsuarioDao {

    public Usuario findByLogin(String user, String pass) {
        // Consulta corregida según tu script de BD
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
                    u.setRol(rs.getString("rol_nombre")); // Usa el alias del JOIN
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en UsuarioDao: " + e.getMessage());
        }
        return null;
    }
}