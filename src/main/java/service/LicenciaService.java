package service;

import dao.Conexion;
import model.Licencia;

import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Servicio para generar licencias.
 * - Verifica que el trámite esté 'aprobado'.
 * - Genera número simple y asegura unicidad con un SELECT.
 * - Inserta licencia y actualiza trámite en la misma transacción.
 */
public class LicenciaService {

    public LicenciaService() {}

    public Licencia generarLicencia(int idTramite, Integer creadoPor) throws Exception {
        String sqlSelectTramite = "SELECT estado FROM tramites WHERE id = ?";
        String sqlCheckNumero = "SELECT COUNT(*) FROM licencias WHERE numero_licencia = ?";
        String sqlInsertLic = "INSERT INTO licencias (tramite_id, numero_licencia, fecha_emision, fecha_vencimiento, created_by, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        String sqlUpdateTramite = "UPDATE tramites SET estado = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conexion = new Conexion().getConexion()) {
            conexion.setAutoCommit(false);
            try {
                // 1) verificar estado del trámite
                try (PreparedStatement psTra = conexion.prepareStatement(sqlSelectTramite)) {
                    psTra.setInt(1, idTramite);
                    try (ResultSet rs = psTra.executeQuery()) {
                        if (!rs.next()) throw new Exception("Trámite no encontrado.");
                        String estado = rs.getString(1);
                        if (!"aprobado".equalsIgnoreCase(estado)) throw new Exception("Solo se puede generar licencia si el trámite está aprobado.");
                    }
                }

                // 2) generar número único simple
                String numero;
                int intentos = 0;
                do {
                    numero = generarNumeroSimple();
                    try (PreparedStatement psChk = conexion.prepareStatement(sqlCheckNumero)) {
                        psChk.setString(1, numero);
                        try (ResultSet rs = psChk.executeQuery()) {
                            rs.next();
                            if (rs.getInt(1) == 0) break;
                        }
                    }
                    intentos++;
                    if (intentos > 10) throw new Exception("No se pudo generar número de licencia único.");
                } while (true);

                // 3) insertar licencia
                LocalDate hoy = LocalDate.now();
                LocalDate vencimiento = hoy.plusYears(5);
                Integer idLicencia = null;
                try (PreparedStatement psIns = conexion.prepareStatement(sqlInsertLic, Statement.RETURN_GENERATED_KEYS)) {
                    psIns.setInt(1, idTramite);
                    psIns.setString(2, numero);
                    psIns.setDate(3, Date.valueOf(hoy));
                    psIns.setDate(4, Date.valueOf(vencimiento));
                    if (creadoPor != null) psIns.setInt(5, creadoPor); else psIns.setNull(5, Types.INTEGER);
                    psIns.executeUpdate();
                    try (ResultSet rs = psIns.getGeneratedKeys()) {
                        if (rs.next()) idLicencia = rs.getInt(1);
                    }
                }

                if (idLicencia == null) throw new Exception("No se pudo crear la licencia.");

                // 4) actualizar trámite a licencia_emitida
                try (PreparedStatement psUpd = conexion.prepareStatement(sqlUpdateTramite)) {
                    psUpd.setString(1, "licencia_emitida");
                    psUpd.setInt(2, idTramite);
                    psUpd.executeUpdate();
                }

                conexion.commit();

                // preparar objeto Licencia para retorno
                Licencia licencia = new Licencia();
                licencia.setId(idLicencia);
                licencia.setTramiteId(idTramite);
                licencia.setNumeroLicencia(numero);
                licencia.setFechaEmision(hoy);
                licencia.setFechaVencimiento(vencimiento);
                licencia.setCreatedBy(creadoPor);
                licencia.setCreatedAt(OffsetDateTime.now());
                return licencia;
            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private String generarNumeroSimple() {
        return "LIC-" + System.currentTimeMillis();
    }
}