package com.example.tic_pv.Controlador;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tic_pv.Interfaces.ApiService;
import com.example.tic_pv.MainActivity;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.Desparasitacion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.HistorialMedico;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.Modelo.Notificacion;
import com.example.tic_pv.Modelo.NotificacionRequest;
import com.example.tic_pv.Modelo.ProgramarNotificacionRequest;
import com.example.tic_pv.Modelo.RetrofitClient;
import com.example.tic_pv.Modelo.Seguimiento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControladorNotificaciones {
    private static final String TAG = "Notificaciones";
    private FirebaseFirestore bd = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference;
    private Notificacion notificacion;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    public void enviarNotificacionSolicitudAdopcion(String idSolicitudAdopcion) {
        notificacion = new Notificacion();

        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SOLICITUD_ADOPCION.toString());
        notificacion.setIdRelacionado(idSolicitudAdopcion);
        notificacion.setTitulo("¡Nueva solicitud de adopción en espera!");
        notificacion.setCuerpo("Un usuario ha enviado una solicitud para adoptar una mascota. Revisa los detalles en la aplicación.");
        notificacion.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacion.establecerFechaHoraActual();

        bd.collection("Cuentas").whereEqualTo("rol", "Administrador").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot resultado = task.getResult();
                if (!resultado.isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : resultado) {
                        String estado = documentSnapshot.getString("estado");
                        String idCuenta = documentSnapshot.getId();
                        String token = documentSnapshot.getString("dispositivo");
                        assert estado != null;
                        if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            notificacion.setIdUsuarioReceptor(idCuenta);

                            enviarNotificacionServidor(token, notificacion);

                        } else {
                            Log.e("ERROR NOTIFICACION", "No existen adminsitradores registrados");
                        }
                    }
                }
            } else {
                Log.e("ERROR", "No se pudieron obtener las cuentas registradas en la BDD");
            }
        });

        // Token del usuario al que se enviará la notificación
