package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Controlador.SessionManager;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.MisSeguimientosAdoptanteFragment;
import com.example.tic_pv.Vista.Fragments.MisSeguimientosVoluntarioFragment;

public class MisSeguimientosActivity extends AppCompatActivity {

    private String idVoluntario, idAdoptante, rolUsuario;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_seguimientos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);
        rolUsuario = sessionManager.getUserRole();

        Intent intent = getIntent();
        idVoluntario = intent.getStringExtra("idVoluntario");
        idAdoptante = intent.getStringExtra("idAdoptante");

        if (rolUsuario.equalsIgnoreCase("Voluntario")) {
            Bundle bundleVoluntario = new Bundle();
            bundleVoluntario.putString("idVoluntario", idVoluntario);
            MisSeguimientosVoluntarioFragment misSeguimientosVoluntarioFragment = new MisSeguimientosVoluntarioFragment();
            misSeguimientosVoluntarioFragment.setArguments(bundleVoluntario);

            controladorUtilidades.reemplazarFragments(R.id.fLFragmentMisSeguimientos, getSupportFragmentManager(), misSeguimientosVoluntarioFragment);
        } else if (rolUsuario.equalsIgnoreCase("Adoptante")) {
            Bundle bundleAdoptante = new Bundle();
            bundleAdoptante.putString("idAdoptante", idAdoptante);
            MisSeguimientosAdoptanteFragment misSeguimientosAdoptanteFragment = new MisSeguimientosAdoptanteFragment();
            misSeguimientosAdoptanteFragment.setArguments(bundleAdoptante);

            controladorUtilidades.reemplazarFragments(R.id.fLFragmentMisSeguimientos, getSupportFragmentManager(), misSeguimientosAdoptanteFragment);
        } else {
            Toast.makeText(this, "Su rol no tiene seguimientos asignados", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}