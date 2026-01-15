package com.example.tic_pv.Vista.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Controlador.ControladorDomicilio;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.EditarMascotaActivity;
import com.example.tic_pv.databinding.FragmentIniciarAdopcionBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IniciarAdopcionFragment extends Fragment {

    private FragmentIniciarAdopcionBinding binding;
    private Adopcion adopcion;
    private String idCuenta, idMascota;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private File archivoFotos;
    private Uri videoUri, uriTemporal;
    private MediaController mediaController;
    private boolean reproduciendo = false;
    private boolean fotosCedulaSubidas = false;
    private boolean fotoServiciosSubida = false;
    private boolean videoSubido = false;
    private boolean opcionDomicilioSeleccionado = false;
    private List<Uri> listaUrisFotos = new ArrayList<>(); // Lista para almacenar las URIs de las fotos
    private List<ImageView> listaImageViews = new ArrayList<>(); // Lista de ImageView para mostrar las fotos
    private int posicionSeleccionada = -1; // Índice del ImageView donde se colocará la imagen
    private int posicionTipoDomicilio;
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentIniciarAdopcionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        assert getArguments() != null;
        idCuenta = getArguments().getString("idCuenta");
        idMascota = getArguments().getString("idMascota");

        listaImageViews.add(binding.iVFotoCedulaFrontal);
        listaImageViews.add(binding.iVFotoCedulaReverso);
        listaImageViews.add(binding.iVFotoServicios);

        binding.lLTomarFotoCedReverso.setVisibility(View.INVISIBLE);
        binding.btnEnviarSolicitud.setEnabled(false);
        binding.btnEnviarSolicitud.setBackgroundColor(Color.GRAY);
        binding.btnEnviarSolicitud.setTextColor(Color.DKGRAY);

        //Configurar el Radio Button para que aparezcan los requisitos
        configurarRadioButtons();

        //Listener para el RadioGroup
        binding.rGTipoDomicilio.setOnCheckedChangeListener((group, checkedId) -> {
            opcionDomicilioSeleccionado = checkedId != -1;
            confirmarRequisito(4);
            verificarActivarBoton();
        });

        //Configuraciones para las fotos de los requisitos
        configurarBotonesImagenes(binding.lLTomarFotoCedFrontal, binding.iVFotoCedulaFrontal, 0);
        configurarBotonesImagenes(binding.lLTomarFotoCedReverso, binding.iVFotoCedulaReverso, 1);
        configurarBotonesImagenes(binding.lLTomarFotoServicios, binding.iVFotoServicios, 2);

        //Definir el Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Mensaje de confirmación");
        builder.setMessage("¿Seguro que desea enviar la solicitud de adopción?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iniciarAdopcion();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
        });

        //Configuración de la presentación del vídeo
        configurarVideoView();

        binding.lLGrabarHogarMascota.setOnClickListener(v -> {
            verificarPermisosGrabarVideo();
        });
        
        binding.btnEnviarSolicitud.setOnClickListener(v-> {
            for (Uri uri: listaUrisFotos) {
                Log.d("URIS", "URI: " + uri.toString());
            }
//            iniciarAdopcion();
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        });

        mostrarInformacion();


        return view;
    }

    private void configurarVideoView() {
        //Asegurarse de que el vídeo esté preparado para poder reproducirlo y evitar errores de sincronización
        binding.videoHogarMascota.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                binding.iVReproducirVideo.setVisibility(View.INVISIBLE);
                binding.videoHogarMascota.start();
            }
        });

        //Código para reproducir vídeo al tocar el VideoView
        binding.videoHogarMascota.setOnClickListener(v -> {
            if (videoUri != null) {
                if (reproduciendo) {
                    binding.videoHogarMascota.pause();
                    binding.iVReproducirVideo.setVisibility(View.VISIBLE);
                    binding.iVReproducirVideo.setImageResource(R.drawable.ic_reproducir);
                } else {
                    binding.videoHogarMascota.start();
                    binding.iVReproducirVideo.setVisibility(View.INVISIBLE);
                }
                reproduciendo = !reproduciendo;
            } else {
                Toast.makeText(requireContext(), "No se ha grabado un video", Toast.LENGTH_SHORT).show();
            }
        });

        // Establecer el listener para cuando el video termine
        binding.videoHogarMascota.setOnCompletionListener(mediaPlayer -> {
            reproduciendo = !reproduciendo;
            // Cambiar la imagen a "play" cuando el video termine
            binding.iVReproducirVideo.setVisibility(View.VISIBLE);
            binding.iVReproducirVideo.setImageResource(R.drawable.ic_reproducir);

        });
    }

    private void configurarBotonesImagenes(LinearLayout linearLayout, ImageView imagen, int numero){
        linearLayout.setOnClickListener(v -> { verificarPermisoCamara(numero); });
        imagen.setOnClickListener(v -> { configurarVistaAmpliada(numero); });
    }

    private void configurarVistaAmpliada(int posicion) {

        if (!listaUrisFotos.isEmpty() && posicion < listaUrisFotos.size() && listaUrisFotos.get(posicion) != null) {
            Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_imagen_ampliada);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ZoomageView imagenAmpliada = dialog.findViewById(R.id.iVFotoAmpliada);
            Glide.with(requireContext())
                    .load(listaUrisFotos.get(posicion))
                    .fitCenter()
                    .into(imagenAmpliada);

            dialog.show();
        } else {
            Toast.makeText(requireContext(), "No se ha tomado una foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarRadioButtons() {
        binding.rBOpcionCerramiento.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                binding.rLRequisitosIniciarAdopcion.setVisibility(View.VISIBLE);
                binding.tVErrorCerramiento.setVisibility(View.GONE);
            }
        }));

        binding.rBOpcionNoCerramiento.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                binding.rLRequisitosIniciarAdopcion.setVisibility(View.GONE);
                binding.tVErrorCerramiento.setVisibility(View.VISIBLE);
            }
        }));
    }

    private void mostrarInformacion() {

        configurarChecksRequisitos(binding.lLRequisitoFotoCedula, binding.iVRequisitoFotoCedula);
        configurarChecksRequisitos(binding.lLRequisitoFotoServicios, binding.iVRequisitoFotoServicios);
        configurarChecksRequisitos(binding.lLRequisitoVideoHogarMascota, binding.iVRequisitoVideoHogarMascota);
        configurarChecksRequisitos(binding.lLRequisitoTipoDomicilio, binding.iVRequisitoTipoDomicilio);

        db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        String nombreUsuario = documento.getString("nombre");
                        String cedulaUsuario = documento.getString("cedula");

//                        binding.tvBienvenidaVP.setText("Información personal y de domicilio");
                        binding.tVNombreAdoptante.setText(nombreUsuario);
                        binding.tVCedulaAdoptante.setText(cedulaUsuario);

                    }
                } else {
                    Toast.makeText(getContext(), "No se encontraron documentos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error al obtener los documentos", Toast.LENGTH_LONG).show();
            }
        });

        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {

                    String nombreMascota = documentSnapshot.getString("nombre");
                    String fotoMascota = documentSnapshot.getString("fotoMascota");

                    binding.tVNombreMascota.setText(nombreMascota);
                    Glide.with(requireContext())
                            .load(fotoMascota)
                            .fitCenter()
                            .into(binding.iVFotoMascotaInicioAdopcion);
                }
            }
        });
    }

    private void configurarChecksRequisitos(LinearLayout ll, ImageView iv) {
        ll.setBackgroundResource(R.color.red);
        iv.setImageResource(R.drawable.ic_cerrar);
    }

    private void verificarPermisoCamara(int posicion) {
        posicionSeleccionada = posicion; // Guardar el índice del ImageView seleccionado
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            solicitarPermisosCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        while (listaUrisFotos.size() < 3) {
            listaUrisFotos.add(null);
        }

        // Crear un archivo temporal para guardar la foto
        File archivoFoto = new File(requireContext().getCacheDir(), "requisito_" + System.currentTimeMillis() + ".jpg");
        uriTemporal = FileProvider.getUriForFile(requireContext(), "com.example.tic_pv.fileprovider", archivoFoto);

        // Abrir la cámara
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTemporal);
        seleccionarFotoLauncher.launch(intent);
    }

    // Lanzador para solicitar permisos de cámara
    private final ActivityResultLauncher<String> solicitarPermisosCamaraLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirCamara();
                } else {
                    Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
                }
            }
    );

    // Lanzador para recibir el resultado de la cámara
    private final ActivityResultLauncher<Intent> seleccionarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Si la foto fue tomada correctamente, reemplazar la URI antigua con la nueva
                    if (posicionSeleccionada >= 0 && posicionSeleccionada < listaUrisFotos.size()) {
                        Uri uriAntigua = listaUrisFotos.get(posicionSeleccionada);

                        // Eliminar la imagen antigua solo si hay una nueva
                        if (uriAntigua != null) {
                            eliminarArchivo(uriAntigua);
                        }

                        listaUrisFotos.set(posicionSeleccionada, uriTemporal);

                        // Mostrar la foto en el ImageView correspondiente
                        Glide.with(requireContext())
                                .load(uriTemporal)
                                .fitCenter()
                                .into(listaImageViews.get(posicionSeleccionada));

                        if (posicionSeleccionada == 0) {
                            binding.lLTomarFotoCedReverso.setVisibility(View.VISIBLE);
                        }

                        confirmarRequisito(posicionSeleccionada);
                    }
                } else {
                    // Si la cámara se cancela, la imagen anterior se mantiene
                    uriTemporal = null;
                }
            }
    );

    private void verificarPermisosGrabarVideo() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            grabarVideo();
        } else {
            // Verificar si el usuario denegó los permisos permanentemente
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                // Mostrar un diálogo explicando por qué se necesitan los permisos
//                new AlertDialog.Builder(requireContext())
//                        .setTitle("Permisos requeridos")
//                        .setMessage("Se necesitan permisos de cámara y micrófono para grabar videos.")
//                        .setPositiveButton("Aceptar", (dialog, which) -> {
                // Solicitar los permisos nuevamente
                permisosGrabarLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
//                        })
//                        .setNegativeButton("Cancelar", (dialog, which) -> {
//                            Toast.makeText(requireContext(), "Permisos denegados", Toast.LENGTH_SHORT).show();
//                        })
//                        .show();
            } else {
                // Si no se han denegado permanentemente, solicitarlos normalmente
                permisosGrabarLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
            }
        }
    }

    // Lanzador para solicitar permisos
    private final ActivityResultLauncher<String[]> permisosGrabarLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean permisoCamara = Boolean.TRUE.equals(result.get(Manifest.permission.CAMERA));
                boolean permisoAlmacenamiento = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));

                if (permisoCamara && permisoAlmacenamiento) {
                    grabarVideo();
                }
            }
    );

    // Abrir la cámara para grabar con tiempo limitado de 15 segundos
    private void grabarVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15); // Límite de 15 segundos
        capturarVideoLauncher.launch(intent);
    }

    // Lanzador para capturar video
    private final ActivityResultLauncher<Intent> capturarVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData();
                    binding.videoHogarMascota.setVideoURI(videoUri);
                    confirmarRequisito(3);
                }
            }
    );

    private void confirmarRequisito(int posicion) {
        // Arrays con los elementos que se van a modificar según la posición
        View[] layouts = {
                null, // Índice 0 no se usa
                binding.lLRequisitoFotoCedula,
                binding.lLRequisitoFotoServicios,
                binding.lLRequisitoVideoHogarMascota,
                binding.lLRequisitoTipoDomicilio
        };

        ImageView[] iconos = {
                null,
                binding.iVRequisitoFotoCedula,
                binding.iVRequisitoFotoServicios,
                binding.iVRequisitoVideoHogarMascota,
                binding.iVRequisitoTipoDomicilio
        };

        // Flags de estado
        boolean[] estados = {true, fotosCedulaSubidas, fotoServiciosSubida, videoSubido, opcionDomicilioSeleccionado};

        // Validar posición dentro del rango válido
        if (posicion < 1 || posicion >= layouts.length) return;

        // Aplicar cambios a los elementos correspondientes
        layouts[posicion].setBackgroundResource(R.color.light_green);
        iconos[posicion].setImageResource(R.drawable.ic_visto_bueno);

        //Animación de escala en el ícono
        iconos[posicion].setScaleX(0f);
        iconos[posicion].setScaleY(0f);
        iconos[posicion].animate().scaleX(1f).scaleY(1f).setDuration(300).start();

        // Actualizar la variable de estado correspondiente
        estados[posicion] = true;

        // Actualizar las variables originales según la posición
        fotosCedulaSubidas = estados[1];
        fotoServiciosSubida = estados[2];
        videoSubido = estados[3];
        opcionDomicilioSeleccionado = estados[4];

        // Verificar si se debe activar el botón
        verificarActivarBoton();
    }

    private void verificarActivarBoton() {

        if (fotosCedulaSubidas && fotoServiciosSubida && videoSubido && opcionDomicilioSeleccionado) {
            binding.btnEnviarSolicitud.setEnabled(true);
            binding.btnEnviarSolicitud.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow));
            binding.btnEnviarSolicitud.setTextColor(ContextCompat.getColor(requireContext(), R.color.lila));
        }
    }

    private void eliminarArchivo(Uri uri) {
        File archivo = new File(uri.getPath());
        if (archivo.exists() && archivo.delete()) {
            Log.d("EliminarArchivo", "Archivo eliminado: " + uri.toString());
        } else {
            Log.e("EliminarArchivo", "No se pudo eliminar el archivo: " + uri.toString());
        }
    }

    private void iniciarAdopcion() {

        posicionTipoDomicilio = binding.rBOpcionArrendado.isChecked() ? 0 : 1;

        adopcion = new Adopcion();
        adopcion.setFechaEmision(controladorUtilidades.obtenerFechaActual());
        adopcion.setAdoptante(idCuenta);
        adopcion.setMascotaAdopcion(idMascota);
        adopcion.setTieneCerramiento(true);
        adopcion.setTipoDomicilio(posicionTipoDomicilio);

        validarAdopcionesExistentes(idMascota);

//        Toast.makeText(requireContext(), "FECHA ACTUAL: "+ adopcion.getFechaEmision(), Toast.LENGTH_SHORT).show();
//        controladorAdopcion.iniciarProcesoAdopcion(listaUrisFotos, videoUri, adopcion, binding.lLBarraProgresoIniciarAdopcion, requireContext());

//        Toast.makeText(requireContext(), "Esta es la posicion del domicilio: " + posicionTipoDomicilio, Toast.LENGTH_SHORT).show();

//        binding.lLBarraProgresoIniciarAdopcion.setVisibility(View.VISIBLE);

    }

    // Método para validar que no existan adopciones ya iniciadas
    private void validarAdopcionesExistentes (String mascota) {


        String titulo = "ALERTA";
        String mensaje = "No se puede iniciar la adopción, ya existe una adopción en proceso.";
        String estadoRechazada = EstadosCuentas.RECHAZADA.toString();

        db.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", mascota)  // Filtras en el servidor
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    // Configurar alerta personalizada
                    Dialog dialog = controladorUtilidades.crearAlertaPersonalizada(titulo, mensaje, requireContext());
                    LinearLayout btnAceptar = dialog.findViewById(R.id.lLbtnAceptar);
                    btnAceptar.setVisibility(View.VISIBLE);

                    btnAceptar.setOnClickListener(v -> {
                        dialog.dismiss();
                    });

                    long count = queryDocumentSnapshots.getDocuments()
                            .stream()
                            .filter(doc -> !estadoRechazada.equalsIgnoreCase(doc.getString("estado")))
                            .count();

                    boolean disponible = count == 0;

                    if (disponible) {
                        controladorAdopcion.iniciarProcesoAdopcion(listaUrisFotos, videoUri,
                                adopcion, binding.lLBarraProgresoIniciarAdopcion, requireContext());
                    } else {
                        dialog.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo obtener la lista de solicitudes pendientes: " + e.getMessage());
                });
    }


}