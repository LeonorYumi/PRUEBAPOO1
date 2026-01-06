package model;

import java.time.OffsetDateTime;

public class Tramite {
    private Integer id;
    private Integer solicitanteId;
    private String estado; // pendiente, en_examenes, aprobado, reprobado, licencia_emitida
    private OffsetDateTime fechaCreacion;
    private Integer createdBy;

    public Tramite() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Integer solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
}