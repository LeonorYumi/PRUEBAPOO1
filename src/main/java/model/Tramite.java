package model;

import javafx.beans.property.*;

public class Tramite {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty cedula = new SimpleStringProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final StringProperty fecha = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();
    private final IntegerProperty solicitanteId = new SimpleIntegerProperty();
    private final IntegerProperty createdBy = new SimpleIntegerProperty();

    public Tramite() {}

    // Constructor que pide el GestionTramiteController
    public Tramite(int id, String cedula, String nombre, String tipo, String fecha, String estado) {
        setId(id);
        setCedula(cedula);
        setNombre(nombre);
        setTipo(tipo);
        setFecha(fecha);
        setEstado(estado);
    }

    // Métodos Property para las Tablas (Elimina errores en GestionTramiteController)
    public IntegerProperty idProperty() { return id; }
    public StringProperty cedulaProperty() { return cedula; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty tipoProperty() { return tipo; }
    public StringProperty fechaProperty() { return fecha; }
    public StringProperty estadoProperty() { return estado; }

    // Getters y Setters Estándar (Elimina errores en TramiteDao)
    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }

    public String getCedula() { return cedula.get(); }
    public void setCedula(String v) { cedula.set(v); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String v) { nombre.set(v); }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }

    public String getFecha() { return fecha.get(); }
    public void setFecha(String v) { fecha.set(v); }

    public String getEstado() { return estado.get(); }
    public void setEstado(String v) { estado.set(v); }

    public int getSolicitanteId() { return solicitanteId.get(); }
    public void setSolicitanteId(int v) { solicitanteId.set(v); }

    public int getCreatedBy() { return createdBy.get(); }
    public void setCreatedBy(int v) { createdBy.set(v); }
}