package com.example.tasksphere.modelo.entidad;

import java.util.Date;

public class Fichaje {

    private String documentFechaId , listaFichajeId;
    Date fechaEmpiezo, FechaFin;

    public Fichaje() {
    }

    public String getDocumentFechaId() {
        return documentFechaId;
    }

    public void setDocumentFechaId(String documentFechaId) {
        this.documentFechaId = documentFechaId;
    }

    public String getListaFichajeId() {
        return listaFichajeId;
    }

    public void setListaFichajeId(String listaFichajeId) {
        this.listaFichajeId = listaFichajeId;
    }

    public Date getFechaEmpiezo() {
        return fechaEmpiezo;
    }

    public void setFechaEmpiezo(Date fechaEmpiezo) {
        this.fechaEmpiezo = fechaEmpiezo;
    }

    public Date getFechaFin() {
        return FechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        FechaFin = fechaFin;
    }
}
