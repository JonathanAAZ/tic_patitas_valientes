package com.example.tic_pv.Modelo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContratoAdopcion {
    private String id;
    private String estado;
    private String idAdoptante;
    private String idMascota;
    private String fechaContratoAdopcion;
    private String horaContratoAdopcion;
    private String firmaAdministrador;
    private String firmaAdoptante;
    private List<String> listaClausulas;

    public ContratoAdopcion() {
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

    public String getFechaContratoAdopcion() {
        return fechaContratoAdopcion;
    }

    public void setFechaContratoAdopcion(String fechaContratoAdopcion) {
        this.fechaContratoAdopcion = fechaContratoAdopcion;
    }

    public String getHoraContratoAdopcion() {
        return horaContratoAdopcion;
    }

    public void setHoraContratoAdopcion(String horaContratoAdopcion) {
        this.horaContratoAdopcion = horaContratoAdopcion;
    }

    public String getFirmaAdministrador() {
        return firmaAdministrador;
    }

    public void setFirmaAdministrador(String firmaAdministrador) {
        this.firmaAdministrador = firmaAdministrador;
    }

    public String getFirmaAdoptante() {
        return firmaAdoptante;
    }

    public void setFirmaAdoptante(String firmaAdoptante) {
        this.firmaAdoptante = firmaAdoptante;
    }

    public List<String> getListaClausulas() {
        return listaClausulas;
    }

    public void setListaClausulas(List<String> listaClausulas) {
        this.listaClausulas = listaClausulas;
    }

    public void establecerFechaHoraActual() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        this.fechaContratoAdopcion = formatoFecha.format(new Date());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
        this.horaContratoAdopcion = formatoHora.format(new Date());
    }
}
