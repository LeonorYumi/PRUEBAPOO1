package dao;

import model.Solicitante;
import java.sql.*;

public class SolicitanteDao {
    public Integer create(Solicitante s) throws Exception {
        String sql = "INSERT INTO solicitantes (cedula, nombre, fecha_nacimiento, tipo_licencia, created_by, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getCedula());
            ps.setString(2, s.getNombre());
            ps.setDate(3, Date.valueOf(s.getFechaNacimiento()));
            ps.setString(4, s.getTipoLicencia());
            if (s.getCreatedBy() != null) ps.setInt(5, s.getCreatedBy());
            else ps.setNull(5, Types.INTEGER);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}