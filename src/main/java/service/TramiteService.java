package service;

import dao.Conexion;
import model.Tramite;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TramiteService {

    private static final double NOTA_APROBATORIA = 14.0;

    /**
     * NUEVO MÉTODO: Recupera todos los trámites para la tabla de Gestión.
     * Usa LEFT JOIN para asegurar que los nuevos usuarios aparezcan aunque tengan datos incompletos.
     */
    public List<Tramite> listarTodosLosTramites() throws Exception {
        List<Tramite> lista = new ArrayList<>();
        String sql = "SELECT t.id, s.cedula, s.nombre, t.tipo_licencia, t.fecha_creacion, t.estado " +
                "FROM tramites t " +
                "LEFT JOIN solicitantes s ON t.solicitante_id = s.id " +
                "ORDER BY t.id DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tramite t = new Tramite();
                t.setId(rs.getInt("id"));
                t.setCedula(rs.getString("cedula") != null ? rs.getString("cedula") : "N/A");
                t.setNombre(rs.getString("nombre") != null ? rs.getString("nombre") : "Sin Nombre");
                t.setTipoLicencia(rs.getString("tipo_licencia") != null ? rs.getString("tipo_licencia") : "-");

                // Manejo de fecha para evitar errores si la columna es nueva
                Timestamp ts = rs.getTimestamp("fecha_creacion");
                t.setFecha(ts != null ? ts.toString().substring(0, 10) : "S/F");

                t.setEstado(rs.getString("estado"));
                lista.add(t);
            }
        }
        return lista;
    }

    /**
     * Registra las notas en la tabla 'examenes' y actualiza el estado en 'tramites'.
     */
    public void registrarExamen(int idTramite, Double notaTeorica, Double notaPractica, Integer creadoPor) throws Exception {

        if (notaTeorica != null && (notaTeorica < 0 || notaTeorica > 20))
            throw new Exception("Nota Teórica fuera de rango (0-20).");
        if (notaPractica != null && (notaPractica < 0 || notaPractica > 20))
            throw new Exception("Nota Práctica fuera de rango (0-20).");

        String sqlInsertExamen = "INSERT INTO examenes (tramite_id, nota_teorica, nota_practica, aprobado) VALUES (?, ?, ?, ?)";
        String sqlUpdateTramite = "UPDATE tramites SET estado = ? WHERE id = ?";

        try (Connection conexion = Conexion.getConexion()) {
            conexion.setAutoCommit(false);
            try {
                boolean aprobado = (notaTeorica != null && notaPractica != null &&
                        notaTeorica >= NOTA_APROBATORIA && notaPractica >= NOTA_APROBATORIA);

                try (PreparedStatement psEx = conexion.prepareStatement(sqlInsertExamen)) {
                    psEx.setInt(1, idTramite);
                    psEx.setDouble(2, (notaTeorica != null) ? notaTeorica : 0.0);
                    psEx.setDouble(3, (notaPractica != null) ? notaPractica : 0.0);
                    psEx.setBoolean(4, aprobado);
                    psEx.executeUpdate();
                }

                String nuevoEstado = aprobado ? "aprobado" : "reprobado";

                try (PreparedStatement psTra = conexion.prepareStatement(sqlUpdateTramite)) {
                    psTra.setString(1, nuevoEstado);
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

    /**
     * Busca el trámite por ID (Usado en Verificación de Requisitos).
     */
    public Tramite buscarTramitePorId(int idTramite) throws Exception {
        String sql = "SELECT t.id, s.cedula, s.nombre, t.tipo_licencia, t.estado " +
                "FROM tramites t " +
                "JOIN solicitantes s ON t.solicitante_id = s.id " +
                "WHERE t.id = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTramite);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tramite t = new Tramite();
                    t.setId(rs.getInt("id"));
                    t.setCedula(rs.getString("cedula"));
                    t.setNombre(rs.getString("nombre"));
                    t.setTipoLicencia(rs.getString("tipo_licencia"));
                    t.setEstado(rs.getString("estado"));
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Consulta para reportes con filtros dinámicos.
     */
    public List<Tramite> consultarTramitesReporte(LocalDate inicio, LocalDate fin, String estado, String tipo, String cedula) throws Exception {
        List<Tramite> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.id, s.cedula, s.nombre, t.tipo_licencia, t.estado " +
                        "FROM tramites t JOIN solicitantes s ON t.solicitante_id = s.id WHERE 1=1 "
        );

        if (estado != null && !estado.equalsIgnoreCase("Todos")) {
            sql.append(" AND t.estado = ?");
        }
        if (cedula != null && !cedula.trim().isEmpty()) {
            sql.append(" AND s.cedula LIKE ?");
        }

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (estado != null && !estado.equalsIgnoreCase("Todos")) {
                ps.setString(paramIndex++, estado);
            }
            if (cedula != null && !cedula.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + cedula.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tramite t = new Tramite();
                    t.setId(rs.getInt("id"));
                    t.setCedula(rs.getString("cedula"));
                    t.setNombre(rs.getString("nombre"));
                    t.setTipoLicencia(rs.getString("tipo_licencia"));
                    t.setEstado(rs.getString("estado"));
                    lista.add(t);
                }
            }
        }
        return lista;
    }
}