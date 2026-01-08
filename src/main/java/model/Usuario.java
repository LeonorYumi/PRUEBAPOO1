package model;

public class Usuario {
    private int id;
    private String nombre;
    private String cedula;   // Añadido para req 4.7
    private String username;
    private String password; // Para capturar la contraseña plana antes de enviarla al SQL
    private String rol;
    private boolean activo;
    private String estado;   // Para mostrar "Activo/Inactivo" en la tabla

    public Usuario() {}

    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getEstado() { return activo ? "Activo" : "Inactivo"; }

    public void setEstado(String estado) {
        this.activo = "activo".equalsIgnoreCase(estado);
        this.estado = estado;
    }
}