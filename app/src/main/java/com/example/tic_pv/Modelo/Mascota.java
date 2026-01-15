package com.example.tic_pv.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Mascota implements Parcelable {
    private String id;
    private String estadoMascota;
    private String fotoMascota;
    private String nombreMascota;
    private String especieMascota;
    private String razaMascota;
    private String edadMascota;
    private String sexoMascota;
    private String colorMascota;
    private String caracterMascota;
    private String domicilio;
    private String codigoQR;
    private String fechaEsterilizacion;
    private boolean mascotaAdoptada;
    private boolean mascotaVacunada;
    private boolean mascotaEsterilizada;
    private boolean mascotaDesparasitada;

    // Constructor
    public Mascota() {
    }

    public Mascota(String estadoMascota, String fotoMascota, String nombreMascota, String especieMascota, String razaMascota, String edadMascota, String sexoMascota, String colorMascota, String caracterMascota, String domicilio, String fechaEsterilizacion, boolean mascotaAdoptada, boolean mascotaVacunada, boolean mascotaEsterilizada, boolean mascotaDesparasitada) {
        this.estadoMascota = estadoMascota;
        this.fotoMascota = fotoMascota;
        this.nombreMascota = nombreMascota;
        this.especieMascota = especieMascota;
        this.razaMascota = razaMascota;
        this.edadMascota = edadMascota;
        this.sexoMascota = sexoMascota;
        this.colorMascota = colorMascota;
        this.caracterMascota = caracterMascota;
        this.domicilio = domicilio;
        this.fechaEsterilizacion = fechaEsterilizacion;
        this.mascotaAdoptada = mascotaAdoptada;
        this.mascotaVacunada = mascotaVacunada;
        this.mascotaEsterilizada = mascotaEsterilizada;
        this.mascotaDesparasitada = mascotaDesparasitada;
    }

    // Getters y setters

    // Parcelable: métodos necesarios

    // Constructor que crea un objeto a partir de un Parcel
    protected Mascota(Parcel in) {
        id = in.readString();
        estadoMascota = in.readString();
        fotoMascota = in.readString();
        nombreMascota = in.readString();
        especieMascota = in.readString();
        razaMascota = in.readString();
        edadMascota = in.readString();
        sexoMascota = in.readString();
        colorMascota = in.readString();
        caracterMascota = in.readString();
        domicilio = in.readString();
        codigoQR = in.readString();
        fechaEsterilizacion = in.readString();
        mascotaAdoptada = in.readByte() != 0;
        mascotaVacunada = in.readByte() != 0;
        mascotaEsterilizada = in.readByte() != 0;
        mascotaDesparasitada = in.readByte() != 0;
    }

    // Describe el tipo de contenido (en este caso siempre será 0)
    @Override
    public int describeContents() {
        return 0;
    }

    // Escribe los datos del objeto en el Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(estadoMascota);
        dest.writeString(fotoMascota);
        dest.writeString(nombreMascota);
        dest.writeString(especieMascota);
        dest.writeString(razaMascota);
        dest.writeString(edadMascota);
        dest.writeString(sexoMascota);
        dest.writeString(colorMascota);
        dest.writeString(caracterMascota);
        dest.writeString(domicilio);
        dest.writeString(codigoQR);
        dest.writeString(fechaEsterilizacion);
        dest.writeByte((byte) (mascotaAdoptada ? 1 : 0));
        dest.writeByte((byte) (mascotaVacunada ? 1 : 0));
        dest.writeByte((byte) (mascotaEsterilizada ? 1 : 0));
        dest.writeByte((byte) (mascotaDesparasitada ? 1 : 0));
    }

    // CREATOR: Esta es una constante que permite la creación de objetos de la clase Mascota a partir de un Parcel
    public static final Creator<Mascota> CREATOR = new Creator<Mascota>() {
        @Override
        public Mascota createFromParcel(Parcel in) {
            return new Mascota(in);
        }

        @Override
        public Mascota[] newArray(int size) {
            return new Mascota[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstadoMascota() {
        return estadoMascota;
    }

    public void setEstadoMascota(String estadoMascota) {
        this.estadoMascota = estadoMascota;
    }

    public String getFotoMascota() {
        return fotoMascota;
    }

    public void setFotoMascota(String fotoMascota) {
        this.fotoMascota = fotoMascota;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getEspecieMascota() {
        return especieMascota;
    }

    public void setEspecieMascota(String especieMascota) {
        this.especieMascota = especieMascota;
    }

    public String getRazaMascota() {
        return razaMascota;
    }

    public void setRazaMascota(String razaMascota) {
        this.razaMascota = razaMascota;
    }

    public String getEdadMascota() {
        return edadMascota;
    }

    public void setEdadMascota(String edadMascota) {
        this.edadMascota = edadMascota;
    }

    public String getSexoMascota() {
        return sexoMascota;
    }

    public void setSexoMascota(String sexoMascota) {
        this.sexoMascota = sexoMascota;
    }

    public String getColorMascota() {
        return colorMascota;
    }

    public void setColorMascota(String colorMascota) {
        this.colorMascota = colorMascota;
    }

    public String getCaracterMascota() {
        return caracterMascota;
    }

    public void setCaracterMascota(String caracterMascota) {
        this.caracterMascota = caracterMascota;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(String codigoQR) {
        this.codigoQR = codigoQR;
    }

    public String getFechaEsterilizacion() {
        return fechaEsterilizacion;
    }

    public void setFechaEsterilizacion(String fechaEsterilizacion) {
        this.fechaEsterilizacion = fechaEsterilizacion;
    }

    public boolean isMascotaAdoptada() {
        return mascotaAdoptada;
    }

    public void setMascotaAdoptada(boolean mascotaAdoptada) {
        this.mascotaAdoptada = mascotaAdoptada;
    }

    public boolean isMascotaVacunada() {
        return mascotaVacunada;
    }

    public void setMascotaVacunada(boolean mascotaVacunada) {
        this.mascotaVacunada = mascotaVacunada;
    }

    public boolean isMascotaEsterilizada() {
        return mascotaEsterilizada;
    }

    public void setMascotaEsterilizada(boolean mascotaEsterilizada) {
        this.mascotaEsterilizada = mascotaEsterilizada;
    }

    public boolean isMascotaDesparasitada() {
        return mascotaDesparasitada;
    }

    public void setMascotaDesparasitada(boolean mascotaDesparasitada) {
        this.mascotaDesparasitada = mascotaDesparasitada;
    }
}
