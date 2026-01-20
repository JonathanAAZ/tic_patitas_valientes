package com.example.tic_pv.Controlador;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tic_pv.Modelo.Desparasitacion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.HistorialMedico;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.Modelo.Notificacion;
import com.example.tic_pv.Vista.GestionarSolicitudesAdopcionActivity;
import com.example.tic_pv.Vista.VerMascotasActivity;
import com.example.tic_pv.Vista.VerMiMascotaActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ControladorHistorialMedico {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("historial_medico");
    private final ControladorNotificaciones controladorNotificaciones = new ControladorNotificaciones();
    public void obtenerVacuna (String idVacuna, String idMascota, Callback<HistorialMedico> callback ) {

        databaseReference.child(idMascota).child(idVacuna).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("FIREBASE", "No se pudo encontrar la vacuna");
                }
                HistorialMedico vacuna = new HistorialMedico();
                vacuna.setId(snapshot.getKey());
                vacuna.setNombre(snapshot.child("nombre").getValue(String.class));
                vacuna.setFechaColocacion(snapshot.child("fechaColocacion").getValue(String.class));
                vacuna.setHoraRecordatorio(snapshot.child("horaRecordatorio").getValue(String.class));
                vacuna.setFechaProxima(snapshot.child("fechaProxima").getValue(String.class));
                vacuna.setTipo(snapshot.child("tipo").getValue(String.class));

                callback.onComplete(vacuna);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }

    public void obtenerDesparasitacion (String idDesparasitacion, String idMascota, Callback<Desparasitacion> callback ) {

        databaseReference.child(idMascota).child(idDesparasitacion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("FIREBASE", "No se pudo encontrar la vacuna");
                }
                Desparasitacion desparasitacion = new Desparasitacion();
                desparasitacion.setId(snapshot.getKey());
                desparasitacion.setNombre(snapshot.child("nombre").getValue(String.class));
                desparasitacion.setFechaColocacion(snapshot.child("fechaColocacion").getValue(String.class));
                desparasitacion.setHoraRecordatorio(snapshot.child("horaRecordatorio").getValue(String.class));
                desparasitacion.setFechaProxima(snapshot.child("fechaProxima").getValue(String.class));
                desparasitacion.setTipo(snapshot.child("tipo").getValue(String.class));
                desparasitacion.setPesoMascota(snapshot.child("pesoMascota").getValue(Float.class));
                desparasitacion.setCantidadDesparasitante(snapshot.child("cantidadDesparasitante").getValue(Float.class));

                callback.onComplete(desparasitacion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }

    public void guardarInformacionVacuna (HistorialMedico vacuna, Mascota mascota, LinearLayout barraProgreso, Context context) {
        vacuna.setId(UUID.randomUUID().toString());
        vacuna.setTipo(EstadosCuentas.VACUNA.toString());

        HashMap<String, Object> mapVacuna = new HashMap<>();
        mapVacuna.put("nombre", vacuna.getNombre());
        mapVacuna.put("fechaColocacion", vacuna.getFechaColocacion());
        mapVacuna.put("horaRecordatorio", vacuna.getHoraRecordatorio());
        mapVacuna.put("fechaProxima", vacuna.getFechaProxima());
        mapVacuna.put("tipo", vacuna.getTipo());


        databaseReference.child(mascota.getId()).child(vacuna.getId()).setValue(mapVacuna)
                .addOnSuccessListener(command -> {
                    Intent i = new Intent(context, VerMiMascotaActivity.class);
                    i.putExtra("idMascota", mascota.getId());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                    Toast.makeText(context, "Información de vacuna guardada correctamente", Toast.LENGTH_SHORT).show();
                    barraProgreso.setVisibility(View.GONE);

                    // PRUEBA
                    controladorNotificaciones.enviarNotificacionVacuna(vacuna, mascota, vacuna.getHoraRecordatorio());

                }).addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al guardar la información de la vacuna");
                });
    }

    public void editarVacuna (HistorialMedico vacuna, Mascota mascota, String fechaOriginal, String horaOriginal, Context context, LinearLayout barra) {
        Map<String, Object> mapActualizacionesVacuna = new HashMap<>();
        mapActualizacionesVacuna.put("nombre", vacuna.getNombre());
        mapActualizacionesVacuna.put("fechaColocacion", vacuna.getFechaColocacion());
        mapActualizacionesVacuna.put("fechaProxima", vacuna.getFechaProxima());
        mapActualizacionesVacuna.put("horaRecordatorio", vacuna.getHoraRecordatorio());
        mapActualizacionesVacuna.put("tipo", vacuna.getTipo());

        databaseReference.child(mascota.getId()).child(vacuna.getId()).updateChildren(mapActualizacionesVacuna).addOnSuccessListener(command -> {
            if (!fechaOriginal.equalsIgnoreCase(vacuna.getFechaColocacion()) || !horaOriginal.equalsIgnoreCase(vacuna.getHoraRecordatorio())) {
                controladorNotificaciones.modificarNotificacionesHistorial(vacuna, mascota);
            }
            Intent i = new Intent(context, VerMiMascotaActivity.class);
            i.putExtra("idMascota", mascota.getId());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            barra.setVisibility(View.GONE);
            Toast.makeText(context, "Se ha actualizado la información correctamente.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.e("FIREBASE-REALTIME", "No fue posible actualizar la información de la vacuna.");
        });
    }

    public void eliminarHistorial(String idHistorial, String idMascota, Callback<Boolean> callback) {
        databaseReference.child(idMascota).child(idHistorial).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("REALTIME", "Vacuna eliminada exitosamente");
                        callback.onComplete(true);
                        controladorNotificaciones.eliminarNotificacionesHistorial(idHistorial, idMascota);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Log.e("REALTIME", "Error al eliminar la vacuna");
                    }
                });
    }

    public void obtenerNumeroVacunas(String idMascota, CallbackCantidad callback) {

        databaseReference.child(idMascota)
                .orderByChild("tipo")
                .equalTo(EstadosCuentas.VACUNA.toString())  // Filtrar solo vacunas
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int cantidadVacunas = 0;

                        for (DataSnapshot data : snapshot.getChildren()) {
                            cantidadVacunas++;
                        }

                        callback.onResultado(cantidadVacunas);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    public void obtenerListaVacunas(String idMascota, Callback<List<HistorialMedico>> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("historial_medico").child(idMascota);
        ArrayList<HistorialMedico> listaVacunas = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    HistorialMedico vacuna = new HistorialMedico();
                    vacuna.setId(data.getKey());
                    vacuna.setNombre(data.child("nombre").getValue(String.class));
                    vacuna.setFechaColocacion(data.child("fechaColocacion").getValue(String.class));
                    vacuna.setFechaProxima(data.child("fechaProxima").getValue(String.class));
                    vacuna.setTipo(data.child("tipo").getValue(String.class));

                    if (vacuna.getTipo().equalsIgnoreCase(EstadosCuentas.VACUNA.toString())) {
                        listaVacunas.add(vacuna);
                    }
                }
                callback.onComplete(listaVacunas);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    public void obtenerListaDesparasitaciones(String idMascota, Callback<List<Desparasitacion>> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("historial_medico").child(idMascota);
        ArrayList<Desparasitacion> listaDesparasitaciones = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    Desparasitacion desparasitacion = new Desparasitacion();
                    desparasitacion.setId(data.getKey());
                    desparasitacion.setNombre(data.child("nombre").getValue(String.class));
                    desparasitacion.setFechaColocacion(data.child("fechaColocacion").getValue(String.class));
                    desparasitacion.setFechaProxima(data.child("fechaProxima").getValue(String.class));
                    desparasitacion.setTipo(data.child("tipo").getValue(String.class));
                    if (data.child("pesoMascota").getValue(Float.class) != null) {
                        desparasitacion.setPesoMascota(data.child("pesoMascota").getValue(Float.class));
                    }
                    if (data.child("cantidadDesparasitante").getValue(Float.class) != null) {
                        desparasitacion.setCantidadDesparasitante(data.child("cantidadDesparasitante").getValue(Float.class));
                    }

                    if (desparasitacion.getTipo().equalsIgnoreCase(EstadosCuentas.DESPARASITACION.toString())) {
                        listaDesparasitaciones.add(desparasitacion);
                    }
                }
                callback.onComplete(listaDesparasitaciones);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    public void guardarInformacionDesparasitacion (Desparasitacion desparasitacion, Mascota mascota, LinearLayout barraProgreso, Context context) {
        desparasitacion.setId(UUID.randomUUID().toString());
        desparasitacion.setTipo(EstadosCuentas.DESPARASITACION.toString());

        HashMap<String, Object> mapDesparasitacion = new HashMap<>();
        mapDesparasitacion.put("nombre", desparasitacion.getNombre());
        mapDesparasitacion.put("fechaColocacion", desparasitacion.getFechaColocacion());
        mapDesparasitacion.put("horaRecordatorio", desparasitacion.getHoraRecordatorio());
        mapDesparasitacion.put("fechaProxima", desparasitacion.getFechaProxima());
        mapDesparasitacion.put("tipo", desparasitacion.getTipo());
        mapDesparasitacion.put("pesoMascota", desparasitacion.getPesoMascota());
        mapDesparasitacion.put("cantidadDesparasitante", desparasitacion.getCantidadDesparasitante());


        databaseReference.child(mascota.getId()).child(desparasitacion.getId()).setValue(mapDesparasitacion)
                .addOnSuccessListener(command -> {
                    Intent i = new Intent(context, VerMiMascotaActivity.class);
                    i.putExtra("idMascota", mascota.getId());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                    Toast.makeText(context, "Información de desparasitación guardada correctamente", Toast.LENGTH_SHORT).show();
                    barraProgreso.setVisibility(View.GONE);

                    controladorNotificaciones.enviarNotificacionDesparasitacion(desparasitacion, mascota, desparasitacion.getHoraRecordatorio());

                }).addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al guardar la información de la vacuna");
                });
    }

    public void editarDesparasitacion (Desparasitacion desparasitacion, Mascota mascota, String fechaOriginal, String horaOriginal, Context context, LinearLayout barra) {
        Map<String, Object> mapActualizacionesVacuna = new HashMap<>();
        mapActualizacionesVacuna.put("nombre", desparasitacion.getNombre());
        mapActualizacionesVacuna.put("fechaColocacion", desparasitacion.getFechaColocacion());
        mapActualizacionesVacuna.put("fechaProxima", desparasitacion.getFechaProxima());
        mapActualizacionesVacuna.put("horaRecordatorio", desparasitacion.getHoraRecordatorio());
        mapActualizacionesVacuna.put("tipo", desparasitacion.getTipo());
        mapActualizacionesVacuna.put("pesoMascota", desparasitacion.getPesoMascota());
        mapActualizacionesVacuna.put("cantidadDesparasitante", desparasitacion.getCantidadDesparasitante());

        databaseReference.child(mascota.getId()).child(desparasitacion.getId()).updateChildren(mapActualizacionesVacuna).addOnSuccessListener(command -> {
            if (!fechaOriginal.equalsIgnoreCase(desparasitacion.getFechaColocacion()) || !horaOriginal.equalsIgnoreCase(desparasitacion.getHoraRecordatorio())) {
                controladorNotificaciones.modificarNotificacionesHistorial(desparasitacion, mascota);
            }
            Intent i = new Intent(context, VerMiMascotaActivity.class);
            i.putExtra("idMascota", mascota.getId());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            barra.setVisibility(View.GONE);
            Toast.makeText(context, "Se ha actualizado la información correctamente.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.e("FIREBASE-REALTIME", "No fue posible actualizar la información de la vacuna.");
        });
    }

    public void obtenerFechaUltimaVacuna(String idMascota, TextView textView) {
        databaseReference.child(idMascota)
                .orderByChild("fechaColocacion")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fechaMasCercana = null;

                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (Objects.requireNonNull(data.child("tipo").getValue(String.class))
                                        .equalsIgnoreCase(EstadosCuentas.VACUNA.toString())) {
                                    String fechaActual = data.child("fechaColocacion").getValue(String.class);

                                    if (fechaMasCercana == null || Objects.requireNonNull(fechaActual).compareTo(fechaMasCercana) > 0) {
                                        fechaMasCercana = fechaActual;
                                    }
                                }
                            }
                        }

                        if (fechaMasCercana != null) {
                            textView.setText(fechaMasCercana);
                        } else {
                            textView.setText("N/A");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE", error.getMessage());
                    }
                });
    }

    public void obtenerFechaUltimaDesparasitacion(String idMascota, TextView textView) {
        databaseReference.child(idMascota)
                .orderByChild("fechaColocacion")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fechaMasCercana = null;

                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (Objects.requireNonNull(data.child("tipo").getValue(String.class))
                                        .equalsIgnoreCase(EstadosCuentas.DESPARASITACION.toString())) {
                                    String fechaActual = data.child("fechaColocacion").getValue(String.class);

                                    if (fechaMasCercana == null || Objects.requireNonNull(fechaActual).compareTo(fechaMasCercana) > 0) {
                                        fechaMasCercana = fechaActual;
                                    }
                                }
                            }
                        }

                        if (fechaMasCercana != null) {
                            textView.setText(fechaMasCercana);
                        } else {
                            textView.setText("N/A");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE", error.getMessage());
                    }
                });
    }


    // Callback genérico
    public interface Callback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }
    public interface CallbackCantidad {
        void onResultado(int cantidad);
        void onError(Exception e);
    }

}
