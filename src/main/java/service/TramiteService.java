package service;

import dao.Conexion;

import java.sql.*;

/**
 * Servicio para registrar exámenes y actualizar el estado del trámite.
 * Regla: nota mínima aprobatoria = 14.
 */
public class TramiteService {

    private static final double NOTA_APROBATORIA = 14.0;

    public TramiteService() {}

    /**
     * Inserta un examen y actualiza estado del trámite en la misma transacción.
     * Si ambas notas >= NOTA_APROBATORIA -> 'aprobado', sino -> 'reprobado'.
     */
    public void registrarExamen(int idTramite, Double notaTeorica, Double notaPractica, Integer creadoPor) throws Exception {
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

                String estado = (notaTeorica != null && notaPractica != null && notaTeorica >= NOTA_APROBATORIA && notaPractica >= NOTA_APROBATORIA)
                        ? "aprobado" : "reprobado";

                try (PreparedStatement psTra = conexion.prepareStatement(sqlUpdateTramite)) {
                    psTra.setString(1, estado);
                    psTra.setInt(2, idTramite);
                    psTra.executeUpdate();
                }

                conexion.commit();
            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }
}