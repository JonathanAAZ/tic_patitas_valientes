package com.example.tic_pv.Vista.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.BottomNavigationMenu;
import com.example.tic_pv.databinding.FragmentAgregarFotoMascotaBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgregarFotoMascotaFragment extends Fragment {

    private FragmentAgregarFotoMascotaBinding binding;
    private StorageReference storageReference;
    private ControladorMascota controladorMascota = new ControladorMascota();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private Uri uriFotoMascota;
    private File archivoFotoMascota;
    private Mascota mascota;
    private boolean procesando;
    private static final int REQUEST_CAMARA_PERMISSION = 1001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference("FotosMascotas");

//        CODIGO PARA MOSTRAR IMAGEN DE ERROR Y DE CARGANDO PARA FUTURAS FUNCIONES
//        Glide.with(this)
//                .load(R.drawable.nombre_de_tu_imagen)
//                .placeholder(R.drawable.imagen_cargando) // Imagen mientras carga
//                .error(R.drawable.imagen_error) // Imagen en caso de error
//                .into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAgregarFotoMascotaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        assert getArguments() != null;
        mascota = getArguments().getParcelable("mascotaTemporal");

        Glide.with(this)
                .load(R.drawable.logo_patitas_valientes)
                .fitCenter()// Ajustar la imagen al ImageView
                .into(binding.ivAgregarFotoMascota);

        binding.lLTomarFotoMascota.setOnClickListener(v -> verificarPermisoCamara());

        binding.lLSubirFotoMascota.setOnClickListener(v -> verificarPermisoGaleria());

        binding.btnSiguienteFotoMasc.setOnClickListener(v -> {
            //Verificar si ya está en proceso
            if (procesando) {
                return; //Salir si ya está procesando
            }

            //Marcar como "Procesando"
            procesando = true;

            //Deshabilitar el botón inmediantamente
//            v.setEnabled(false);
            crearMascota();
        });

        return view;
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            solicitarPermisosCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        // Crear un archivo temporal para guardar la foto
//        archivoFotoMascota = new File(requireActivity().getExternalFilesDir(null), "foto_" + System.currentTimeMillis() + ".jpg");
        archivoFotoMascota = new File(requireActivity().getCacheDir(), "foto_" + System.currentTimeMillis() + ".jpg");

        uriFotoMascota = FileProvider.getUriForFile(requireContext(), "com.example.tic_pv.fileprovider", archivoFotoMascota);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoMascota);  // Guardar en archivo
        System.out.println("DATOS DE MASCOTA" + mascota);
        seleccionarFotoLauncher.launch(intent);
    }

    private final ActivityResultLauncher<String> solicitarPermisosCamaraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            abrirCamara();
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
        }
    });

    private final ActivityResultLauncher<Intent> seleccionarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Mostrar la foto en alta calidad
//                    binding.ivAgregarFotoMascota.setImageURI(photoUri);

                    //Limpiar la imagen anterior
                    binding.ivAgregarFotoMascota.setImageDrawable(null);
//                    binding.ivAgregarFotoMascota.setImageURI(uriFotoMascota);

                    Glide.with(requireContext())
                            .load(uriFotoMascota)
                            .fitCenter()  // Ajustar la imagen al ImageView
                            .into(binding.ivAgregarFotoMascota);
                }
            }
    );

    private void verificarPermisoGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seleccionarFotoGaleriaLauncher.launch(intent);
    }

    private final ActivityResultLauncher <Intent> seleccionarFotoGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    //Obtener la URI de la foto seleccionada
                    uriFotoMascota = result.getData().getData();

//                    binding.ivAgregarFotoMascota.setImageURI(uriFotoMascota);
                    //Mostrar la imagen seleccionada en el ImageView
                    Glide.with(requireContext())
                            .load(uriFotoMascota)
                            .placeholder(R.drawable.ic_huella_mascota)
                            .fitCenter()
                            .into(binding.ivAgregarFotoMascota);
                }
            }
    );

    private final ActivityResultLauncher <String> solicitarPermisosGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirGaleria();
                } else {
                    Toast.makeText(requireContext(), "Permiso para acceder a la galería denegado", Toast.LENGTH_LONG).show();
                }
            }
    );

    private void crearMascota () {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        if (uriFotoMascota == null) {
            builder.setTitle("Alerta");
            builder.setMessage("Debe agregar una foto para la mascota");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    procesando = false;
                }
            });
        } else {
            builder.setTitle("Mensaje de confirmación");
            builder.setMessage("¿Desea guardar la información proporcionada?");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    binding.lLBarraProgresoAgregarFotoMascota.setVisibility(View.VISIBLE);
                    controladorMascota.crearMascotaConFoto(uriFotoMascota, mascota, requireContext(), binding.lLBarraProgresoAgregarFotoMascota);
//                    binding.barraProgresoAgregarFotoMascota.setVisibility(View.GONE);
                    procesando = false;
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    binding.lLBarraProgresoAgregarFotoMascota.setVisibility(View.GONE);
                    procesando = false;
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

//    private void subirFotoMascotaFirebase () {
//        if (uriFotoMascota != null) {
//            //Mostrar indicador de carga
//
//
//            //Crear una referencia única para la imagen
//            String nombreArchivo = "foto_mascota_" + System.currentTimeMillis() + ".jpg";
//            StorageReference archivoReferencia = storageReference.child(nombreArchivo);
//
//            //Subir la foto a Firebase
//            archivoReferencia.putFile(uriFotoMascota)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        //Obtener la URL de descarga
//                        archivoReferencia.getDownloadUrl().addOnSuccessListener(uri -> {
//                            String urlDescarga = uri.toString();
//                            Toast.makeText(requireContext(), "Foto subida exitosamente", Toast.LENGTH_SHORT).show();
//                            mascota.setFotoMascota(urlDescarga);
//                        });
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(requireContext(), "Error al subir la foto", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(requireContext(), "Por favor seleccione o tome una foto de la mascota", Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
