package com.example.tic_pv.Vista;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.ListaMascotasAdopcionFragment;
import com.example.tic_pv.databinding.ActivityVerMascotasBinding;

public class VerMascotasActivity extends AppCompatActivity {

    private ActivityVerMascotasBinding binding;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_mascotas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityVerMascotasBinding.inflate(getLayoutInflater());


        controladorUtilidades.reemplazarFragments(R.id.fLFragmentCatalogoMascotas, getSupportFragmentManager(), new ListaMascotasAdopcionFragment());

    }
}