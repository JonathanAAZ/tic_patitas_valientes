package com.example.tic_pv.Controlador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.BottomNavigationMenu;
import com.example.tic_pv.Vista.EditarMascotaActivity;
import com.example.tic_pv.Vista.Fragments.ListaMascotasAdopcionFragment;
import com.example.tic_pv.Vista.VerMascotasActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControladorMascota {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference("FotosMascotas");
    private EstadosCuentas estadoMascota;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private EstadosCuentas estadoObj;

    public void obtenerMascota (String idMascota, CallbackMascota<Mascota> callbackMascota) {
        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(document.getId());
                    mascota.setNombreMascota(document.getString("nombre"));
                    mascota.setFotoMascota(document.getString("fotoMascota"));
                    mascota.setEdadMascota(document.getString("edad"));
                    mascota.setEspecieMascota(document.getString("especie"));
                    mascota.setColorMascota(document.getString("color"));
                    mascota.setSexoMascota(document.getString("sexo"));
                    mascota.setRazaMascota(document.getString("raza"));
                    mascota.setCaracterMascota(document.getString("caracter"));
                    mascota.setFechaEsterilizacion(document.getString("fechaEsterilizacion"));
                    mascota.setMascotaAdoptada(Boolean.TRUE.equals(document.getBoolean("mascotaAdoptada")));
                    mascota.setMascotaVacunada(Boolean.TRUE.equals(document.getBoolean("vacunacionMascota")));
                    mascota.setMascotaDesparasitada(Boolean.TRUE.equals(document.getBoolean("desparasitacionMascota")));
                    mascota.setMascotaEsterilizada(Boolean.TRUE.equals(document.getBoolean("esterilizacionMascota")));
                    mascota.setEstadoMascota(document.getString("estado"));

                    callbackMascota.onComplete(mascota);
                }
            }
        });
    }



    public void crearMascotaConFoto(Uri uriFotoMascota, Mascota mascota, Context context, LinearLayout barraProgreso) {
        barraProgreso.setVisibility(View.VISIBLE);
        if (uriFotoMascota != null) {
            // Crear una referencia única para la imagen
            String nombreArchivo = "foto_mascota_" + System.currentTimeMillis() + ".jpg";
            StorageReference archivoReferencia = storageReference.child(nombreArchivo);

            // Subir la foto a Firebase
            archivoReferencia.putFile(uriFotoMascota)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de descarga de la foto
                        archivoReferencia.getDownloadUrl().addOnSuccessListener(uri -> {
                            String urlDescarga = uri.toString();
                            mascota.setFotoMascota(urlDescarga); // Establece la URL de la foto en el objeto Mascota
                            guardarMascotaEnFirebase(mascota, context, barraProgreso); // Guardar la mascota en la base de datos
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al subir la foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            barraProgreso.setVisibility(View.GONE);
            Toast.makeText(context, "Por favor seleccione o tome una foto de la mascota", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarMascotaEnFirebase(Mascota mascota, Context context, LinearLayout barraProgreso) {
        Map<String, Object> mapMascotas = new HashMap<>();
        mapMascotas.put("estado", mascota.getEstadoMascota());
        mapMascotas.put("fotoMascota", mascota.getFotoMascota()); // Asegúrate de que esto tenga la URL
        mapMascotas.put("nombre", mascota.getNombreMascota());
        mapMascotas.put("especie", mascota.getEspecieMascota());
        mapMascotas.put("raza", mascota.getRazaMascota());
        mapMascotas.put("edad", mascota.getEdadMascota());
        mapMascotas.put("sexo", mascota.getSexoMascota());
        mapMascotas.put("color", mascota.getColorMascota());
        mapMascotas.put("caracter", mascota.getCaracterMascota());
        mapMascotas.put("domicilioMascota", mascota.getDomicilio());
        mapMascotas.put("fechaEsterilizacion", mascota.getFechaEsterilizacion());
        mapMascotas.put("mascotaAdoptada", mascota.isMascotaAdoptada());
        mapMascotas.put("vacunacionMascota", mascota.isMascotaVacunada());
        mapMascotas.put("esterilizacionMascota", mascota.isMascotaEsterilizada());
        mapMascotas.put("desparasitacionMascota", mascota.isMascotaDesparasitada());

        db.collection("Mascotas").add(mapMascotas)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "La mascota ha sido creada correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, VerMascotasActivity.class); // Especifica la nueva actividad
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent); // Abre la nueva actividad
                    barraProgreso.setVisibility(View.GONE);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al crear la mascota: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void obtenerListaMascotasAdopcion(Context context, Callback<List<Mascota>> callback) {
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
                        mascota.setMascotaVacunada(document.getBoolean("vacunacionMascota"));
                        mascota.setMascotaDesparasitada(document.getBoolean("desparasitacionMascota"));
                        mascota.setMascotaEsterilizada(document.getBoolean("esterilizacionMascota"));
                        mascota.setEstadoMascota(document.getString("estado"));

                        if (!document.getBoolean("mascotaAdoptada")) {
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

    public void obtenerListaMascotas(Callback<List<Mascota>> callback) {
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
                        mascota.setMascotaAdoptada(Boolean.TRUE.equals(document.getBoolean("mascotaAdoptada")));

                        listaMascotas.add(mascota);
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

    // Callback para obtener mascota
    public interface CallbackMascota<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public void actualizarEstadoMascota (Context context, String id, FragmentManager fragmentManager) {
        db.collection("Mascotas").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String estadoActual = document.getString("estado");
                        String nuevoEstado = estadoActual.equals(estadoMascota.ACTIVO.toString()) ? estadoMascota.INACTIVO.toString() : estadoMascota.ACTIVO.toString();

                        Map<String, Object> mapActualizaciones = new HashMap<>();
                        mapActualizaciones.put("estado", nuevoEstado);

                        db.collection("Mascotas").document(id).update(mapActualizaciones).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                controladorUtilidades.reemplazarFragments(R.id.fLFragmentCatalogoMascotas, fragmentManager, new ListaMascotasAdopcionFragment());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error al actualizar estado en la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Error al obtener el estado actual de la mascota", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void editarInformacionMascota (Uri fotoMascota, Mascota mascota, Context context, LinearLayout barraProgreso) {
        barraProgreso.setVisibility(View.VISIBLE);
        if (fotoMascota != null) {
            String fotoActual = mascota.getFotoMascota();
            StorageReference archivoReferencia = storageReference.child(fotoActual);

            //Subir la nueva foto sobreescribiendo la anterior
            archivoReferencia.putFile(fotoMascota)
                    .addOnSuccessListener(taskSnapshot -> {
                        archivoReferencia.getDownloadUrl().addOnSuccessListener(uri -> {
                            String nuevaUrl = uri.toString();
                            mascota.setFotoMascota(nuevaUrl);
                            editarMascotaFirebase(mascota, context, barraProgreso);
                        });
                    });
        } else {
//            Toast.makeText(context, "No se ha seleccionado una nueva foto para la mascota", Toast.LENGTH_SHORT).show();
            editarMascotaFirebase(mascota, context, barraProgreso);
        }
    }

    private void editarMascotaFirebase (Mascota mascota, Context context, LinearLayout barra) {
        Map <String, Object> mapActualizacionesMascota = new HashMap<>();
//        mapActualizacionesMascota.put("estado", mascota.getEstadoMascota());
        mapActualizacionesMascota.put("fotoMascota", mascota.getFotoMascota()); // Asegúrate de que esto tenga la URL
        mapActualizacionesMascota.put("nombre", mascota.getNombreMascota());
        mapActualizacionesMascota.put("especie", mascota.getEspecieMascota());
        mapActualizacionesMascota.put("raza", mascota.getRazaMascota());
        mapActualizacionesMascota.put("edad", mascota.getEdadMascota());
        mapActualizacionesMascota.put("sexo", mascota.getSexoMascota());
        mapActualizacionesMascota.put("color", mascota.getColorMascota());
        mapActualizacionesMascota.put("caracter", mascota.getCaracterMascota());
        mapActualizacionesMascota.put("domicilioMascota", mascota.getDomicilio());
        mapActualizacionesMascota.put("fechaEsterilizacion", mascota.getFechaEsterilizacion());
        mapActualizacionesMascota.put("mascotaAdoptada", mascota.isMascotaAdoptada());
//        mapActualizacionesMascota.put("vacunacionMascota", mascota.isMascotaVacunada());
        mapActualizacionesMascota.put("esterilizacionMascota", mascota.isMascotaEsterilizada());
//        mapActualizacionesMascota.put("desparasitacionMascota", mascota.isMascotaDesparasitada());

        db.collection("Mascotas").document(mascota.getId()).update(mapActualizacionesMascota).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "La información ha sido guardada correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, VerMascotasActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
                barra.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                barra.setVisibility(View.GONE);
                Toast.makeText(context, "Hubo un error al guardar la información en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filtrarMascotas (String filtro, String criterio, ArrayList <Mascota> mascotasOriginal, ArrayList <Mascota> mascotas, TextView textNoExiste) {
        int longitud = criterio.length();
        List<Mascota> coleccionMascotas = new ArrayList<>();
        //Valores por defecto de los spinners;

        Map<String, Predicate<Mascota>> filtros = new HashMap<>();
        filtros.put("Nombre", i -> i.getNombreMascota().toLowerCase().contains(criterio.toLowerCase()));
        filtros.put("Activos", i -> i.getEstadoMascota().equals(EstadosCuentas.ACTIVO.toString()));
        filtros.put("Inactivos", i -> i.getEstadoMascota().equals(EstadosCuentas.INACTIVO.toString()));
        filtros.put("Cachorros", i -> i.getEdadMascota().equalsIgnoreCase(EstadosCuentas.CACHORRO.toString()));
        filtros.put("Adultos", i -> i.getEdadMascota().equalsIgnoreCase(EstadosCuentas.ADULTO.toString()));
        filtros.put("Caninos", i -> i.getEspecieMascota().equalsIgnoreCase(EstadosCuentas.CANINO.toString()));
        filtros.put("Felinos", i -> i.getEspecieMascota().equalsIgnoreCase(EstadosCuentas.FELINO.toString()));
        filtros.put("Machos", i -> i.getSexoMascota().equalsIgnoreCase(EstadosCuentas.MACHO.toString()));
        filtros.put("Hembras", i -> i.getSexoMascota().equalsIgnoreCase(EstadosCuentas.HEMBRA.toString()));
        filtros.put("Vacunados", Mascota::isMascotaVacunada);
        filtros.put("No vacunados", i -> !i.isMascotaVacunada());
        filtros.put("Esterilizados", Mascota::isMascotaEsterilizada);
        filtros.put("No esterilizados", i -> !i.isMascotaEsterilizada());
        filtros.put("Desparasitados", Mascota::isMascotaDesparasitada);
        filtros.put("No desparasitados", i -> !i.isMascotaDesparasitada());

        if (longitud == 0) {
            mascotas.clear();
            mascotas.addAll(mascotasOriginal);
        } else {

            // Manejo del caso "default"
            // Si el filtro no coincide con ninguno, usar un predicado que no filtre nada (todos los elementos pasan)
            Predicate<Mascota> predicado = filtros.getOrDefault(filtro, i -> true);

            // Aplicar el filtro y actualizar la lista de mascotas
            assert predicado != null;
            coleccionMascotas = mascotasOriginal.stream()
                    .filter(predicado)
                    .collect(Collectors.toList());


            //Actualizar la lista de mascotas filtradas
            mascotas.clear();
            mascotas.addAll(coleccionMascotas);
        }

        //Mostrar/ocultar el texto "No se encontraron resultados"
        if (mascotas.isEmpty()) {
            textNoExiste.setVisibility(View.VISIBLE);
        } else {
            textNoExiste.setVisibility(View.GONE);
        }
    }

    public void obtenerListaMisMascotas(String idUsuario, Callback<List<Mascota>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Adopciones").
                whereEqualTo("estado", EstadosCuentas.COMPLETADA.toString()).
                whereEqualTo("adoptante", idUsuario).get().
                addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                            String idMascota = documentSnapshot.getString("mascotaAdopcion");

                            assert idMascota != null;
                            db.collection("Mascotas").document(idMascota)
                                    .get()
                                    .addOnSuccessListener(documentSnapshots -> {
                                        ArrayList<Mascota> listaMascotas = new ArrayList<>();
                                        Mascota mascota = new Mascota();
                                        mascota.setId(documentSnapshots.getId());
                                        mascota.setNombreMascota(documentSnapshots.getString("nombre"));
                                        mascota.setFotoMascota(documentSnapshots.getString("fotoMascota"));
                                        mascota.setEdadMascota(documentSnapshots.getString("edad"));
                                        mascota.setEspecieMascota(documentSnapshots.getString("especie"));
                                        mascota.setSexoMascota(documentSnapshots.getString("sexo"));
                                        mascota.setMascotaVacunada(Boolean.TRUE.equals(documentSnapshots.getBoolean("vacunacionMascota")));
                                        mascota.setMascotaDesparasitada(Boolean.TRUE.equals(documentSnapshots.getBoolean("desparasitacionMascota")));
                                        mascota.setMascotaEsterilizada(Boolean.TRUE.equals(documentSnapshots.getBoolean("esterilizacionMascota")));
                                        mascota.setEstadoMascota(documentSnapshots.getString("estado"));

                                        if (Boolean.TRUE.equals(documentSnapshots.getBoolean("mascotaAdoptada"))) {
                                            listaMascotas.add(mascota);
                                        }

                                        // Notificar que los datos están listos
                                        callback.onComplete(listaMascotas);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirebaseError", "Error al obtener mascotas", e);
                                        callback.onError(e);
                                    });
                        }
                    } else {
                        ArrayList<Mascota> listaVacia = new ArrayList<>();
                        callback.onComplete(listaVacia);
                        Log.e("FIREBASE", "No se encontraron documentos");
                    }
                        });

    }

}
