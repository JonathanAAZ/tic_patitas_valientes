package com.example.tic_pv.Controlador;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControladorDomicilio {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idDomicilio, idDomicilioEditar, nombreVoluntarioEditar;
    private ArrayAdapter<String> adaptadorGeneral;
    private ArrayList<String> nombresVoluntarios = new ArrayList<>();
    private HashMap<String, String> mapaDomicilios = new HashMap<>();

    public void cargarVoluntariosSpinner(Spinner spVoluntarios, TextView tv) {

        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    nombresVoluntarios.clear();
                    nombresVoluntarios.add("--- Seleccione un voluntario ---");

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Usuario usuario = new Usuario();
                        usuario.setNombre(documentSnapshot.getString("nombre"));
                        usuario.setCuenta(documentSnapshot.getString("cuentaUsuario"));
                        usuario.setDomicilioUsuario(documentSnapshot.getString("domicilioUsuario"));

                        Task<DocumentSnapshot> cuentaTask = db.collection("Cuentas").document(usuario.getCuenta()).get().addOnSuccessListener(cuentaSnapshot -> {
                                    if (cuentaSnapshot.exists() && "Voluntario".equals(cuentaSnapshot.getString("rol"))) {
                                        nombresVoluntarios.add(usuario.getNombre());
                                        mapaDomicilios.put(usuario.getNombre(), usuario.getDomicilioUsuario());
                                    }
                                });

                        tasks.add(cuentaTask);
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(innerTask -> {
                        ArrayAdapter<String> adaptador = new ArrayAdapter<>(spVoluntarios.getContext(), R.layout.spinner_layout, nombresVoluntarios);
                        adaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                        spVoluntarios.setAdapter(adaptador);

                        spVoluntarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                tv.setError(null);
                                String nombreSeleccionado = nombresVoluntarios.get(position);
                                String domicilio = mapaDomicilios.get(nombreSeleccionado);
                                Log.d("DomicilioSeleccionado", "Domicilio" + domicilio);
                                idDomicilio = domicilio;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    });

                } else {
                    Log.e("FirebaseError", "Error al cargar usuarios", task.getException());
                }
            }
        });
    }

    public String obtenerDomicilioSeleccionado () {
        return this.getIdDomicilio();
    }

    public void cargarSpinnerVoluntariosEditar (Spinner spVoluntarioEditar, TextView tv, OnAdapterReadyListener listener) {
        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    nombresVoluntarios.clear();
                    nombresVoluntarios.add("--- Seleccione un voluntario ---");

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Usuario usuario = new Usuario();
                        usuario.setNombre(documentSnapshot.getString("nombre"));
                        usuario.setCuenta(documentSnapshot.getString("cuentaUsuario"));
                        usuario.setDomicilioUsuario(documentSnapshot.getString("domicilioUsuario"));

                        Task<DocumentSnapshot> cuentaTask = db.collection("Cuentas").document(usuario.getCuenta()).get().addOnSuccessListener(cuentaSnapshot -> {
                            if (cuentaSnapshot.exists() && "Voluntario".equals(cuentaSnapshot.getString("rol"))) {
                                nombresVoluntarios.add(usuario.getNombre());
                                mapaDomicilios.put(usuario.getNombre(), usuario.getDomicilioUsuario());
                            }
                        });

                        tasks.add(cuentaTask);
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(innerTask -> {
                        ArrayAdapter<String> adaptador = new ArrayAdapter<>(spVoluntarioEditar.getContext(), R.layout.spinner_layout, nombresVoluntarios);
                        adaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                        spVoluntarioEditar.setAdapter(adaptador);

                        spVoluntarioEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                tv.setError(null);
                                String nombreSeleccionado = nombresVoluntarios.get(position);
                                String domicilio = mapaDomicilios.get(nombreSeleccionado);
                                Log.d("DomicilioSeleccionado", "Domicilio" + domicilio);
                                idDomicilioEditar = domicilio;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        // Notificar que el adaptador está listo
                        if (listener != null) {
                            listener.onAdapterReady(adaptador);
                        }

                    });

                } else {
                    Log.e("FirebaseError", "Error al cargar usuarios", task.getException());
                }
            }
        });
    }
    public String obtenerDomicilioSeleccionadoEditar () {
        return this.getIdDomicilioEditar();
    }
    public interface OnAdapterReadyListener {
        void onAdapterReady(ArrayAdapter<String> adaptador);
    }
    public void obtenerNombreUsuario(String id, OnNombreUsuarioObtenidoListener listener) {
        db.collection("Usuarios").whereEqualTo("domicilioUsuario", id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String nombre = documentSnapshot.getString("nombre");
                        if (listener != null) {
                            listener.onNombreObtenido(nombre);
                        }
                        return; // Termina el bucle al encontrar el primer resultado
                    }
                } else {
                    if (listener != null) {
                        listener.onNombreObtenido(null); // No se encontró el usuario
                    }
                }
            } else {
                if (listener != null) {
                    listener.onNombreObtenido(null); // Error al consultar Firebase
                }
            }
        });
    }

    public interface OnNombreUsuarioObtenidoListener {
        void onNombreObtenido(String nombre);
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public String getIdDomicilio() {
        return idDomicilio;
    }

    public void setIdDomicilio(String idDomicilio) {
        this.idDomicilio = idDomicilio;
    }

    public String getNombreVoluntarioEditar() {
        return nombreVoluntarioEditar;
    }

    public void setNombreVoluntarioEditar(String nombreVoluntarioEditar) {
        this.nombreVoluntarioEditar = nombreVoluntarioEditar;
    }

    public String getIdDomicilioEditar() {
        return idDomicilioEditar;
    }

    public void setIdDomicilioEditar(String idDomicilioEditar) {
        this.idDomicilioEditar = idDomicilioEditar;
    }

    public ArrayAdapter<String> getAdaptadorGeneral() {
        return adaptadorGeneral;
    }

    public void setAdaptadorGeneral(ArrayAdapter<String> adaptador) {
        this.adaptadorGeneral = adaptador;
    }

    public String configurarStringDomicilio (String pais, String provincia, String canton, String parroquia, String barrio, String calles) {

        return "o\tPaís: " + pais + "\n" + "o\tProvincia: " + provincia + "\n" + "o\tCantón: " + canton + "\n" + "o\tParroquia: " + parroquia + "\n" + "o\tBarrio: " + barrio + "\n" + "o\tCalles: " + calles;
    }

    //    public void cargarVoluntariosSpinner (Spinner spVoluntarios) {
//        ArrayList <String> nombresVoluntarios = new ArrayList<>();
//        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot documentSnapshot: task.getResult()) {
//                        Usuario usuario = new Usuario();
//                        usuario.setNombre(documentSnapshot.getString("nombre"));
//                        usuario.setDomicilioUsuario(documentSnapshot.getString("domicilioUsuario"));
//                        usuario.setCuenta(documentSnapshot.getString("cuentaUsuario"));
//
//                        db.collection("Cuentas").document(usuario.getCuenta()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                if (documentSnapshot.exists()) {
//                                    if (documentSnapshot.getString("rol").equals("Voluntario")) {
//                                        nombresVoluntarios.add(usuario.getNombre());
//                                    }
//                                }
//                            }
//                        });
//                    }
//                    ArrayAdapter <String> adaptador = new ArrayAdapter<>(spVoluntarios.getContext(), R.layout.spinner_layout, nombresVoluntarios);
//                    adaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
//                    spVoluntarios.setAdapter(adaptador);
//                }
//            }
//        });
//    }




}
