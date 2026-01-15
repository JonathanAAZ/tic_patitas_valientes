package com.example.tic_pv.Modelo;

import java.util.ArrayList;

public class Seguimiento {
    private String id;
    private String estado;
    private String idAdoptante;
    private String idMascota;
    private String idVoluntario;
    private String nombreAdoptante;
    private String nombreMascota;
    private String nombreVoluntario;

    private String listaPreguntas;

    public Seguimiento(String estado, String idAdoptante, String idMascota, String listaPreguntas) {
        this.estado = estado;
        this.idAdoptante = idAdoptante;
        this.idMascota = idMascota;
        this.listaPreguntas = listaPreguntas;
    }

    public Seguimiento() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdAdoptante() {
        return idAdoptante;
    }

    public void setIdAdoptante(String idAdoptante) {
        this.idAdoptante = idAdoptante;
    }

    public String getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(String idMascota) {
        this.idMascota = idMascota;
    }

    public String getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(String idVoluntario) {
        this.idVoluntario = idVoluntario;
    }

    public String getNombreAdoptante() {
        return nombreAdoptante;
    }

    public void setNombreAdoptante(String nombreAdoptante) {
        this.nombreAdoptante = nombreAdoptante;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombreVoluntario() {
        return nombreVoluntario;
    }

    public void setNombreVoluntario(String nombreVoluntario) {
        this.nombreVoluntario = nombreVoluntario;
    }

    public String getListaPreguntas() {
        return listaPreguntas;
    }

    public void setListaPreguntas(String listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }
}

