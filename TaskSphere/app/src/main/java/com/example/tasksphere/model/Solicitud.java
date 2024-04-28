package com.example.tasksphere.model;

public class Solicitud {

    String estado, fecha, usuario;

    public Solicitud() {
    }

    public Solicitud(String estado, String fecha, String usuario) {
        this.estado = estado;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
