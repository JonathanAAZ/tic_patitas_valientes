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

        // Un solo setContentView, y con el binding: antes se inflaba una segunda copia del
        // layout que nunca se mostraba, así que cualquier binding.algo tocaba vistas invisibles
        binding = ActivityVerMascotasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.iVRegresarVerMascotas.setOnClickListener(v -> finish());

        controladorUtilidades.reemplazarFragments(R.id.fLFragmentCatalogoMascotas, getSupportFragmentManager(), new ListaMascotasAdopcionFragment());

    }
}