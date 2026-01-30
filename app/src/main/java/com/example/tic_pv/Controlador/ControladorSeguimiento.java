package com.example.tic_pv.Controlador;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.Modelo.Mensaje;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.BottomNavigationMenu;
import com.example.tic_pv.Vista.EditarDomicilioActivity;
import com.example.tic_pv.Vista.VerInformacionPerfilActivity;
import com.example.tic_pv.Vista.VerMascotasActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControladorSeguimiento {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference("FotosMascotas");
    private EstadosCuentas estadoMascota;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorNotificaciones controladorNotificaciones = new ControladorNotificaciones();
    private EstadosCuentas estadoObj;

    private void agregarSeguimientoFirebase(Seguimiento seguimiento) {
        Map<String, Object> mapSeguimiento = new HashMap<>();
        mapSeguimiento.put("estado", EstadosCuentas.ACTIVO.toString());
        mapSeguimiento.put("idAdoptante", seguimiento.getIdAdoptante());
        mapSeguimiento.put("nombreAdoptante", seguimiento.getNombreAdoptante());
        mapSeguimiento.put("idMascota", seguimiento.getIdMascota());
        mapSeguimiento.put("nombreMascota", seguimiento.getNombreMascota());
        mapSeguimiento.put("idVoluntario", "");
        mapSeguimiento.put("nombreVoluntario", "");
        mapSeguimiento.put("listaMensajes", seguimiento.getListaMensajes());

        db.collection("Seguimientos").add(mapSeguimiento)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FIREBASE", "Seguimiento creado correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al crear el seguimiento");
                });
    }

    public void crearSeguimiento (String idCuenta, String idMascota) {
        Seguimiento seguimiento = new Seguimiento();
        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    seguimiento.setIdMascota(idMascota);
                    seguimiento.setNombreMascota(document.getString("nombre"));
                    seguimiento.setListaMensajes(UUID.randomUUID().toString());

                    db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            QuerySnapshot querySnapshot = task1.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot documento : querySnapshot) {
                                    seguimiento.setIdAdoptante(idCuenta);
                                    seguimiento.setNombreAdoptante(documento.getString("nombre"));

                                    agregarSeguimientoFirebase(seguimiento);
                                }
                            } else {
                                Log.e("FIREBASE", "No existe el documento");
                            }
                        } else {
                            Log.e("FIREBASE", "No se pudo completar la tarea");
                        }
                    });

                }
            }
        });
    }

    public interface Callback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public interface CallbackSeguimientosVol<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public void obtenerSeguimientosDisponibles(Callback<ArrayList<Seguimiento>> seguimientosCallback) {
        db.collection("Seguimientos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Seguimiento> listaSeguimientos = new ArrayList<>();
                    Seguimiento seguimiento;
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        seguimiento = new Seguimiento();
                        seguimiento.setId(documentSnapshot.getId());
                        seguimiento.setEstado(documentSnapshot.getString("estado"));
                        seguimiento.setIdAdoptante(documentSnapshot.getString("idAdoptante"));
                        seguimiento.setNombreAdoptante(documentSnapshot.getString("nombreAdoptante"));
                        seguimiento.setIdMascota(documentSnapshot.getString("idMascota"));
                        seguimiento.setNombreMascota(documentSnapshot.getString("nombreMascota"));
                        seguimiento.setIdVoluntario(documentSnapshot.getString("idVoluntario"));
                        seguimiento.setNombreVoluntario(documentSnapshot.getString("nombreVoluntario"));
                        seguimiento.setListaMensajes(documentSnapshot.getString("listaMensajes"));

                        if (seguimiento.getIdVoluntario().equalsIgnoreCase("") &&
                        seguimiento.getEstado().equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            listaSeguimientos.add(seguimiento);
                        }
                    }
                    seguimientosCallback.onComplete(listaSeguimientos);
                } else {
                    Log.e("FIREBASE", "Error al obtener voluntarios");
                }
            }
        });
    }

    public void obtenerSeguimientosVoluntario(String idVoluntario, CallbackSeguimientosVol<ArrayList<Seguimiento>> seguimientosCallback) {
        db.collection("Seguimientos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Seguimiento> listaSeguimientos = new ArrayList<>();
                    Seguimiento seguimiento;
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        seguimiento = new Seguimiento();
                        seguimiento.setId(documentSnapshot.getId());
                        seguimiento.setEstado(documentSnapshot.getString("estado"));
                        seguimiento.setIdAdoptante(documentSnapshot.getString("idAdoptante"));
                        seguimiento.setNombreAdoptante(documentSnapshot.getString("nombreAdoptante"));
                        seguimiento.setIdMascota(documentSnapshot.getString("idMascota"));
                        seguimiento.setNombreMascota(documentSnapshot.getString("nombreMascota"));
                        seguimiento.setIdVoluntario(documentSnapshot.getString("idVoluntario"));
                        seguimiento.setNombreVoluntario(documentSnapshot.getString("nombreVoluntario"));
                        seguimiento.setListaMensajes(documentSnapshot.getString("listaMensajes"));

                        if (seguimiento.getIdVoluntario().equalsIgnoreCase(idVoluntario) &&
                                seguimiento.getEstado().equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            listaSeguimientos.add(seguimiento);
                        }
                    }
                    seguimientosCallback.onComplete(listaSeguimientos);
                } else {
                    Log.e("FIREBASE", "Error al obtener voluntarios");
                }
            }
        });
    }

    public void asignarSeguimientoVoluntario(Context context, String idSeguimiento, String idVoluntario, String nombreVoluntario, Callback<ArrayList<Seguimiento>> nuevaLista) {

        db.collection("Seguimientos").document(idSeguimiento).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idVoluntarioActual = documentSnapshot.getString("idVoluntario");

                        // Validamos que esté vacío o null
                        if (idVoluntarioActual == null || idVoluntarioActual.isEmpty()) {

                            Seguimiento seguimiento = new Seguimiento();
                            seguimiento.setId(documentSnapshot.getId());
                            seguimiento.setIdVoluntario(idVoluntario);
                            seguimiento.setNombreVoluntario(nombreVoluntario);

                            Map<String, Object> mapSeguimiento = new HashMap<>();
                            mapSeguimiento.put("idVoluntario", seguimiento.getIdVoluntario());
                            mapSeguimiento.put("nombreVoluntario", seguimiento.getNombreVoluntario());

                            db.collection("Seguimientos").document(idSeguimiento).update(mapSeguimiento)
                                    .addOnSuccessListener(unused -> {
                                            Toast.makeText(context, "Seguimiento asignado correctamente.", Toast.LENGTH_SHORT).show();
                                            obtenerSeguimientosDisponibles(new Callback<ArrayList<Seguimiento>>() {
                                                @Override
                                                public void onComplete(ArrayList<Seguimiento> result) {
                                                    nuevaLista.onComplete(result);

                                                    controladorNotificaciones.enviarNotificacionAsignacionSeguimiento(seguimiento);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Log.e("ERROR", "Error al actualizar los seguimientos.");
                                                }
                                            });
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Error al asignar el seguimiento.", Toast.LENGTH_SHORT).show());
                        } else {
                            // Ya tiene voluntario asignado
                            Toast.makeText(context, "Este seguimiento ya tiene voluntario asignado.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FIRESTORE", "Seguimiento no encontrado.");
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE", "Error al obtener el seguimiento"));
    }

    public void reasignarSeguimientoVoluntario(Context context, String idVoluntarioOriginal,
                                               String idSeguimiento, String idVoluntario,
                                               String nombreVoluntario,
                                               CallbackSeguimientosVol<ArrayList<Seguimiento>> nuevaLista) {

        db.collection("Seguimientos").document(idSeguimiento).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Seguimiento seguimiento = new Seguimiento();
                        seguimiento.setId(documentSnapshot.getId());
                        seguimiento.setIdVoluntario(idVoluntario);
                        seguimiento.setNombreVoluntario(nombreVoluntario);

                        Map<String, Object> mapSeguimiento = new HashMap<>();
                        mapSeguimiento.put("idVoluntario", seguimiento.getIdVoluntario());
                        mapSeguimiento.put("nombreVoluntario", seguimiento.getNombreVoluntario());

                        db.collection("Seguimientos").document(idSeguimiento).update(mapSeguimiento)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Seguimiento reasignado correctamente.", Toast.LENGTH_SHORT).show();
                                    obtenerSeguimientosVoluntario(idVoluntarioOriginal, new CallbackSeguimientosVol<ArrayList<Seguimiento>>() {
                                        @Override
                                        public void onComplete(ArrayList<Seguimiento> result) {
                                            nuevaLista.onComplete(result);

                                            controladorNotificaciones.enviarNotificacionAsignacionSeguimiento(seguimiento);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Error al asignar el seguimiento.", Toast.LENGTH_SHORT).show());

                    } else {
                        Log.e("FIRESTORE", "Seguimiento no encontrado.");
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE", "Error al obtener el seguimiento"));
    }

    public void obtenerOManejarMensajes(String idChat, Callback<ArrayList<Mensaje>> callback) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(idChat)
                .child("mensajes");

        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Si el nodo no existe, lo creamos
                    messagesRef.setValue(new ArrayList<>()); // Crear el nodo vacío
                    callback.onComplete(new ArrayList<>()); // Retornamos una lista vacía
                } else {
                    // Si el nodo existe, obtenemos los mensajes
                    ArrayList<Mensaje> mensajes = new ArrayList<>();
                    for (DataSnapshot hijo : dataSnapshot.getChildren()) {
                        Mensaje mensaje = hijo.getValue(Mensaje.class);
                        if (mensaje != null) {
                            mensajes.add(mensaje);
                        }
                    }
                    callback.onComplete(mensajes); // Retornamos la lista completa
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // En caso de error al leer de Firebase
                callback.onError(databaseError.toException());
            }
        });
    }


}
