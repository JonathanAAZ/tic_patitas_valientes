package com.example.tic_pv.Modelo;
import android.os.Parcel;
import android.os.Parcelable;

public class Domicilio implements Parcelable{

    private String idDomicilio;
    private String pais;
    private String provincia;
    private String canton;
    private String parroquia;
    private String barrio;
    private String calles;

    private String estadoDomicilio;

    public Domicilio() {
    }

    public Domicilio(String pais, String provincia, String canton, String parroquia, String barrio, String calles, String estadoDomicilio) {
        this.pais = pais;
        this.provincia = provincia;
        this.canton = canton;
        this.parroquia = parroquia;
        this.barrio = barrio;
        this.calles = calles;
        this.estadoDomicilio = estadoDomicilio;
    }

    protected Domicilio(Parcel in) {
        idDomicilio = in.readString();
        pais = in.readString();
        provincia = in.readString();
        canton = in.readString();
        parroquia = in.readString();
        barrio = in.readString();
        calles = in.readString();
        estadoDomicilio = in.readString();
    }

    public static final Creator<Domicilio> CREATOR = new Creator<Domicilio>() {
        @Override
        public Domicilio createFromParcel(Parcel in) {
            return new Domicilio(in);
        }

        @Override
        public Domicilio[] newArray(int size) {
            return new Domicilio[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idDomicilio);
        dest.writeString(pais);
        dest.writeString(provincia);
        dest.writeString(canton);
        dest.writeString(parroquia);
        dest.writeString(barrio);
        dest.writeString(calles);
        dest.writeString(estadoDomicilio);
    }

    public String getIdDomicilio() {
        return idDomicilio;
    }

    public void setIdDomicilio(String idDomicilio) {
        this.idDomicilio = idDomicilio;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getParroquia() {
        return parroquia;
    }

    public void setParroquia(String parroquia) {
        this.parroquia = parroquia;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getCalles() {
        return calles;
    }

    public void setCalles(String calles) {
        this.calles = calles;
    }

    public String getEstadoDomicilio() {
        return estadoDomicilio;
    }

    public void setEstadoDomicilio(String estadoDomicilio) {
        this.estadoDomicilio = estadoDomicilio;
    }
}
