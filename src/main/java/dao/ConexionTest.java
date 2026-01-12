package dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class ConexionTest {

    public static void main(String[] args) {

        try (Connection c = Conexion.getConexion()) {

            if (c == null) {
                System.out.println(" No hay conexión");
                return;
            }

            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios");

            if (rs.next()) {
                System.out.println(" BD FUNCIONA - Usuarios registrados: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println(" Error al consultar BD");
            e.printStackTrace();
        }
    }
}
