
package model;
import dao.Conexion;
public class test {
    public static void main(String[] args) {
        if (Conexion.getConexion() != null) {
            System.out.println(" LISTO, LA BD CONECTA");
        }
    }
}
