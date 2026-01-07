package model;

import java.time.OffsetDateTime;

public class Reporte {
    private Integer id;
    private String descripcion;
    private OffsetDateTime fecha;
    // Agregar campos según necesidad

    public Reporte() {}

    public Integer getId() {
        return id; }
    public void setId(Integer id) {
        this.id = id; }

    public String getDescripcion() {
        return descripcion; }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion; }

    public OffsetDateTime getFecha() {
        return fecha; }
    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha; }
}