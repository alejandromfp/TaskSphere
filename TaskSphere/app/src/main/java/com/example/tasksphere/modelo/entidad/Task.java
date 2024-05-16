package com.example.tasksphere.modelo.entidad;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Task implements Serializable {
    private String taskId, taskName, taskDescription, asignadaA ,fechaInicio, fechaFinal;
    private Date fechaCreacion;

    public Task() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getAsignadaA() {
        return asignadaA;
    }

    public void setAsignadaA(String asignadaA) {
        this.asignadaA = asignadaA;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
}
