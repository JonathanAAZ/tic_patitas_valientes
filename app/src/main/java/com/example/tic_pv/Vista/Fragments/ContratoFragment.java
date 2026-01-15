package com.example.tic_pv.Vista.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorContrato;
import com.example.tic_pv.Controlador.ControladorDomicilio;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.ContratoAdopcion;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.FragmentContratoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ContratoFragment extends Fragment {

    private FragmentContratoBinding binding;
    private String idCuenta, idMascota, idContrato, firmaAdministrador, firmaAdoptante,
            domicilioConcatenado, fotoMascota;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorDomicilio controladorDomicilio = new ControladorDomicilio();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContratoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        assert getArguments() != null;
        idContrato = getArguments().getString("idContrato");
        idCuenta = getArguments().getString("idCuenta");
        idMascota = getArguments().getString("idMascota");

//        Log.d("CONTRATO: ", idContrato);
//        Log.d("CUENTA: ", idCuenta);
//        Log.d("MASCOTA: ", idCuenta);

        llenarInformacionContrato();

        return view;
    }

    private void llenarInformacionContrato() {

        db.collection("ContratoGeneral")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Tomar el primer documento (asumiendo que solo hay uno)
                        DocumentReference docRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        CollectionReference clausulaRef = docRef.collection("Clausulas");

                        // Aquí puedes usar clausulaRef para lo que necesites
                        clausulaRef.get().addOnSuccessListener(clausulasSnapshot -> {
                            for (DocumentSnapshot documentSnapshot : clausulasSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String id = documentSnapshot.getId();
                                    String titulo = documentSnapshot.getString("titulo");
                                    String contenido = documentSnapshot.getString("contenido");
                                    SpannableStringBuilder clausula = null;
                                    String cl = "";

                                    assert titulo != null;
                                    switch (id) {
                                        case "1":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVPrimeraClausulaF);
                                            break;
                                        case "1.1":
                                            clausula = controladorUtilidades.ponerTextoNegrita("", contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVPrimeraClausulaContinuacionF);
                                            break;
                                        case "2":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVSegundaClausulaContinuacionF);
                                            break;
                                        case "3":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVTerceraClausulaContinuacionF);
                                            break;
                                        case "4":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVCuartaClausulaContinuacionF);
                                            break;
                                        case "5":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVQuintaClausulaContinuacionF);
                                            break;
                                        case "6":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVSextaClausulaContinuacionF);
                                            break;
                                        case "7":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVSeptimaClausulaContinuacionF);
                                            break;
                                        case "8":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVOctavaClausulaContinuacionF);
                                            break;
                                        case "9":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVNovenaClausulaContinuacionF);
                                            break;
                                        case "10":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVDecimaClausulaContinuacionF);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        });
                        presentarInformacionAdoptanteMascota();
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void presentarInformacionAdoptanteMascota() {

        db.collection("ContratosAdopciones").document(idContrato).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {


                        // Obtener IDs necesarios para rellenar el contrato
                        idCuenta = documentSnapshot.getString("idAdoptante");
                        idMascota = documentSnapshot.getString("idMascota");

                        // Formatear fecha para presentarla en el contrato
                        String fechaFormateada = controladorUtilidades.convertirFecha(documentSnapshot.getString("fechaContratoAdopcion"));

                        // Obtener las firmas del contrato
                        firmaAdministrador = documentSnapshot.getString("firmaAdministrador");
                        firmaAdoptante = documentSnapshot.getString("firmaAdoptante");

                        binding.tVFechaEmisionContratoF.setText(fechaFormateada);

                        controladorUtilidades.insertarImagenDesdeBDD(firmaAdministrador,
                                binding.iVFirmaAdminF, requireContext());

                        controladorUtilidades.insertarImagenDesdeBDD(firmaAdoptante,
                                binding.iVFirmaAdoptanteF, requireContext());

//                        Glide.with(requireContext())
//                                .load(firmaAdministrador)
//                                .fitCenter()
//                                .placeholder(R.drawable.ic_cargando)
//                                .into(binding.iVFirmaAdminF);
//
//                        Glide.with(requireContext())
//                                .load(firmaAdoptante)
//                                .fitCenter()
//                                .placeholder(R.drawable.ic_cargando)
//                                .into(binding.iVFirmaAdoptanteF);

//                        ControladorContrato.obtenerBitmapAsincrono(firmaAdministrador, bitmap -> {
//                            bitMapFirmaAdministrador = bitmap;
//                        });
                    } else {
                        Log.e("ERROR", "No existe el documento");
                    }
                }
            }
        });

        db.collection("Cuentas").document(idCuenta).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String correo = documentSnapshot.getString("correo");
                        binding.tVCorreoContratoF.setText(correo);
                    } else {
                        Log.e("ERROR", "No existe el documento");
                    }
                }
            }
        });

        db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        String domicilio = documento.getString("domicilioUsuario");
                        String nombreUsuario = documento.getString("nombre");
                        String cedulaUsuario = documento.getString("cedula");
                        String edad = Objects.requireNonNull(documento.getLong("edad")).intValue() + " años";
                        String fechaNac = documento.getString("fechaNac");
                        String estadoCivil = documento.getString("estadoCiv");
                        String ocupacion = documento.getString("ocupacion");
                        String telefono = documento.getString("telefono");
                        String ig = documento.getString("ig");
                        String fb = documento.getString("fb");

