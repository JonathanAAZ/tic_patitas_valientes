package com.example.tic_pv.Vista;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageView;
import com.codesgood.views.JustifiedTextView;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Controlador.ControladorContrato;
import com.example.tic_pv.Controlador.ControladorDomicilio;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.ContratoAdopcion;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivityCompletarProcesoAdopcionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletarProcesoAdopcionActivity extends AppCompatActivity {

    private ActivityCompletarProcesoAdopcionBinding binding;
    private String idAdopcion, idContrato, idCuenta, idMascota;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorDomicilio controladorDomicilio = new ControladorDomicilio();
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private ControladorContrato controladorContrato = new ControladorContrato();
    private String fotoMascota, firmaAdministrador, domicilioConcatenado;
    private File archivoFirma, archivoPDF;
    private Uri uriPdfGenerado, uriFotoFirma, videoUri, uriFotoFirmaFinal;
    private Bitmap bitMapFotoMascota, bitMapFirmaAdministrador, bitMapFirmaAdoptante;
    private CropImageView imagenRecortada;
    private boolean reproduciendo = false;
    private Adopcion adopcion = new Adopcion();
    private ContratoAdopcion contrato = new ContratoAdopcion();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String instruccionesVideo = "Para completar el proceso de adopción, grabe un video en el que exprese su compromiso con el bienestar de la mascota (tendrá un límite de 35 segundos). En el video, debe mencionar:\n" +
            "\n" +
            " - Su nombre completo y número de cédula.\n" +
            "\n" +
            " - La fecha en la que está realizando la adopción.\n" +
            "\n" +
            " - El nombre de la mascota adoptada.\n" +
            "\n" +
            " - Su compromiso de responder a los seguimientos que se le realicen.\n" +
            "\n" +
            " - La disposición de esterilizar a la mascota si es necesario.\n" +
            "\n" +
            " - El compromiso de brindarle todos los cuidados, atención y amor que requiere.\n" +
            "\n" +
            " - La responsabilidad de cumplir con su esquema de vacunación.\n" +
            "\n" +
            "El mensaje debe ser claro y audible. Asegúrese de que el video tenga buena iluminación y que su rostro sea visible.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_completar_proceso_adopcion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        idAdopcion = i.getStringExtra("idAdopcion");
        idContrato = i.getStringExtra("idContrato");
        idCuenta = i.getStringExtra("idCuenta");
        idMascota = i.getStringExtra("idMascota");

        binding = ActivityCompletarProcesoAdopcionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mostrar motivo de reenvío de requisitos
        mostrarMotivoReenvioRequisitos();

        //Llenar la información del contrato
        llenarInformacionContrato();

        //Completar las instrucciones del video de compromiso
        binding.tVInstruccionesVideoCompromiso.setText(instruccionesVideo);

        //Configurar botones para cambiar a Contrato o Vídeo de compromiso
        controladorUtilidades.configurarColoresBotones(binding.lLBtnFirmarContrato, binding.lLBtnVideoCompromiso);

        //Configurar el Video View
        configurarVideoView();

        //Botón para cambiar el fragment para firmar el contrato
        binding.lLBtnFirmarContrato.setOnClickListener(v -> {
            binding.fLFirmarContrato.setVisibility(View.VISIBLE);
            binding.fLVideoCompromiso.setVisibility(View.GONE);
            controladorUtilidades.configurarColoresBotones(binding.lLBtnFirmarContrato, binding.lLBtnVideoCompromiso);
        });

        //Botón para cambiar el fragment para grabar el video de compromiso
        binding.lLBtnVideoCompromiso.setOnClickListener(v -> {
            binding.fLFirmarContrato.setVisibility(View.GONE);
            binding.fLVideoCompromiso.setVisibility(View.VISIBLE);
            controladorUtilidades.configurarColoresBotones(binding.lLBtnVideoCompromiso, binding.lLBtnFirmarContrato);
        });

        //Inicializar Dialog para recortar imágenes
        Dialog dialogRecortarImagenes = new Dialog(this);
        dialogRecortarImagenes.setContentView(R.layout.dialog_recortar_imagen);
        Objects.requireNonNull(dialogRecortarImagenes.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Obtener el CropImageView del dialog para asignar la imagen que se va a editar
        imagenRecortada = dialogRecortarImagenes.findViewById(R.id.iVRecortarImagen);
        imagenRecortada.setGuidelines(CropImageView.Guidelines.ON);
        imagenRecortada.setAspectRatio(16, 9); // Relación de aspecto 16:9 para que sea rectangular

        //Botòn para seleccionar la foto de la firma
        binding.lLSubirFotoFirmaContrato.setOnClickListener(v -> {

            LinearLayout btnRecortarImagen = dialogRecortarImagenes.findViewById(R.id.lLRecortarImagen);
            LinearLayout btnSubirImagen = dialogRecortarImagenes.findViewById(R.id.lLSubirFotoRecortar);
            LinearLayout btnTomarFoto = dialogRecortarImagenes.findViewById(R.id.lLTomarFotoRecortar);

            btnRecortarImagen.setOnClickListener(view -> {
                if (imagenRecortada.getImageUri() != null) {
                    bitMapFirmaAdoptante = imagenRecortada.getCroppedImage();

                    //Convertir el Bitmap de la imagen recortada en Uri para subirla al BDD
                    assert bitMapFirmaAdoptante != null;
                    uriFotoFirmaFinal = controladorUtilidades.obtenerUriDeBitmap(bitMapFirmaAdoptante, this);

                    binding.iVFirmaAdoptante.setImageBitmap(bitMapFirmaAdoptante);

                    dialogRecortarImagenes.dismiss();
                } else {
                    Toast.makeText(this, "No se ha subido una imagen.", Toast.LENGTH_SHORT).show();
                }
            });

            btnSubirImagen.setOnClickListener(view -> {
                verificarPermisoGaleria();
            });

            btnTomarFoto.setOnClickListener(view -> {
                verificarPermisoCamara();
            });


            dialogRecortarImagenes.create();
            dialogRecortarImagenes.show();
//            verificarPermisoGaleria();
        });

        //Botón para grabar video de compromiso
        binding.lLGrabarVideoCompromiso.setOnClickListener(v -> {
            verificarPermisosGrabarVideo();
        });

        //Botón para generar el PDF del contrato
        binding.lLBtnGenerarPDF.setOnClickListener(v -> {
            binding.tVTextoBarraProgreso.setText("Generando el PDF del contrato de adopción. Por favor, espere un momento…");
            binding.lLBarraProgresoCompletarAdopcion.setVisibility(View.VISIBLE);
            if (uriFotoFirmaFinal != null) {
                verificarPermisosAlmacenamiento();
            } else {
                binding.lLBarraProgresoCompletarAdopcion.setVisibility(View.GONE);
                Toast.makeText(this, "No ha firmado el contrato de adopción.", Toast.LENGTH_SHORT).show();
            }

        });

        //Botón para completar el proceso de adopción
        binding.lLGuardarInformacion.setOnClickListener(v -> {
            if (uriFotoFirmaFinal == null) {
                Toast.makeText(this, "Por favor, firme el contrato de adopción.", Toast.LENGTH_SHORT).show();
            } else if (videoUri == null) {
                Toast.makeText(this, "Por favor, grabe el vídeo de compromiso.", Toast.LENGTH_SHORT).show();
            } else {
                guardarInformacionAdopcion();
            }
        });

    }

    //Solicitar permisos para tomar fotos
    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            solicitarPermisosCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        // Crear un archivo temporal para guardar la foto
//        archivoFotoMascota = new File(requireActivity().getExternalFilesDir(null), "foto_" + System.currentTimeMillis() + ".jpg");
        archivoFirma = new File(getCacheDir(), "foto_" + System.currentTimeMillis() + ".jpg");

        uriFotoFirma = FileProvider.getUriForFile(this, "com.example.tic_pv.fileprovider", archivoFirma);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoFirma);  // Guardar en archivo
        seleccionarFotoLauncher.launch(intent);
    }

    private final ActivityResultLauncher<String> solicitarPermisosCamaraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            abrirCamara();
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
        }
    });

    private final ActivityResultLauncher<Intent> seleccionarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Mostrar la foto en alta calidad
                    imagenRecortada.setImageUriAsync(uriFotoFirma);
                }
            }
    );

    //Solicitar permisos para acceder a la galería
    private void verificarPermisoGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.ACTION_PICK, MediaStore.ACTION_IMAGE_CAPTURE);
        seleccionarFotoGaleriaLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> seleccionarFotoGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    //Obtener la URI de la foto seleccionada
                    uriFotoFirma = result.getData().getData();
                    if (uriFotoFirma != null) {
                        imagenRecortada.setImageUriAsync(uriFotoFirma);
//                        editarImagen(uriFirmaAdoptante);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> solicitarPermisosGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirGaleria();
                } else {
                    Toast.makeText(this, "Permiso para acceder a la galería denegado", Toast.LENGTH_LONG).show();
                }
            }
    );

    //Solicitar permisos para grabar vídeos
    private void verificarPermisosGrabarVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            grabarVideo();
        } else {
            // Verificar si el usuario denegó los permisos permanentemente
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                permisosGrabarLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
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
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 35); // Límite de 35 segundos
        capturarVideoLauncher.launch(intent);
    }

    // Lanzador para capturar video
    private final ActivityResultLauncher<Intent> capturarVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData();
                    binding.videoCompromisoCompletar.setVisibility(View.VISIBLE);
                    binding.videoCompromisoCompletar.setVideoURI(videoUri);
                }
            }
    );

    private void configurarVideoView() {
        //Asegurarse de que el vídeo esté preparado para poder reproducirlo y evitar errores de sincronización
        binding.videoCompromisoCompletar.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                binding.iVReproducirVideoCompletarProceso.setVisibility(View.INVISIBLE);
                binding.videoCompromisoCompletar.start();
            }
        });

        //Código para reproducir vídeo al tocar el VideoView
        binding.rLVideoCompromisoCompletar.setOnClickListener(v -> {
            if (videoUri != null) {
                if (reproduciendo) {
                    binding.videoCompromisoCompletar.pause();
                    binding.iVReproducirVideoCompletarProceso.setVisibility(View.VISIBLE);
                    binding.iVReproducirVideoCompletarProceso.setImageResource(R.drawable.ic_reproducir);
                } else {
                    binding.videoCompromisoCompletar.start();
                    binding.iVReproducirVideoCompletarProceso.setVisibility(View.INVISIBLE);
                }
                reproduciendo = !reproduciendo;
            } else {
                Toast.makeText(this, "No se ha grabado un video", Toast.LENGTH_SHORT).show();
            }
        });

        // Establecer el listener para cuando el video termine
        binding.videoCompromisoCompletar.setOnCompletionListener(mediaPlayer -> {
            reproduciendo = !reproduciendo;
            // Cambiar la imagen a "play" cuando el video termine
            binding.iVReproducirVideoCompletarProceso.setVisibility(View.VISIBLE);
            binding.iVReproducirVideoCompletarProceso.setImageResource(R.drawable.ic_reproducir);

        });
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
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVPrimeraClausula);
                                            break;
                                        case "1.1":
                                            clausula = controladorUtilidades.ponerTextoNegrita("", contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVPrimeraClausulaContinuacion);
                                            break;
                                        case "2":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVSegundaClausulaContinuacion);
                                            break;
                                        case "3":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVTerceraClausulaContinuacion);
                                            break;
                                        case "4":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVCuartaClausulaContinuacion);
                                            break;
                                        case "5":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVQuintaClausulaContinuacion);
                                            break;
                                        case "6":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVSextaClausulaContinuacion);
                                            break;
                                        case "7":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVSeptimaClausulaContinuacion);
                                            break;
                                        case "8":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVOctavaClausulaContinuacion);
                                            break;
                                        case "9":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVNovenaClausulaContinuacion);
                                            break;
                                        case "10":
                                            clausula = controladorUtilidades.ponerTextoNegrita(titulo, contenido);
                                            controladorUtilidades.colocarTextoEnTextView(clausula, binding.tVDecimaClausulaContinuacion);
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

        Log.d("INFORMACION CONTRATO", "contrato" + idContrato);

        db.collection("ContratosAdopciones").document(idContrato).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        contrato = new ContratoAdopcion();
                        contrato.establecerFechaHoraActual();
                        String fecha = contrato.getFechaContratoAdopcion();
                        String fechaFormateada = controladorUtilidades.convertirFecha(fecha);
                        firmaAdministrador = documentSnapshot.getString("firmaAdministrador");

                        binding.tVFechaEmisionContrato.setText(fechaFormateada);
                        Glide.with(getBaseContext())
                                .load(firmaAdministrador)
                                .fitCenter()
                                .placeholder(R.drawable.ic_cargando)
                                .into(binding.iVFirmaAdmin);

                        ControladorContrato.obtenerBitmapAsincrono(firmaAdministrador, bitmap -> {
                            bitMapFirmaAdministrador = bitmap;
                        });
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
                        binding.tVCorreoContrato.setText(correo);
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
                        binding.tVNombreAdopcionContrato.setText(nombreUsuario);
                        binding.tVNumeroCedulaContrato.setText(cedulaUsuario);
                        binding.tVEdadAdoptanteContrato.setText(edad);
                        binding.tVFechaNacimientoContrato.setText(fechaNac);
                        binding.tVEstadoCivilContrato.setText(estadoCivil);
                        binding.tVOcupacionContrato.setText(ocupacion);
                        binding.tVTelefonoContrato.setText(telefono);
                        binding.tVInstaContrato.setText(ig);
                        binding.tVFBContrato.setText(fb);

                        binding.tVNombreFirmaAdoptante.setText(nombreUsuario);

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

                                        binding.tVDomicilioContrato.setText(domicilioConcatenado);

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

                        Glide.with(getBaseContext())
                                .load(fotoMascota)
                                .fitCenter()
                                .placeholder(R.drawable.ic_cargando)
                                .into(binding.iVFotoMascotaContrato);

                        ControladorContrato.obtenerBitmapAsincrono(fotoMascota, bitmap -> {
                            bitMapFotoMascota = bitmap;
                        });

                        binding.tVEspecieMascotaContrato.setText(especie);
                        binding.tVNombreMascotaContrato.setText(nombreMascota);
                        binding.tVRazaMascotaContrato.setText(raza);
                        binding.tVSexoMascotaContrato.setText(sexo);
                        binding.tVFechaEsteriMascotaContrato.setText(fechaEsteri);
                        binding.tVOtrasMascotaContrato.setText(caracter);
