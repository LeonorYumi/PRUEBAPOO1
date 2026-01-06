package model;

public class Requisito {
    private Integer id;
    private Integer tramiteId;
    private boolean certificadoMedico;
    private boolean pago;
    private boolean multas;
    private String observaciones;

    public Requisito() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTramiteId() { return tramiteId; }
    public void setTramiteId(Integer tramiteId) { this.tramiteId = tramiteId; }

    public boolean isCertificadoMedico() { return certificadoMedico; }
    public void setCertificadoMedico(boolean certificadoMedico) { this.certificadoMedico = certificadoMedico; }

    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }

    public boolean isMultas() { return multas; }
    public void setMultas(boolean multas) { this.multas = multas; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}