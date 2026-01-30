package com.example.tic_pv.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Timestamp;

public class Mensaje implements Parcelable {
    private String id;
    private String emisor;
    private String idEmisor;
    private String contenido;
    private String receptor;
    private String idReceptor;
    private long timestamp;

    public Mensaje() {
    }

    public Mensaje(String id, String emisor, String idEmisor, String contenido, String receptor, String idReceptor, long timestamp) {
        this.id = id;
        this.emisor = emisor;
        this.idEmisor = idEmisor;
        this.contenido = contenido;
        this.receptor = receptor;
        this.idReceptor = idReceptor;
        this.timestamp = timestamp;
    }
    protected Mensaje (Parcel in) {
        id = in.readString();
        emisor = in.readString();
        idEmisor = in.readString();
        contenido = in.readString();
        receptor = in.readString();
        idReceptor = in.readString();
        timestamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(emisor);
        dest.writeString(idEmisor);
        dest.writeString(contenido);
        dest.writeString(receptor);
        dest.writeString(idReceptor);
        dest.writeLong(timestamp);
    }

    public static final Creator<Mensaje> CREATOR = new Creator<Mensaje>() {
        @Override
        public Mensaje createFromParcel(Parcel in) {
            return new Mensaje(in);
        }

        @Override
        public Mensaje[] newArray(int size) {
            return new Mensaje[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
