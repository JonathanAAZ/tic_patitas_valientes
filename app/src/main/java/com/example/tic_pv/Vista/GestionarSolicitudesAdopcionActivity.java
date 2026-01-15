package com.example.tic_pv.Vista;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.AdopcionesEnProgresoFragment;
import com.example.tic_pv.Vista.Fragments.SolicitudesAdopcionPendientesFragment;
import com.example.tic_pv.databinding.ActivityGestionarSolicitudesAdopcionBinding;

public class GestionarSolicitudesAdopcionActivity extends AppCompatActivity {
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ActivityGestionarSolicitudesAdopcionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestionar_solicitudes_adopcion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityGestionarSolicitudesAdopcionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controladorUtilidades.configurarColoresBotones(binding.lLSolicitudesPendientes, binding.lLAdopcionesProceso);

        binding.lLSolicitudesPendientes.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLSolicitudesPendientes, binding.lLAdopcionesProceso);
            controladorUtilidades.reemplazarFragments(R.id.fLFragmentSolicitudesAdopcion, getSupportFragmentManager(), new SolicitudesAdopcionPendientesFragment());
        });

        binding.lLAdopcionesProceso.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLAdopcionesProceso, binding.lLSolicitudesPendientes);
            controladorUtilidades.reemplazarFragments(R.id.fLFragmentSolicitudesAdopcion, getSupportFragmentManager(), new AdopcionesEnProgresoFragment());
        });

        controladorUtilidades.reemplazarFragments(R.id.fLFragmentSolicitudesAdopcion, getSupportFragmentManager(), new SolicitudesAdopcionPendientesFragment());

    }
}