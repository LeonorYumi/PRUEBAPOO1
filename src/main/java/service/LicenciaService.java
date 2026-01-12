package service;

import dao.Conexion;
import model.Licencia;
import java.sql.*;
import java.time.LocalDate;

public class LicenciaService {

    // El metodo recibe 2 parámetros para coincidir con el controlador
    public Licencia generarLicencia(int tramiteId, int usuarioId) throws Exception {
        String sqlL = "INSERT INTO licencias (tramite_id, fecha_emision, fecha_vencimiento, numero) VALUES (?, ?, ?, ?)";
        String sqlT = "UPDATE tramites SET estado = 'licencia_emitida' WHERE id = ?";

        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);
            try {
                String numLic = "LIC-" + (System.currentTimeMillis() / 1000);

                try (PreparedStatement psL = con.prepareStatement(sqlL)) {
                    psL.setInt(1, tramiteId);
                    psL.setDate(2, Date.valueOf(LocalDate.now()));
                    psL.setDate(3, Date.valueOf(LocalDate.now().plusYears(5)));
                    psL.setString(4, numLic);
                    psL.executeUpdate();
                }

                try (PreparedStatement psT = con.prepareStatement(sqlT)) {
                    psT.setInt(1, tramiteId);
                    psT.executeUpdate();
                }

                con.commit();

                Licencia lic = new Licencia();
                lic.setNumeroLicencia(numLic);
                return lic;
            } catch (Exception e) {
                con.rollback();
                throw new Exception(e.getMessage());
            }
        }
    }
}