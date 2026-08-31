package com.example.tic_pv.Controlador;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
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
import java.util.Objects;
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

    public void crearSeguimiento(String idCuenta, String idMascota) {
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

    // Retira la mascota del adoptante: vuelve al catálogo de adopción, sale del perfil
    // del adoptante y el seguimiento se cierra.
    public void retirarMascotaAdoptante(Seguimiento seguimiento, String motivo, Callback<Void> callback) {
        if (seguimiento.getIdMascota() == null || seguimiento.getIdMascota().isEmpty()) {
            callback.onError(new IllegalStateException("El seguimiento no tiene mascota asociada"));
            return;
        }

        // 1. La mascota vuelve al catálogo. Es el mismo campo que filtra CatalogoMascotas
        //    y obtenerListaMisMascotas, así que con esto sale del perfil del adoptante.
        db.collection("Mascotas").document(seguimiento.getIdMascota())
                .update("mascotaAdoptada", false)
                .addOnSuccessListener(unused -> {

                    // 2. Los recordatorios ya programados apuntan al ex adoptante. Se cancelan
                    //    los suyos; los del voluntario se conservan, porque la mascota vuelve a
                    //    estar bajo su cuidado.
                    controladorNotificaciones.eliminarNotificacionesVacunaAdoptante(
                            seguimiento.getIdMascota(), seguimiento.getIdAdoptante());

                    // 3. El seguimiento se cierra dejando registrado el motivo
                    Map<String, Object> mapCierre = new HashMap<>();
                    mapCierre.put("estado", EstadosCuentas.RECHAZADA.toString());
                    mapCierre.put("motivoRetiro", motivo);

                    db.collection("Seguimientos").document(seguimiento.getId())
                            .update(mapCierre)
                            .addOnSuccessListener(unused1 -> {

                                // 4. Se avisa al adoptante, que de otro modo vería desaparecer
                                //    la mascota de su perfil sin ninguna explicación
                                controladorNotificaciones.enviarNotificacionMascotaRetirada(seguimiento, motivo);

                                // 5. Se cierra también la adopción completada. Si se dejara en
                                //    COMPLETADA y la mascota se adoptara de nuevo, volvería a
                                //    aparecer en "Mis mascotas" del adoptante anterior.
                                cerrarAdopcionCompletada(seguimiento, callback);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FIREBASE", "Error al cerrar el seguimiento");
                                callback.onError(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al devolver la mascota al catálogo");
                    callback.onError(e);
                });
    }

    private void cerrarAdopcionCompletada(Seguimiento seguimiento, Callback<Void> callback) {
        db.collection("Adopciones")
                .whereEqualTo("adoptante", seguimiento.getIdAdoptante())
                .whereEqualTo("mascotaAdopcion", seguimiento.getIdMascota())
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot documento : querySnapshot.getDocuments()) {
                        documento.getReference().update("estado", EstadosCuentas.RECHAZADA.toString());

                        // El contrato firmado acredita una adopción que ya no existe, así que
                        // se anula. Si no, quedarían dos contratos firmados para la misma
                        // mascota cuando otra persona la adopte.
                        anularContratoAdopcion(documento.getString("contratoAdopcion"));
                    }
                    callback.onComplete(null);
                })
                .addOnFailureListener(e -> {
                    // La mascota ya volvió al catálogo, que es lo esencial de la operación
                    Log.e("FIREBASE", "Error al cerrar la adopción completada");
                    callback.onComplete(null);
                });
    }

    // Marca el contrato como anulado. No se borra: es el respaldo documental de que la
    // adopción existió.
    private void anularContratoAdopcion(String idContrato) {
        if (idContrato == null || idContrato.isEmpty()) {
            Log.e("FIREBASE", "La adopción no tiene un contrato asociado que anular");
            return;
        }

        db.collection("ContratosAdopciones").document(idContrato)
                .update("estado", EstadosCuentas.ANULADO.toString())
                .addOnFailureListener(e ->
                        Log.e("FIREBASE", "Error al anular el contrato de adopción"));
    }

    // Marca que el usuario está viendo el chat, para no notificarle los mensajes que
    // ya está leyendo. Se llama desde onResume de las Activities de chat.
    public void marcarPresenciaChat(String listaMensajes, String idUsuario) {
        DatabaseReference presenciaRef = FirebaseDatabase.getInstance()
                .getReference("presenciaChats")
                .child(listaMensajes)
                .child(idUsuario);

        // Si la app se cierra sin pasar por onPause, Firebase limpia la marca por su cuenta
        presenciaRef.onDisconnect().removeValue();
        presenciaRef.setValue(true);
    }

    // Se llama desde onPause de las Activities de chat
    public void quitarPresenciaChat(String listaMensajes, String idUsuario) {
        FirebaseDatabase.getInstance()
                .getReference("presenciaChats")
                .child(listaMensajes)
                .child(idUsuario)
                .removeValue();
    }

    // Obtiene de una sola vez las fotos de perfil de los dos participantes del chat,
    // para que el adaptador no tenga que consultarlas mensaje por mensaje
    public void obtenerFotosPerfilChat(Seguimiento seguimiento, Callback<HashMap<String, String>> fotosCallback) {
        HashMap<String, String> fotosPorUsuario = new HashMap<>();

        db.collection("Cuentas").document(seguimiento.getIdVoluntario()).get().addOnCompleteListener(taskVoluntario -> {
            if (taskVoluntario.isSuccessful()) {
                DocumentSnapshot docVoluntario = taskVoluntario.getResult();
                if (docVoluntario.exists()) {
                    fotosPorUsuario.put(seguimiento.getIdVoluntario(), docVoluntario.getString("fotoPerfil"));
                }
            }

            db.collection("Cuentas").document(seguimiento.getIdAdoptante()).get().addOnCompleteListener(taskAdoptante -> {
                if (taskAdoptante.isSuccessful()) {
                    DocumentSnapshot docAdoptante = taskAdoptante.getResult();
                    if (docAdoptante.exists()) {
                        fotosPorUsuario.put(seguimiento.getIdAdoptante(), docAdoptante.getString("fotoPerfil"));
                    }
                }
                fotosCallback.onComplete(fotosPorUsuario);
            });
        });
    }

    public void obtenerSeguimientosAdoptante(String idAdoptante, CallbackSeguimientosVol<ArrayList<Seguimiento>> seguimientosCallback) {
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

                        // Solo se listan los seguimientos del adoptante que ya tienen un voluntario
                        // asignado, porque sin voluntario no hay con quién conversar
                        if (seguimiento.getIdAdoptante().equalsIgnoreCase(idAdoptante) &&
                                !seguimiento.getIdVoluntario().isEmpty() &&
                                seguimiento.getEstado().equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            listaSeguimientos.add(seguimiento);
                        }
                    }
                    seguimientosCallback.onComplete(listaSeguimientos);
                } else {
                    Log.e("FIREBASE", "Error al obtener los seguimientos del adoptante");
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

        if (idVoluntarioOriginal.equalsIgnoreCase(idVoluntario)) {
            Toast.makeText(context, "El seguimiento ya se encuentra asignado a este voluntario.", Toast.LENGTH_SHORT).show();
        } else {
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

    public void subirFotoChat (Uri foto,
                               Seguimiento seguimiento,
                               Dialog dialog,
                               DatabaseReference databaseReference,
                               LinearLayout enviarMultimedia,
                               ProgressBar barraProgreso,
                               boolean enviaVoluntario,
                               Mensaje mensajeRespondido) {

        MediaManager.get().upload(foto)
                .option("resource_type", "image") // Asegura que Cloudinary lo reconozca como imagen
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("CLOUDINARY", "Subiendo imagen...");
                        enviarMultimedia.setEnabled(false);
                        barraProgreso.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String urlFoto = Objects.requireNonNull(resultData.get("secure_url")).toString();
                        Log.d("CLOUDINARY", "Imagen subida correctamente: " + urlFoto);
                        enviarMensaje(urlFoto, databaseReference, seguimiento, enviaVoluntario, mensajeRespondido);
                        dialog.dismiss();
                        enviarMultimedia.setEnabled(true);
                        barraProgreso.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("CLOUDINARY", "Error al subir imagen: " + error.getDescription());
                        barraProgreso.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    public void subirVideoChat(Uri video,
                               Seguimiento seguimiento,
                               Dialog dialog,
                               DatabaseReference databaseReference,
                               LinearLayout enviarMultimedia,
                               ProgressBar barraProgreso,
                               boolean enviaVoluntario,
                               Mensaje mensajeRespondido) {
        MediaManager.get().upload(video)
                .option("resource_type", "video")
                .option("transformation", new Transformation<>()
                        .quality("auto:low")
                        .fetchFormat("mp4")
                )
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("CLOUDINARY", "Subiendo video...");
                        enviarMultimedia.setEnabled(false);
                        barraProgreso.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {

                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String urlVideo = Objects.requireNonNull(resultData.get("secure_url")).toString();
                        Log.d("CLOUDINARY", "Video subido correctamente: " + urlVideo);
                        enviarMensaje(urlVideo, databaseReference, seguimiento, enviaVoluntario, mensajeRespondido);
                        dialog.dismiss();
                        enviarMultimedia.setEnabled(true);
                        barraProgreso.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("CLOUDINARY", "Error al subir el video: " + error.getDescription());
                        barraProgreso.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                }).dispatch();

    }

    private void enviarMensaje(String urlMultimedia, DatabaseReference mensajesRef, Seguimiento seguimiento, boolean enviaVoluntario, Mensaje mensajeRespondido) {
        if (!urlMultimedia.isEmpty()) {
            // Generate a unique Firebase key for the message
            DatabaseReference mensajeRef = mensajesRef.push(); // Generate a node with a unique ID
            String mensajeId = mensajeRef.getKey(); // Get the key (ID)

            // El emisor depende del lado desde el que se envía la multimedia
            String nombreEmisor = enviaVoluntario ? seguimiento.getNombreVoluntario() : seguimiento.getNombreAdoptante();
            String idEmisor = enviaVoluntario ? seguimiento.getIdVoluntario() : seguimiento.getIdAdoptante();
            String nombreReceptor = enviaVoluntario ? seguimiento.getNombreAdoptante() : seguimiento.getNombreVoluntario();
            String idReceptor = enviaVoluntario ? seguimiento.getIdAdoptante() : seguimiento.getIdVoluntario();

            // Create the new message object and assign the Firebase key as ID
            Mensaje nuevoMensaje = new Mensaje(
                    mensajeId,  // Use the key generated by Firebase as the ID
                    nombreEmisor,
                    idEmisor,
                    urlMultimedia,
                    nombreReceptor,
                    idReceptor,
                    System.currentTimeMillis()
            );

            // Si la multimedia se envía respondiendo a otro mensaje, se conserva la cita
            if (mensajeRespondido != null) {
                nuevoMensaje.setIdMensajeRespondido(mensajeRespondido.getId());
                nuevoMensaje.setEmisorRespondido(mensajeRespondido.getEmisor());
                nuevoMensaje.setContenidoRespondido(mensajeRespondido.getContenido());
            }

            // Save the message in Firebase using the generated key
            mensajeRef.setValue(nuevoMensaje).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FIREBASE", "Mensaje enviado correctamente");
                    controladorNotificaciones.enviarNotificacionMensajeChat(seguimiento, enviaVoluntario, urlMultimedia);
                } else {
                    Log.e("FIREBASE", "Error al enviar el mensaje");
                }
            });
        }
    }


}
