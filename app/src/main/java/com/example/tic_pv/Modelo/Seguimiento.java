package com.example.tic_pv.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Seguimiento implements Parcelable {
    private String id;
    private String estado;
    private String idAdoptante;
    private String idMascota;
    private String idVoluntario;
    private String nombreAdoptante;
    private String nombreMascota;
    private String nombreVoluntario;

    private String listaMensajes;

    public Seguimiento(String estado, String idAdoptante, String idMascota, String listaMensajes) {
        this.estado = estado;
        this.idAdoptante = idAdoptante;
        this.idMascota = idMascota;
        this.listaMensajes = listaMensajes;
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

    public String getListaMensajes() {
        return listaMensajes;
    }

    public void setListaMensajes(String listaPreguntas) {
        this.listaMensajes = listaPreguntas;
    }

    protected Seguimiento (Parcel in) {
        id = in.readString();
        estado = in.readString();
        idAdoptante = in.readString();
        idMascota = in.readString();
        idVoluntario = in.readString();
        nombreAdoptante = in.readString();
        nombreMascota = in.readString();
        nombreVoluntario = in.readString();
        listaMensajes = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(estado);
        dest.writeString(idAdoptante);
        dest.writeString(idMascota);
        dest.writeString(idVoluntario);
        dest.writeString(nombreAdoptante);
        dest.writeString(nombreMascota);
        dest.writeString(nombreVoluntario);
        dest.writeString(listaMensajes);
    }
    public static final Creator<Seguimiento> CREATOR = new Creator<Seguimiento>() {
        @Override
        public Seguimiento createFromParcel(Parcel in) {
            return new Seguimiento(in);
        }

        @Override
        public Seguimiento[] newArray(int size) {
            return new Seguimiento[size];
        }
    };
}

