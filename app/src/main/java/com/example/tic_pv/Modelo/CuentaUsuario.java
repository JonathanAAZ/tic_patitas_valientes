package com.example.tic_pv.Modelo;
import android.os.Parcel;
import android.os.Parcelable;
public class CuentaUsuario implements Parcelable{

    private String idCuenta;
    private String fotoPerfil;
    private String correo;
    private String clave;
    private String rol;
    private String estadoCuenta;
    private String idDispositivo;

    public CuentaUsuario() {
    }

    public CuentaUsuario(String fotoPerfil, String correo, String clave, String rol, String estadoCuenta, String idDispositivo) {
        this.fotoPerfil = fotoPerfil;
        this.correo = correo;
        this.clave = clave;
        this.rol = rol;
        this.estadoCuenta = estadoCuenta;
        this.idDispositivo = idDispositivo;
    }

    // Constructor que recibe Parcel
    protected CuentaUsuario(Parcel in) {
        idCuenta = in.readString();
        fotoPerfil = in.readString();
        correo = in.readString();
        clave = in.readString();
        rol = in.readString();
        estadoCuenta = in.readString();
        idDispositivo = in.readString();
    }

    // Implementación de Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idCuenta);
        dest.writeString(fotoPerfil);
        dest.writeString(correo);
        dest.writeString(clave);
        dest.writeString(rol);
        dest.writeString(estadoCuenta);
        dest.writeString(idDispositivo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CuentaUsuario> CREATOR = new Creator<CuentaUsuario>() {
        @Override
        public CuentaUsuario createFromParcel(Parcel in) {
            return new CuentaUsuario(in);
        }

        @Override
        public CuentaUsuario[] newArray(int size) {
            return new CuentaUsuario[size];
        }
    };

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public String getIdDispositivo() { return idDispositivo; }

    public void setIdDispositivo(String idDispositivo) { this.idDispositivo = idDispositivo; }
}
