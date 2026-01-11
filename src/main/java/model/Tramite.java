package model;

public class Tramite {
    private int id;
    private int solicitanteId;
    private String nombre;
    private String cedula;
    private String tipoLicencia;
    private String fecha;
    private String estado;
    private int createdBy;

    // 🚩 LO QUE FALTA PARA EL DETALLE (REQUERIMIENTO 4.5)
    private double notaTeorica;
    private double notaPractica;
    private boolean fotos;
    private boolean certificadoEstudios;
    private boolean sinMultas;

    public Tramite() {}

    // Constructor completo para reportes y carga de datos
    public Tramite(int id, String cedula, String nombre, String tipoLicencia, String fecha, String estado) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.tipoLicencia = tipoLicencia;
        this.fecha = fecha;
        this.estado = estado;
    }

    // --- GETTERS  ---
    public int getId() { return id; }
    public int getSolicitanteId() { return solicitanteId; }
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getTipoLicencia() { return tipoLicencia; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public int getCreatedBy() { return createdBy; }


    public double getNotaTeorica() { return notaTeorica; }
    public double getNotaPractica() { return notaPractica; }
    public boolean isFotos() { return fotos; }
    public boolean isCertificadoEstudios() { return certificadoEstudios; }
    public boolean isSinMultas() { return sinMultas; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setSolicitanteId(int solicitanteId) { this.solicitanteId = solicitanteId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }


    public void setNotaTeorica(double notaTeorica) { this.notaTeorica = notaTeorica; }
    public void setNotaPractica(double notaPractica) { this.notaPractica = notaPractica; }
    public void setFotos(boolean fotos) { this.fotos = fotos; }
    public void setCertificadoEstudios(boolean certificadoEstudios) { this.certificadoEstudios = certificadoEstudios; }
    public void setSinMultas(boolean sinMultas) { this.sinMultas = sinMultas; }
}