//                        binding.tvBienvenidaVP.setText("Información personal y de domicilio");
                        binding.tVNombreAdopcionContratoF.setText(nombreUsuario);
                        binding.tVNumeroCedulaContratoF.setText(cedulaUsuario);
                        binding.tVEdadAdoptanteContratoF.setText(edad);
                        binding.tVFechaNacimientoContratoF.setText(fechaNac);
                        binding.tVEstadoCivilContratoF.setText(estadoCivil);
                        binding.tVOcupacionContratoF.setText(ocupacion);
                        binding.tVTelefonoContratoF.setText(telefono);
                        binding.tVInstaContratoF.setText(ig);
                        binding.tVFBContratoF.setText(fb);

                        binding.tVNombreFirmaAdoptanteF.setText(nombreUsuario);

                        db.collection("Domicilios").document(domicilio).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        String pais = documentSnapshot.getString("pais");
                                        String provincia = documentSnapshot.getString("provincia");
                                        String canton = documentSnapshot.getString("canton");
                                        String parroquia = documentSnapshot.getString("parroquia");
                                        String barrio = documentSnapshot.getString("barrio");
                                        String calles = documentSnapshot.getString("calles");

                                        domicilioConcatenado = controladorDomicilio.configurarStringDomicilio(pais,
                                                provincia, canton, parroquia, barrio, calles);

                                        binding.tVDomicilioContratoF.setText(domicilioConcatenado);

                                    } else {
                                        Log.e("ERROR", "No existe el documento");
                                    }
                                }
                            }
                        });

                    }
                } else {
                    Log.e("ERROR", "No se encontraron documentos");
//                    Toast.makeText(holder.itemView.getContext(), "No se encontraron documentos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ERROR", "Error al obtener documentos");
//                Toast.makeText(holder.itemView.getContext(), "Error al obtener los documentos", Toast.LENGTH_LONG).show();
            }
        });


        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String especie = documentSnapshot.getString("especie");
                        String nombreMascota = documentSnapshot.getString("nombre");
                        String raza = documentSnapshot.getString("raza");
                        String sexo = documentSnapshot.getString("sexo");
                        String fechaEsteri = documentSnapshot.getString("fechaEsterilizacion");
                        String caracter = documentSnapshot.getString("caracter");
                        fotoMascota = documentSnapshot.getString("fotoMascota");

                        Glide.with(requireContext())
                                .load(fotoMascota)
                                .fitCenter()
                                .placeholder(R.drawable.ic_cargando)
                                .into(binding.iVFotoMascotaContratoF);

                        binding.tVEspecieMascotaContratoF.setText(especie);
                        binding.tVNombreMascotaContratoF.setText(nombreMascota);
                        binding.tVRazaMascotaContratoF.setText(raza);
                        binding.tVSexoMascotaContratoF.setText(sexo);
                        binding.tVFechaEsteriMascotaContratoF.setText(fechaEsteri);
                        binding.tVOtrasMascotaContratoF.setText(caracter);
//                        holder.tVMascotaPend.setText(nombreMascota);
                    } else {
                        Log.e("ERROR", "No existe el documento");
                    }
                }
            }
        });
    }
}