//        String token = "ef2NDYXZSSW_NisMwYFAs7:APA91bEMqUnQmkL6ARj-PFJ5-BqlQClr2NjlPgN925KJHsWdiNFVbko5mURF-MwFAMVvZv1_jc2vNLEEsaSMQI2IU_nVo_PEqoBn_GktBSKv7ynk2Inrgp8";


    }

    public void enviarNotificacionSolicitudAceptadaRechazada(Adopcion adopcion) {
        notificacion = new Notificacion();

        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SOLICITUD_ADOPCION.toString());
        notificacion.setIdRelacionado(adopcion.getId());
        if (adopcion.getEstadoAdopcion().equalsIgnoreCase(EstadosCuentas.ACEPTADA.toString())) {
            notificacion.setTitulo("¡Su solicitud de adopción ha sido aprobada!");
            notificacion.setCuerpo("Por favor, complete los requisitos necesarios para finalizar el proceso de adopción.");
        } else {
            notificacion.setTitulo("Actualización sobre su solicitud de adopción");
            notificacion.setCuerpo("Lamentamos informarle que su solicitud de adopción ha sido rechazada. Para más información, por favor revise las observaciones enviadas.");
        }
        notificacion.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacion.establecerFechaHoraActual();


        bd.collection("Cuentas").document(adopcion.getAdoptante()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estado = documentSnapshot.getString("estado");
                String token = documentSnapshot.getString("dispositivo");
                notificacion.setIdUsuarioReceptor(adopcion.getAdoptante());

                assert estado != null;
                if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                    enviarNotificacionServidor(token, notificacion);
                } else {
                    Log.e("ERROR_NOTIFICACION", "No existe un usuario activo para enviar la notificación");
                }

            }
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "No se encontró la cuenta del adoptante para la notificación");
        });

    }

    public void enviarNotificacionContratoFirmado(Adopcion adopcion) {
        notificacion = new Notificacion();

        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SOLICITUD_ADOPCION.toString());
        notificacion.setIdRelacionado(adopcion.getId());
        notificacion.setTitulo("¡Requisitos de adopción completados!");
        notificacion.setCuerpo("El adoptante ha completado todos los requisitos pendientes para la solicitud de adopción. Por favor, proceda con la validación correspondiente.");
        notificacion.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacion.establecerFechaHoraActual();

        bd.collection("Cuentas").whereEqualTo("rol", "Administrador").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot resultado = task.getResult();
                if (!resultado.isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : resultado) {
                        String estado = documentSnapshot.getString("estado");
                        String idCuenta = documentSnapshot.getId();
                        String token = documentSnapshot.getString("dispositivo");
                        assert estado != null;
                        if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            notificacion.setIdUsuarioReceptor(idCuenta);

                            enviarNotificacionServidor(token, notificacion);

                        } else {
                            Log.e("ERROR NOTIFICACION", "No existen adminsitradores registrados");
                        }
                    }
                }
            } else {
                Log.e("ERROR", "No se pudieron obtener las cuentas registradas en la BDD");
            }
        });

    }

    public void enviarNotificacionAdopcionTerminada(Adopcion adopcion) {
        notificacion = new Notificacion();

        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SOLICITUD_ADOPCION.toString());
        notificacion.setIdRelacionado(adopcion.getId());
        notificacion.setTitulo("¡Proceso de adopción completado con éxito!");
        notificacion.setCuerpo("El administrador ha validado los últimos requisitos enviados. Ya puede acceder a la información de su mascota en la opción \"Mis mascotas\".");
        notificacion.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacion.establecerFechaHoraActual();

        bd.collection("Cuentas").document(adopcion.getAdoptante()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estado = documentSnapshot.getString("estado");
                String token = documentSnapshot.getString("dispositivo");
                notificacion.setIdUsuarioReceptor(adopcion.getAdoptante());

                assert estado != null;
                if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                    enviarNotificacionServidor(token, notificacion);
                } else {
                    Log.e("ERROR_NOTIFICACION", "No existe un usuario activo para enviar la notificación");
                }

            }
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "No se encontró la cuenta del adoptante para la notificación");
        });

    }

    public void enviarNotificacionReenvioRequisitos(Adopcion adopcion) {
        notificacion = new Notificacion();

        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SOLICITUD_ADOPCION.toString());
        notificacion.setIdRelacionado(adopcion.getId());
        notificacion.setTitulo("¡Reenvío de requisitos solicitado!");
        notificacion.setCuerpo("El administrador requiere que envíe nuevamente los requisitos. En el detalle de la solicitud podrá consultar los motivos de esta petición.");
        notificacion.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacion.establecerFechaHoraActual();

        bd.collection("Cuentas").document(adopcion.getAdoptante()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estado = documentSnapshot.getString("estado");
                String token = documentSnapshot.getString("dispositivo");
                notificacion.setIdUsuarioReceptor(adopcion.getAdoptante());

                assert estado != null;
                if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                    enviarNotificacionServidor(token, notificacion);
                } else {
                    Log.e("ERROR_NOTIFICACION", "No existe un usuario activo para enviar la notificación");
                }

            }
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "No se encontró la cuenta del adoptante para la notificación");
        });

    }

    public void enviarNotificacionAsignacionSeguimiento(Seguimiento seguimiento) {
        notificacion = new Notificacion();

        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SEGUIMIENTO_ASIGNADO.toString());
        notificacion.setIdRelacionado(seguimiento.getId());
        notificacion.setTitulo("¡Nuevo seguimiento asignado!");
        notificacion.setCuerpo("Se le ha asignado un nuevo seguimiento. Inicie las acciones correspondientes para proceder.");
        notificacion.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacion.establecerFechaHoraActual();

        bd.collection("Cuentas").document(seguimiento.getIdVoluntario()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estado = documentSnapshot.getString("estado");
                String token = documentSnapshot.getString("dispositivo");
                notificacion.setIdUsuarioReceptor(seguimiento.getIdVoluntario());

                assert estado != null;
                if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                    enviarNotificacionServidor(token, notificacion);
                } else {
                    Log.e("ERROR_NOTIFICACION", "No existe un usuario activo para enviar la notificación");
                }

            }
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "No se encontró la cuenta del adoptante para la notificación");
        });

    }

    // Notifica al otro participante del chat de seguimiento.
    // enviaVoluntario indica desde qué lado se envió el mensaje.
    public void enviarNotificacionMensajeChat(Seguimiento seguimiento, boolean enviaVoluntario, String contenidoMensaje) {
        String idReceptor = enviaVoluntario ? seguimiento.getIdAdoptante() : seguimiento.getIdVoluntario();
        String nombreEmisor = enviaVoluntario ? seguimiento.getNombreVoluntario() : seguimiento.getNombreAdoptante();

        if (idReceptor == null || idReceptor.isEmpty()) {
            Log.e(TAG, "El seguimiento no tiene receptor al que notificar el mensaje");
            return;
        }

        // Si el receptor tiene el chat abierto no se le notifica, ya está viendo el mensaje
        FirebaseDatabase.getInstance()
                .getReference("presenciaChats")
                .child(seguimiento.getListaMensajes())
                .child(idReceptor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d(TAG, "El receptor está dentro del chat, no se envía notificación");
                            return;
                        }
                        enviarNotificacionMensaje(seguimiento, idReceptor, nombreEmisor, contenidoMensaje);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Si no se puede leer la presencia se prefiere notificar de más antes que perder el aviso
                        enviarNotificacionMensaje(seguimiento, idReceptor, nombreEmisor, contenidoMensaje);
                    }
                });
    }

    private void enviarNotificacionMensaje(Seguimiento seguimiento, String idReceptor, String nombreEmisor, String contenidoMensaje) {
        // Se usa una variable local en lugar del campo compartido para que dos mensajes
        // seguidos no se pisen entre sí
        Notificacion notificacionMensaje = new Notificacion();

        notificacionMensaje.setId(UUID.randomUUID().toString());
        notificacionMensaje.setTipoNotificacion(EstadosCuentas.NOTIFICACION_MENSAJE_CHAT.toString());
        notificacionMensaje.setIdRelacionado(seguimiento.getId());
        notificacionMensaje.setTitulo("Nuevo mensaje de " + nombreEmisor);
        notificacionMensaje.setCuerpo(resumirMensajeChat(contenidoMensaje));
        notificacionMensaje.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacionMensaje.setIdUsuarioReceptor(idReceptor);
        notificacionMensaje.establecerFechaHoraActual();

        bd.collection("Cuentas").document(idReceptor).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estado = documentSnapshot.getString("estado");
                String token = documentSnapshot.getString("dispositivo");

                if (estado != null && estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                    enviarNotificacionServidor(token, notificacionMensaje);
                } else {
                    Log.e("ERROR_NOTIFICACION", "El receptor del mensaje no es un usuario activo");
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "No se encontró la cuenta del receptor para notificar el mensaje");
        });
    }

    // La multimedia viaja como URL, así que se describe en lugar de mostrar el enlace
    private String resumirMensajeChat(String contenido) {
        if (contenido == null || contenido.isEmpty()) {
            return "Le ha enviado un mensaje";
        }

        if (controladorUtilidades.esImagen(contenido)) {
            return "Le ha enviado una foto";
        }

        if (controladorUtilidades.esVideo(contenido)) {
            return "Le ha enviado un video";
        }

        if (contenido.length() > 120) {
            return contenido.substring(0, 117) + "...";
        }

        return contenido;
    }

    // Avisa al adoptante de que la mascota fue retirada. Sin esto vería desaparecer a la
    // mascota de su perfil sin ninguna explicación.
    public void enviarNotificacionMascotaRetirada(Seguimiento seguimiento, String motivo) {
        if (seguimiento.getIdAdoptante() == null || seguimiento.getIdAdoptante().isEmpty()) {
            Log.e(TAG, "El seguimiento no tiene adoptante al que avisar del retiro");
            return;
        }

        // Variable local en lugar del campo compartido, para que otra notificación en
        // curso no sobrescriba estos datos antes de que responda Firestore
        Notificacion notificacionRetiro = new Notificacion();

        String nombreMascota = seguimiento.getNombreMascota() != null ? seguimiento.getNombreMascota() : "la mascota";

        notificacionRetiro.setId(UUID.randomUUID().toString());
        notificacionRetiro.setTipoNotificacion(EstadosCuentas.NOTIFICACION_MASCOTA_RETIRADA.toString());
        notificacionRetiro.setIdRelacionado(seguimiento.getId());
        notificacionRetiro.setTitulo("Su proceso de adopción ha finalizado");
        String cuerpo = "Le informamos que " + nombreMascota + " ha sido retirada de su hogar y el "
                + "seguimiento ha finalizado.";
        if (motivo != null && !motivo.trim().isEmpty()) {
            cuerpo += "\n\nMotivo: " + motivo.trim();
        }
        cuerpo += "\n\nSi tiene dudas sobre esta decisión, comuníquese con la agrupación.";

        notificacionRetiro.setCuerpo(cuerpo);

        notificacionRetiro.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacionRetiro.setIdUsuarioReceptor(seguimiento.getIdAdoptante());
        notificacionRetiro.establecerFechaHoraActual();

        bd.collection("Cuentas").document(seguimiento.getIdAdoptante()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String estado = documentSnapshot.getString("estado");
                        String token = documentSnapshot.getString("dispositivo");

                        if (estado != null && estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            enviarNotificacionServidor(token, notificacionRetiro);
                        } else {
                            Log.e("ERROR_NOTIFICACION", "El adoptante no es un usuario activo");
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("ERROR", "No se encontró la cuenta del adoptante para avisar del retiro"));
    }

    // Al retirar una mascota, los recordatorios ya programados siguen apuntando al ex
    // adoptante. Se eliminan solo los suyos: los del voluntario se conservan, porque la
    // mascota vuelve a estar bajo su cuidado.
    public void eliminarNotificacionesVacunaAdoptante(String idMascota, String idAdoptante) {
        if (idMascota == null || idMascota.isEmpty() || idAdoptante == null || idAdoptante.isEmpty()) {
            return;
        }

        DatabaseReference refNotificaciones = FirebaseDatabase.getInstance().getReference("notificaciones");

        // El historial médico se guarda como historial_medico/{idMascota}/{idHistorial}
        FirebaseDatabase.getInstance().getReference("historial_medico").child(idMascota)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot historial : snapshot.getChildren()) {
                            String idHistorial = historial.getKey();
                            if (idHistorial == null) {
                                continue;
                            }

                            // Cada notificación guarda el id del historial en idRelacionado
                            refNotificaciones.child(idAdoptante)
                                    .orderByChild("idRelacionado")
                                    .equalTo(idHistorial)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot notificaciones) {
                                            for (DataSnapshot notificacion : notificaciones.getChildren()) {
                                                String key = notificacion.getKey();
                                                notificacion.getRef().removeValue();
                                                eliminarNotificacionProgramada(key);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("FIREBASE", "Error al leer las notificaciones del adoptante");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE", "Error al leer el historial médico de la mascota");
                    }
                });
    }

    // Cancela la programación en el servidor para que el recordatorio no se dispare.
    // Devuelve la tarea para poder esperarla antes de programar recordatorios nuevos.
    private Task<Void> eliminarNotificacionProgramada(String idNotificacion) {
        if (idNotificacion == null) {
            return Tasks.forResult(null);
        }

        return bd.collection("NotificacionesProgramadas")
                .whereEqualTo("idNotificacion", idNotificacion)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FIREBASE", "Error al eliminar la notificación programada");
                        return Tasks.forResult(null);
                    }

                    List<Task<Void>> borrados = new ArrayList<>();
                    for (DocumentSnapshot documento : task.getResult().getDocuments()) {
                        borrados.add(documento.getReference().delete());
                    }

                    return Tasks.whenAll(borrados);
                });
    }

    // Programa en el servidor un recordatorio de seguimiento para el voluntario encargado.
    // Los recordatorios del plan se generan en ControladorSeguimiento, que decide las fechas.
    public void programarRecordatorioSeguimiento(String idVoluntario, String idSeguimiento,
                                                 String fecha, String hora,
                                                 String titulo, String cuerpo) {
        if (idVoluntario == null || idVoluntario.isEmpty() || fecha == null) {
            Log.e(TAG, "No hay voluntario o fecha para programar el recordatorio del seguimiento");
            return;
        }

        Notificacion recordatorio = new Notificacion();
        recordatorio.setId(UUID.randomUUID().toString());
        recordatorio.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SEGUIMIENTO_PROGRAMADO.toString());
        recordatorio.setIdRelacionado(idSeguimiento);
        recordatorio.setTitulo(titulo);
        recordatorio.setCuerpo(cuerpo);
        recordatorio.setEstado(EstadosCuentas.NOTIFICACION_PROGRAMADA.toString());
        recordatorio.setFechaNotificacion(fecha);
        recordatorio.setHoraNotificacion(hora);

        String fechaServidor = controladorUtilidades.convertirFechaAFormatoServidor(fecha, hora);
        validarUsuarioProgramarNotificacion(idVoluntario, fechaServidor, recordatorio);
    }

    // Cancela los recordatorios pendientes de un seguimiento. Se usa al finalizarlo y al
    // retirar la mascota: en los dos casos ya no hay nada que recordar.
    public void eliminarNotificacionesSeguimiento(String idSeguimiento, String idVoluntario) {
        eliminarNotificacionesSeguimiento(idSeguimiento, idVoluntario, null);
    }

    // alTerminar sirve para reprogramar recién cuando no queda ningún recordatorio viejo,
    // porque si no se solaparían los del plan anterior con los del nuevo.
    public void eliminarNotificacionesSeguimiento(String idSeguimiento, String idVoluntario, Runnable alTerminar) {
        if (idSeguimiento == null || idSeguimiento.isEmpty()
                || idVoluntario == null || idVoluntario.isEmpty()) {
            if (alTerminar != null) {
                alTerminar.run();
            }
            return;
        }

        // Cada recordatorio guarda el id del seguimiento en idRelacionado
        FirebaseDatabase.getInstance().getReference("notificaciones").child(idVoluntario)
                .orderByChild("idRelacionado")
                .equalTo(idSeguimiento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Task<?>> pendientes = new ArrayList<>();

                        for (DataSnapshot notificacion : snapshot.getChildren()) {
                            pendientes.add(notificacion.getRef().removeValue());
                            pendientes.add(eliminarNotificacionProgramada(notificacion.getKey()));
                        }

                        Tasks.whenAll(pendientes).addOnCompleteListener(task -> {
                            if (alTerminar != null) {
                                alTerminar.run();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE", "Error al leer los recordatorios del seguimiento");
                        if (alTerminar != null) {
                            alTerminar.run();
                        }
                    }
                });
    }

    // Avisa al adoptante de que el seguimiento terminó y ya no hay que reportar nada
    public void enviarNotificacionSeguimientoFinalizado(Seguimiento seguimiento) {
        if (seguimiento.getIdAdoptante() == null || seguimiento.getIdAdoptante().isEmpty()) {
            Log.e(TAG, "El seguimiento no tiene adoptante al que avisar del cierre");
            return;
        }

        // Variable local en lugar del campo compartido, para que otra notificación en
        // curso no sobrescriba estos datos antes de que responda Firestore
        Notificacion notificacionCierre = new Notificacion();

        String nombreMascota = seguimiento.getNombreMascota() != null
                ? seguimiento.getNombreMascota()
                : "su mascota";

        notificacionCierre.setId(UUID.randomUUID().toString());
        notificacionCierre.setTipoNotificacion(EstadosCuentas.NOTIFICACION_SEGUIMIENTO_FINALIZADO.toString());
        notificacionCierre.setIdRelacionado(seguimiento.getId());
        notificacionCierre.setTitulo("Seguimiento finalizado");
        notificacionCierre.setCuerpo("El seguimiento de " + nombreMascota + " ha finalizado. "
                + "Gracias por acompañar todo el proceso.\n\n"
                + "Ya no recibirá controles periódicos ni podrá escribir en este chat. "
                + "Si necesita ayuda, comuníquese con la agrupación.");
        notificacionCierre.setEstado(EstadosCuentas.NOTIFICACION_ENVIADA.toString());
        notificacionCierre.setIdUsuarioReceptor(seguimiento.getIdAdoptante());
        notificacionCierre.establecerFechaHoraActual();

        bd.collection("Cuentas").document(seguimiento.getIdAdoptante()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String estado = documentSnapshot.getString("estado");
                        String token = documentSnapshot.getString("dispositivo");

                        if (estado != null && estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                            enviarNotificacionServidor(token, notificacionCierre);
                        } else {
                            Log.e("ERROR_NOTIFICACION", "El adoptante no es un usuario activo");
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("ERROR", "No se encontró la cuenta del adoptante para avisar del cierre"));
    }

    public void enviarNotificacionVacuna(HistorialMedico vacuna, Mascota mascota, String hora) {

        // Fecha principal en formato ISO "yyyy-MM-dd'T'HH:mm:ssXXX"
        String fechaProgramada = controladorUtilidades.convertirFechaAFormatoServidor(vacuna.getFechaProxima(), hora);

        // Obtener tres fechas consecutivas hacia atrás o adelante según corresponda
        ArrayList<String> fechasNotificaciones = new ArrayList<>(controladorUtilidades.obtenerTresFechasConsecutivas(fechaProgramada));

        String idActual = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // ============================================================
        // 1) Enviar notificaciones al voluntario (usuario actual)
        // ============================================================

        bd.collection("Cuentas").document(idActual).get()
                .addOnSuccessListener(documentSnapshot -> {
                    int contador = 0;
                    boolean esVoluntario = Objects.requireNonNull(
                            documentSnapshot.getString("rol")
                    ).equalsIgnoreCase("Voluntario");

                    if (esVoluntario) {

                        for (String fecha : fechasNotificaciones) {
                            String mensaje = "";
                            Notificacion notif = new Notificacion();

                            if (vacuna.getTipo().equalsIgnoreCase(EstadosCuentas.VACUNA.toString())) {
                                mensaje = generarMensajeNotificacionVacuna(mascota, contador);
                                notif.setTitulo("¡Recordatorio para vacunación!");
                                notif.setCuerpo(mensaje);
                            } else {
                                mensaje = generarMensajeNotificacionDesparasitacion(mascota, contador);
                                notif.setTitulo("¡Recordatorio para desparasitación!");
                                notif.setCuerpo(mensaje);
                            }


                            notif.setId(UUID.randomUUID().toString());
                            notif.setTipoNotificacion(EstadosCuentas.NOTIFICACION_VACUNA.toString());
                            notif.setIdRelacionado(vacuna.getId());

                            notif.setEstado(EstadosCuentas.NOTIFICACION_PROGRAMADA.toString());
                            notif.setFechaNotificacion(controladorUtilidades.convertirFechaServidorAFormatoNormal(fecha));
                            notif.setHoraNotificacion(hora);

                            validarUsuarioProgramarNotificacion(idActual, fecha, notif);
                            contador++;
                        }
                    }

                });

        // ============================================================
        // 2) Enviar notificaciones al adoptante (si existe)
        // ============================================================

        bd.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", mascota.getId())
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {

                            for (QueryDocumentSnapshot documento : querySnapshot) {

                                String idAdoptante = documento.getString("adoptante");
                                int contador = 0;
                                for (String fecha : fechasNotificaciones) {
                                    Notificacion notif = new Notificacion();
                                    String mensaje = "";

                                    if (vacuna.getTipo().equalsIgnoreCase(EstadosCuentas.VACUNA.toString())) {
                                        mensaje = generarMensajeNotificacionVacuna(mascota, contador);
                                        notif.setTitulo("¡Recordatorio para vacunación!");
                                        notif.setCuerpo(mensaje);
                                    } else {
                                        mensaje = generarMensajeNotificacionDesparasitacion(mascota, contador);
                                        notif.setTitulo("¡Recordatorio para desparasitación!");
                                        notif.setCuerpo(mensaje);
                                    }

                                    notif.setId(UUID.randomUUID().toString());
                                    notif.setTipoNotificacion(EstadosCuentas.NOTIFICACION_VACUNA.toString());
                                    notif.setIdRelacionado(vacuna.getId());

                                    notif.setEstado(EstadosCuentas.NOTIFICACION_PROGRAMADA.toString());
                                    notif.setFechaNotificacion(controladorUtilidades.convertirFechaServidorAFormatoNormal(fecha));  // ← fecha de la iteración
                                    notif.setHoraNotificacion(hora);

                                    validarUsuarioProgramarNotificacion(idAdoptante, fecha, notif);
                                    contador++;
                                }
                            }

                        } else {
                            Log.e("FIREBASE", "No se encontraron documentos");
                        }

                    } else {
                        Log.e("FIREBASE", "Error al obtener documentos");
                    }

                });
    }

    public void modificarNotificacionesHistorial(HistorialMedico vacuna, Mascota mascota) {

        String idActual = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificaciones");

        bd.collection("Cuentas").document(idActual).get()
                .addOnSuccessListener(doc -> {

                    boolean esVoluntario = Objects.requireNonNull(
                            doc.getString("rol")
                    ).equalsIgnoreCase("Voluntario");

                    if (!esVoluntario) return;

                    Query query = ref.child(idActual)
                            .orderByChild("idRelacionado")
                            .equalTo(vacuna.getId());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (!snapshot.exists()) {
                                Log.d("FIREBASE", "No hay notificaciones con ese idRelacionado");
                                return;
                            }

                            List<Task<?>> tasks = new ArrayList<>();

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                Task<Void> rtdbDeleteTask = snap.getRef().removeValue();
                                tasks.add(rtdbDeleteTask);

                                rtdbDeleteTask.addOnSuccessListener(aVoid -> {
                                    Log.d("FIREBASE", "Eliminado correctamente → " + snap.getKey());

                                    bd.collection("NotificacionesProgramadas")
                                            .whereEqualTo("idNotificacion", snap.getKey())
                                            .get()
                                            .addOnSuccessListener(querySnapshot -> {

                                                if (querySnapshot.isEmpty()) {
                                                    Log.d("Firestore", "No existe notificación con ese idNotificacion");
                                                    return;
                                                }

                                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                                                    Task<Void> firestoreDeleteTask =
                                                            bd.collection("NotificacionesProgramadas")
                                                                    .document(doc.getId())
                                                                    .delete()
                                                                    .addOnSuccessListener(unused ->
                                                                            Log.d("Firestore", "Eliminado correctamente → " + doc.getId()))
                                                                    .addOnFailureListener(e ->
                                                                            Log.e("Firestore", "Error al eliminar → " + doc.getId(), e));

                                                    tasks.add(firestoreDeleteTask);
                                                }

                                            })
                                            .addOnFailureListener(e ->
                                                    Log.e("Firestore", "Error en consulta", e));
                                }).addOnFailureListener(e ->
                                        Log.e("FIREBASE", "Error al eliminar → " + snap.getKey(), e));
                            }

                            // 🔹 Esperar a que TODAS las tareas terminen
                            Tasks.whenAll(tasks)
                                    .addOnSuccessListener(unused -> {
                                        Log.d("SYNC", "Eliminaciones completadas en RTDB + Firestore");
                                        enviarNotificacionVacuna(vacuna, mascota, vacuna.getHoraRecordatorio());
                                    })
                                    .addOnFailureListener(e ->
                                            Log.e("SYNC", "Error esperando tareas", e));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FIREBASE", error.getMessage());
                        }
                    });
                });

        bd.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", mascota.getId())
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {

                            for (QueryDocumentSnapshot documento : querySnapshot) {

                                String idAdoptante = documento.getString("adoptante");
                                assert idAdoptante != null;
                                Query query = ref.child(idAdoptante)
                                        .orderByChild("idRelacionado")
                                        .equalTo(vacuna.getId());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (!snapshot.exists()) {
                                            Log.d("FIREBASE", "No hay notificaciones con ese idRelacionado");
                                            return;
                                        }

                                        List<Task<?>> tasks = new ArrayList<>();

                                        for (DataSnapshot snap : snapshot.getChildren()) {

                                            Task<Void> rtdbDeleteTask = snap.getRef().removeValue();
                                            tasks.add(rtdbDeleteTask);

                                            rtdbDeleteTask.addOnSuccessListener(aVoid -> {
                                                Log.d("FIREBASE", "Eliminado correctamente → " + snap.getKey());

                                                bd.collection("NotificacionesProgramadas")
                                                        .whereEqualTo("idNotificacion", snap.getKey())
                                                        .get()
                                                        .addOnSuccessListener(querySnapshot -> {

                                                            if (querySnapshot.isEmpty()) {
                                                                Log.d("Firestore", "No existe notificación con ese idNotificacion");
                                                                return;
                                                            }

                                                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                                                                Task<Void> firestoreDeleteTask =
                                                                        bd.collection("NotificacionesProgramadas")
                                                                                .document(doc.getId())
                                                                                .delete()
                                                                                .addOnSuccessListener(unused ->
                                                                                        Log.d("Firestore", "Eliminado correctamente → " + doc.getId()))
                                                                                .addOnFailureListener(e ->
                                                                                        Log.e("Firestore", "Error al eliminar → " + doc.getId(), e));

                                                                tasks.add(firestoreDeleteTask);
                                                            }

                                                        })
                                                        .addOnFailureListener(e ->
                                                                Log.e("Firestore", "Error en consulta", e));
                                            }).addOnFailureListener(e ->
                                                    Log.e("FIREBASE", "Error al eliminar → " + snap.getKey(), e));
                                        }

                                        // 🔹 Esperar a que TODAS las tareas terminen
                                        Tasks.whenAll(tasks)
                                                .addOnSuccessListener(unused -> {
                                                    Log.d("SYNC", "Eliminaciones completadas en RTDB + Firestore");
                                                    enviarNotificacionVacuna(vacuna, mascota, vacuna.getHoraRecordatorio());
                                                })
                                                .addOnFailureListener(e ->
                                                        Log.e("SYNC", "Error esperando tareas", e));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("FIREBASE", error.getMessage());
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

    public void eliminarNotificacionesHistorial(String idHistorial, String idMascota) {
        String idActual = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificaciones");

        bd.collection("Cuentas").document(idActual).get()
                .addOnSuccessListener(doc -> {

                    boolean esVoluntario = Objects.requireNonNull(
                            doc.getString("rol")
                    ).equalsIgnoreCase("Voluntario");

                    if (!esVoluntario) return;

                    Query query = ref.child(idActual)
                            .orderByChild("idRelacionado")
                            .equalTo(idHistorial);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (!snapshot.exists()) {
                                Log.d("FIREBASE", "No hay notificaciones con ese idRelacionado");
                                return;
                            }

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String key = dataSnapshot.getKey();

                                dataSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("REALTIME", "Notificaciones eliminadas correctamente.");
                                                bd.collection("NotificacionesProgramadas").whereEqualTo("idNotificacion", key).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                                                if (!documentSnapshots.isEmpty()) {
                                                                    // Eliminar todos los documentos encontrados
                                                                    for (DocumentSnapshot document : documentSnapshots) {
                                                                        document.getReference().delete()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d("Firestore", "Documento eliminado con idNotificacion: " + key);
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.e("Firestore", "Error al eliminar documento: " + e.getMessage());
                                                                                    }
                                                                                });
                                                                    }
                                                                } else {
                                                                    Log.d("Firestore", "No se encontró ningún documento con idNotificacion: " + key);
                                                                }
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("REALTIME", "Error al eliminar notificaciones.");
                                            }
                                        });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FIREBASE", error.getMessage());
                        }
                    });
                });

        bd.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", idMascota)
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {

                            for (QueryDocumentSnapshot documento : querySnapshot) {

                                String idAdoptante = documento.getString("adoptante");
                                assert idAdoptante != null;
                                Query query = ref.child(idAdoptante)
                                        .orderByChild("idRelacionado")
                                        .equalTo(idHistorial);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (!snapshot.exists()) {
                                            Log.d("FIREBASE", "No hay notificaciones con ese idRelacionado");
                                            return;
                                        }

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            String key = dataSnapshot.getKey();

                                            dataSnapshot.getRef().removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("REALTIME", "Notificaciones eliminadas correctamente.");
                                                            bd.collection("NotificacionesProgramadas").whereEqualTo("idNotificacion", key).get()
                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot documentSnapshots) {
                                                                            if (!documentSnapshots.isEmpty()) {
                                                                                // Eliminar todos los documentos encontrados
                                                                                for (DocumentSnapshot document : documentSnapshots) {
                                                                                    document.getReference().delete()
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    Log.d("Firestore", "Documento eliminado con idNotificacion: " + key);
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Log.e("Firestore", "Error al eliminar documento: " + e.getMessage());
                                                                                                }
                                                                                            });
                                                                                }
                                                                            } else {
                                                                                Log.d("Firestore", "No se encontró ningún documento con idNotificacion: " + key);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("REALTIME", "Error al eliminar notificaciones.");
                                                        }
                                                    });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("FIREBASE", error.getMessage());
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

    public void enviarNotificacionDesparasitacion(Desparasitacion desparasitacion, Mascota mascota, String hora) {

        // Fecha principal en formato ISO "yyyy-MM-dd'T'HH:mm:ssXXX"
        String fechaProgramada = controladorUtilidades.convertirFechaAFormatoServidor(desparasitacion.getFechaProxima(), hora);

        // Obtener tres fechas consecutivas hacia atrás o adelante según corresponda
        ArrayList<String> fechasNotificaciones = new ArrayList<>(controladorUtilidades.obtenerTresFechasConsecutivas(fechaProgramada));

        String idActual = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // ============================================================
        // 1) Enviar notificaciones al voluntario (usuario actual)
        // ============================================================

        bd.collection("Cuentas").document(idActual).get()
                .addOnSuccessListener(documentSnapshot -> {
                    int contador = 0;
                    boolean esVoluntario = Objects.requireNonNull(
                            documentSnapshot.getString("rol")
                    ).equalsIgnoreCase("Voluntario");

                    if (esVoluntario) {

                        for (String fecha : fechasNotificaciones) {
                            String mensaje = generarMensajeNotificacionDesparasitacion(mascota, contador);
                            Notificacion notif = new Notificacion();
                            notif.setId(UUID.randomUUID().toString());
                            notif.setTipoNotificacion(EstadosCuentas.NOTIFICACION_VACUNA.toString());
                            notif.setIdRelacionado(desparasitacion.getId());
                            notif.setTitulo("¡Recordatorio para desparasitación!");
                            notif.setCuerpo(mensaje);
                            notif.setEstado(EstadosCuentas.NOTIFICACION_PROGRAMADA.toString());
                            notif.setFechaNotificacion(controladorUtilidades.convertirFechaServidorAFormatoNormal(fecha));
                            notif.setHoraNotificacion(hora);

                            validarUsuarioProgramarNotificacion(idActual, fecha, notif);
                            contador++;
                        }
                    }

                });

        // ============================================================
        // 2) Enviar notificaciones al adoptante (si existe)
        // ============================================================

        bd.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", mascota.getId())
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {

                            for (QueryDocumentSnapshot documento : querySnapshot) {

                                String idAdoptante = documento.getString("adoptante");
                                int contador = 0;
                                for (String fecha : fechasNotificaciones) {
                                    String mensaje = generarMensajeNotificacionDesparasitacion(mascota, contador);
                                    Notificacion notif = new Notificacion();
                                    notif.setId(UUID.randomUUID().toString());
                                    notif.setTipoNotificacion(EstadosCuentas.NOTIFICACION_VACUNA.toString());
                                    notif.setIdRelacionado(desparasitacion.getId());
                                    notif.setTitulo("¡Recordatorio para desparasitación!");
                                    notif.setCuerpo(mensaje);
                                    notif.setEstado(EstadosCuentas.NOTIFICACION_PROGRAMADA.toString());
                                    notif.setFechaNotificacion(controladorUtilidades.convertirFechaServidorAFormatoNormal(fecha));  // ← fecha de la iteración
                                    notif.setHoraNotificacion(hora);

                                    validarUsuarioProgramarNotificacion(idAdoptante, fecha, notif);
                                    contador++;
                                }
                            }

                        } else {
                            Log.e("FIREBASE", "No se encontraron documentos");
                        }

                    } else {
                        Log.e("FIREBASE", "Error al obtener documentos");
                    }

                });
    }


    @NonNull
    private static String generarMensajeNotificacionVacuna(Mascota mascota, int contador) {
        String mensaje = "Hoy corresponde realizar el procedimiento de vacunación para " + mascota.getNombreMascota() + ". Recuerde completarlo y actualizar el registro en la aplicación.";
        if (contador > 0) {
            mensaje = "En " + contador + " día(s) corresponde aplicar la próxima vacuna programada para " + mascota.getNombreMascota() + ". Por favor, tome las previsiones necesarias para cumplir con el procedimiento.";
        }
        return mensaje;
    }

    @NonNull
    private static String generarMensajeNotificacionDesparasitacion(Mascota mascota, int contador) {
        String mensaje = "Hoy corresponde realizar el procedimiento de desparasitación para " + mascota.getNombreMascota() + ". Recuerde completarlo y actualizar el registro en la aplicación.";
        if (contador > 0) {
            mensaje = "En " + contador + " día(s) corresponde realizar la desparasitación programada para " + mascota.getNombreMascota() + ". Por favor, tome las previsiones necesarias para cumplir con el procedimiento.";
        }
        return mensaje;
    }


    private void validarUsuarioProgramarNotificacion(String idUsuario, String fechaProgramada, Notificacion notificacionVacuna) {
        // Método para enviar notificación
        assert idUsuario != null;
        bd.collection("Cuentas").document(idUsuario).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estado = documentSnapshot.getString("estado");
                String token = documentSnapshot.getString("dispositivo");
                notificacionVacuna.setIdUsuarioReceptor(idUsuario);

                assert estado != null;
                if (estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())) {
                    programarNotificacionServidor(token, notificacionVacuna, fechaProgramada);
                } else {
                    Log.e("ERROR_NOTIFICACION", "No existe un usuario activo para enviar la notificación");
                }

            }
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "No se encontró la cuenta del adoptante para la notificación");
        });
    }

    private void enviarNotificacionServidor(String tokenUsuario, Notificacion notificacion) {
        NotificacionRequest request = new NotificacionRequest(tokenUsuario, notificacion.getTitulo(), notificacion.getCuerpo());
        ApiService apiService = RetrofitClient.getApiService();

        apiService.enviarNotificacion(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notificación enviada exitosamente");
                    guardarNotificacionEnBD(notificacion);
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string(); // Obtener respuesta del servidor
                        Log.e("Notificaciones", "Error en la respuesta: " + errorBody);
                    } catch (IOException e) {
                        Log.e("Notificaciones", "Error: " + e.getMessage());
                    }
                    Log.e("Notificaciones", "Error al enviar notificación");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    public void programarNotificacionServidor(String tokenUsuario, Notificacion notificacion, String fechaProgramada) {

        ProgramarNotificacionRequest request = new ProgramarNotificacionRequest(
                tokenUsuario,
                notificacion.getTitulo(),
                notificacion.getCuerpo(),
                fechaProgramada,
                notificacion.getIdUsuarioReceptor(),
                notificacion.getId()
        );

        ApiService apiService = RetrofitClient.getApiService();

        apiService.programarNotificacion(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                if (response.isSuccessful()) {
                    Log.d(TAG, "Notificación programada exitosamente");
                    guardarNotificacionEnBD(notificacion);
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Error en respuesta: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error leyendo errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
            }
        });
    }

    private void guardarNotificacionEnBD(Notificacion notificacion) {
        databaseReference = FirebaseDatabase.getInstance().getReference("notificaciones");

        HashMap<String, Object> mapNotificacion = new HashMap<>();
        mapNotificacion.put("titulo", notificacion.getTitulo());
        mapNotificacion.put("cuerpo", notificacion.getCuerpo());
        mapNotificacion.put("tipoNotificacion", notificacion.getTipoNotificacion());
        mapNotificacion.put("idRelacionado", notificacion.getIdRelacionado());
        mapNotificacion.put("fechaNotificacion", notificacion.getFechaNotificacion());
        mapNotificacion.put("horaNotificacion", notificacion.getHoraNotificacion());
        mapNotificacion.put("estado", notificacion.getEstado());

        databaseReference.child(notificacion.getIdUsuarioReceptor()).child(notificacion.getId()).setValue(mapNotificacion)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Notificaciones", "Notificación guardada correctamente");
                }).addOnFailureListener(e -> {
                    Log.e("Notificaciones", "Error al guardar la notificación" + e.getMessage());
                });
    }

    public void obtenerListaNotificaciones(String idUsuario, CallBackGenerico<List<Notificacion>> callbackgenerico) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificaciones").child(idUsuario);
        ArrayList<Notificacion> listaNotificaciones = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notificacion notificacion = new Notificacion();
                    notificacion.setId(data.getKey());
                    notificacion.setTitulo(data.child("titulo").getValue(String.class));
                    notificacion.setCuerpo(data.child("cuerpo").getValue(String.class));
                    notificacion.setTipoNotificacion(data.child("tipoNotificacion").getValue(String.class));
                    notificacion.setIdRelacionado(data.child("idRelacionado").getValue(String.class));
                    notificacion.setFechaNotificacion(data.child("fechaNotificacion").getValue(String.class));
                    notificacion.setHoraNotificacion(data.child("horaNotificacion").getValue(String.class));
                    notificacion.setEstado(data.child("estado").getValue(String.class));

                    if (notificacion.getEstado().equalsIgnoreCase(EstadosCuentas.NOTIFICACION_ENVIADA.toString())) {
                        listaNotificaciones.add(notificacion);
                    }
                }
                callbackgenerico.onComplete(listaNotificaciones);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "Error al obtener las notificaciones");
            }
        });
    }

    // Callback genérico
    public interface CallBackGenerico<T> {
        void onComplete(T result);

        void onError(Exception e);
    }

}
