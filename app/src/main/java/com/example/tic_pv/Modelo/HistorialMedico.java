package com.example.tic_pv.Modelo;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class HistorialMedico implements Parcelable {
    private String id;
    private String nombre;
    private String fechaColocacion;
    private String horaRecordatorio;
    private String fechaProxima;
    private String tipo;

    // Constructor vacío
    public HistorialMedico() {
    }

    // Constructor con parámetros
    public HistorialMedico(String id, String nombre, String fechaColocacion, String horaRecordatorio, String fechaProxima, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.fechaColocacion = fechaColocacion;
        this.horaRecordatorio = horaRecordatorio;
        this.fechaProxima = fechaProxima;
        this.tipo = tipo;
    }

    // Constructor que crea un objeto a partir de un Parcel
    protected HistorialMedico(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        fechaColocacion = in.readString();
        horaRecordatorio = in.readString();
        fechaProxima = in.readString();
        tipo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(fechaColocacion);
        dest.writeString(horaRecordatorio);
        dest.writeString(fechaProxima);
        dest.writeString(tipo);
    }

    public static final Creator<HistorialMedico> CREATOR = new Creator<HistorialMedico>() {
        @Override
        public HistorialMedico createFromParcel(Parcel in) {
            return new HistorialMedico(in);
        }

        @Override
        public HistorialMedico[] newArray(int size) {
            return new HistorialMedico[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaColocacion() {
        return fechaColocacion;
    }

    public void setFechaColocacion(String fechaColocacion) {
        this.fechaColocacion = fechaColocacion;
    }
    public String getHoraRecordatorio() {
        return horaRecordatorio;
    }
    public void setHoraRecordatorio(String horaRecordatorio) {
        this.horaRecordatorio = horaRecordatorio;
    }

    public String getFechaProxima() {
        return fechaProxima;
    }

    public void setFechaProxima(String fechaProxima) {
        this.fechaProxima = fechaProxima;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
