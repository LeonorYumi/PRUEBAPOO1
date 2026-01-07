package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://b9ga2qwoxdhztatd3zbd-mysql.services.clever-cloud.com:3306/b9ga2qwoxdhztatd3zbd?serverTimezone=UTC";

    private static final String USER = "ud0pqkaemwq7pgmt";
    private static final String PASS = "4ooY8FHTmSqT0TGFoV6L";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
