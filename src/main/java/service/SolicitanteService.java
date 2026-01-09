package service;

import dao.Conexion;
import model.Solicitante;
import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;

public class SolicitanteService {
    private static final int EDAD_MINIMA = 18;

    public Integer crearSolicitanteConTramite(Solicitante solicitante) throws Exception {
        if (solicitante.getFechaNacimiento() == null) throw new Exception("Fecha de nacimiento es obligatoria.");
        if (!esMayorEdad(solicitante.getFechaNacimiento())) throw new Exception("Mínimo " + EDAD_MINIMA + " años.");

        String sqlSol = "INSERT INTO solicitantes (cedula, nombre, fecha_nacimiento, tipo_licencia, fecha_solicitud, created_by) VALUES (?, ?, ?, ?, NOW(), ?)";
        String sqlTra = "INSERT INTO tramites (solicitante_id, tipo_licencia, estado, created_by, fecha_creacion) VALUES (?, ?, ?, ?, NOW())";

        try (Connection con = new Conexion().getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psSol = con.prepareStatement(sqlSol, Statement.RETURN_GENERATED_KEYS)) {
                // Datos del Solicitante
                psSol.setString(1, solicitante.getCedula());
                psSol.setString(2, solicitante.getNombre());
                psSol.setDate(3, Date.valueOf(solicitante.getFechaNacimiento()));
                psSol.setString(4, solicitante.getTipoLicencia());
                if (solicitante.getCreatedBy() != null) psSol.setInt(5, solicitante.getCreatedBy());
                else psSol.setNull(5, Types.INTEGER);

                psSol.executeUpdate();

                Integer idSol = null;
                try (ResultSet rs = psSol.getGeneratedKeys()) {
                    if (rs.next()) idSol = rs.getInt(1);
                }

                // Datos del Trámite
                try (PreparedStatement psTra = con.prepareStatement(sqlTra)) {
                    psTra.setInt(1, idSol);
                    psTra.setString(2, solicitante.getTipoLicencia());
                    psTra.setString(3, "pendiente");
                    if (solicitante.getCreatedBy() != null) psTra.setInt(4, solicitante.getCreatedBy());
                    else psTra.setNull(4, Types.INTEGER);
                    psTra.executeUpdate();
                }

                con.commit();
                solicitante.setId(idSol);
                return idSol;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private boolean esMayorEdad(LocalDate fecha) {
        return Period.between(fecha, LocalDate.now()).getYears() >= EDAD_MINIMA;
    }
}