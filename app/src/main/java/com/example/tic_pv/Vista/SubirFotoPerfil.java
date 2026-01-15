package com.example.tic_pv.Vista;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Domicilio;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SubirFotoPerfil extends AppCompatActivity {

    private ProgressBar barraProgreso;
    private ImageView vistaImagenSubirFoto;
    private FirebaseAuth auth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser authUsuario;
    private EstadosCuentas estadoObj;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    private Usuario usuario;
    private CuentaUsuario cuenta;
    private Domicilio domicilio;
//    private String urlSinFotoPerfil = "https://firebasestorage.googleapis.com/v0/b/tic-pv.appspot.com/o/FotosPerfil%2Fsin_foto_perfil.jpg?alt=media&token=64c8a6b1-09e3-401a-a8f9-45664aa0f562";
    private String urlSinFotoPerfil = "sin_foto_perfil.jpg";
    private ControladorUsuario controladorUsuario = new ControladorUsuario();

//    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
//    private ActivityResultLauncher<String> solicitarPermisoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subir_foto_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSeleccionarFotoPerfil = findViewById(R.id.btnSeleccionarFotoPerfil);
        Button btnGuardarCuenta = findViewById(R.id.btnGuardarFotoPerfil);
        barraProgreso = findViewById(R.id.barraProgresoSubirFotoPerfil);
        vistaImagenSubirFoto = findViewById(R.id.vistaFotoPerfilSubida);

        Glide.with(this)
                .load(R.drawable.logo_patitas_valientes)
                .fitCenter()
                .into(vistaImagenSubirFoto);

        AlertDialog.Builder builder = new AlertDialog.Builder(SubirFotoPerfil.this);
        builder.setTitle("Mensaje de confirmación");
        builder.setMessage("¿Está seguro/a de que desea crear una nueva cuenta con la información ingresada?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnGuardarCuenta.setEnabled(false);
                btnSeleccionarFotoPerfil.setEnabled(false);
                barraProgreso.setVisibility(View.VISIBLE);
                crearCuentaUsuario();

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        auth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("FotosPerfil");

        Intent i = getIntent();
        usuario = i.getParcelableExtra("usuario");
        cuenta = i.getParcelableExtra("cuenta");
        domicilio = i.getParcelableExtra("domicilio");

        //Botón para seleccionar foto de perfil
        btnSeleccionarFotoPerfil.setOnClickListener(v -> verificarPermisoGaleria());

        //Botón para crear la cuenta
        btnGuardarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.create();
                dialog.show();
//                barraProgreso.setVisibility(View.VISIBLE);
//                crearCuentaUsuario();
//
//                Intent i = new Intent(SubirFotoPerfil.this, IniciarSesionActivity.class);
//                startActivity(i);
//                finish();

            }
        });
    }

//    private void abrirSelectorArchivo() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        seleccionarFotoGaleriaLauncher.launch(intent);
//    }

    //ANTERIORES METODOS
//    private void verificarPermisosGaleria() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
////                abrirSelectorArchivo();
////            } else {
//            solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
////            }
//        } if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
////            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
////                abrirSelectorArchivo();
////            } else {
//            solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
////            }
//        } else {
////            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
////                abrirSelectorArchivo();
////            } else {
//                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
////            }
//        }
//    }
//
//    private final ActivityResultLauncher <Intent> seleccionarFotoGaleriaLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    uriImage = result.getData().getData();
//                    vistaImagenSubirFoto.setImageURI(uriImage);
//                    System.out.println("URI DE IMAGEN SELECCIONADA: " + uriImage.toString());
//                }
//            }
//    );
//
//    private final ActivityResultLauncher <String> solicitarPermisosGaleriaLauncher = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            isGranted -> {
//                if (isGranted) {
//                    abrirSelectorArchivo();
//                } else {
//                    Toast.makeText(this, "Permiso para acceder a la galería denegado", Toast.LENGTH_SHORT).show();
//                }
//            }
//    );

    //HASTA AQUI LOS METODOS ANTERIORES

    private void verificarPermisoGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
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
        seleccionarFotoGaleriaLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> seleccionarFotoGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    //Obtener la URI de la foto seleccionada
                    uriImage = result.getData().getData();

//                    binding.ivAgregarFotoMascota.setImageURI(uriFotoMascota);
                    //Mostrar la imagen seleccionada en el ImageView
                    Glide.with(this)
                            .load(uriImage)
                            .fitCenter()
                            .into(vistaImagenSubirFoto);
                }
            }
    );

    private final ActivityResultLauncher <String> solicitarPermisosGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirGaleria();
                } else {
                    Toast.makeText(this, "Permiso para acceder a la galería denegado", Toast.LENGTH_LONG).show();
                }
            }
    );

    private void crearCuentaUsuario() {
        String correoString = cuenta.getCorreo();
        String claveString = cuenta.getClave();

        auth.createUserWithEmailAndPassword(correoString, claveString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String idDB = auth.getCurrentUser().getUid();

                    if (uriImage != null) {

                        String url = auth.getCurrentUser().getUid() + "." + obtenerExtensionArchivo(uriImage);
//                        Guardar imagen con el ID de usuario y la extensión de archivo
                        StorageReference referenciaArchivo = storageReference.child(url);

                        referenciaArchivo.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                referenciaArchivo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String descargarUri = uri.toString();
                                        subirFotoPerfil(descargarUri, idDB);

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubirFotoPerfil.this, "No se agregó la foto de perfil", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        StorageReference referenciaArchivo = storageReference.child(urlSinFotoPerfil); // Ruta de la imagen predeterminada

                        referenciaArchivo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String descargarUri = uri.toString();
                                subirFotoPerfil(descargarUri, idDB);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubirFotoPerfil.this, "No se pudo obtener la imagen predeterminada", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    Toast.makeText(SubirFotoPerfil.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("MENSAJE ERROR : " + e.getMessage());
                Toast.makeText(SubirFotoPerfil.this, "No se pudo crear la cuenta", Toast.LENGTH_LONG).show();
            }
        });

        barraProgreso.setVisibility(View.GONE);
    }

