package dao;

import model.Requisito;
import dao.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class RequisitoDao {

    public Integer create(Requisito r) throws Exception {
        String sql = "INSERT INTO requisitos (tramite_id, certificado_medico, pago, multas, observaciones) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getTramiteId());
            ps.setBoolean(2, r.isCertificadoMedico());
            ps.setBoolean(3, r.isPago());
            ps.setBoolean(4, r.isMultas());
            ps.setString(5, r.getObservaciones());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return null;
    }

    public void update(Requisito r) throws Exception {
        String sql = "UPDATE requisitos SET certificado_medico = ?, pago = ?, multas = ?, observaciones = ? WHERE tramite_id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, r.isCertificadoMedico());
            ps.setBoolean(2, r.isPago());
            ps.setBoolean(3, r.isMultas());
            ps.setString(4, r.getObservaciones());
            ps.setInt(5, r.getTramiteId());
            ps.executeUpdate();
        }
    }

    public Requisito findByTramite(int tramiteId) throws Exception {
        String sql = "SELECT id, tramite_id, certificado_medico, pago, multas, observaciones FROM requisitos WHERE tramite_id = ?";
        try (Connection c = new Conexion().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tramiteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Requisito r = new Requisito();
                    r.setId(rs.getInt("id"));
                    r.setTramiteId(rs.getInt("tramite_id"));
                    r.setCertificadoMedico(rs.getBoolean("certificado_medico"));
                    r.setPago(rs.getBoolean("pago"));
                    r.setMultas(rs.getBoolean("multas"));
                    r.setObservaciones(rs.getString("observaciones"));
                    return r;
                }
            }
        }
        return null;
    }
}
