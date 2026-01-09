package model;

public class Tramite {
    private int id;
    private int solicitanteId;
    private String nombre;
    private String cedula;
    private String tipoLicencia; // 👈 Antes quizás se llamaba solo 'tipo'
    private String fecha;
    private String estado;
    private int createdBy;

    public Tramite() {}

    // Constructor para pruebas y reportes
    public Tramite(int id, String cedula, String nombre, String tipoLicencia, String fecha, String estado) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.tipoLicencia = tipoLicencia;
        this.fecha = fecha;
        this.estado = estado;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public int getSolicitanteId() { return solicitanteId; }
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getTipoLicencia() { return tipoLicencia; } // 👈 Este es el que faltaba
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public int getCreatedBy() { return createdBy; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setSolicitanteId(int solicitanteId) { this.solicitanteId = solicitanteId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; } // 👈 ESTO SOLUCIONA EL ERROR
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}