//    private boolean esUriAccesible(Uri uri, Context context) {
//        try {
//            ContentResolver resolver = context.getContentResolver();
//            resolver.openInputStream(uri).close();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    private void subirFotoPerfil (String urlFotoPerfil, String id) {
//        UserProfileChangeRequest subirFotoPerfil = new UserProfileChangeRequest.Builder()
//                .setPhotoUri(uri).build();
//        auth.getCurrentUser().updateProfile(subirFotoPerfil);
//        System.out.println("DIRECCION DE LA FOTO DE PERFIL: " + uri.toString());
//        crearCuentaDB(uri.toString(), id);

        UserProfileChangeRequest actualizarFotoPerfil = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(urlFotoPerfil))
                .build();

        Objects.requireNonNull(auth.getCurrentUser()).updateProfile(actualizarFotoPerfil)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Foto de perfil actualizada con éxito: " + urlFotoPerfil);
                    crearCuentaDB(urlFotoPerfil, id);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void crearCuentaDB(String urlFoto, String usuarioID) {

        cuenta.setFotoPerfil(urlFoto);
        cuenta.setIdCuenta(usuarioID);

        Map<String, Object> mapCuentas = new HashMap<>();
//        mapCuentas.put("id", cuenta.getIdCuenta());
        mapCuentas.put("estado", cuenta.getEstadoCuenta());
        mapCuentas.put("fotoPerfil", cuenta.getFotoPerfil());
        mapCuentas.put("correo", cuenta.getCorreo());
        mapCuentas.put("clave", cuenta.getClave());
        mapCuentas.put("rol", cuenta.getRol());
        mapCuentas.put("dispositivo", cuenta.getIdDispositivo());


        mFirestore.collection("Cuentas").document(cuenta.getIdCuenta()).set(mapCuentas).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SubirFotoPerfil.this, "Se creó la cuenta correctamente", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SubirFotoPerfil.this, "No se creó la cuenta en DB", Toast.LENGTH_LONG).show();
                System.out.println("Error crear cuentas en DB" + e);
            }
        });

        crearDomicilioDB(cuenta.getIdCuenta());

    }

    private void crearDomicilioDB (String idCuenta) {
        Map<String, Object> mapDomicilios = new HashMap<>();
        mapDomicilios.put("estado", domicilio.getEstadoDomicilio());
        mapDomicilios.put("pais", domicilio.getPais());
        mapDomicilios.put("provincia", domicilio.getProvincia());
        mapDomicilios.put("canton", domicilio.getCanton());
        mapDomicilios.put("parroquia", domicilio.getParroquia());
        mapDomicilios.put("barrio", domicilio.getBarrio());
        mapDomicilios.put("calles", domicilio.getCalles());

        mFirestore.collection("Domicilios").add(mapDomicilios).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("ID del domicilio: " + documentReference.getId());

//                documentReference.update("id", documentReference.getId());

                usuario.setDomicilioUsuario(documentReference.getId());
                usuario.setCuenta(idCuenta);

                Map<String, Object> mapUsuarios = new HashMap<>();
                mapUsuarios.put("estado", usuario.getEstadoUsuario());
                mapUsuarios.put("nombre", usuario.getNombre());
                mapUsuarios.put("cedula", usuario.getCedula());
                mapUsuarios.put("edad", usuario.getEdad());
                mapUsuarios.put("fechaNac", usuario.getFechaNacimento());
                mapUsuarios.put("estadoCiv", usuario.getEstadoCivil());
                mapUsuarios.put("ocupacion", usuario.getOcupacion());
                mapUsuarios.put("telefono", usuario.getTelefono());
                mapUsuarios.put("ig", usuario.getIg());
                mapUsuarios.put("fb", usuario.getFb());
                mapUsuarios.put("domicilioUsuario", usuario.getDomicilioUsuario());
                mapUsuarios.put("cuentaUsuario", usuario.getCuenta());

                mFirestore.collection("Usuarios").add(mapUsuarios).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        documentReference.update("id", documentReference.getId());

                        //Iniciar la nueva actividad luego de completar el registro
                        Intent i = new Intent(SubirFotoPerfil.this, IniciarSesionActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                        Toast.makeText(SubirFotoPerfil.this, "Se creó el usuario en DB", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubirFotoPerfil.this, "No se creó el usuario en DB", Toast.LENGTH_LONG).show();
                        System.out.println("Error crear usuarios en DB" + e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                usuario.setDomicilioUsuario("No se agregó el domicilio");
                Toast.makeText(SubirFotoPerfil.this, "No se pudo agregar el domicilio a la BD", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obtenerExtensionArchivo(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}