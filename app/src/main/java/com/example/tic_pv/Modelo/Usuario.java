package com.example.tic_pv.Modelo;
import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable{
    private String idUsuario;
    private String nombre;
    private String cedula;
    private int edad;
    private String fechaNacimento;
    private String estadoCivil;
    private String ocupacion;
    private String telefono;
    private String estadoUsuario;
    private String ig;
    private String fb;
    private String domicilioUsuario;
    private String cuenta;

    public Usuario() {
    }

    public Usuario(String nombre, String cedula, int edad, String fechaNacimento, String estadoCivil, String ocupacion, String telefono, String estadoUsuario, String ig, String fb) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.edad = edad;
        this.fechaNacimento = fechaNacimento;
        this.estadoCivil = estadoCivil;
        this.ocupacion = ocupacion;
        this.telefono = telefono;
        this.estadoUsuario = estadoUsuario;
        this.ig = ig;
        this.fb = fb;
    }

    protected Usuario(Parcel in) {
        idUsuario = in.readString();
        nombre = in.readString();
        cedula = in.readString();
        edad = in.readInt();
        fechaNacimento = in.readString();
        estadoCivil = in.readString();
        ocupacion = in.readString();
        telefono = in.readString();
        estadoUsuario = in.readString();
        ig = in.readString();
        fb = in.readString();
        domicilioUsuario = in.readString();
        cuenta = in.readString();
    }

    // Implementación de Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUsuario);
        dest.writeString(nombre);
        dest.writeString(cedula);
        dest.writeInt(edad);
        dest.writeString(fechaNacimento);
        dest.writeString(estadoCivil);
        dest.writeString(ocupacion);
        dest.writeString(telefono);
        dest.writeString(estadoUsuario);
        dest.writeString(ig);
        dest.writeString(fb);
        dest.writeString(domicilioUsuario);
        dest.writeString(cuenta);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getFechaNacimento() {
        return fechaNacimento;
    }

    public void setFechaNacimento(String fechaNacimento) {
        this.fechaNacimento = fechaNacimento;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getIg() {
        return ig;
    }

    public void setIg(String ig) {
        this.ig = ig;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getDomicilioUsuario() {
        return domicilioUsuario;
    }

    public void setDomicilioUsuario(String domicilioUsuario) {
        this.domicilioUsuario = domicilioUsuario;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }


}
