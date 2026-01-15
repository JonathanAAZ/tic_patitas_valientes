package com.example.tic_pv.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Desparasitacion extends HistorialMedico implements Parcelable {
    private float pesoMascota;
    private float cantidadDesparasitante;

    public Desparasitacion() {
        super();
    }

    public Desparasitacion(String id, String nombre, String fechaColocacion, String horaRecordatorio, String fechaProxima, float pesoMascota, float cantidadDesparasitante, String tipo) {
        super(id, nombre, fechaColocacion, horaRecordatorio, fechaProxima, tipo);
        this.pesoMascota = pesoMascota;
        this.cantidadDesparasitante = cantidadDesparasitante;
    }

    protected Desparasitacion(Parcel in) {
        super(in); // Llama al constructor Parcel del padre
        pesoMascota = in.readFloat();
        cantidadDesparasitante = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags); // Escribe primero los campos del padre
        dest.writeFloat(pesoMascota);
        dest.writeFloat(cantidadDesparasitante);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Desparasitacion> CREATOR = new Creator<Desparasitacion>() {
        @Override
        public Desparasitacion createFromParcel(Parcel in) {
            return new Desparasitacion(in);
        }

        @Override
        public Desparasitacion[] newArray(int size) {
            return new Desparasitacion[size];
        }
    };

    public float getPesoMascota() {
        return pesoMascota;
    }

    public void setPesoMascota(float pesoMascota) {
        this.pesoMascota = pesoMascota;
    }

    public float getCantidadDesparasitante() {
        return cantidadDesparasitante;
    }

    public void setCantidadDesparasitante(float cantidadDesparasitante) {
        this.cantidadDesparasitante = cantidadDesparasitante;
    }
}
