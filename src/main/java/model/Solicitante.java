package model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Solicitante {
    private Integer id;
    private String cedula;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String tipoLicencia;
    private OffsetDateTime fechaSolicitud;
    private Integer createdBy;

    public Solicitante() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTipoLicencia() { return tipoLicencia; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }

    public OffsetDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(OffsetDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
}