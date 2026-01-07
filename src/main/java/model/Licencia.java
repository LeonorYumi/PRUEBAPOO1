package model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Licencia {
    private Integer id;
    private Integer tramiteId;
    private String numeroLicencia;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private Integer createdBy;
    private OffsetDateTime createdAt;

    public Licencia() {}

    public Integer getId() {
        return id; }
    public void setId(Integer id) {
        this.id = id; }

    public Integer getTramiteId() {
        return tramiteId; }
    public void setTramiteId(Integer tramiteId) {
        this.tramiteId = tramiteId; }

    public String getNumeroLicencia() {
        return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia; }

    public LocalDate getFechaEmision() {
        return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento; }

    public Integer getCreatedBy() {
        return createdBy; }
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy; }

    public OffsetDateTime getCreatedAt() {
        return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt; }
}