package com.example.tic_pv.Vista;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Controlador.ControladorContrato;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.ContratoAdopcion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivityResponderSolicitudAdopcionBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;

import java.util.Objects;

public class ResponderSolicitudAdopcionActivity extends AppCompatActivity {

    private ActivityResponderSolicitudAdopcionBinding binding;
    private String idSolicitudAdopcion;
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private ControladorContrato controladorContrato = new ControladorContrato();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private Adopcion solicitudAdopcion;
    private ContratoAdopcion contratoAdopcion;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Dialog observacionesRechazo;
    private String nombreFotoAdministrador = "firma_administracion.png";
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_responder_solicitud_adopcion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityResponderSolicitudAdopcionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Obtener la referencia a Firebase Storage para la foto del adminsitrador
        storageReference = FirebaseStorage.getInstance().getReference("FotosFirmas");

        //Obtener el ID de la solicitud enviada
        Intent i = getIntent();
        idSolicitudAdopcion = i.getStringExtra("idSolicitud");

        //Inicializar adopcion
        solicitudAdopcion = new Adopcion();

        //Subrayar textos
        binding.tVSeccionAdoptante.setPaintFlags(binding.tVSeccionAdoptante.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tVSeccionMascota.setPaintFlags(binding.tVSeccionMascota.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tVSeccionRequisitos.setPaintFlags(binding.tVSeccionRequisitos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Presentar la información del adoptante y la mascotra
        presentarInformacionSolicitud();

        //Inicializar Dialog para enviar observaciones
        observacionesRechazo = new Dialog(this);

        //Configurar ImageViews para la vista ampliada
        configurarImageViews(binding.iVFotoCedulaFrontalSolicitud, 0);
        configurarImageViews(binding.iVFotoCedulaReversoSolicitud, 1);
        configurarImageViews(binding.iVFotoServiciosSolicitud, 2);

        //Configurar AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ResponderSolicitudAdopcionActivity.this);
        builder.setTitle("Mensaje de confirmación");
        builder.setMessage("¿Está seguro/a de que desea aceptar la solicitud de adopción enviada?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Obtener la URL de la firma del administrador con Storage Reference
                StorageReference referenciaFotoAdministrador = storageReference.child(nombreFotoAdministrador);
                referenciaFotoAdministrador.getDownloadUrl().addOnSuccessListener(uri -> {
                    String urlFirmaAdministrador = uri.toString();

                    //Cambiar el estado de la solicitud
                    solicitudAdopcion.setEstadoAdopcion(EstadosCuentas.ACEPTADA.toString());

                    //Crear el contrato de adopción
                    contratoAdopcion = new ContratoAdopcion();
                    contratoAdopcion.setEstado(EstadosCuentas.NO_FIRMADO.toString());
                    contratoAdopcion.setFirmaAdministrador(urlFirmaAdministrador);
                    contratoAdopcion.setIdAdoptante(solicitudAdopcion.getAdoptante());
                    contratoAdopcion.setIdMascota(solicitudAdopcion.getMascotaAdopcion());

                    controladorContrato.crearContratoAdopcionDB(contratoAdopcion,
                            solicitudAdopcion,
                            binding.lLBarraProgresoResponderAdopcion,
                            ResponderSolicitudAdopcionActivity.this);
                }).addOnFailureListener(e -> {
                    Toast.makeText(ResponderSolicitudAdopcionActivity.this, "Error al obtener la firma del administrador", Toast.LENGTH_SHORT).show();
                });

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
        });

        //Configurar los botones para aceptar o rechazar solicitud de adopción
        binding.lLAceptarSolicitudAdopcion.setOnClickListener(v -> {
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        binding.lLRechazarSolicitudAdopcion.setOnClickListener(v -> {
            solicitudAdopcion.setEstadoAdopcion(EstadosCuentas.RECHAZADA.toString());

            observacionesRechazo.setContentView(R.layout.dialog_motivo_rechazo_solicitud);
            Objects.requireNonNull(observacionesRechazo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Definir elementos de la alerta
            ImageView btnSalirObservacionesRechazo = observacionesRechazo.findViewById(R.id.iVSalirObservacionesRechazo);
            TextView tVEncabezadoMotivo = observacionesRechazo.findViewById(R.id.tVEncMotivoRechazoSolicitud);
            tVEncabezadoMotivo.setText("Por favor, indique el motivo por la cual la solicitud de adopción ha sido rechazada.");
            EditText eTObservaciones = observacionesRechazo.findViewById(R.id.etMotivoRechazoSolicitud);
            LinearLayout btnEnviarObservaciones = observacionesRechazo.findViewById(R.id.lLEnviarMotivoRechazo);

            btnSalirObservacionesRechazo.setOnClickListener(view -> { observacionesRechazo.dismiss(); });
            btnEnviarObservaciones.setOnClickListener(view -> {

                boolean textVacio = controladorUtilidades.validarTextoVacio(eTObservaciones, "Por favor, agregue el motivo");

                if (!textVacio) {
                    Toast.makeText(this, "Debe ingresar un motivo para el rechazo de la solicitud", Toast.LENGTH_SHORT).show();
                } else {
                    solicitudAdopcion.setObservaciones(eTObservaciones.getText().toString());
                    controladorAdopcion.rechazarSolicitudAdopcion(solicitudAdopcion,
                            binding.lLBarraProgresoResponderAdopcion,
                            this);
                    observacionesRechazo.dismiss();
                }
//                redirigirVistaSolicitudes();
            });

            observacionesRechazo.create();
            observacionesRechazo.show();
        });
    }

    private void presentarInformacionSolicitud() {
        controladorAdopcion.obtenerSolicitudAdopcion(idSolicitudAdopcion, new ControladorAdopcion.Callback<Adopcion>() {
            @Override
            public void onComplete(Adopcion result) {
                solicitudAdopcion = result;
                binding.tVFechaEmisionSolicitud.setText(solicitudAdopcion.getFechaEmision());

                controladorUtilidades.insertarImagenDesdeBDD(solicitudAdopcion.getFotoCedulaFrontal(),
                        binding.iVFotoCedulaFrontalSolicitud,
                        getBaseContext());

                controladorUtilidades.insertarImagenDesdeBDD(solicitudAdopcion.getFotoCedulaPosterior(),
                        binding.iVFotoCedulaReversoSolicitud,
                        getBaseContext());

                controladorUtilidades.insertarImagenDesdeBDD(solicitudAdopcion.getFotoServiciosBasicos(),
                        binding.iVFotoServiciosSolicitud,
                        getBaseContext());

                switch (solicitudAdopcion.getTipoDomicilio()) {
                    case 0:
                        binding.tVTipoDomicilioSolicitud.setText(EstadosCuentas.ARRENDADO.toString());
                        break;
                    case 1:
                        binding.tVTipoDomicilioSolicitud.setText(EstadosCuentas.PROPIO.toString());
                        break;
                    default:
                        break;
                }

                //Descargar video desde Cloudinary
//                controladorUtilidades.insertarVideoDesdeBDD(solicitudAdopcion.getVideoHogarMascota(),
//                        binding.videoHogarMascotaSolicitud,
//                        binding.barraProgresoVideoSolicitud,
//                        binding.iVReproducirVideoSolicitud);

                presentarAdoptanteYMascota(solicitudAdopcion.getAdoptante(), solicitudAdopcion.getMascotaAdopcion());
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener solicitud de adopción");
            }
        });
    }

    private void presentarAdoptanteYMascota(String idCuenta, String idMascota){
        db.collection("Cuentas").document(idCuenta).get().addOnSuccessListener(documentSnapshot -> {
            String fotoAdoptante = documentSnapshot.getString("fotoPerfil");

            controladorUtilidades.insertarImagenDesdeBDD(fotoAdoptante,
                    binding.iVFotoAdoptanteSolicitud,
                    this);
//            Glide.with(this)
//                    .load(fotoAdoptante)
//                    .placeholder(R.drawable.ic_cargando)
//                    .fitCenter()
//                    .into(binding.iVFotoAdoptanteSolicitud);
        }).addOnFailureListener(command -> {
            Log.e("ERROR", "No se encontró la cuenta " + command.getMessage());
        });

        db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()){
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String nombreAdoptante = documentSnapshot.getString("nombre");
                        String cedulaAdoptante = documentSnapshot.getString("cedula");

                        binding.tVNombreAdoptanteSolicita.setText(nombreAdoptante);
                        binding.tVCedulaAdoptanteSolicitud.setText(cedulaAdoptante);
                    }
                }
            }
        });

