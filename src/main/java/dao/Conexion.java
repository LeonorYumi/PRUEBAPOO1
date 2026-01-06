package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexion robusta para MySQL remoto.
 * - Lee DB_URL, DB_USER, DB_PASSWORD desde variables de entorno si existen.
 * - Normaliza URL si contiene credenciales antes de la @.
 * - Agrega parámetros comunes: serverTimezone, useSSL, allowPublicKeyRetrieval.
 */
public class Conexion {

    private static final String DEFAULT_JDBC_URL =
            "jdbc:mysql://b9ga2qwoxdhztatd3zbd-mysql.services.clever-cloud.com:3306/b9ga2qwoxdhztatd3zbd";
    private static final String DEFAULT_USER = "ugy8hvqbtqckzd5e";
    private static final String DEFAULT_PASS = "hXanAUQAhRULp5r9fQJu";

    public Connection getConexion() throws SQLException {
        String rawUrl = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");

        if (rawUrl == null || rawUrl.isBlank()) rawUrl = DEFAULT_JDBC_URL;
        if (user == null || user.isBlank()) user = DEFAULT_USER;
        if (pass == null || pass.isBlank()) pass = DEFAULT_PASS;

        String jdbcUrl = normalizeJdbcUrl(rawUrl);

        // Añadimos parámetros que suelen ser necesarios en conexiones remotas MySQL:
        // - serverTimezone=UTC (evita excepciones de zona horaria)
        // - useSSL=false (o true si tu servidor lo requiere)
        // - allowPublicKeyRetrieval=true (necesario en algunas configuraciones remotas)
        String params = "serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
        if (jdbcUrl.contains("?")) {
            jdbcUrl = jdbcUrl + "&" + params;
        } else {
            jdbcUrl = jdbcUrl + "?" + params;
        }

        System.out.println("Conexion: intentado conectar con:");
        System.out.println("  JDBC URL = " + jdbcUrl);
        System.out.println("  USER = " + user);

        try {
            Connection con = DriverManager.getConnection(jdbcUrl, user, pass);
            System.out.println("CONEXIÓN EXITOSA A MYSQL");
            return con;
        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXIÓN: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorCode: " + e.getErrorCode());
            e.printStackTrace(System.err);
            throw e;
        }
    }

    private String normalizeJdbcUrl(String raw) {
        raw = raw.trim();

        // Caso: alguien dejó la URL con credenciales tipo: jdbc:mysql://user:pass@host:port/db
        // Eliminamos user:pass@ si existe en esa posición.
        if ((raw.startsWith("jdbc:mysql://") || raw.startsWith("mysql://")) && raw.contains("@")) {
            String working = raw;
            if (working.startsWith("mysql://")) working = "jdbc:mysql://" + working.substring("mysql://".length());
            if (working.startsWith("jdbc:mysql://")) {
                String afterProto = working.substring("jdbc:mysql://".length());
                if (afterProto.contains("@")) {
                    String[] parts = afterProto.split("@", 2);
                    String hostPart = parts[1]; // host:port/db...
                    return "jdbc:mysql://" + hostPart;
                } else {
                    return working;
                }
            }
        }

        // Si ya es jdbc:mysql:// retorna tal cual (se añadirá params)
        if (raw.startsWith("jdbc:mysql://")) return raw;

        // Si viene en formato mysql://host:... convertimos
        if (raw.startsWith("mysql://")) return "jdbc:mysql://" + raw.substring("mysql://".length());

        // En cualquier otro caso intentamos devolver tal cual
        return raw;
    }
}