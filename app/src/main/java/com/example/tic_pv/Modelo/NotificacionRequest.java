package com.example.tic_pv.Modelo;

public class NotificacionRequest {
    private String token;
    private String titulo;
    private String cuerpo;

    //Esta es la clase que contiene la notificación que serán enviada al Backend de Render
    public NotificacionRequest(String token, String titulo, String cuerpo) {
        this.token = token;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
    }
}
