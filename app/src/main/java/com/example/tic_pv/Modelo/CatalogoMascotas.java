package com.example.tic_pv.Modelo;

import android.content.Context;
import android.util.Log;

import com.example.tic_pv.Controlador.ControladorMascota;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CatalogoMascotas {
    private ArrayList <Mascota> listaAnimalesAdopcion = new ArrayList<>();

    public CatalogoMascotas() {
    }

    public CatalogoMascotas(ArrayList<Mascota> listaAnimalesAdopcion) {
        this.listaAnimalesAdopcion = listaAnimalesAdopcion;
    }

    public ArrayList<Mascota> getListaAnimalesAdopcion() {
        return listaAnimalesAdopcion;
    }

    public void setListaAnimalesAdopcion(ArrayList<Mascota> listaAnimalesAdopcion) {
        this.listaAnimalesAdopcion = listaAnimalesAdopcion;
    }

    public void obtenerListaMascotasAdopcion(Context context, CatalogoMascotas.Callback<List<Mascota>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Mascotas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Mascota> listaMascotas = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Mascota mascota = new Mascota();
                        mascota.setId(document.getId());
                        mascota.setNombreMascota(document.getString("nombre"));
                        mascota.setFotoMascota(document.getString("fotoMascota"));
                        mascota.setEdadMascota(document.getString("edad"));
                        mascota.setEspecieMascota(document.getString("especie"));
                        mascota.setSexoMascota(document.getString("sexo"));
                        mascota.setMascotaVacunada(Boolean.TRUE.equals(document.getBoolean("vacunacionMascota")));
                        mascota.setMascotaDesparasitada(Boolean.TRUE.equals(document.getBoolean("desparasitacionMascota")));
                        mascota.setMascotaEsterilizada(Boolean.TRUE.equals(document.getBoolean("esterilizacionMascota")));
                        mascota.setEstadoMascota(document.getString("estado"));

                        if (Boolean.FALSE.equals(document.getBoolean("mascotaAdoptada"))) {
                            listaMascotas.add(mascota);
                        }
                    }
                    // Notificar que los datos están listos
                    callback.onComplete(listaMascotas);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error al obtener mascotas", e);
                    callback.onError(e);
                });
    }

    // Callback genérico
    public interface Callback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }


}
