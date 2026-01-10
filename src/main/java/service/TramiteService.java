package service;

import dao.Conexion;
import model.Tramite;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.File;

public class TramiteService {

    private static final double NOTA_MINIMA = 14.0;

    /**
     * MÉTODO CORREGIDO: Ahora acepta los 5 parámetros que envía el ReporteAdminController.
     */
    public List<Tramite> consultarTramitesReporte(LocalDate inicio, LocalDate fin, String estado, String tipo, String cedula) throws Exception {
        List<Tramite> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.id, s.cedula, s.nombre, t.tipo_licencia, t.estado, t.fecha_creacion " +
                        "FROM tramites t LEFT JOIN solicitantes s ON t.solicitante_id = s.id WHERE 1=1"
        );

        // Construcción dinámica de la consulta SQL
        if (estado != null && !estado.equalsIgnoreCase("Todos")) sql.append(" AND t.estado = ?");
        if (tipo != null && !tipo.equalsIgnoreCase("Todos")) sql.append(" AND t.tipo_licencia = ?");
        if (cedula != null && !cedula.trim().isEmpty()) sql.append(" AND s.cedula LIKE ?");
        if (inicio != null) sql.append(" AND t.fecha_creacion >= ?");
        if (fin != null) sql.append(" AND t.fecha_creacion <= ?");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (estado != null && !estado.equalsIgnoreCase("Todos")) ps.setString(idx++, estado);
            if (tipo != null && !tipo.equalsIgnoreCase("Todos")) ps.setString(idx++, tipo);
            if (cedula != null && !cedula.trim().isEmpty()) ps.setString(idx++, "%" + cedula.trim() + "%");
            if (inicio != null) ps.setDate(idx++, Date.valueOf(inicio));
            if (fin != null) ps.setDate(idx++, Date.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tramite t = new Tramite();
                    t.setId(rs.getInt("id"));
                    t.setCedula(rs.getString("cedula") != null ? rs.getString("cedula") : "N/A");
                    t.setNombre(rs.getString("nombre") != null ? rs.getString("nombre") : "S/N");
                    t.setTipoLicencia(rs.getString("tipo_licencia"));
                    t.setEstado(rs.getString("estado"));

                    Timestamp ts = rs.getTimestamp("fecha_creacion");
                    t.setFecha(ts != null ? ts.toString().substring(0, 10) : "S/F");
                    lista.add(t);
                }
            }
        }
        return lista;
    }

    /**
     * Lista general para la gestión del analista.
     */
    public List<Tramite> listarTodosLosTramites() throws Exception {
        return consultarTramitesReporte(null, null, "Todos", "Todos", null);
    }

    /**
     * Exportación a CSV.
     */
    public void exportarA_CSV(List<Tramite> lista, File archivo) throws Exception {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            writer.println("ID;Cedula;Nombre;Tipo;Estado;Fecha");
            for (Tramite t : lista) {
                writer.println(String.format("%d;%s;%s;%s;%s;%s",
                        t.getId(), t.getCedula(), t.getNombre(), t.getTipoLicencia(), t.getEstado(), t.getFecha()));
            }
        }
    }

    /**
     * Búsqueda por ID.
     */
    public Tramite buscarTramitePorId(int id) throws Exception {
        String sql = "SELECT t.id, s.nombre, t.estado, t.tipo_licencia FROM tramites t JOIN solicitantes s ON t.solicitante_id = s.id WHERE t.id = ?";
        try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tramite t = new Tramite();
                    t.setId(rs.getInt("id"));
                    t.setNombre(rs.getString("nombre"));
                    t.setEstado(rs.getString("estado"));
                    t.setTipoLicencia(rs.getString("tipo_licencia"));
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Registro de exámenes con transacción.
     */
    public void registrarExamen(int idTramite, double notaT, double notaP) throws Exception {
        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);
            try {
                boolean aprobado = (notaT >= NOTA_MINIMA && notaP >= NOTA_MINIMA);

                String sqlEx = "INSERT INTO examenes (tramite_id, nota_teorica, nota_practica, aprobado) VALUES (?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sqlEx)) {
                    ps.setInt(1, idTramite); ps.setDouble(2, notaT); ps.setDouble(3, notaP); ps.setBoolean(4, aprobado);
                    ps.executeUpdate();
                }

                String sqlTr = "UPDATE tramites SET estado = ? WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlTr)) {
                    ps.setString(1, aprobado ? "aprobado" : "reprobado");
                    ps.setInt(2, idTramite);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (Exception e) { con.rollback(); throw e; }
        }
    }
}