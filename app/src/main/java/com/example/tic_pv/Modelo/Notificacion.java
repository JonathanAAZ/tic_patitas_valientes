package com.example.tic_pv.Modelo;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notificacion {
    private String id;
    private String idUsuarioReceptor;
    private String titulo;
    private String cuerpo;
    private String tipoNotificacion;
    private String idRelacionado; //ID del objeto con el que está relacionada la notificación
    private String fechaNotificacion;
    private String horaNotificacion;
    private String estado;

    public Notificacion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuarioReceptor() {
        return idUsuarioReceptor;
    }

    public void setIdUsuarioReceptor(String idUsuarioReceptor) {
        this.idUsuarioReceptor = idUsuarioReceptor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(String tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    public String getIdRelacionado() {
        return idRelacionado;
    }

    public void setIdRelacionado(String idRelacionado) {
        this.idRelacionado = idRelacionado;
    }

    public String getFechaNotificacion() {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(String fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public String getHoraNotificacion() {
        return horaNotificacion;
    }

    public void setHoraNotificacion(String horaNotificacion) {
        this.horaNotificacion = horaNotificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void establecerFechaHoraActual() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        this.fechaNotificacion = formatoFecha.format(new Date());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
        this.horaNotificacion = formatoHora.format(new Date());
    }
}
