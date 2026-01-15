package com.example.tic_pv.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Adopcion implements Parcelable {

    private String id;
    private String estadoAdopcion;
    private String fechaEmision;
    private String fotoCedulaFrontal;
    private String fotoCedulaPosterior;
    private String fotoServiciosBasicos;
    private String videoHogarMascota;
    private boolean tieneCerramiento;
    private int tipoDomicilio;
    private String videoCompromiso;
    private String contratoAdopcion;
    private String seguimientoAdopcion;
    private String adoptante;
    private String mascotaAdopcion;
    private String observaciones;

    public Adopcion() {
    }

    public Adopcion(String estadoAdopcion, String fotoCedulaFrontal, String fotoCedulaPosterior, String fotoServiciosBasicos, String videoHogarMascota, boolean cerramientoHogar, int tipoDomicilio) {
        this.estadoAdopcion = estadoAdopcion;
        this.fotoCedulaFrontal = fotoCedulaFrontal;
        this.fotoCedulaPosterior = fotoCedulaPosterior;
        this.fotoServiciosBasicos = fotoServiciosBasicos;
        this.videoHogarMascota = videoHogarMascota;
        this.tieneCerramiento = cerramientoHogar;
        this.tipoDomicilio = tipoDomicilio;
    }

    protected Adopcion(Parcel in) {
        id = in.readString();
        estadoAdopcion = in.readString();
        fechaEmision = in.readString();
        fotoCedulaFrontal = in.readString();
        fotoCedulaPosterior = in.readString();
        fotoServiciosBasicos = in.readString();
        videoHogarMascota = in.readString();
        tieneCerramiento = in.readByte() != 0;
        tipoDomicilio = in.readInt();
        videoCompromiso = in.readString();
        contratoAdopcion = in.readString();
        seguimientoAdopcion = in.readString();
        adoptante = in.readString();
        mascotaAdopcion = in.readString();
        observaciones = in.readString();
    }

    public static final Creator<Adopcion> CREATOR = new Creator<Adopcion>() {
        @Override
        public Adopcion createFromParcel(Parcel in) {
            return new Adopcion(in);
        }

        @Override
        public Adopcion[] newArray(int size) {
            return new Adopcion[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(estadoAdopcion);
        dest.writeString(fechaEmision);
        dest.writeString(fotoCedulaFrontal);
        dest.writeString(fotoCedulaPosterior);
        dest.writeString(fotoServiciosBasicos);
        dest.writeString(videoHogarMascota);
        dest.writeByte((byte) (tieneCerramiento ? 1 : 0));
        dest.writeInt(tipoDomicilio);
        dest.writeString(videoCompromiso);
        dest.writeString(contratoAdopcion);
        dest.writeString(seguimientoAdopcion);
        dest.writeString(adoptante);
        dest.writeString(mascotaAdopcion);
        dest.writeString(observaciones);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstadoAdopcion() {
        return estadoAdopcion;
    }

    public void setEstadoAdopcion(String estadoAdopcion) {
        this.estadoAdopcion = estadoAdopcion;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getFotoCedulaFrontal() {
        return fotoCedulaFrontal;
    }

    public void setFotoCedulaFrontal(String fotoCedulaFrontal) {
        this.fotoCedulaFrontal = fotoCedulaFrontal;
    }

    public String getFotoCedulaPosterior() {
        return fotoCedulaPosterior;
    }

    public void setFotoCedulaPosterior(String fotoCedulaPosterior) {
        this.fotoCedulaPosterior = fotoCedulaPosterior;
    }

    public String getFotoServiciosBasicos() {
        return fotoServiciosBasicos;
    }

    public void setFotoServiciosBasicos(String fotoServiciosBasicos) {
        this.fotoServiciosBasicos = fotoServiciosBasicos;
    }

    public String getVideoHogarMascota() {
        return videoHogarMascota;
    }

    public void setVideoHogarMascota(String videoHogarMascota) {
        this.videoHogarMascota = videoHogarMascota;
    }

    public boolean isTieneCerramiento() {
        return tieneCerramiento;
    }

    public void setTieneCerramiento(boolean tieneCerramiento) {
        this.tieneCerramiento = tieneCerramiento;
    }

    public int getTipoDomicilio() {
        return tipoDomicilio;
    }

    public void setTipoDomicilio(int tipoDomicilio) {
        this.tipoDomicilio = tipoDomicilio;
    }

    public String getVideoCompromiso() {
        return videoCompromiso;
    }

    public void setVideoCompromiso(String videoCompromiso) {
        this.videoCompromiso = videoCompromiso;
    }

    public String getContratoAdopcion() {
        return contratoAdopcion;
    }

    public void setContratoAdopcion(String contratoAdopcion) {
        this.contratoAdopcion = contratoAdopcion;
    }

    public String getSeguimientoAdopcion() {
        return seguimientoAdopcion;
    }

    public void setSeguimientoAdopcion(String seguimientoAdopcion) {
        this.seguimientoAdopcion = seguimientoAdopcion;
    }

    public String getAdoptante() {
        return adoptante;
    }

    public void setAdoptante(String adoptante) {
        this.adoptante = adoptante;
    }

    public String getMascotaAdopcion() {
        return mascotaAdopcion;
    }

    public void setMascotaAdopcion(String mascotaAdopcion) {
        this.mascotaAdopcion = mascotaAdopcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
