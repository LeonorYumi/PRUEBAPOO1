package model;

public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String passwordHash; // Corresponde a password_hash en SQL
    private String rol;          // Aquí guardaremos 'ADMIN' o 'ANALISTA'
    private boolean activo;

    // Constructor vacío
    public Usuario() {}

    // Getters y Setters (Encapsulamiento)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}