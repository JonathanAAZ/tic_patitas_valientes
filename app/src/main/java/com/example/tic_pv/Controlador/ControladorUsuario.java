package com.example.tic_pv.Controlador;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Domicilio;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.SubirFotoPerfil;
import com.example.tic_pv.Vista.VerInformacionPerfilActivity;
import com.example.tic_pv.Vista.VerUsuariosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ControladorUsuario {

    private String idCuenta, idUsuario, idDomicilio;
    ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EstadosCuentas estado;
    private ArrayList<String> listaUsuarios = new ArrayList<>();
    private HashMap<String, String> mapUsuarios = new HashMap<>();
    private Usuario usuario = new Usuario();

    public ControladorUsuario() {
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void actualizarEstadoUsuario(Context context) {
        // Primero, obtenemos el estado actual de la cuenta
        db.collection("Cuentas").document(idCuenta).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String estadoActual = document.getString("estado");
                        String nuevoEstado = estadoActual.equals("ACTIVO") ? "INACTIVO" : "ACTIVO";

                        Map<String, Object> mapActualizaciones = new HashMap<>();
                        mapActualizaciones.put("estado", nuevoEstado);

                        // Actualizamos la cuenta
                        db.collection("Cuentas").document(idCuenta).update(mapActualizaciones).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Actualizamos los usuarios y domicilios asociados
                                actualizarUsuariosYDomicilios(context, nuevoEstado);

                                Intent i = new Intent(context, VerUsuariosActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("id", idCuenta);
                                context.startActivity(i);
//                                if (context instanceof Activity) {
//                                    ((Activity) context).finish();
//                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error al actualizar el estado en la BD", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Error al obtener el estado actual", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUsuariosYDomicilios(Context context, final String nuevoEstado) {
        db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot documento : querySnapshot) {
                            String idUsuario = documento.getId();
                            String idDomicilio = documento.getString("domicilioUsuario");

                            Map<String, Object> mapActualizaciones = new HashMap<>();
                            mapActualizaciones.put("estado", nuevoEstado);

                            // Actualizar usuario
                            db.collection("Usuarios").document(idUsuario).update(mapActualizaciones)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error al actualizar usuario en la BD", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Actualizar domicilio si existe
                            if (idDomicilio != null && !idDomicilio.isEmpty()) {
                                db.collection("Domicilios").document(idDomicilio).update(mapActualizaciones)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Error al actualizar domicilio en la BD", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Error al obtener usuarios asociados", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void filtrarInformación(String filtro, ArrayList<Usuario> usuariosOriginal, ArrayList<Usuario> usuarios,
                                   ArrayList<CuentaUsuario> cuentasOriginal, ArrayList<CuentaUsuario> cuentas) {
//        int longitud = filtro.length();
//        Map<String, CuentaUsuario> cuentasMap = new HashMap<>();
//
//        if (longitud == 0) {
//            usuarios.clear();
//            usuarios.addAll(usuariosOriginal);
//
//            cuentas.clear();
//            cuentas.addAll(cuentasOriginal);
//        } else {
//            List<Usuario> coleccionUsuarios = usuarios.stream().filter(i -> i.getNombre().toLowerCase().contains(filtro.toLowerCase())).collect(Collectors.toList());
//
//            if (coleccionUsuarios.isEmpty()) {
//                coleccionUsuarios = usuariosOriginal.stream()
//                        .filter(i -> i.getCedula().contains(filtro.toLowerCase()))
//                        .collect(Collectors.toList());
//            }
//
//            usuarios.clear();
//            usuarios.addAll(coleccionUsuarios);
//            coleccionUsuarios.clear();
//
//            sincronizarCuentasUsuarios(cuentasMap, cuentasOriginal, cuentas, usuarios);
//
//        }
        int longitud = filtro.length();
        Map<String, CuentaUsuario> cuentasMap = new HashMap<>();

        if (longitud == 0) {
            // Si el filtro está vacío, restaurar las listas originales
            usuarios.clear();
            usuarios.addAll(usuariosOriginal);

            cuentas.clear();
            cuentas.addAll(cuentasOriginal);
        } else {
            // Filtrar usuarios por nombre
            List<Usuario> coleccionUsuarios = usuariosOriginal.stream()
                    .filter(i -> i.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());

            // Si no se encontraron coincidencias por nombre, filtrar por cédula
            if (coleccionUsuarios.isEmpty()) {
                coleccionUsuarios = usuariosOriginal.stream()
                        .filter(i -> i.getCedula().contains(filtro))
                        .collect(Collectors.toList());
            }

            // Actualizar la lista de usuarios filtrados
            usuarios.clear();
            usuarios.addAll(coleccionUsuarios);

            // Sincronizar las cuentas con los usuarios filtrados
            sincronizarCuentasUsuarios(cuentasMap, cuentasOriginal, cuentas, usuarios);
        }
    }

    public void sincronizarCuentasUsuarios(Map<String, CuentaUsuario> map, ArrayList<CuentaUsuario> cuentasOriginal, ArrayList<CuentaUsuario> cuentas, ArrayList<Usuario> usuarios) {

        for (CuentaUsuario cuenta : cuentasOriginal) {
            map.put(cuenta.getIdCuenta(), cuenta);
        }

        // Crear una lista de cuentas sincronizadas
        List<CuentaUsuario> coleccionCuentas = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (map.containsKey(usuario.getCuenta())) {
                coleccionCuentas.add(map.get(usuario.getCuenta()));
            }
        }

        // Actualizar la lista de cuentas con las cuentas sincronizadas
        cuentas.clear();
        cuentas.addAll(coleccionCuentas);
    }

    public void verificarCorreoUsado(final String correo, final OnCorreoVerificadoListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Cuentas").whereEqualTo("correo", correo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        listener.onCorreoVerificado(false); // Correo no está en uso
                    } else {
                        listener.onCorreoVerificado(true); // Correo ya está usado
                    }
                } else {
                    System.out.println("Error de base de datos");
                }
            }
        });
    }

    public interface OnCorreoVerificadoListener {
        void onCorreoVerificado(boolean enUso);
    }

    public void cambiarClaveUsuario(FirebaseUser usuario, String claveNueva, Context context) {
        usuario.updatePassword(claveNueva).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    actualizarClaveDB(usuario.getUid(), claveNueva, context);
                } else {
                    Toast.makeText(context, "Error al cambiar clave", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void actualizarClaveDB(String idCuenta, String clave, Context context) {

        Map<String, Object> map = new HashMap<>();
        map.put("clave", clave);

        db.collection("Cuentas").document(idCuenta).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(context, VerInformacionPerfilActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calcularEdad(String fechaNacimiento) {
        // Definir el formato de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Parsear la fecha de nacimiento
            LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formatter);

            // Obtener la fecha actual
            LocalDate fechaActual = LocalDate.now();

            // Calcular el periodo entre la fecha de nacimiento y la fecha actual
            Period periodo = Period.between(fechaNac, fechaActual);

            // Devolver la cantidad de años como la edad
            return periodo.getYears();

        } catch (DateTimeParseException e) {
            // Manejar excepción en caso de formato de fecha inválido

//            System.out.println("El formato de la fecha de nacimiento es inválido. Por favor, use el formato dd/MM/yyyy.");
            return -1; // Devolver -1 para indicar un error en el formato
        }
    }

    public void obtenerTokenRegistro(TokenCallBack callBack) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "ERROR AL OBTENER EL TOKEN", task.getException());
                        callBack.tokenRecibido(null); // Enviar null en caso de error
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM", "Token del usuario: " + token);
                    callBack.tokenRecibido(token); // Retornar el token a través del callback
                });
    }

    public interface TokenCallBack {
        void tokenRecibido(String token);
    }

    public void actualizarTokenInicioSesion(String id) {

        obtenerTokenRegistro(new TokenCallBack() {
            @Override
            public void tokenRecibido(String token) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("dispositivo", token);
                db.collection("Cuentas").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TOKEN", "TOKEN ACTUALIZADO");
                        db.collection("NotificacionesProgramadas")
                                .whereEqualTo("idUsuario", idUsuario)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (querySnapshot.isEmpty()) {
                                        Log.d("TOKEN_UPDATE", "No hay documentos con ese usuario");
                                        return;
                                    }
                                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                        doc.getReference()
                                                .update("token", token)
                                                .addOnSuccessListener(aVoid ->
                                                        Log.d("TOKEN_UPDATE", "Token actualizado"))
                                                .addOnFailureListener(e ->
                                                        Log.e("TOKEN_UPDATE", "Error al actualizar token: " + e.getMessage()));
                                    }
                                })
                                .addOnFailureListener(error ->
                                        Log.e("TOKEN_UPDATE", "Error consultando Firestore: " + error.getMessage()));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TOKEN", "ERROR AL ACTUALIZAR EL TOKEN");
                    }
                });
            }
        });
    }

    // Callback genérico
    public interface Callback<T> {
        void onComplete(T result);

        void onError(Exception e);
    }

    // Callback para cuentas
    public interface CallbackCuentas<T> {
        void onComplete(T result);

        void onError(Exception e);
    }

    // Obtener lista de voluntarios
    public void obtenerListaUsuarios(Callback<ArrayList<Usuario>> voluntariosCallback) {

        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Usuario> listaVoluntarios = new ArrayList<>();
                    Usuario usuario;
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        usuario = new Usuario();
                        usuario.setNombre(documentSnapshot.getString("nombre"));
                        usuario.setCedula(documentSnapshot.getString("cedula"));
                        usuario.setCuenta(documentSnapshot.getString("cuentaUsuario"));
                        listaVoluntarios.add(usuario);
                    }
                    voluntariosCallback.onComplete(listaVoluntarios);
                } else {
                    Log.e("FIREBASE", "Error al obtener voluntarios");
                }
            }
        });
    }

    public void obtenerCuentas(CallbackCuentas<Map<String, CuentaUsuario>> mapCallbackCuentas) {

        db.collection("Cuentas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Map<String, CuentaUsuario> cuentasMap = new HashMap<>();

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        CuentaUsuario cuenta = new CuentaUsuario();
                        cuenta.setIdCuenta(documentSnapshot.getId());
                        cuenta.setFotoPerfil(documentSnapshot.getString("fotoPerfil"));
                        cuenta.setRol(documentSnapshot.getString("rol"));
                        cuenta.setEstadoCuenta(documentSnapshot.getString("estado"));
                        cuentasMap.put(cuenta.getIdCuenta(), cuenta);
                    }

                    mapCallbackCuentas.onComplete(cuentasMap);

                } else {
                    Log.e("FIREBASE", "Error al obtener las cuentas");
                }
            }
        });
    }

    public void cargarVoluntariosSpinnerSegui(Spinner spVoluntarios, TextView tv) {
        db.collection("Usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                listaUsuarios.clear();
                listaUsuarios.add("--- Seleccione un voluntario ---");

                for (DocumentSnapshot doc : task.getResult()) {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(doc.getString("nombre"));
                    usuario.setCuenta(doc.getString("cuentaUsuario"));
                    usuario.setDomicilioUsuario(doc.getString("domicilioUsuario"));

                    Task<DocumentSnapshot> cuentaTask = db.collection("Cuentas")
                            .document(usuario.getCuenta())
                            .get()
                            .addOnSuccessListener(cuentaSnapshot -> {
                                if (cuentaSnapshot.exists() && "Voluntario".equals(cuentaSnapshot.getString("rol"))) {
                                    listaUsuarios.add(usuario.getNombre());
                                    mapUsuarios.put(usuario.getNombre(), usuario.getCuenta());
                                }
                            });

                    tasks.add(cuentaTask);
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener(innerTask -> {
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            spVoluntarios.getContext(),
                            R.layout.spinner_layout,
                            listaUsuarios
                    );
                    adaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                    spVoluntarios.setAdapter(adaptador);

                    spVoluntarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tv.setError(null);
                            String nombreSeleccionado = listaUsuarios.get(position);
                            String cuenta = mapUsuarios.get(nombreSeleccionado);
                            usuario.setNombre(nombreSeleccionado);
                            usuario.setCuenta(cuenta);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                });
            } else {
                Log.e("FIREBASE", "Error al cargar usuarios", task.getException());
            }
        });
    }

    public Usuario obtenerUsuarioSeleccionado() {
        return this.getUsuario();
    }

}