//                        holder.tVMascotaPend.setText(nombreMascota);
                    } else {
                        Log.e("ERROR", "No existe el documento");
                    }
                }
            }
        });
    }

    private final ActivityResultLauncher<String> solicitarPermisosAlmacenamientoLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    guardarPDF();
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void guardarPDF() {
        binding.lLBarraProgresoCompletarAdopcion.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    guardarPDFEnDescargas();
                } else {
                    guardarPDFEnLegacyStorage();
                }

                // Volver al hilo principal para ocultar la barra de progreso y mostrar mensaje
                runOnUiThread(() -> {
                    binding.lLBarraProgresoCompletarAdopcion.setVisibility(View.GONE);
                    Toast.makeText(this, "PDF guardado exitosamente", Toast.LENGTH_LONG).show();
                    controladorContrato.abrirPDFGenerado(uriPdfGenerado, archivoPDF, this);
                });

            } catch (Exception e) {
                Log.e("ERROR: ", "Error al generar PDF " + e.getMessage());
                // Manejo de error en el hilo principal
                runOnUiThread(() -> {
                    binding.lLBarraProgresoCompletarAdopcion.setVisibility(View.GONE);
                    Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void guardarPDFEnLegacyStorage() {
        File directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String nombreArchivo = "ContratoAdopcion_" + idContrato + ".pdf";
        archivoPDF = new File(directorioDescargas, nombreArchivo);

        // Si el archivo ya existe, eliminarlo antes de crear uno nuevo
        if (archivoPDF.exists()) {
            boolean eliminado = archivoPDF.delete();
            if (eliminado) {
                Log.d("PDF", "Archivo eliminado antes de crear uno nuevo: " + archivoPDF.getAbsolutePath());
            } else {
                Log.e("PDF", "No se pudo eliminar el archivo anterior.");
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (OutputStream outputStream = Files.newOutputStream(archivoPDF.toPath())) {
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);

                // Configurar márgenes más grandes (en puntos): izquierda, derecha, superior, inferior
                float margen = 72f;

                Document document = new Document(pdfDocument);
//                document.setMargins(margen, margen, margen, margen);
                rellenarPDF(document, pdfDocument);
//                document.close();
            } catch (Exception e) {
                Log.e("ERROR: ", "Error al generar PDF " + e.getMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void guardarPDFEnDescargas() {
        String nombreArchivo = "ContratoAdopcion_" + idContrato + ".pdf";

        // Eliminar archivo existente en MediaStore antes de crear uno nuevo
        eliminarCacheMediaStore(nombreArchivo);

        // Crear el nuevo PDF en Descargas
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        uriPdfGenerado = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        if (uriPdfGenerado != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uriPdfGenerado)) {
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);

                // Configurar márgenes más grandes (en puntos): izquierda, derecha, superior, inferior
                float margen = 72f;

                Document documento = new Document(pdfDocument);
//                documento.setMargins(margen, margen, margen, margen);
                rellenarPDF(documento, pdfDocument);

//                documento.close();
            } catch (Exception e) {
                Log.e("ERROR: ", "Error al generar PDF " + e.getMessage());
            }
        }
    }


    private void verificarPermisosAlmacenamiento() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Android 9 o menor requiere permisos de almacenamiento
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                solicitarPermisosAlmacenamientoLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                guardarPDF();
            }
        } else {
            // Android 10+ (Scoped Storage)
            guardarPDF();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void eliminarCacheMediaStore(String fileName) {
        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL);
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};

        int deletedRows = getContentResolver().delete(collection, selection, selectionArgs);
        if (deletedRows > 0) {
            Log.d("PDF", "Archivo eliminado de MediaStore antes de crear uno nuevo.");
        } else {
            Log.d("PDF", "No se encontró el archivo en MediaStore, se creará uno nuevo.");
        }
    }

    private void rellenarPDF(Document document, PdfDocument pdfDocument) throws IOException {

        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        // Crear color personalizado (rojo en este caso)
        DeviceRgb colorPersonalizado = new DeviceRgb(31, 78, 121);

        Paragraph titulo = new Paragraph(new Text("CONTRATO DE ADOPCIÓN / MASCOTAS").setFont(boldFont))
                .setFontSize(16) // Tamaño de la fuente
                .setFontColor(colorPersonalizado) // Color del texto
                .setTextAlignment(TextAlignment.CENTER); // Centrar texto
        document.add(titulo);

        // Convertir Bitmap a byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitMapFotoMascota.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Crear la imagen usando ImageDataFactory de iText 7
        ImageData datoImagenMascota = ImageDataFactory.create(byteArray);
        Image fotoMascota = new Image(datoImagenMascota);

        // Establecer el ancho de la imagen
        float anchoFotoMascota = pdfDocument.getDefaultPageSize().getWidth() * 0.25f;  // 25% del ancho de la página
        fotoMascota.setWidth(anchoFotoMascota);
//        image.setAutoScaleHeight(true);  // Mantener la proporción de la altura

        // Centrar la imagen
        Paragraph parrafoFotoMascota = new Paragraph();
        parrafoFotoMascota.add(fotoMascota);
        parrafoFotoMascota.setTextAlignment(TextAlignment.CENTER);  // Centrar la imagen

        // Agregar la imagen al documento
        document.add(parrafoFotoMascota);

        //Agregar Clausula 1
        agregarParrafoClausulasPDF(binding.tVPrimeraClausula.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Titulo de la seccion de información del adoptante
        agregarParrafoBOLD(binding.tVEncSeccionAdoptante.getText().toString() + "\n", document, colorPersonalizado, boldFont);

        //Seccion información adoptante
        Paragraph informacionAdoptanteUno = new Paragraph();

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncNombreAdoptanteContrato.getText().toString(),
                binding.tVNombreAdopcionContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncNumeroCedulaContrato.getText().toString(),
                binding.tVNumeroCedulaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncEdadAdoptanteContrato.getText().toString(),
                binding.tVEdadAdoptanteContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncFechaNacimientoContrato.getText().toString(),
                binding.tVFechaNacimientoContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncEstadoCivilContrato.getText().toString(),
                binding.tVEstadoCivilContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncOcupacionContrato.getText().toString(),
                binding.tVOcupacionContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteUno,
                binding.tVEncDomicilioContrato.getText().toString(),
                "",
                colorPersonalizado,
                boldFont);

        document.add(informacionAdoptanteUno);

        //Agregar párrafo con la información del domicilio aplicando doble tabulación
        agregarParrafoDobleTabulacion(domicilioConcatenado, document, colorPersonalizado);

        //Continuar concatenando strings para terminar el párrafo
        Paragraph informacionAdoptanteDos = new Paragraph();

        agregarLineaBoldYNormal(
                informacionAdoptanteDos,
                binding.tVEncCorreoContrato.getText().toString(),
                binding.tVCorreoContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteDos,
                binding.tVEncInstaContrato.getText().toString(),
                binding.tVInstaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteDos,
                binding.tVEncFBContrato.getText().toString(),
                binding.tVFBContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdoptanteDos,
                binding.tVEncTelefonoContrato.getText().toString(),
                binding.tVTelefonoContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        document.add(informacionAdoptanteDos);

        //Agregar título de la sección del administrador
        agregarParrafoBOLD(binding.tVEncSeccionAdminContrato.getText().toString() + "\n",
                document,
                colorPersonalizado,
                boldFont);

        //Agregar párrafo con la información del administrador
        Paragraph informacionAdmin = new Paragraph();

        agregarLineaBoldYNormal(
                informacionAdmin,
                binding.tVEncNombreAdminContrato.getText().toString(),
                binding.tVNombreAdminContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdmin,
                binding.tVEncCedulaAdminContrato.getText().toString(),
                binding.tVCedulaAdminContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdmin,
                binding.tVEncCorreoAdminContrato.getText().toString(),
                binding.tVCorreoAdminContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(
                informacionAdmin,
                binding.tVEncTelefonoAdminContrato.getText().toString(),
                binding.tVTelefonoAdminContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        document.add(informacionAdmin);

        //Agregar continuación de la primera clausula 1.1
        agregarParrafoClausulasPDF(binding.tVPrimeraClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Reemplazar nombre del adoptante en el texto de la segunda clausula
        String segundaClausulaConReemplazo = controladorUtilidades.reemplazarPalabraString(
                binding.tVSegundaClausulaContinuacion.getText().toString(),
                "[nombre]",
                binding.tVNombreAdopcionContrato.getText().toString());

        //Agregar segunda clausula con el nombre reemplazado
        agregarParrafoClausulasPDF(segundaClausulaConReemplazo,
                document,
                colorPersonalizado,
                boldFont);

        //Párrafo con la información de la mascota
        Paragraph parrafoInformacionMascota = new Paragraph();

        agregarLineaBoldYNormal(parrafoInformacionMascota,
                binding.tVEncEspecieMascotaContrato.getText().toString(),
                binding.tVEspecieMascotaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(parrafoInformacionMascota,
                binding.tVEncNombreMascotaContrato.getText().toString(),
                binding.tVNombreMascotaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(parrafoInformacionMascota,
                binding.tVEncRazaMascotaContrato.getText().toString(),
                binding.tVRazaMascotaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(parrafoInformacionMascota,
                binding.tVEncSexoMascotaContrato.getText().toString(),
                binding.tVSexoMascotaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(parrafoInformacionMascota,
                binding.tVEncFechaEsteriMascotaContrato.getText().toString(),
                binding.tVFechaEsteriMascotaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        agregarLineaBoldYNormal(parrafoInformacionMascota,
                binding.tVEncOtrasMascotaContrato.getText().toString(),
                binding.tVOtrasMascotaContrato.getText().toString() + "\n",
                colorPersonalizado,
                boldFont);

        document.add(parrafoInformacionMascota);

        //Agregar tercera clausula
        agregarParrafoClausulasPDF(binding.tVTerceraClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar cuarta clausula
        agregarParrafoClausulasPDF(binding.tVCuartaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar quinta clausula
        agregarParrafoClausulasPDF(binding.tVQuintaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar sexta clausula
        agregarParrafoClausulasPDF(binding.tVSextaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar séptima clausula
        agregarParrafoClausulasPDF(binding.tVSeptimaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar octava clausula
        agregarParrafoClausulasPDF(binding.tVOctavaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar novena clausula
        agregarParrafoClausulasPDF(binding.tVNovenaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar décima clausula
        agregarParrafoClausulasPDF(binding.tVDecimaClausulaContinuacion.getText().toString(),
                document,
                colorPersonalizado,
                boldFont);

        //Agregar fecha de emisión del contrato
        agregarParrafo("\n" + binding.tVEncFechaEmisionContrato.getText().toString() +
                        binding.tVFechaEmisionContrato.getText().toString() + "\n\n",
                document,
                colorPersonalizado);

        //Agregar las firmas al PDF
        agregarFirmasAlPDF(pdfDocument,
                document,
                bitMapFirmaAdministrador,
                binding.tVNombreFirmaAdmin.getText().toString(),
                bitMapFirmaAdoptante,
                binding.tVNombreFirmaAdoptante.getText().toString(),
                colorPersonalizado);

        // Cerrar el documento
        document.close();
    }

    private void agregarParrafo(String texto, Document documento, DeviceRgb colorTexto) {
        Paragraph parrafo = new Paragraph(texto)
                .setFontSize(12) // Tamaño de la fuente
                .setFontColor(colorTexto) // Color del texto
                .setTextAlignment(TextAlignment.JUSTIFIED); // Centrar texto

        documento.add(parrafo);
    }

    private void agregarParrafoClausulasPDF(String texto, Document documento, DeviceRgb colorTexto, PdfFont fuente) {

        Paragraph parrafo = new Paragraph();

        int segundoDosPuntos = texto.indexOf(":", texto.indexOf(":") + 1);
        if (segundoDosPuntos != -1) {
            String primeraParte = texto.substring(0, segundoDosPuntos + 1); // Incluye el segundo ":"
            String parteNegrita = primeraParte.replace("\n", " ");
            String parteNormal = texto.substring(segundoDosPuntos + 1); // El resto

            // Crear fragmentos de texto
            Text textoNegrita = new Text(parteNegrita).setFont(fuente);
            Text textoNormal = new Text(parteNormal);

            // Añadir al párrafo
            parrafo.add(textoNegrita)
                    .add(textoNormal)
                    .setFontSize(12)
                    .setFontColor(colorTexto)
                    .setTextAlignment(TextAlignment.JUSTIFIED);
        } else {
            parrafo.add(texto)
                    .setFontSize(12)
                    .setFontColor(colorTexto)
                    .setTextAlignment(TextAlignment.JUSTIFIED);
        }

        documento.add(parrafo);
    }

    private void agregarParrafoBOLD(String texto, Document documento, DeviceRgb colorTexto, PdfFont fuente) {
        Paragraph parrafo = new Paragraph(new Text(texto).setFont(fuente))
                .setFontSize(12) // Tamaño de la fuente
                .setFontColor(colorTexto) // Color del texto
                .setTextAlignment(TextAlignment.JUSTIFIED); // Centrar texto

        documento.add(parrafo);
    }

    private void agregarParrafoDobleTabulacion(String texto, Document documento, DeviceRgb colorTexto) {
        Paragraph parrafo = new Paragraph(texto)
                .setMarginLeft(40f)
                .setFontSize(12) // Tamaño de la fuente
                .setFontColor(colorTexto) // Color del texto
                .setTextAlignment(TextAlignment.JUSTIFIED); // Centrar texto

        documento.add(parrafo);
    }

    private void agregarLineaBoldYNormal(Paragraph parrafo, String bold, String normal,
                                         DeviceRgb colorTexto, PdfFont fuente) {

//        String primerTexto = bold.replace("\n", " ");
        String primerTexto = controladorUtilidades.reemplazarPalabraString(bold, "\n", " ");

        parrafo.add(new Text(primerTexto).setFont(fuente))
                .add(normal)
                .setMarginLeft(20f)
                .setFontSize(12) // Tamaño de la fuente
                .setFontColor(colorTexto) // Color del texto
                .setTextAlignment(TextAlignment.JUSTIFIED);

    }

    public void agregarFirmasAlPDF(PdfDocument pdfDocument, Document document,
                                   Bitmap bitmapFirma1, String nombre1,
                                   Bitmap bitmapFirma2, String nombre2, DeviceRgb colorTexto) {
        try {
            // Convertir las imágenes de firmas a bytes
            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
            bitmapFirma1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream1);
            byte[] byteArray1 = byteArrayOutputStream1.toByteArray();

            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            bitmapFirma2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2);
            byte[] byteArray2 = byteArrayOutputStream2.toByteArray();

            // Crear las imágenes con iText
            ImageData datoImagenFirma1 = ImageDataFactory.create(byteArray1);
            Image firma1 = new Image(datoImagenFirma1);

            ImageData datoImagenFirma2 = ImageDataFactory.create(byteArray2);
            Image firma2 = new Image(datoImagenFirma2);

            // Establecer el ancho de las imágenes (25% del ancho de la página, ajustable)
            float anchoFirma = pdfDocument.getDefaultPageSize().getWidth() * 0.25f;
            firma1.setWidth(anchoFirma);
            firma2.setWidth(anchoFirma);

            // Crear una tabla con 2 columnas
            Table table = new Table(2); // 2 columnas
            table.setWidth(pdfDocument.getDefaultPageSize().getWidth() * 0.9f); // 90% del ancho de la página
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            //Configurar color del borde de la tabla
            SolidBorder border = new SolidBorder(new DeviceRgb(255, 255, 255), 1f);

            //Quitar el borde de la table
            table.setBorder(border);

            // Celda para la firma izquierda
            Paragraph parrafoFirma1 = new Paragraph()
                    .add(firma1)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);

            Cell celda1 = new Cell();
            celda1.add(parrafoFirma1);
            celda1.setBorder(border);
            table.addCell(celda1);

            // Celda para la firma derecha
            Paragraph parrafoFirma2 = new Paragraph()
                    .add(firma2)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);

            Cell celda2 = new Cell();
            celda2.add(parrafoFirma2);
            celda2.setBorder(border);
            table.addCell(celda2);

            // Celda para el nombre debajo de la firma izquierda
            Paragraph nombreFirma1 = new Paragraph(nombre1)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(colorTexto);

            Cell celda3 = new Cell();
            celda3.add(nombreFirma1);
            celda3.setBorder(border);
            table.addCell(celda3);

            // Celda para el nombre debajo de la firma derecha
            Paragraph nombreFirma2 = new Paragraph(nombre2)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(colorTexto);

            Cell celda4 = new Cell();
            celda4.add(nombreFirma2);
            celda4.setBorder(border);
            table.addCell(celda4);

            // Agregar la tabla al documento
            document.add(table);

        } catch (Exception e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void guardarInformacionAdopcion() {
        binding.tVTextoBarraProgreso.setText("Enviando información. Esto puede tardar dependiendo de su conexión a Internet. Por favor, espere…");
        adopcion.setId(idAdopcion);

        contrato.setId(idContrato);

        controladorAdopcion.completarAdopcion(uriFotoFirmaFinal, videoUri, adopcion, contrato,
                binding.lLBarraProgresoCompletarAdopcion, this);
    }

    private void mostrarMotivoReenvioRequisitos () {

        controladorAdopcion.obtenerSolicitudAdopcion(idAdopcion, new ControladorAdopcion.Callback<Adopcion>() {
            @Override
            public void onComplete(Adopcion result) {
                String motivo = result.getObservaciones();

                if (motivo.isEmpty()) {
                    binding.rLMotivoReenvio.setVisibility(View.GONE);
                } else {
                    binding.rLMotivoReenvio.setVisibility(View.VISIBLE);
                    binding.tVTextoMotivo.setText(motivo);
                }

            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al presentar el motivo del reenvío de requisitos.");
            }
        });
    }
}