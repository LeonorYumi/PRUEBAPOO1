package model;

import java.time.OffsetDateTime;

public class Examen {
    private Integer id;
    private Integer tramiteId;
    private Double notaTeorica;
    private Double notaPractica;
    private OffsetDateTime fecha;
    private Integer createdBy;

    public Examen() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTramiteId() { return tramiteId; }
    public void setTramiteId(Integer tramiteId) { this.tramiteId = tramiteId; }

    public Double getNotaTeorica() { return notaTeorica; }
    public void setNotaTeorica(Double notaTeorica) { this.notaTeorica = notaTeorica; }

    public Double getNotaPractica() { return notaPractica; }
    public void setNotaPractica(Double notaPractica) { this.notaPractica = notaPractica; }

    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
}