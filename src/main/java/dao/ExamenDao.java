package dao;

import model.Examen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para examenes: create, findByTramite.
 */
public class ExamenDao {

    public Integer create(Examen e) throws Exception {
        String sql = "INSERT INTO examenes (tramite_id, nota_teorica, nota_practica, fecha, created_by) VALUES (?, ?, ?, NOW(), ?)";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getTramiteId());
            if (e.getNotaTeorica() != null) ps.setDouble(2, e.getNotaTeorica()); else ps.setNull(2, java.sql.Types.DOUBLE);
            if (e.getNotaPractica() != null) ps.setDouble(3, e.getNotaPractica()); else ps.setNull(3, java.sql.Types.DOUBLE);
            if (e.getCreatedBy() != null) ps.setInt(4, e.getCreatedBy()); else ps.setNull(4, java.sql.Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }

    public List<Examen> findByTramite(int tramiteId) throws Exception {
        String sql = "SELECT id, tramite_id, nota_teorica, nota_practica, fecha, created_by FROM examenes WHERE tramite_id = ?";
        List<Examen> list = new ArrayList<>();
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tramiteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Examen ex = new Examen();
                    ex.setId(rs.getInt("id"));
                    ex.setTramiteId(rs.getInt("tramite_id"));
                    ex.setNotaTeorica(rs.getObject("nota_teorica") != null ? rs.getDouble("nota_teorica") : null);
                    ex.setNotaPractica(rs.getObject("nota_practica") != null ? rs.getDouble("nota_practica") : null);
                    ex.setFecha(rs.getTimestamp("fecha").toInstant().atOffset(java.time.ZoneOffset.UTC));
                    ex.setCreatedBy(rs.getInt("created_by"));
                    list.add(ex);
                }
            }
        }
        return list;
    }
}