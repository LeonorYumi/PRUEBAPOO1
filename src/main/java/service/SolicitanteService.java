package service;

import dao.Conexion;
import model.Solicitante;

import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;


public class SolicitanteService {

    private static final int EDAD_MINIMA = 18;

    public SolicitanteService() {}


    public Integer crearSolicitanteConTramite(Solicitante solicitante) throws Exception {
        if (solicitante.getFechaNacimiento() == null) throw new Exception("Fecha de nacimiento es obligatoria.");
        if (!esMayorEdad(solicitante.getFechaNacimiento())) throw new Exception("El solicitante debe tener al menos 18 años.");

        String sqlInsertSolicitante = "INSERT INTO solicitantes (cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by) VALUES (?, ?, ?, ?, NOW(), ?)";
        String sqlInsertTramite = "INSERT INTO tramites (solicitante_id, estado, fecha_creacion, created_by) VALUES (?, ?, NOW(), ?)";

        try (Connection conexion = new Conexion().getConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psSol = conexion.prepareStatement(sqlInsertSolicitante, Statement.RETURN_GENERATED_KEYS)) {
                psSol.setString(1, solicitante.getCedula());
                psSol.setString(2, solicitante.getNombre());
                psSol.setDate(3, Date.valueOf(solicitante.getFechaNacimiento()));
                psSol.setString(4, solicitante.getTipoLicencia());
                if (solicitante.getCreatedBy() != null) psSol.setInt(5, solicitante.getCreatedBy()); else psSol.setNull(5, Types.INTEGER);
                psSol.executeUpdate();

                Integer idSolicitante = null;
                try (ResultSet rs = psSol.getGeneratedKeys()) {
                    if (rs.next()) idSolicitante = rs.getInt(1);
                }
                if (idSolicitante == null) throw new Exception("No se pudo crear el solicitante.");

                try (PreparedStatement psTra = conexion.prepareStatement(sqlInsertTramite)) {
                    psTra.setInt(1, idSolicitante);
                    psTra.setString(2, "pendiente");
                    if (solicitante.getCreatedBy() != null) psTra.setInt(3, solicitante.getCreatedBy()); else psTra.setNull(3, Types.INTEGER);
                    psTra.executeUpdate();
                }

                conexion.commit();
                solicitante.setId(idSolicitante);
                solicitante.setFechaSolicitud(OffsetDateTime.now());
                return idSolicitante;
            } catch (Exception ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private boolean esMayorEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= EDAD_MINIMA;
    }
}