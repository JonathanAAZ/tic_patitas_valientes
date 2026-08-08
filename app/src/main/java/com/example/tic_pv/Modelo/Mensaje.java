package com.example.tic_pv.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class Mensaje implements Parcelable {
    private String id;
    private String emisor;
    private String idEmisor;
    private String contenido;
    private String receptor;
    private String idReceptor;
    private long timestamp;

    // Datos del mensaje al que responde, vacíos si no es una respuesta.
    // Se guardan copiados para poder pintar la cita sin volver a buscar el original.
    private String idMensajeRespondido;
    private String emisorRespondido;
    private String contenidoRespondido;

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
        idMensajeRespondido = in.readString();
        emisorRespondido = in.readString();
        contenidoRespondido = in.readString();
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
        dest.writeString(idMensajeRespondido);
        dest.writeString(emisorRespondido);
        dest.writeString(contenidoRespondido);
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

    public String getIdMensajeRespondido() {
        return idMensajeRespondido;
    }

    public void setIdMensajeRespondido(String idMensajeRespondido) {
        this.idMensajeRespondido = idMensajeRespondido;
    }

    public String getEmisorRespondido() {
        return emisorRespondido;
    }

    public void setEmisorRespondido(String emisorRespondido) {
        this.emisorRespondido = emisorRespondido;
    }

    public String getContenidoRespondido() {
        return contenidoRespondido;
    }

    public void setContenidoRespondido(String contenidoRespondido) {
        this.contenidoRespondido = contenidoRespondido;
    }

    // Un mensaje es respuesta si conserva el id del original.
    // Se excluye para que Firebase no lo guarde como un campo más del mensaje.
    @Exclude
    public boolean esRespuesta() {
        return idMensajeRespondido != null && !idMensajeRespondido.isEmpty();
    }
}
