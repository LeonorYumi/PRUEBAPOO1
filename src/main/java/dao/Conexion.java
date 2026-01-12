package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://bt2unnanzjj0wfsyfd2e-mysql.services.clever-cloud.com:3306/bt2unnanzjj0wfsyfd2e"
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "ud0pqkaemwq7pgmt";
    private static final String PASS = "4ooY8FHTmSqT0TGFoV6L";

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 🔥 CLAVE
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("CONEXIÓN EXITOSA A MYSQL");
            return con;
        } catch (Exception e) {
            System.out.println(" ERROR DE CONEXIÓN A MYSQL");
            e.printStackTrace();
            return null;
        }
    }
}
