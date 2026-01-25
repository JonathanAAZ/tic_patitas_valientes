package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Controlador.SessionManager;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.MisSeguimientosVoluntarioFragment;

public class MisSeguimientosActivity extends AppCompatActivity {

    private String idVoluntario, rolUsuario;
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

        Bundle bundleVoluntario = new Bundle();
        bundleVoluntario.putString("idVoluntario", idVoluntario);
        MisSeguimientosVoluntarioFragment misSeguimientosVoluntarioFragment = new MisSeguimientosVoluntarioFragment();
        misSeguimientosVoluntarioFragment.setArguments(bundleVoluntario);

        if (rolUsuario.equalsIgnoreCase("Voluntario")) {
            controladorUtilidades.reemplazarFragments(R.id.fLFragmentMisSeguimientos, getSupportFragmentManager(), misSeguimientosVoluntarioFragment);
        }
    }
}