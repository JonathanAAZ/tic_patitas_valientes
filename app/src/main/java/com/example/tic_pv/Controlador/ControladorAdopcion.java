package com.example.tic_pv.Controlador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.ContratoAdopcion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Vista.CatalogoMascotasActivity;
import com.example.tic_pv.Vista.GestionarSolicitudesAdopcionActivity;
import com.example.tic_pv.Vista.ResponderSolicitudAdopcionActivity;
import com.example.tic_pv.Vista.VerMascotasActivity;
import com.example.tic_pv.Vista.VerSolicitudesAdopcionActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.kernel.geom.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ControladorAdopcion {

    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference("FotosFirmas");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ControladorNotificaciones controladorNotificaciones = new ControladorNotificaciones();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();
    public void iniciarProcesoAdopcion(List<Uri> listaUrisFotos, Uri videoHogarMascota, Adopcion adopcion, LinearLayout barraProgreso, Context context) {
        barraProgreso.setVisibility(View.VISIBLE);

        if (listaUrisFotos == null || listaUrisFotos.size() < 3 || videoHogarMascota == null) {
//            Toast.makeText(context, "Debe subir las 3 imágenes y el video", Toast.LENGTH_SHORT).show();
            barraProgreso.setVisibility(View.GONE);
            return;
        }

        AtomicInteger archivosSubidos = new AtomicInteger(0);
        int totalArchivos = 4; // 3 imágenes + 1 video

        // Subir imágenes a Cloudinary
        for (int i = 0; i < listaUrisFotos.size(); i++) {
            Uri uriFoto = listaUrisFotos.get(i);
            if (uriFoto != null) {
                int finalContador = i;

                MediaManager.get().upload(uriFoto)
                        .option("resource_type", "image") // Asegura que Cloudinary lo reconozca como imagen
                        .callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {
                                Log.d("CLOUDINARY", "Subiendo imagen...");
                            }

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {}

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                String urlDescarga = (String) resultData.get("secure_url");

                                // Asignar la URL al atributo correspondiente
                                switch (finalContador) {
                                    case 0:
                                        adopcion.setFotoCedulaFrontal(urlDescarga);
                                        break;
                                    case 1:
                                        adopcion.setFotoCedulaPosterior(urlDescarga);
                                        break;
                                    case 2:
                                        adopcion.setFotoServiciosBasicos(urlDescarga);
                                        break;
                                }

                                verificarFinalizacion(archivosSubidos, totalArchivos, adopcion, barraProgreso, context);
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
        }

        // Subir video a Cloudinary
        MediaManager.get().upload(videoHogarMascota)
                .option("resource_type", "video") // Asegura que Cloudinary lo reconozca como video
                .option("transformation", new Transformation<>()
                        .quality("auto:low") // Reduce calidad automáticamente
                        .fetchFormat("mp4") // Asegura formato MP4
                )
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("CLOUDINARY", "Subiendo video...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String urlDescarga = (String) resultData.get("secure_url");
                        adopcion.setVideoHogarMascota(urlDescarga);
                        verificarFinalizacion(archivosSubidos, totalArchivos, adopcion, barraProgreso, context);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("CLOUDINARY", "Error al subir video: " + error.getDescription());
                        barraProgreso.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }


    // Método para verificar si todas las imágenes y el video han sido subidos
    private void verificarFinalizacion(AtomicInteger archivosSubidos, int totalArchivos, Adopcion adopcion, LinearLayout barraProgreso, Context context) {
        if (archivosSubidos.incrementAndGet() == totalArchivos) {
            guardarInicioAdopcionFirebase(adopcion, barraProgreso, context); // Guardar adopción en Firebase
        }
    }

    private void guardarInicioAdopcionFirebase(Adopcion inicioAdopcion, LinearLayout barra, Context context) {
        Map <String, Object> mapAdopciones = new HashMap<>();
        mapAdopciones.put("estado", EstadosCuentas.PENDIENTE.toString());
        mapAdopciones.put("fechaEmision", inicioAdopcion.getFechaEmision());
        mapAdopciones.put("fotoCedulaFrontal", inicioAdopcion.getFotoCedulaFrontal());
        mapAdopciones.put("fotoCedulaPosterior", inicioAdopcion.getFotoCedulaPosterior());
        mapAdopciones.put("fotoServiciosBasicos", inicioAdopcion.getFotoServiciosBasicos());
        mapAdopciones.put("videoHogarMascota", inicioAdopcion.getVideoHogarMascota());
        mapAdopciones.put("cerramiento", inicioAdopcion.isTieneCerramiento());
        mapAdopciones.put("tipoDomicilio", inicioAdopcion.getTipoDomicilio());
        mapAdopciones.put("videoCompromiso", "");
        mapAdopciones.put("contratoAdopcion", "");
        mapAdopciones.put("seguimientoAdopcion", "");
        mapAdopciones.put("adoptante", inicioAdopcion.getAdoptante());
        mapAdopciones.put("mascotaAdopcion", inicioAdopcion.getMascotaAdopcion());
        mapAdopciones.put("observaciones", "");

        db.collection("Adopciones").add(mapAdopciones)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Solicitud de adopción enviada", Toast.LENGTH_SHORT).show();
                    barra.setVisibility(View.GONE);
                    Intent intent = new Intent(context, CatalogoMascotasActivity.class); // Especifica la nueva actividad
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    controladorNotificaciones.enviarNotificacionSolicitudAdopcion(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al enviar la solicitud", Toast.LENGTH_SHORT).show();
                });
    }

    public void obtenerListaSolicitudesPendientes(Context context, Callback<List<Adopcion>> callback) {
        db.collection("Adopciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Adopcion> solicitudesPendientes = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Adopcion solicitud = new Adopcion();
                        solicitud.setId(documentSnapshot.getId());
                        solicitud.setAdoptante(documentSnapshot.getString("adoptante"));
                        solicitud.setMascotaAdopcion(documentSnapshot.getString("mascotaAdopcion"));
                        solicitud.setContratoAdopcion(documentSnapshot.getString("contratoAdopcion"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoCompromiso"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoHogarMascota"));
                        solicitud.setEstadoAdopcion(documentSnapshot.getString("estado"));
                        solicitud.setFechaEmision(documentSnapshot.getString("fechaEmision"));
                        solicitud.setObservaciones(documentSnapshot.getString("observaciones"));

                        if (Objects.requireNonNull(documentSnapshot.getString("estado")).equalsIgnoreCase(EstadosCuentas.PENDIENTE.toString())) {
                            solicitudesPendientes.add(solicitud);
                        }
                    }
                    callback.onComplete(solicitudesPendientes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "No se pudo obtener la lista de solicitudes pendientes", Toast.LENGTH_SHORT).show();
                    callback.onError(e);
                });
    }

    public void obtenerListaAdopcionesPorRevisar(Context context, Callback<List<Adopcion>> callback) {
        db.collection("Adopciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Adopcion> adopcionesPorRevisar = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Adopcion solicitud = new Adopcion();
                        solicitud.setId(documentSnapshot.getId());
                        solicitud.setAdoptante(documentSnapshot.getString("adoptante"));
                        solicitud.setMascotaAdopcion(documentSnapshot.getString("mascotaAdopcion"));
                        solicitud.setContratoAdopcion(documentSnapshot.getString("contratoAdopcion"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoCompromiso"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoHogarMascota"));
                        solicitud.setEstadoAdopcion(documentSnapshot.getString("estado"));
                        solicitud.setFechaEmision(documentSnapshot.getString("fechaEmision"));
                        solicitud.setObservaciones(documentSnapshot.getString("observaciones"));

                        if (solicitud.getEstadoAdopcion().equalsIgnoreCase(EstadosCuentas.EN_REVISION.toString())
                        || solicitud.getEstadoAdopcion().equalsIgnoreCase(EstadosCuentas.ACEPTADA.toString())) {
                            adopcionesPorRevisar.add(solicitud);
                        }
                    }
                    callback.onComplete(adopcionesPorRevisar);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "No se pudo obtener la lista de solicitudes pendientes", Toast.LENGTH_SHORT).show();
                    callback.onError(e);
                });
    }

    public void obtenerListaAdopcionesAceptadas(Context context, Callback<List<Adopcion>> callback) {
        db.collection("Adopciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Adopcion> adopcionesAceptadas = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Adopcion solicitud = new Adopcion();
                        solicitud.setId(documentSnapshot.getId());
                        solicitud.setAdoptante(documentSnapshot.getString("adoptante"));
                        solicitud.setMascotaAdopcion(documentSnapshot.getString("mascotaAdopcion"));
                        solicitud.setContratoAdopcion(documentSnapshot.getString("contratoAdopcion"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoCompromiso"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoHogarMascota"));
                        solicitud.setEstadoAdopcion(documentSnapshot.getString("estado"));
                        solicitud.setFechaEmision(documentSnapshot.getString("fechaEmision"));
                        solicitud.setObservaciones(documentSnapshot.getString("observaciones"));

                        String estado = Objects.requireNonNull(documentSnapshot.getString("estado"));

                        if (estado.equalsIgnoreCase(EstadosCuentas.RECHAZADA.toString())) {
                            adopcionesAceptadas.add(solicitud);
                        }
                    }
                    callback.onComplete(adopcionesAceptadas);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "No se pudo obtener la lista de solicitudes pendientes", Toast.LENGTH_SHORT).show();
                    callback.onError(e);
                });
    }

    public void obtenerListaSolicitudesEnviadas(String idCuenta, Callback<List<Adopcion>> callback) {
        db.collection("Adopciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Adopcion> solicitudesEnviadas = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Adopcion solicitud = new Adopcion();
                        solicitud.setId(documentSnapshot.getId());
                        solicitud.setAdoptante(documentSnapshot.getString("adoptante"));
                        solicitud.setContratoAdopcion(documentSnapshot.getString("contratoAdopcion"));
                        solicitud.setMascotaAdopcion(documentSnapshot.getString("mascotaAdopcion"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoCompromiso"));
                        solicitud.setVideoHogarMascota(documentSnapshot.getString("videoHogarMascota"));
                        solicitud.setEstadoAdopcion(documentSnapshot.getString("estado"));
                        solicitud.setFechaEmision(documentSnapshot.getString("fechaEmision"));
                        solicitud.setObservaciones(documentSnapshot.getString("observaciones"));

                        String idAdoptante = solicitud.getAdoptante();
                        if (idAdoptante.equals(idCuenta)) {
                            solicitudesEnviadas.add(solicitud);
                        }

                        Log.d("CONTRATOOOO DOS", "contrato" + solicitud.getObservaciones());
                    }
                    callback.onComplete(solicitudesEnviadas);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo obtener la lista de solicitudes pendientes" + e.getMessage());
//                    Toast.makeText(context, "No se pudo obtener la lista de solicitudes pendientes", Toast.LENGTH_SHORT).show();
                    callback.onError(e);
                });
    }

    // Callback genérico
    public interface Callback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public void obtenerSolicitudAdopcion (String idSolicitud, Callback<Adopcion> callback) {
        db.collection("Adopciones").document(idSolicitud).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Adopcion solicitudAdopcion = new Adopcion();
                solicitudAdopcion.setId(documentSnapshot.getId());
                solicitudAdopcion.setEstadoAdopcion(documentSnapshot.getString("estado"));
                solicitudAdopcion.setAdoptante(documentSnapshot.getString("adoptante"));
                solicitudAdopcion.setTieneCerramiento(Boolean.TRUE.equals(documentSnapshot.getBoolean("cerramiento")));
                solicitudAdopcion.setFechaEmision(documentSnapshot.getString("fechaEmision"));
                solicitudAdopcion.setFotoCedulaFrontal(documentSnapshot.getString("fotoCedulaFrontal"));
                solicitudAdopcion.setFotoCedulaPosterior(documentSnapshot.getString("fotoCedulaPosterior"));
                solicitudAdopcion.setFotoServiciosBasicos(documentSnapshot.getString("fotoServiciosBasicos"));
                solicitudAdopcion.setMascotaAdopcion(documentSnapshot.getString("mascotaAdopcion"));
                solicitudAdopcion.setTipoDomicilio(Objects.requireNonNull(documentSnapshot.getLong("tipoDomicilio")).intValue());
                solicitudAdopcion.setVideoHogarMascota(documentSnapshot.getString("videoHogarMascota"));
                solicitudAdopcion.setObservaciones(documentSnapshot.getString("observaciones"));

                callback.onComplete(solicitudAdopcion);
            }
        });
    }

    public void rechazarSolicitudAdopcion(Adopcion adopcion, LinearLayout barra, Context context) {
        barra.setVisibility(View.VISIBLE);
        Map<String, Object> mapRechazarSolicitud = new HashMap<>();
        mapRechazarSolicitud.put("estado", EstadosCuentas.RECHAZADA);
        mapRechazarSolicitud.put("observaciones", adopcion.getObservaciones());

        db.collection("Adopciones").document(adopcion.getId()).update(mapRechazarSolicitud).addOnSuccessListener(command -> {
            Log.d("FIRESTORE", "Contenido actualizado correctamente");
            barra.setVisibility(View.GONE);

            //Iniciar vista de solicitudes pendientes
            Intent i = new Intent(context, GestionarSolicitudesAdopcionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            controladorNotificaciones.enviarNotificacionSolicitudAceptadaRechazada(adopcion);
        }).addOnFailureListener(e -> {
            Log.e("FIRESTORE", "Error al actualizar contenido");
        });
    }

    public void actualizarEstadoAdopcion(Adopcion adopcionActualizar, Map<String, Object> map, LinearLayout barra, Context context){
        db.collection("Adopciones").document(adopcionActualizar.getId()).update(map).addOnSuccessListener(command -> {
            Log.d("FIRESTORE", "Contenido actualizado correctamente");
            barra.setVisibility(View.GONE);

            //Iniciar la vista de las solicitudes
            Intent i = new Intent(context, GestionarSolicitudesAdopcionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            controladorNotificaciones.enviarNotificacionSolicitudAceptadaRechazada(adopcionActualizar);
        }).addOnFailureListener(e -> {
            Log.e("FIRESTORE", "Error al actualizar contenido");
        });
    }

    public void completarAdopcion (Uri firmaAdoptante, Uri videoCompromiso, Adopcion adopcion,
                                   ContratoAdopcion contrato, LinearLayout barraProgreso, Context context) {
        barraProgreso.setVisibility(View.VISIBLE);

        String nombreArchivo = "firma_adoptante_" + System.currentTimeMillis() + ".jpg";
        StorageReference referenciaArchivo = storageReference.child(nombreArchivo);

        referenciaArchivo.putFile(firmaAdoptante)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de descarga de la foto
                    referenciaArchivo.getDownloadUrl().addOnSuccessListener(uri -> {
                        String urlFoto = uri.toString();
                        subirVideoCompromiso(videoCompromiso, barraProgreso, urlFoto, adopcion,
                                contrato, context);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al subir la foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

//        MediaManager.get().upload(firmaAdoptante)
//                .option("resource_type", "image") // Asegura que Cloudinary lo reconozca como imagen
//                .callback(new UploadCallback() {
//                    @Override
//                    public void onStart(String requestId) {
//                        Log.d("CLOUDINARY", "Subiendo imagen...");
//                    }
//
//                    @Override
//                    public void onProgress(String requestId, long bytes, long totalBytes) {}
//
//                    @Override
//                    public void onSuccess(String requestId, Map resultData) {
//                        String urlFoto = (String) resultData.get("secure_url");
//                        subirVideoCompromiso(videoCompromiso, barraProgreso, urlFoto, adopcion,
//                                contrato, context);
//                    }
//
//                    @Override
//                    public void onError(String requestId, ErrorInfo error) {
//                        Toast.makeText(context, "Ocurrió un error al intentar subir la foto de la firma.", Toast.LENGTH_SHORT).show();
//                        Log.e("CLOUDINARY", "Error al subir imagen: " + error.getDescription());
//                        barraProgreso.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onReschedule(String requestId, ErrorInfo error) {}
//                }).dispatch();

    }

    public void subirVideoCompromiso(Uri video, LinearLayout barraProgreso, String urlFotoAdop,
                                     Adopcion adopcion, ContratoAdopcion contrato, Context context) {

        //Para ahorrar creditos, no se sube el video usando esto
        String urlVideo = "https://res.cloudinary.com/de3pikkwa/video/upload/v1744002396/ex4gs7wl60das4wnqocm.mp4";
        completarAdopcionContratoFirebase(urlFotoAdop, urlVideo, adopcion,
                contrato, barraProgreso, context);

        // Subir video a Cloudinary
//        MediaManager.get().upload(video)
//                .option("resource_type", "video") // Asegura que Cloudinary lo reconozca como video
//                .option("transformation", new Transformation<>()
//                        .quality("auto:low") // Reduce calidad automáticamente
//                        .fetchFormat("mp4") // Asegura formato MP4
//                )
//                .callback(new UploadCallback() {
//                    @Override
//                    public void onStart(String requestId) {
//                        Log.d("CLOUDINARY", "Subiendo video...");
//                    }
//
//                    @Override
//                    public void onProgress(String requestId, long bytes, long totalBytes) {}
//
//                    @Override
//                    public void onSuccess(String requestId, Map resultData) {
//                        String urlVideo = (String) resultData.get("secure_url");
//                        completarAdopcionContratoFirebase(urlFotoAdop, urlVideo, adopcion,
//                                contrato, barraProgreso, context);
//                    }
//
//                    @Override
//                    public void onError(String requestId, ErrorInfo error) {
//                        Toast.makeText(context, "Ocurrió un error al intentar subir el vídeo de compromiso.", Toast.LENGTH_SHORT).show();
//                        Log.e("CLOUDINARY", "Error al subir video: " + error.getDescription());
//                        barraProgreso.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onReschedule(String requestId, ErrorInfo error) {}
//                }).dispatch();
    }

    private void completarAdopcionContratoFirebase(String urlFirma, String videoComp, Adopcion adopcion,
                                           ContratoAdopcion contrato, LinearLayout barra, Context context) {

        Map<String, Object> mapCompletarAdopcion = new HashMap<>();
        mapCompletarAdopcion.put("estado", EstadosCuentas.EN_REVISION);
        mapCompletarAdopcion.put("videoCompromiso", videoComp);

        db.collection("Adopciones").document(adopcion.getId()).update(mapCompletarAdopcion)
                .addOnSuccessListener(command -> {

                    Map <String, Object> mapContrato = new HashMap<>();
                    mapContrato.put("estado", EstadosCuentas.FIRMADO.toString());
                    mapContrato.put("fechaContratoAdopcion", contrato.getFechaContratoAdopcion());
                    mapContrato.put("firmaAdoptante", urlFirma);
                    mapContrato.put("horaContratoAdopcion", contrato.getHoraContratoAdopcion());

                    db.collection("ContratosAdopciones").document(contrato.getId()).update(mapContrato)
                            .addOnSuccessListener(command1 -> {
                                Log.d("FIRESTORE", "Colecciones actualizadas correctamente");
                                Toast.makeText(context, "Proceso de adopción completado, espere la aprobación del administrador.", Toast.LENGTH_SHORT).show();
                                barra.setVisibility(View.GONE);
                                controladorNotificaciones.enviarNotificacionContratoFirmado(adopcion);
                                Intent i = new Intent(context, VerSolicitudesAdopcionActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            }).addOnFailureListener(e -> {Log.e("FIRESTORE", "Error al actualizar el contrato");});

                }).addOnFailureListener(e -> {Log.e("FIRESTORE", "Error al actualizar la adopción");});
    }

    public void terminarAdopcion(Adopcion adopcion, LinearLayout barra, Context context) {
        db.collection("Adopciones").document(adopcion.getId()).
                update("estado", EstadosCuentas.COMPLETADA.toString()).addOnSuccessListener(command -> {
                    db.collection("Mascotas").document(adopcion.getMascotaAdopcion())
                            .update("mascotaAdoptada", true).addOnSuccessListener(command1 -> {
                                Toast.makeText(context, "El proceso de adopción ha sido completado satisfactoriamente.", Toast.LENGTH_SHORT).show();
                                barra.setVisibility(View.GONE);

                                controladorSeguimiento.crearSeguimiento(adopcion.getAdoptante(), adopcion.getMascotaAdopcion());

                                controladorNotificaciones.enviarNotificacionAdopcionTerminada(adopcion);

                                Intent i = new Intent(context, GestionarSolicitudesAdopcionActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            });
                });
    }

    public void solicitarReenvioRequisitos(Adopcion adopcion, LinearLayout barra, Context context) {
        barra.setVisibility(View.VISIBLE);
        Map<String, Object> mapRechazarSolicitud = new HashMap<>();
        mapRechazarSolicitud.put("estado", EstadosCuentas.ACEPTADA);
        mapRechazarSolicitud.put("observaciones", adopcion.getObservaciones());
//        mapRechazarSolicitud.put("contratoAdopcion", "");
        mapRechazarSolicitud.put("videoCompromiso", "");

        db.collection("Adopciones").document(adopcion.getId()).update(mapRechazarSolicitud).addOnSuccessListener(command -> {
            Log.d("FIRESTORE", "Contenido actualizado correctamente");
            barra.setVisibility(View.GONE);

            controladorUtilidades.eliminarVideoCloudinary(adopcion.getVideoCompromiso());
            controladorUtilidades.eliminarFirmaContrato(adopcion.getContratoAdopcion());

            //Iniciar vista de solicitudes pendientes
            Intent i = new Intent(context, GestionarSolicitudesAdopcionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            controladorNotificaciones.enviarNotificacionReenvioRequisitos(adopcion);
        }).addOnFailureListener(e -> {
            Log.e("FIRESTORE", "Error al actualizar contenido");
        });
    }

    public void obtenerNombreAdoptante(String idMascota, Callback<String> callback) {
        db.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", idMascota)
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot documento : querySnapshot) {
                                String idAdoptante = documento.getString("adoptante");

                                db.collection("Usuarios")
                                        .whereEqualTo("cuentaUsuario", idAdoptante)
                                        .get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                QuerySnapshot querySnapshot2 = task2.getResult();
                                                if (!querySnapshot2.isEmpty()) {
                                                    for (QueryDocumentSnapshot documento2 : querySnapshot2) {
                                                        String nombreAdoptante = documento2.getString("nombre");
                                                        callback.onComplete(nombreAdoptante);
                                                        return;
                                                    }
                                                } else {
                                                    Log.e("FIREBASE", "No se encontró el adoptante");
                                                }
                                            } else {
                                                Log.e("FIREBASE", "Error al obtener el adoptante");
                                            }
                                        });
                            }

                        } else {
                            Log.e("FIREBASE", "No se encontraron documentos");
                        }

                    } else {
                        Log.e("FIREBASE", "Error al obtener documentos");
                    }

                });
    }

}
