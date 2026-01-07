
import dao.Conexion;

public class TestConexion {
    public static void main(String[] args) {
        try {
            Conexion.getConexion();
            System.out.println("✅ CONECTADO A MYSQL");
        } catch (Exception e) {
            System.err.println("❌ NO CONECTA");
            e.printStackTrace();
        }
    }
}
