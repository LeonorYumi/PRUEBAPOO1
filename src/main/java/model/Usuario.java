package model;

import java.time.OffsetDateTime;

public class Usuario {
    private Integer id;
    private String nombre;
    private String cedula;
    private String username;
    private String passwordHash;
    private String rol; // ADMIN or ANALISTA
    private boolean activo;
    private OffsetDateTime createdAt;

    public Usuario() {}

    public Integer getId() {
        return id; }
    public void setId(Integer id) {
        this.id = id; }

    public String getNombre() {
        return nombre; }
    public void setNombre(String nombre) {
        this.nombre = nombre; }

    public String getCedula() {
        return cedula; }
    public void setCedula(String cedula) {
        this.cedula = cedula; }

    public String getUsername() {
        return username; }
    public void setUsername(String username) {
        this.username = username; }

    public String getPasswordHash() {
        return passwordHash; }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash; }

    public String getRol() {
        return rol; }
    public void setRol(String rol) {
        this.rol = rol; }

    public boolean isActivo() {
        return activo; }
    public void setActivo(boolean activo) {
        this.activo = activo; }

    public OffsetDateTime getCreatedAt() {
        return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt; }
}