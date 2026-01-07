package service;

import dao.Conexion;

import java.sql.*;

/**
 * Servicio para guardar o actualizar requisitos de un trámite.
 * Si los requisitos son correctos (certificado + pago y sin multas) cambia estado a 'en_examenes',
 * en caso contrario a 'rechazado'.
 */
public class RequisitoService {

    public RequisitoService() {}

    /**
     * Guarda o actualiza los requisitos relacionados a un trámite.
     * creadoPor puede ser null si no hay sesión disponible.
     */
    public void guardarRequisitos(int idTramite, boolean certificadoMedico, boolean pago, boolean multas, String observaciones, Integer creadoPor) throws Exception {
        String sqlSelect = "SELECT id FROM requisitos WHERE tramite_id = ?";
        String sqlInsert = "INSERT INTO requisitos (tramite_id, certificado_medico, pago, multas, observaciones, created_by) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE requisitos SET certificado_medico = ?, pago = ?, multas = ?, observaciones = ? WHERE tramite_id = ?";
        String sqlUpdateTramite = "UPDATE tramites SET estado = ?, updated_at = NOW() WHERE id = ?";

        boolean todoOk = certificadoMedico && pago && !multas;
        String nuevoEstado = todoOk ? "en_examenes" : "rechazado";

        try (Connection conexion = new Conexion().getConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psSel = conexion.prepareStatement(sqlSelect)) {
                psSel.setInt(1, idTramite);
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) {
                    // existe -> update
                    try (PreparedStatement psUpd = conexion.prepareStatement(sqlUpdate)) {
                        psUpd.setBoolean(1, certificadoMedico);
                        psUpd.setBoolean(2, pago);
                        psUpd.setBoolean(3, multas);
                        psUpd.setString(4, observaciones);
                        psUpd.setInt(5, idTramite);
                        psUpd.executeUpdate();
                    }
                } else {
                    // no existe -> insert
                    try (PreparedStatement psIns = conexion.prepareStatement(sqlInsert)) {
                        psIns.setInt(1, idTramite);
                        psIns.setBoolean(2, certificadoMedico);
                        psIns.setBoolean(3, pago);
                        psIns.setBoolean(4, multas);
                        psIns.setString(5, observaciones);
                        if (creadoPor != null) psIns.setInt(6, creadoPor); else psIns.setNull(6, Types.INTEGER);
                        psIns.executeUpdate();
                    }
                }

                // actualizar estado del trámite
                try (PreparedStatement psTra = conexion.prepareStatement(sqlUpdateTramite)) {
                    psTra.setString(1, nuevoEstado);
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