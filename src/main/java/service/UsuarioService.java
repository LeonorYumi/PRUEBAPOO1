package service;

import dao.UsuarioDao;
import model.Usuario;

/**
 * Servicio de usuarios simple y didáctico.
 * - No usa hashing (contraseñas en claro para la demo).
 * - Métodos en español y variables en español para facilitar la defensa.
 */
public class UsuarioService {

    public final UsuarioDao usuarioDao = new UsuarioDao();

    public UsuarioService() {}

    /**
     * Autentica con usuario y contraseña en claro.
     * Devuelve Usuario si las credenciales son correctas.
     * Devuelve null si el usuario no existe o contraseña incorrecta.
     * Lanza Exception si el usuario está inactivo.
     */
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) throws Exception {
        Usuario usuario = usuarioDao.findByUsername(nombreUsuario);
        if (usuario == null) return null;

        if (!usuario.isActivo()) throw new Exception("Usuario inactivo. Contacte al administrador.");

        String passAlmacenada = usuario.getPasswordHash(); // aquí se usa como campo de contraseña en claro
        if (passAlmacenada == null || !passAlmacenada.equals(contrasena)) {
            // credenciales incorrectas
            return null;
        }

        // autenticación exitosa
        return usuario;
    }

    /**
     * Crea un usuario. La contraseña se recibe en claro y se guarda tal cual (para la demo).
     * Retorna el id generado por el DAO.
     */
    public Integer crearUsuario(Usuario usuario, String contrasena) throws Exception {
        if (contrasena == null || contrasena.isBlank()) throw new Exception("Contraseña requerida.");
        usuario.setPasswordHash(contrasena); // guardamos plain para simplificar la exposición
        return usuarioDao.create(usuario);
    }

    /**
     * Activa o desactiva un usuario por id.
     */
    public void cambiarEstadoUsuario(int idUsuario, boolean activo) throws Exception {
        Usuario usuario = usuarioDao.findById(idUsuario);
        if (usuario == null) throw new Exception("Usuario no existe.");
        usuario.setActivo(activo);
        usuarioDao.update(usuario);
    }
}