        db.collection("Mascotas").document(idMascota).get().addOnSuccessListener(documentSnapshot -> {
            String nombreMascota = documentSnapshot.getString("nombre");
            String fotoMascota = documentSnapshot.getString("fotoMascota");

            binding.tVNombreMascotaSolicitud.setText(nombreMascota);

            controladorUtilidades.insertarImagenDesdeBDD(fotoMascota,
                    binding.iVFotoMascotaInicioAdopcion,
                    getBaseContext());

//            Glide.with(this)
//                    .load(fotoMascota)
//                    .placeholder(R.drawable.ic_cargando)
//                    .fitCenter()
//                    .into(binding.iVFotoMascotaInicioAdopcion);
        }).addOnFailureListener(command -> {
            Log.e("ERROR", "No se encontró la mascota " + command.getMessage());
        });
    }

    private void configurarImageViews(ImageView imageView, int posicion) {
        if (solicitudAdopcion != null) {
            imageView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_imagen_ampliada);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ZoomageView imagenAmpliada = dialog.findViewById(R.id.iVFotoAmpliada);
                switch (posicion) {
                    case 0:
                        asignarImagenDialog(solicitudAdopcion.getFotoCedulaFrontal(), imagenAmpliada);
                        break;
                    case 1:
                        asignarImagenDialog(solicitudAdopcion.getFotoCedulaPosterior(), imagenAmpliada);
                        break;
                    case 2:
                        asignarImagenDialog(solicitudAdopcion.getFotoServiciosBasicos(), imagenAmpliada);
                        break;
                    default:
                        break;
                }
                dialog.show();
            });
        } else {
            Toast.makeText(this, "No hay una foto seleccionada", Toast.LENGTH_SHORT).show();
        }

    }

    private void asignarImagenDialog(String url, ZoomageView imagen) {
        Glide.with(this)
                .load(url)
                .fitCenter()
                .into(imagen);
    }

    private void redirigirVistaSolicitudes() {
        Intent i = new Intent(ResponderSolicitudAdopcionActivity.this, GestionarSolicitudesAdopcionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


}