package com.example.tic_pv.Vista;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
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

import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.ContratoFragment;
import com.example.tic_pv.Vista.Fragments.VideoCompromisoFragment;
import com.example.tic_pv.databinding.ActivityUltimosRequisitosBinding;

import java.util.Objects;

public class UltimosRequisitosActivity extends AppCompatActivity {

    private ActivityUltimosRequisitosBinding binding;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private String idAdopcion, idContrato, idCuenta, idMascota;
    private Adopcion adopcion;
    private Dialog observacionesReenvio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ultimos_requisitos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Binding para acceder a los elementos

        binding = ActivityUltimosRequisitosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener el parámetro enviado en la vista anterior
        Intent i = getIntent();
        adopcion = i.getParcelableExtra("adopcion");

        assert adopcion != null;
        idAdopcion = adopcion.getId();
        idContrato = adopcion.getContratoAdopcion();
        idCuenta = adopcion.getAdoptante();
        idMascota = adopcion.getMascotaAdopcion();

        // Definir los parámetros que irán al Fragment del contrato
        Bundle bundle = new Bundle();
        bundle.putString("idContrato", idContrato);
        bundle.putString("idCuenta", idCuenta);
        bundle.putString("idMascota", idMascota);
        ContratoFragment fragmentContrato = new ContratoFragment();
        fragmentContrato.setArguments(bundle);

        // Definir el parámetro que íra al Fragment del video de compromiso
        Bundle bundleVideo = new Bundle();
        bundleVideo.putString("idAdopcion", idAdopcion);
        VideoCompromisoFragment fragmentVideo = new VideoCompromisoFragment();
        fragmentVideo.setArguments(bundleVideo);

        // Inicializar Dialog para enviar observaciones al solicitar reenvio
        observacionesReenvio = new Dialog(this);

        // Configurar los botones para cambiar de fragment
        controladorUtilidades.configurarColoresBotones(binding.lLBtnContrato, binding.lLBtnVerVideoCompromiso);

        // Fragment por defecto
        controladorUtilidades.reemplazarFragments(R.id.fLVerContrato, getSupportFragmentManager(), fragmentContrato);

        binding.lLBtnContrato.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLBtnContrato, binding.lLBtnVerVideoCompromiso);

            binding.fLVerContrato.setVisibility(View.VISIBLE);
            binding.fLVerVideoCompromiso.setVisibility(View.GONE);

            controladorUtilidades.reemplazarFragments(R.id.fLVerContrato, getSupportFragmentManager(), fragmentContrato);
        });

        binding.lLBtnVerVideoCompromiso.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLBtnVerVideoCompromiso, binding.lLBtnContrato);

            binding.fLVerContrato.setVisibility(View.GONE);
            binding.fLVerVideoCompromiso.setVisibility(View.VISIBLE);

            controladorUtilidades.reemplazarFragments(R.id.fLVerVideoCompromiso, getSupportFragmentManager(), fragmentVideo);
        });

        binding.lLTerminarAdopcion.setOnClickListener(v -> {
            binding.lLBarraProgresoUltimosRequisitos.setVisibility(View.VISIBLE);
            controladorAdopcion.terminarAdopcion(adopcion, binding.lLBarraProgresoUltimosRequisitos, this);
        });

        binding.lLSolicitarNuevamente.setOnClickListener(v -> {
            observacionesReenvio.setContentView(R.layout.dialog_motivo_rechazo_solicitud);
            Objects.requireNonNull(observacionesReenvio.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView btnSalirObservacionesRechazo = observacionesReenvio.findViewById(R.id.iVSalirObservacionesRechazo);
            TextView tVEncabezadoMotivo = observacionesReenvio.findViewById(R.id.tVEncMotivoRechazoSolicitud);
            tVEncabezadoMotivo.setText("Por favor, indique el motivo del reenvío de requisitos.");
            EditText eTObservaciones = observacionesReenvio.findViewById(R.id.etMotivoRechazoSolicitud);
            LinearLayout btnEnviarObservaciones = observacionesReenvio.findViewById(R.id.lLEnviarMotivoRechazo);

            btnSalirObservacionesRechazo.setOnClickListener(view -> { observacionesReenvio.dismiss(); });
            btnEnviarObservaciones.setOnClickListener(view -> {

                boolean textVacio = controladorUtilidades.validarTextoVacio(eTObservaciones, "Por favor, agregue el motivo");

                if (!textVacio) {
                    Toast.makeText(this, "Debe ingresar un motivo para el reenvío de requisitos", Toast.LENGTH_SHORT).show();
                } else {
                    adopcion.setObservaciones(eTObservaciones.getText().toString());
                    controladorAdopcion.solicitarReenvioRequisitos(adopcion,
                            binding.lLBarraProgresoUltimosRequisitos,
                            this);
                    observacionesReenvio.dismiss();
                }
            });

            observacionesReenvio.create();
            observacionesReenvio.show();

        });
    }
}