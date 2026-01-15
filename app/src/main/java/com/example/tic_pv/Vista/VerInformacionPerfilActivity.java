package com.example.tic_pv.Vista;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

public class VerInformacionPerfilActivity extends AppCompatActivity {

    private TextView tvBienvenida, tvNombreUsuario, tvVerRolUsuario, tvCorreoUsuario, tvCedulaUsuario,
            tvFechaNacUsuario, tvEstadoCivUsuario, tvOcupacionUsuario,
            tvTelefonoUsuario, tvIgUsuario, tvFbUsuario, tvPaisUsuario,
            tvProvinciaUsuario, tvCantonUsuario, tvParroquiaUsuario,
            tvBarrioUsuario, tvCallesUsuario, tvEdadUsuario, tvEditarInformacionUsuario;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_informacion_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        Intent i = getIntent();
        idCuenta = i.getStringExtra("id");
        rol = i.getStringExtra("rol");
//        boolean desactivarIV = i.getBooleanExtra("disableImageView", false);


        tvBienvenida = findViewById(R.id.tvBienvenidaVP);
        tvNombreUsuario = findViewById(R.id.tvVerNombreUsuario);
        tvVerRolUsuario = findViewById(R.id.tvVerRolUsuario);
        tvCorreoUsuario = findViewById(R.id.tvVerCorreoUsuario);
        tvCedulaUsuario = findViewById(R.id.tvVerCedulaUsuario);
        tvFechaNacUsuario = findViewById(R.id.tvVerFechaNacUsuario);
        tvEdadUsuario = findViewById(R.id.tvVerEdadUsuario);
        tvEstadoCivUsuario = findViewById(R.id.tvVerEstadoCivUsuario);
        tvOcupacionUsuario = findViewById(R.id.tvVerOcupacionUsuario);
        tvTelefonoUsuario = findViewById(R.id.tvVerTelefonoUsuario);
        tvIgUsuario = findViewById(R.id.tvVerIgUsuario);
        tvFbUsuario = findViewById(R.id.tvVerFbUsuario);
        tvPaisUsuario = findViewById(R.id.tvVerPais);
        tvProvinciaUsuario = findViewById(R.id.tvVerProvincia);
        tvCantonUsuario = findViewById(R.id.tvVerCanton);
        tvParroquiaUsuario = findViewById(R.id.tvVerParroquia);
        tvBarrioUsuario = findViewById(R.id.tvVerBarrio);
        tvCallesUsuario = findViewById(R.id.tvVerCalles);
        barraProgreso = findViewById(R.id.barraProgresoVI);
        fotoPerfil = findViewById(R.id.ivVerFotoPerfil);

        tvEditarInformacionUsuario = findViewById(R.id.tVEditarInformacionUsuario);
        tvEditarInformacionUsuario.setPaintFlags(tvEditarInformacionUsuario.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        //Botones
//        btnEditarDatos = findViewById(R.id.btnEditarDatosPersonales);
//        btnEditarDomicilio = findViewById(R.id.btnEditarDomicilioPerfil);
//        btnCambiarClave = findViewById(R.id.btnCambiarClaveUsuario);


        fotoPerfil.setEnabled(false);
//            btnCambiarClave.setVisibility(View.GONE);

        authPerfil = FirebaseAuth.getInstance();
        FirebaseUser usuarioFirebase = authPerfil.getCurrentUser();

        System.out.println("ID USUARIO VER: " + idCuenta);

        barraProgreso.setVisibility(View.VISIBLE);
        mostrarInformacionPerfil(idCuenta);

//        if (usuarioFirebase == null) {
//            Toast.makeText(this, "No se encuentra el usuario", Toast.LENGTH_LONG).show();
//        } else {
//            barraProgreso.setVisibility(View.VISIBLE);
//            mostrarInformacionPerfil(idCuenta);
//        }

        tvEditarInformacionUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(VerInformacionPerfilActivity.this, EditarInformacionPerfilActivity.class);
            intent.putExtra("id", idCuenta);
            intent.putExtra("rol", rol);
            intent.putExtra("enAdministracion", true);
            startActivity(intent);
        });

//        btnEditarDatos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VerInformacionPerfilActivity.this, EditarInformacionPerfilActivity.class);
//                i.putExtra("id", idCuenta);
//                startActivity(i);
//                finish();
//            }
//        });

//        btnCambiarClave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VerInformacionPerfilActivity.this, CambiarClaveActivity.class);
//                i.putExtra("id", idCuenta);
//                startActivity(i);
//                finish();
//            }
//        });

//        btnEditarDomicilio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VerInformacionPerfilActivity.this, EditarDomicilioActivity.class);
//                i.putExtra("id", idCuenta);
//                startActivity(i);
//                finish();
//            }
//        });

    }

    private void mostrarInformacionPerfil(String idCuenta) {

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
                tvCorreoUsuario.setText(correoUsuario);
                tvVerRolUsuario.setText(rolUsuario);

                // Cargar la imagen con Glide
                Glide.with(this)
                        .load(fotoPerfilID)
                        .override(200, 200) // Ajusta el tamaño si es necesario
                        .fitCenter() // Cambia a fitCenter() si prefieres mantener la relación de aspecto
                        .into(fotoPerfil);

                System.out.println("ESTA ES LA URL DE LA FOTO: " + fotoPerfilID);
            } else {
                Toast.makeText(VerInformacionPerfilActivity.this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
            }
        });


        obtenerUsuario(idCuenta);

        barraProgreso.setVisibility(View.GONE);

    }

    // Método para rotar el bitmap basado en la orientación EXIF
    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap; // No rotar si no es necesario
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

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

                        tvBienvenida.setText("Información del Perfil");
                        tvNombreUsuario.setText(nombreUsuario);
                        tvCedulaUsuario.setText(cedulaUsuario);
                        tvFechaNacUsuario.setText(fechaNacUsuario);
                        tvEdadUsuario.setText(edadUsuario);
                        tvEstadoCivUsuario.setText(estadoCivUsuario);
                        tvOcupacionUsuario.setText(ocupacionUsuario);
                        tvTelefonoUsuario.setText(telefonoUsuario);
                        tvIgUsuario.setText(igUsuario);
                        tvFbUsuario.setText(fbUsuario);

                        obtenerDomicilio(domicilioID);

                    }
                } else {
                    Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error al obtener los documentos", Toast.LENGTH_LONG).show();
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

                    tvPaisUsuario.setText(paisUsuario);
                    tvProvinciaUsuario.setText(provinciaUsuario);
                    tvCantonUsuario.setText(cantonUsuario);
                    tvParroquiaUsuario.setText(parroquiaUsuario);
                    tvBarrioUsuario.setText(barrioUsuario);
                    tvCallesUsuario.setText(callesUsuario);
                } else {
                    Toast.makeText(VerInformacionPerfilActivity.this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}