package com.example.tic_pv.Vista;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.HistorialMedicoFragment;
import com.example.tic_pv.databinding.ActivityMisMascotasBinding;
import com.example.tic_pv.databinding.ActivityVerMiMascotaBinding;

public class VerMiMascotaActivity extends AppCompatActivity {

    private ActivityVerMiMascotaBinding binding;
    private String idMascota;
    private ControladorMascota controladorMascota = new ControladorMascota();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_mi_mascota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        idMascota = i.getStringExtra("idMascota");

        binding = ActivityVerMiMascotaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Crear instancia del Fragment para ver historial de vacunas
        Bundle bundleVacuna = new Bundle();
        bundleVacuna.putString("idMascota", idMascota);
        bundleVacuna.putBoolean("isVacuna", true);
        HistorialMedicoFragment historialVacunas = new HistorialMedicoFragment();
        historialVacunas.setArguments(bundleVacuna);

        // Crear instancia del Fragment para ver historial de desparasitaciones
        Bundle bundleDesparasitacion = new Bundle();
        bundleDesparasitacion.putString("idMascota", idMascota);
        bundleDesparasitacion.putBoolean("isVacuna", false);
        HistorialMedicoFragment historialDesparasitaciones = new HistorialMedicoFragment();
        historialDesparasitaciones.setArguments(bundleDesparasitacion);

        presentarInformacionMiMascota(this);

        // Configurar botones para cambiar de fragment
        controladorUtilidades.configurarColoresBotones(binding.lLBtnVacunas, binding.lLDesparasitaciones);

        // Configurar fragments
        controladorUtilidades.reemplazarFragments(R.id.fLHistorialMedico, getSupportFragmentManager(), historialVacunas);

        binding.lLBtnVacunas.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLBtnVacunas, binding.lLDesparasitaciones);

            controladorUtilidades.reemplazarFragments(R.id.fLHistorialMedico, getSupportFragmentManager(), historialVacunas);
        });

        binding.lLDesparasitaciones.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLDesparasitaciones, binding.lLBtnVacunas);

            controladorUtilidades.reemplazarFragments(R.id.fLHistorialMedico, getSupportFragmentManager(), historialDesparasitaciones);
        });

//        binding.lLBtnVerVideoCompromiso.setOnClickListener(v -> {
//            controladorUtilidades.configurarColoresBotones(binding.lLDesparasitaciones, binding.lLBtnVacunas);
//
//            binding.fLVerContrato.setVisibility(View.GONE);
//            binding.fLVerVideoCompromiso.setVisibility(View.VISIBLE);
//
//            controladorUtilidades.reemplazarFragments(R.id.fLVerVideoCompromiso, getSupportFragmentManager(), fragmentVideo);
//        });

    }

    private void presentarInformacionMiMascota(Context context) {
        controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
            @Override
            public void onComplete(Mascota result) {

                controladorUtilidades.insertarImagenDesdeBDD(result.getFotoMascota(),
                        binding.iVFotoMiMascota,
                        context);

                binding.tVNombreMiMascota.setText(result.getNombreMascota());
                binding.tVEspecieMiMascota.setText(result.getEspecieMascota());
                binding.tVSexoMiMascota.setText(result.getSexoMascota());
                binding.tVColorMiMascota.setText(result.getColorMascota());
                binding.tVRazaMiMascota.setText(result.getRazaMascota());
                binding.tVEsterilizMiMascota.setText(result.getFechaEsterilizacion());
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "No se pudo presentar la información de la mascota");
            }
        });
    }
}