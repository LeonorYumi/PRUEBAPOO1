package service;

import dao.Conexion;
import model.Tramite;
import java.sql.*;

public class TramiteService {

    private static final double NOTA_APROBATORIA = 14.0;

    public void registrarExamen(int idTramite, Double notaTeorica, Double notaPractica, Integer creadoPor) throws Exception {


        if (notaTeorica != null && (notaTeorica < 0 || notaTeorica > 20)) throw new Exception("Nota Teórica fuera de rango (0-20).");
        if (notaPractica != null && (notaPractica < 0 || notaPractica > 20)) throw new Exception("Nota Práctica fuera de rango (0-20).");

        String sqlInsertExamen = "INSERT INTO examenes (tramite_id, nota_teorica, nota_practica, fecha, created_by) VALUES (?, ?, ?, NOW(), ?)";
        String sqlUpdateTramite = "UPDATE tramites SET estado = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conexion = new Conexion().getConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psEx = conexion.prepareStatement(sqlInsertExamen)) {
                psEx.setInt(1, idTramite);
                if (notaTeorica != null) psEx.setDouble(2, notaTeorica); else psEx.setNull(2, Types.DOUBLE);
                if (notaPractica != null) psEx.setDouble(3, notaPractica); else psEx.setNull(3, Types.DOUBLE);
                if (creadoPor != null) psEx.setInt(4, creadoPor); else psEx.setNull(4, Types.INTEGER);
                psEx.executeUpdate();

                String estado = (notaTeorica >= NOTA_APROBATORIA && notaPractica >= NOTA_APROBATORIA) ? "aprobado" : "reprobado";

                try (PreparedStatement psTra = conexion.prepareStatement(sqlUpdateTramite)) {
                    psTra.setString(1, estado);
                    psTra.setInt(2, idTramite);
                    psTra.executeUpdate();
                }
                conexion.commit();
            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            }
        }
    }

    // 2. AGREGADO NECESARIO: Sin esto, tu DetalleTramiteController no puede buscar en la BD
    public Tramite buscarTramitePorId(int idTramite) throws Exception {
        String sql = "SELECT t.id, s.cedula, s.nombre, s.tipo_licencia, t.fecha_creacion, t.estado " +
                "FROM tramites t JOIN solicitantes s ON t.solicitante_id = s.id WHERE t.id = ?";
        try (Connection con = new Conexion().getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTramite);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Tramite(rs.getInt("id"), rs.getString("cedula"), rs.getString("nombre"),
                        rs.getString("tipo_licencia"), rs.getString("fecha_creacion"), rs.getString("estado"));
            }
        }
        return null;
    }
}