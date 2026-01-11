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

    // Único método necesario para actualizar datos y estado (activo/inactivo)
    public void actualizarUsuario(Usuario u) throws Exception {
        usuarioDao.update(u);
    }
}