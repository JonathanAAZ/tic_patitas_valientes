package com.example.tic_pv.Vista;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditarFotoPerfilActivity extends AppCompatActivity {

    private ProgressBar barraProgreso;
    private ImageView vistaImagenSubirFoto;
    private FirebaseAuth auth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser authUsuario;
    private StorageReference storageReference;
    private String idCuenta, rol;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_foto_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        idCuenta = i.getStringExtra("id");
        rol = i.getStringExtra("rol");

        LinearLayout btnSeleccionarFotoPerfil = findViewById(R.id.lLSeleccionarFotoPerfilEditar);
        Button btnGuardarFotoPerfilEditada = findViewById(R.id.btnGuardarFotoPerfilEditada);
        barraProgreso = findViewById(R.id.barraProgresoEditarFotoPerfil);
        vistaImagenSubirFoto = findViewById(R.id.ivFotoPerfilSubida);

        auth = FirebaseAuth.getInstance();
        authUsuario = auth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("FotosPerfil");

        Uri uri = authUsuario.getPhotoUrl();
//        Picasso.get().load(uri).into(vistaImagenSubirFoto);

        Glide.with(this)
                .load(uri)
                .fitCenter()
                .into(vistaImagenSubirFoto);

        //Alerta de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarFotoPerfilActivity.this);
        builder.setTitle("Mensaje de confirmación");
        builder.setMessage("¿Desea guardar los cambios?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent i = new Intent(EditarFotoPerfilActivity.this, VerInformacionPerfilActivity.class);
////                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.putExtra("id", idCuenta);
//                startActivity(i);
//                finish();
                barraProgreso.setVisibility(View.VISIBLE);
                actualizarFotoUsuarioDB();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Seleccionar foto de perfil
        btnSeleccionarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                abrirSelectorArchivo();
                verificarPermisoGaleria();
            }
        });

        btnGuardarFotoPerfilEditada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uriImage == null) {
                    Toast.makeText(EditarFotoPerfilActivity.this, "No se ha seleccionado foto de perfil", Toast.LENGTH_SHORT).show();
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                    Intent i = new Intent(EditarFotoPerfilActivity.this, VerInformacionPerfilActivity.class);
//                    i.putExtra("id", idCuenta);
//                    startActivity(i);
                    finish();
                } else {
                    AlertDialog dialog = builder.create();
                    dialog.show();
//                    barraProgreso.setVisibility(View.VISIBLE);
//                    actualizarFotoUsuarioDB();
                }

            }
        });

    }

//    private void abrirSelectorArchivo() {
////        Intent intent = new Intent();
////        intent.setType("image/*");
////        intent.setAction(Intent.ACTION_GET_CONTENT);
////        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        seleccionarFotoGaleriaLauncher.launch(intent);
//    }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            vistaImagenSubirFoto.setImageURI(uriImage);
        }
    }

    private void actualizarFotoUsuarioDB() {
        if (uriImage != null) {
            //Guardar imagen con el ID de usuario y la extensión de archivo
            StorageReference referenciaArchivo = storageReference.child(authUsuario.getUid() + "."
                    + obtenerExtensionArchivo(uriImage));

            //Subir la imagen a la Storage
            referenciaArchivo.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    referenciaArchivo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri descargarUri = uri;

                            UserProfileChangeRequest subirFotoPerfil = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(descargarUri).build();
                            Objects.requireNonNull(auth.getCurrentUser()).updateProfile(subirFotoPerfil);
//                            System.out.println("DIRECCION DE LA FOTO DE PERFIL: " + descargarUri.toString());
                            actualizarFotoCuentaBD(descargarUri.toString(), authUsuario.getUid());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditarFotoPerfilActivity.this, "No se agregó la foto de perfil", Toast.LENGTH_SHORT).show();
                }
            });
        }
//        else {
////            Toast.makeText(EditarFotoPerfilActivity.this, "No se seleccionó una foto de perfil", Toast.LENGTH_LONG).show();
//            Intent i = new Intent(EditarFotoPerfilActivity.this, VerInformacionPerfilActivity.class);
////            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.putExtra("id", idCuenta);
//            startActivity(i);
//            finish();
//        }
        barraProgreso.setVisibility(View.GONE);
    }

    private String obtenerExtensionArchivo(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void actualizarFotoCuentaBD(String urlFoto, String idCuenta) {

        Map<String, Object> mapActualizarFoto = new HashMap<>();
        mapActualizarFoto.put("fotoPerfil", urlFoto);

        mFirestore.collection("Cuentas").document(idCuenta).update(mapActualizarFoto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditarFotoPerfilActivity.this, "Información guardada correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditarFotoPerfilActivity.this, BottomNavigationMenu.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("id", R.id.bMPerfil);
                i.putExtra("rol", rol);
                startActivity(i);
//                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarFotoPerfilActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
                System.out.println("ERROR AL SUBIR FOTO: " + e.getMessage());
            }
        });
    }
}