package service;

import dao.UsuarioDao;
import model.Usuario;

public class UsuarioService {
    private final UsuarioDao usuarioDao = new UsuarioDao();

    public Usuario iniciarSesion(String user, String pass) {
        // Aquí podrías agregar validaciones de seguridad extra
        if (user == null || pass == null || user.isEmpty() || pass.isEmpty()) {
            return null;
        }
        return usuarioDao.findByLogin(user, pass);
    }
}