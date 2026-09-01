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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
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
    // El retiro toca varias colecciones. Si el cierre de la adopción falla, la mascota ya
    // volvió al catálogo pero el proceso quedó a medias, así que hay que reintentarlo.
    private static final String ERROR_CIERRE_ADOPCION =
            "La mascota volvió al catálogo, pero no se pudo cerrar la adopción. Vuelva a intentarlo.";

    // La primera semana el seguimiento es diario: es el periodo en el que se decide si la
    // mascota se queda en el hogar. Después vienen ocho controles mensuales.
    private static final int DIAS_PRIMERA_SEMANA = 7;
    private static final int MESES_FASE_MENSUAL = 8;

    // Pasada la fase mensual los controles son trimestrales; se dejan cuatro programados
    private static final int MESES_FASE_PERIODICA = 3;
    private static final int CONTROLES_FASE_PERIODICA = 4;

    private static final String HORA_RECORDATORIO_POR_DEFECTO = "08:00";

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

        // El plan de recordatorios se cuenta desde la adopción, que es justo cuando se crea
        // el seguimiento. Se programan al asignar al voluntario, que es quien los recibe.
        mapSeguimiento.put("fechaInicioSeguimiento", controladorUtilidades.obtenerFechaActual());
        mapSeguimiento.put("faseSeguimiento", EstadosCuentas.SEGUIMIENTO_DIARIO.toString());
        mapSeguimiento.put("horaSeguimiento", HORA_RECORDATORIO_POR_DEFECTO);
        mapSeguimiento.put("recordatoriosProgramados", false);

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
                        leerPlanSeguimiento(documentSnapshot, seguimiento);

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
                        leerPlanSeguimiento(documentSnapshot, seguimiento);

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

                    // Y los controles del plan de seguimiento tampoco tienen ya sentido
                    controladorNotificaciones.eliminarNotificacionesSeguimiento(
                            seguimiento.getId(), seguimiento.getIdVoluntario());

                    // 3. Se cierra la adopción completada y su contrato. Va antes que el
                    //    cierre del seguimiento: si algo falla aquí, el seguimiento sigue
                    //    activo y el voluntario lo conserva en su lista para reintentarlo.
                    cerrarAdopcionCompletada(seguimiento, new Callback<Void>() {
                        @Override
                        public void onComplete(Void result) {
                            cerrarSeguimientoRetiro(seguimiento, motivo, callback);
                        }

                        @Override
                        public void onError(Exception e) {
                            callback.onError(e);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al devolver la mascota al catálogo");
                    callback.onError(e);
                });
    }

    // Último paso del retiro: el seguimiento queda cerrado con su motivo, ese motivo se deja
    // dentro de la conversación y se avisa al adoptante.
    private void cerrarSeguimientoRetiro(Seguimiento seguimiento, String motivo, Callback<Void> callback) {
        Map<String, Object> mapCierre = new HashMap<>();
        mapCierre.put("estado", EstadosCuentas.RECHAZADA.toString());
        mapCierre.put("motivoRetiro", motivo);

        db.collection("Seguimientos").document(seguimiento.getId())
                .update(mapCierre)
                .addOnSuccessListener(unused -> {

                    // El motivo queda escrito en el chat: el adoptante entra, lo lee, y ya no
                    // puede responder porque el seguimiento dejó de estar activo
                    enviarMensajeRetiro(seguimiento, motivo);

                    // Y se le avisa, que de otro modo vería desaparecer la mascota de su
                    // perfil sin ninguna explicación
                    controladorNotificaciones.enviarNotificacionMascotaRetirada(seguimiento, motivo);

                    callback.onComplete(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al cerrar el seguimiento");
                    callback.onError(e);
                });
    }

    // Deja el motivo del retiro como un mensaje más del voluntario. No dispara la
    // notificación de mensaje nuevo: el aviso del retiro se envía aparte y sería duplicarlo.
    private void enviarMensajeRetiro(Seguimiento seguimiento, String motivo) {
        if (seguimiento.getListaMensajes() == null || seguimiento.getListaMensajes().isEmpty()) {
            Log.e("FIREBASE", "El seguimiento no tiene chat donde dejar el motivo del retiro");
            return;
        }

        DatabaseReference mensajeRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(seguimiento.getListaMensajes())
                .push();

        String nombreMascota = seguimiento.getNombreMascota() != null
                ? seguimiento.getNombreMascota()
                : "la mascota";

        String contenido = "Se ha retirado a " + nombreMascota + " del hogar y este seguimiento queda cerrado.";
        if (motivo != null && !motivo.trim().isEmpty()) {
            contenido += "\n\nMotivo: " + motivo.trim();
        }
        contenido += "\n\nYa no es posible responder en esta conversación.";

        Mensaje mensajeRetiro = new Mensaje(
                mensajeRef.getKey(),
                seguimiento.getNombreVoluntario(),
                seguimiento.getIdVoluntario(),
                contenido,
                seguimiento.getNombreAdoptante(),
                seguimiento.getIdAdoptante(),
                System.currentTimeMillis());

        mensajeRef.setValue(mensajeRetiro)
                .addOnFailureListener(e ->
                        Log.e("FIREBASE", "Error al dejar el motivo del retiro en el chat"));
    }

    private void cerrarAdopcionCompletada(Seguimiento seguimiento, Callback<Void> callback) {
        db.collection("Adopciones")
                .whereEqualTo("adoptante", seguimiento.getIdAdoptante())
                .whereEqualTo("mascotaAdopcion", seguimiento.getIdMascota())
                .whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Task<Void>> cierres = new ArrayList<>();

                    for (DocumentSnapshot documento : querySnapshot.getDocuments()) {
                        cierres.add(documento.getReference()
                                .update("estado", EstadosCuentas.RECHAZADA.toString()));

                        // El contrato firmado acredita una adopción que ya no existe, así que
                        // se anula. Si no, quedarían dos contratos firmados para la misma
                        // mascota cuando otra persona la adopte.
                        Task<Void> anulacion = anularContratoAdopcion(documento.getString("contratoAdopcion"));
                        if (anulacion != null) {
                            cierres.add(anulacion);
                        }
                    }

                    // Se espera a que todas terminen. Si alguna falla no se puede dar el retiro
                    // por bueno: el adoptante conservaría la adopción y su contrato firmado.
                    Tasks.whenAll(cierres)
                            .addOnSuccessListener(unused -> callback.onComplete(null))
                            .addOnFailureListener(e -> {
                                Log.e("FIREBASE", "Error al cerrar la adopción completada");
                                callback.onError(new Exception(ERROR_CIERRE_ADOPCION, e));
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al consultar la adopción completada");
                    callback.onError(new Exception(ERROR_CIERRE_ADOPCION, e));
                });
    }

    // Marca el contrato como anulado. No se borra: es el respaldo documental de que la
    // adopción existió. Devuelve la tarea para poder esperarla, o null si no hay contrato.
    private Task<Void> anularContratoAdopcion(String idContrato) {
        if (idContrato == null || idContrato.isEmpty()) {
            Log.e("FIREBASE", "La adopción no tiene un contrato asociado que anular");
            return null;
        }

        return db.collection("ContratosAdopciones").document(idContrato)
                .update("estado", EstadosCuentas.ANULADO.toString());
    }

    // ============================================================
    // Plan de recordatorios del seguimiento
    // ============================================================

    // Un recordatorio del plan: la fecha en la que toca y el texto que verá el voluntario
    public static class RecordatorioSeguimiento {
        private final String fecha;
        private final String titulo;
        private final String cuerpo;

        public RecordatorioSeguimiento(String fecha, String titulo, String cuerpo) {
            this.fecha = fecha;
            this.titulo = titulo;
            this.cuerpo = cuerpo;
        }

        public String getFecha() {
            return fecha;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getCuerpo() {
            return cuerpo;
        }
    }

    // Programa el plan completo de la adopción: la primera semana día por día, después los
    // ocho controles mensuales y por último los trimestrales. Se llama al asignar el
    // voluntario, que es quien los recibe.
    public void programarPlanSeguimiento(Seguimiento seguimiento, Callback<Void> callback) {
        if (seguimiento.getFechaInicioSeguimiento() == null
                || seguimiento.getFechaInicioSeguimiento().isEmpty()) {
            seguimiento.setFechaInicioSeguimiento(controladorUtilidades.obtenerFechaActual());
        }

        if (seguimiento.getHoraSeguimiento() == null || seguimiento.getHoraSeguimiento().isEmpty()) {
            seguimiento.setHoraSeguimiento(HORA_RECORDATORIO_POR_DEFECTO);
        }

        // Se reprograma desde cero: primero se borra lo que hubiera para no duplicar
        controladorNotificaciones.eliminarNotificacionesSeguimiento(
                seguimiento.getId(),
                seguimiento.getIdVoluntario(),
                () -> {
                    ArrayList<RecordatorioSeguimiento> recordatorios = calcularRecordatoriosIniciales(seguimiento);
                    recordatorios.addAll(calcularRecordatoriosPeriodicos(seguimiento));
                    programarRecordatorios(seguimiento, recordatorios);

                    Map<String, Object> mapPlan = new HashMap<>();
                    mapPlan.put("fechaInicioSeguimiento", seguimiento.getFechaInicioSeguimiento());
                    mapPlan.put("horaSeguimiento", seguimiento.getHoraSeguimiento());
                    mapPlan.put("faseSeguimiento", EstadosCuentas.SEGUIMIENTO_DIARIO.toString());
                    mapPlan.put("recordatoriosProgramados", true);

                    db.collection("Seguimientos").document(seguimiento.getId())
                            .update(mapPlan)
                            .addOnSuccessListener(unused -> {
                                seguimiento.setFaseSeguimiento(EstadosCuentas.SEGUIMIENTO_DIARIO.toString());
                                seguimiento.setRecordatoriosProgramados(true);

                                if (callback != null) {
                                    callback.onComplete(null);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FIREBASE", "Error al guardar el plan de seguimiento");
                                if (callback != null) {
                                    callback.onError(e);
                                }
                            });
                });
    }

    // Libera al adoptante: se cancelan los recordatorios y el chat queda solo de lectura.
    public void finalizarSeguimiento(Seguimiento seguimiento, Callback<Void> callback) {
        Map<String, Object> mapCierre = new HashMap<>();
        mapCierre.put("estado", EstadosCuentas.COMPLETADA.toString());
        mapCierre.put("faseSeguimiento", EstadosCuentas.SEGUIMIENTO_FINALIZADO.toString());

        db.collection("Seguimientos").document(seguimiento.getId())
                .update(mapCierre)
                .addOnSuccessListener(unused -> {
                    controladorNotificaciones.eliminarNotificacionesSeguimiento(
                            seguimiento.getId(), seguimiento.getIdVoluntario());

                    enviarMensajeCierreSeguimiento(seguimiento);
                    controladorNotificaciones.enviarNotificacionSeguimientoFinalizado(seguimiento);

                    callback.onComplete(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Error al finalizar el seguimiento");
                    callback.onError(e);
                });
    }

    // Primera semana un control por día, y después ocho controles mensuales
    public ArrayList<RecordatorioSeguimiento> calcularRecordatoriosIniciales(Seguimiento seguimiento) {
        ArrayList<RecordatorioSeguimiento> recordatorios = new ArrayList<>();

        String inicio = seguimiento.getFechaInicioSeguimiento();
        if (inicio == null || inicio.isEmpty()) {
            return recordatorios;
        }

        String mascota = seguimiento.getNombreMascota() != null ? seguimiento.getNombreMascota() : "la mascota";
        String adoptante = seguimiento.getNombreAdoptante() != null ? seguimiento.getNombreAdoptante() : "el adoptante";

        // La primera semana no se salta ningún día: es la ventana en la que se decide si la
        // mascota se queda en el hogar o hay que retirarla
        for (int dia = 1; dia <= DIAS_PRIMERA_SEMANA; dia++) {
            recordatorios.add(new RecordatorioSeguimiento(
                    controladorUtilidades.sumarDiasAFecha(inicio, dia),
                    "Seguimiento diario de " + mascota,
                    "Día " + dia + " de " + DIAS_PRIMERA_SEMANA + " en el hogar de " + adoptante + ". "
                            + "Converse con el adoptante y verifique cómo va la adaptación."));
        }

        // Los controles mensuales se cuentan desde que termina la primera semana
        String finPrimeraSemana = controladorUtilidades.sumarDiasAFecha(inicio, DIAS_PRIMERA_SEMANA);

        for (int mes = 1; mes <= MESES_FASE_MENSUAL; mes++) {
            String cuerpo = "Control del mes " + mes + " de " + MESES_FASE_MENSUAL + " con " + adoptante + ".";

            if (mes == MESES_FASE_MENSUAL) {
                cuerpo += " Es el último control mensual: a partir de ahora los controles son "
                        + "cada " + MESES_FASE_PERIODICA + " meses, o puede liberar al adoptante.";
            }

            recordatorios.add(new RecordatorioSeguimiento(
                    controladorUtilidades.sumarMesesAFecha(finPrimeraSemana, mes),
                    "Seguimiento mensual de " + mascota,
                    cuerpo));
        }

        return recordatorios;
    }

    // Controles cada tres meses una vez pasada la fase mensual
    public ArrayList<RecordatorioSeguimiento> calcularRecordatoriosPeriodicos(Seguimiento seguimiento) {
        ArrayList<RecordatorioSeguimiento> recordatorios = new ArrayList<>();

        String inicio = seguimiento.getFechaInicioSeguimiento();
        if (inicio == null || inicio.isEmpty()) {
            return recordatorios;
        }

        String mascota = seguimiento.getNombreMascota() != null ? seguimiento.getNombreMascota() : "la mascota";
        String adoptante = seguimiento.getNombreAdoptante() != null ? seguimiento.getNombreAdoptante() : "el adoptante";

        // Se arranca donde terminó la fase mensual
        String finFaseMensual = controladorUtilidades.sumarMesesAFecha(
                controladorUtilidades.sumarDiasAFecha(inicio, DIAS_PRIMERA_SEMANA),
                MESES_FASE_MENSUAL);

        for (int control = 1; control <= CONTROLES_FASE_PERIODICA; control++) {
            recordatorios.add(new RecordatorioSeguimiento(
                    controladorUtilidades.sumarMesesAFecha(finFaseMensual,
                            MESES_FASE_PERIODICA * control),
                    "Seguimiento de " + mascota,
                    "Control trimestral con " + adoptante + " (control " + control + " de "
                            + CONTROLES_FASE_PERIODICA + "). "
                            + "Si la mascota y el adoptante están bien, puede finalizar el seguimiento."));
        }

        return recordatorios;
    }

    // La fase no se guarda al día en Firestore: se deduce de la fecha de inicio, así nunca
    // queda desfasada por no haber nadie que la actualice cuando cambia.
    public String describirFaseSeguimiento(Seguimiento seguimiento) {
        if (EstadosCuentas.SEGUIMIENTO_FINALIZADO.toString().equalsIgnoreCase(seguimiento.getFaseSeguimiento())) {
            return "Seguimiento finalizado";
        }

        String inicio = seguimiento.getFechaInicioSeguimiento();
        if (inicio == null || inicio.isEmpty()) {
            return "Seguimiento sin programar";
        }

        String finPrimeraSemana = controladorUtilidades.sumarDiasAFecha(inicio, DIAS_PRIMERA_SEMANA);
        if (controladorUtilidades.esFechaFutura(finPrimeraSemana)) {
            return "Seguimiento diario";
        }

        String finFaseMensual = controladorUtilidades.sumarMesesAFecha(finPrimeraSemana, MESES_FASE_MENSUAL);
        if (controladorUtilidades.esFechaFutura(finFaseMensual)) {
            return "Seguimiento mensual";
        }

        return "Seguimiento cada " + MESES_FASE_PERIODICA + " meses";
    }

    // Primera fecha del plan que todavía no pasó, para mostrarla en el chat del voluntario
    public String obtenerProximoSeguimiento(Seguimiento seguimiento) {
        ArrayList<RecordatorioSeguimiento> recordatorios = calcularRecordatoriosIniciales(seguimiento);
        recordatorios.addAll(calcularRecordatoriosPeriodicos(seguimiento));

        for (RecordatorioSeguimiento recordatorio : recordatorios) {
            if (recordatorio.getFecha() != null && controladorUtilidades.esFechaFutura(recordatorio.getFecha())) {
                return recordatorio.getFecha();
            }
        }

        return null;
    }

    // Las fechas que ya pasaron no se programan: el servidor las dispararía de inmediato
    private void programarRecordatorios(Seguimiento seguimiento, ArrayList<RecordatorioSeguimiento> recordatorios) {
        String hora = seguimiento.getHoraSeguimiento() != null && !seguimiento.getHoraSeguimiento().isEmpty()
                ? seguimiento.getHoraSeguimiento()
                : HORA_RECORDATORIO_POR_DEFECTO;

        for (RecordatorioSeguimiento recordatorio : recordatorios) {
            if (recordatorio.getFecha() == null || !controladorUtilidades.esFechaFutura(recordatorio.getFecha())) {
                continue;
            }

            controladorNotificaciones.programarRecordatorioSeguimiento(
                    seguimiento.getIdVoluntario(),
                    seguimiento.getId(),
                    recordatorio.getFecha(),
                    hora,
                    recordatorio.getTitulo(),
                    recordatorio.getCuerpo());
        }
    }

    // Deja constancia del cierre en la conversación, igual que se hace con el retiro
    private void enviarMensajeCierreSeguimiento(Seguimiento seguimiento) {
        if (seguimiento.getListaMensajes() == null || seguimiento.getListaMensajes().isEmpty()) {
            Log.e("FIREBASE", "El seguimiento no tiene chat donde anunciar el cierre");
            return;
        }

        DatabaseReference mensajeRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(seguimiento.getListaMensajes())
                .push();

        String nombreMascota = seguimiento.getNombreMascota() != null
                ? seguimiento.getNombreMascota()
                : "la mascota";

        String contenido = "El seguimiento de " + nombreMascota + " ha finalizado. "
                + "Gracias por acompañar todo el proceso.\n\n"
                + "Esta conversación queda como constancia y ya no admite mensajes nuevos.";

        Mensaje mensajeCierre = new Mensaje(
                mensajeRef.getKey(),
                seguimiento.getNombreVoluntario(),
                seguimiento.getIdVoluntario(),
                contenido,
                seguimiento.getNombreAdoptante(),
                seguimiento.getIdAdoptante(),
                System.currentTimeMillis());

        mensajeRef.setValue(mensajeCierre)
                .addOnFailureListener(e ->
                        Log.e("FIREBASE", "Error al anunciar el cierre del seguimiento en el chat"));
    }

    // Los campos del plan se leen igual en las tres listas de seguimientos
    private void leerPlanSeguimiento(DocumentSnapshot documentSnapshot, Seguimiento seguimiento) {
        seguimiento.setFechaInicioSeguimiento(documentSnapshot.getString("fechaInicioSeguimiento"));
        seguimiento.setFaseSeguimiento(documentSnapshot.getString("faseSeguimiento"));
        seguimiento.setHoraSeguimiento(documentSnapshot.getString("horaSeguimiento"));

        // Los seguimientos creados antes del plan no traen este campo
        Boolean programados = documentSnapshot.getBoolean("recordatoriosProgramados");
        seguimiento.setRecordatoriosProgramados(programados != null && programados);
    }

    // Escucha el estado del seguimiento mientras el chat está abierto. Si el voluntario
    // retira la mascota en ese momento, la pantalla se entera sin tener que volver a entrar.
    public ListenerRegistration escucharEstadoSeguimiento(String idSeguimiento, Callback<String> callback) {
        if (idSeguimiento == null || idSeguimiento.isEmpty()) {
            return null;
        }

        return db.collection("Seguimientos").document(idSeguimiento)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        callback.onComplete(documentSnapshot.getString("estado"));
                    }
                });
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
                        leerPlanSeguimiento(documentSnapshot, seguimiento);

                        // Solo se listan los seguimientos del adoptante que ya tienen un voluntario
                        // asignado, porque sin voluntario no hay con quién conversar. Los
                        // cerrados también se listan: el adoptante entra a leer el motivo del
                        // retiro o el aviso de cierre, aunque ya no pueda responder.
                        boolean estadoVisible = seguimiento.getEstado().equalsIgnoreCase(EstadosCuentas.ACTIVO.toString())
                                || seguimiento.getEstado().equalsIgnoreCase(EstadosCuentas.RECHAZADA.toString())
                                || seguimiento.getEstado().equalsIgnoreCase(EstadosCuentas.COMPLETADA.toString());

                        if (seguimiento.getIdAdoptante().equalsIgnoreCase(idAdoptante) &&
                                !seguimiento.getIdVoluntario().isEmpty() &&
                                estadoVisible) {
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

                            // El plan de recordatorios necesita los nombres y la fecha de inicio
                            seguimiento.setIdAdoptante(documentSnapshot.getString("idAdoptante"));
                            seguimiento.setNombreAdoptante(documentSnapshot.getString("nombreAdoptante"));
                            seguimiento.setIdMascota(documentSnapshot.getString("idMascota"));
                            seguimiento.setNombreMascota(documentSnapshot.getString("nombreMascota"));
                            seguimiento.setListaMensajes(documentSnapshot.getString("listaMensajes"));
                            leerPlanSeguimiento(documentSnapshot, seguimiento);

                            Map<String, Object> mapSeguimiento = new HashMap<>();
                            mapSeguimiento.put("idVoluntario", seguimiento.getIdVoluntario());
                            mapSeguimiento.put("nombreVoluntario", seguimiento.getNombreVoluntario());

                            db.collection("Seguimientos").document(idSeguimiento).update(mapSeguimiento)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Seguimiento asignado correctamente.", Toast.LENGTH_SHORT).show();

                                        // Ya hay a quién avisar, así que se programan los recordatorios
                                        programarPlanSeguimiento(seguimiento, null);

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

                            // El plan de recordatorios necesita los nombres y la fecha de inicio
                            seguimiento.setIdAdoptante(documentSnapshot.getString("idAdoptante"));
                            seguimiento.setNombreAdoptante(documentSnapshot.getString("nombreAdoptante"));
                            seguimiento.setIdMascota(documentSnapshot.getString("idMascota"));
                            seguimiento.setNombreMascota(documentSnapshot.getString("nombreMascota"));
                            seguimiento.setListaMensajes(documentSnapshot.getString("listaMensajes"));
                            leerPlanSeguimiento(documentSnapshot, seguimiento);

                            Map<String, Object> mapSeguimiento = new HashMap<>();
                            mapSeguimiento.put("idVoluntario", seguimiento.getIdVoluntario());
                            mapSeguimiento.put("nombreVoluntario", seguimiento.getNombreVoluntario());


                            db.collection("Seguimientos").document(idSeguimiento).update(mapSeguimiento)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Seguimiento reasignado correctamente.", Toast.LENGTH_SHORT).show();

                                        // Los recordatorios del voluntario anterior ya no le
                                        // corresponden: se cancelan y el plan se rehace para el nuevo
                                        controladorNotificaciones.eliminarNotificacionesSeguimiento(
                                                idSeguimiento,
                                                idVoluntarioOriginal,
                                                () -> programarPlanSeguimiento(seguimiento, null));

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
