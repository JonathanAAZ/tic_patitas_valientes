package com.example.tic_pv.Modelo;

public class ProgramarNotificacionRequest {
    private String token;
    private String titulo;
    private String cuerpo;
    private String fechaProgramada; // Formato: YYYY-MM-DD HH:mm:ss
    private String idUsuario;
    private String idNotificacion;

    public ProgramarNotificacionRequest(String token, String titulo, String cuerpo, String fechaProgramada, String idUsuario, String idNotificacion) {
        this.token = token;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.fechaProgramada = fechaProgramada;
        this.idUsuario = idUsuario;
        this.idNotificacion = idNotificacion;
    }

    public String getToken() {
        return token;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public String getFechaProgramada() {
        return fechaProgramada;
    }

    public String getIdUsuario() { return idUsuario; }

    public String getIdNotificacion() { return idNotificacion; }
}
