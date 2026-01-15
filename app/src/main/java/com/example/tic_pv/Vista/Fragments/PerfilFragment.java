package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.EditarFotoPerfilActivity;
import com.example.tic_pv.Vista.EditarInformacionPerfilActivity;
import com.example.tic_pv.Vista.VerInformacionPerfilActivity;
import com.example.tic_pv.databinding.FragmentPerfilBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    private ProgressBar barraProgreso;
    private String nombreUsuario, correoUsuario, cedulaUsuario, fechaNacUsuario, estadoCivUsuario, ocupacionUsuario,
            telefonoUsuario, igUsuario, fbUsuario, paisUsuario, provinciaUsuario, cantonUsuario, parroquiaUsuario,
            barrioUsuario, callesUsuario, edadUsuario;

    private String idCuenta, rol, domicilioID, fotoPerfilID;
    private ImageView fotoPerfil;
    private FirebaseAuth authPerfil;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnEditarDatos, btnEditarDomicilio, btnCambiarClave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.barraProgresoVI.setVisibility(View.VISIBLE);
        binding.tVEditarPerfil.setPaintFlags(binding.tVEditarPerfil.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        assert getArguments() != null;
        idCuenta = getArguments().getString("idCuenta");
        rol = getArguments().getString("rol");

        mostrarInformacionPerfil(idCuenta);

        binding.lLEditarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditarFotoPerfilActivity.class);
                i.putExtra("id", idCuenta);
                i.putExtra("rol", rol);
                i.putExtra("enAdministracion", false);
                startActivity(i);
            }
        });

        binding.tVEditarPerfil.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), EditarInformacionPerfilActivity.class);
            i.putExtra("id", idCuenta);
            i.putExtra("rol", rol);
            i.putExtra("enAdministracion", false);
            startActivity(i);
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void mostrarInformacionPerfil(String idCuenta) {

        binding.barraProgresoVI.setVisibility(View.VISIBLE);

//        db.collection("Cuentas").document(idCuenta).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    correoUsuario = documentSnapshot.getString("correo");
//                    fotoPerfilID = documentSnapshot.getString("fotoPerfil");
//
//                    tvCorreoUsuario.setText(correoUsuario);
//                    Picasso.get().load(fotoPerfilID).into(fotoPerfil);
//                    System.out.println("ESTA ES LA URL DE LA FOTO: " + fotoPerfilID);
//
//                } else {
//                    Toast.makeText(VerInformacionPerfilActivity.this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        db.collection("Cuentas").document(idCuenta).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String correoUsuario = documentSnapshot.getString("correo");
                String rolUsuario = documentSnapshot.getString("rol");
                String fotoPerfilID = documentSnapshot.getString("fotoPerfil");
                binding.tvVerCorreoUsuario.setText(correoUsuario);
                binding.tvVerRolUsuario.setText(rolUsuario);

                // Cargar la imagen con Glide
                Glide.with(this)
                        .load(fotoPerfilID)
                        .fitCenter() // Cambia a fitCenter() si prefieres mantener la relación de aspecto
                        .into(binding.ivVerFotoPerfil);

                System.out.println("ESTA ES LA URL DE LA FOTO: " + fotoPerfilID);
                binding.barraProgresoVI.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "No se encontró el documento", Toast.LENGTH_SHORT).show();
            }
        });


        obtenerUsuario(idCuenta);

        binding.barraProgresoVI.setVisibility(View.GONE);

    }

//    // Método para rotar el bitmap basado en la orientación EXIF
//    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                matrix.postRotate(90);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                matrix.postRotate(180);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                matrix.postRotate(270);
//                break;
//            default:
//                return bitmap; // No rotar si no es necesario
//        }
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

    private void obtenerUsuario(String idCuenta) {
        db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        nombreUsuario = documento.getString("nombre");
                        cedulaUsuario = documento.getString("cedula");
                        fechaNacUsuario = documento.getString("fechaNac");
                        Long edad = documento.getLong("edad");
                        estadoCivUsuario = documento.getString("estadoCiv");
                        ocupacionUsuario = documento.getString("ocupacion");
                        telefonoUsuario = documento.getString("telefono");
                        igUsuario = documento.getString("ig");
                        fbUsuario = documento.getString("fb");
                        domicilioID = documento.getString("domicilioUsuario");

                        edadUsuario = String.valueOf(edad) + " años";

//                        binding.tvBienvenidaVP.setText("Información personal y de domicilio");
                        binding.tvVerNombreUsuario.setText(nombreUsuario);
                        binding.tvVerCedulaUsuario.setText(cedulaUsuario);
                        binding.tvVerFechaNacUsuario.setText(fechaNacUsuario);
                        binding.tvVerEdadUsuario.setText(edadUsuario);
                        binding.tvVerEstadoCivUsuario.setText(estadoCivUsuario);
                        binding.tvVerOcupacionUsuario.setText(ocupacionUsuario);
                        binding.tvVerTelefonoUsuario.setText(telefonoUsuario);
                        binding.tvVerIgUsuario.setText(igUsuario);
                        binding.tvVerFbUsuario.setText(fbUsuario);

                        obtenerDomicilio(domicilioID);

                    }
                } else {
                    Toast.makeText(getContext(), "No se encontraron documentos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error al obtener los documentos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void obtenerDomicilio (String domicilio) {
        db.collection("Domicilios").document(domicilio).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    paisUsuario = documentSnapshot.getString("pais");
                    provinciaUsuario = documentSnapshot.getString("provincia");
                    cantonUsuario = documentSnapshot.getString("canton");
                    parroquiaUsuario = documentSnapshot.getString("parroquia");
                    barrioUsuario = documentSnapshot.getString("barrio");
                    callesUsuario = documentSnapshot.getString("calles");

                    binding.tvVerPais.setText(paisUsuario);
                    binding.tvVerProvincia.setText(provinciaUsuario);
                    binding.tvVerCanton.setText(cantonUsuario);
                    binding.tvVerParroquia.setText(parroquiaUsuario);
                    binding.tvVerBarrio.setText(barrioUsuario);
                    binding.tvVerCalles.setText(callesUsuario);
                } else {
                    Toast.makeText(getContext(), "No se encontró el documento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}