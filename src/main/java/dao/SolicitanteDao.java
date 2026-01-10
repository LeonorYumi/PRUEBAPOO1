package dao;

import model.Solicitante;
import java.sql.*;
import java.time.ZoneOffset;

public class SolicitanteDao {

    /**
     * Guarda un solicitante y crea su trámite automáticamente (Transacción).
     */
    public Integer create(Solicitante s) throws Exception {
        // SQL para Solicitante
        String sqlSol = "INSERT INTO solicitantes (cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by) VALUES (?, ?, ?, ?, NOW(), ?)";
        // SQL para Trámite (Estado inicial: pendiente)
        String sqlTra = "INSERT INTO tramites (solicitante_id, tipo_licencia, estado, fecha_creacion) VALUES (?, ?, 'pendiente', NOW())";

        try (Connection c = new Conexion().getConexion()) {
            // 1. Iniciamos Transacción (Importante para explicar al profesor)
            c.setAutoCommit(false);

            try (PreparedStatement psSol = c.prepareStatement(sqlSol, Statement.RETURN_GENERATED_KEYS)) {
                // Seteamos datos del solicitante
                psSol.setString(1, s.getCedula());
                psSol.setString(2, s.getNombre());
                psSol.setDate(3, Date.valueOf(s.getFechaNacimiento()));
                psSol.setString(4, s.getTipoLicencia());

                if (s.getCreatedBy() != null) psSol.setInt(5, s.getCreatedBy());
                else psSol.setNull(5, Types.INTEGER);

                psSol.executeUpdate();

                // 2. Obtenemos el ID generado automáticamente
                Integer idGenerado = null;
                try (ResultSet keys = psSol.getGeneratedKeys()) {
                    if (keys.next()) idGenerado = keys.getInt(1);
                }

                // 3. Insertamos el Trámite usando ese ID
                try (PreparedStatement psTra = c.prepareStatement(sqlTra)) {
                    psTra.setInt(1, idGenerado);
                    psTra.setString(2, s.getTipoLicencia());
                    psTra.executeUpdate();
                }

                // 4. Si todo salió bien, guardamos definitivamente
                c.commit();
                return idGenerado;

            } catch (Exception ex) {
                // Si algo falló, deshacemos todo para no dejar datos huérfanos
                c.rollback();
                throw ex;
            }
        }
    }

    /**
     * Busca un solicitante por su ID.
     */
    public Solicitante findById(int id) throws Exception {
        String sql = "SELECT id, cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by FROM solicitantes WHERE id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSolicitante(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca un solicitante por su número de cédula.
     */
    public Solicitante findByCedula(String cedula) throws Exception {
        String sql = "SELECT id, cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by FROM solicitantes WHERE cedula = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSolicitante(rs);
                }
            }
        }
        return null;
    }

    /**
     * Método privado para no repetir código al leer los datos de la base.
     */
    private Solicitante mapearSolicitante(ResultSet rs) throws SQLException {
        Solicitante s = new Solicitante();
        s.setId(rs.getInt("id"));
        s.setCedula(rs.getString("cedula"));
        s.setNombre(rs.getString("nombre"));
        s.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        s.setTipoLicencia(rs.getString("tipo_licencia"));

        Timestamp ts = rs.getTimestamp("fecha_solicitud");
        if (ts != null) {
            s.setFechaSolicitud(ts.toInstant().atOffset(ZoneOffset.UTC));
        }

        s.setCreatedBy(rs.getInt("created_by"));
        return s;
    }
}