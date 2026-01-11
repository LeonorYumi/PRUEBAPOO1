package service;

import dao.UsuarioDao;
import model.Usuario;
import java.util.List;

public class UsuarioService {
    private final UsuarioDao usuarioDao = new UsuarioDao();

    public Usuario iniciarSesion(String user, String pass) {
        if (user == null || pass == null || user.isEmpty() || pass.isEmpty()) {
            return null;
        }
        return usuarioDao.findByLogin(user, pass);
    }

    // Métodos para el Administrador
    public void crearUsuario(Usuario u) throws Exception {
        usuarioDao.save(u);
    }

    public List<Usuario> obtenerTodos() throws Exception {
        return usuarioDao.findAll();
    }

    public void actualizarUsuario(Usuario u) throws Exception {
        usuarioDao.update(u);
    }

    public void eliminarUsuario(String cedula) throws Exception {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new Exception("No se puede eliminar: La cédula del usuario es inválida.");
        }
        usuarioDao.delete(cedula);
    }
}