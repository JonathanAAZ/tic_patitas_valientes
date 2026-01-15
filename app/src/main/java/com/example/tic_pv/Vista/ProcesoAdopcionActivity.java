package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.IniciarAdopcionFragment;
import com.example.tic_pv.Vista.Fragments.PerfilFragment;
import com.example.tic_pv.databinding.ActivityProcesoAdopcionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProcesoAdopcionActivity extends AppCompatActivity {

    private ActivityProcesoAdopcionBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proceso_adopcion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityProcesoAdopcionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        String proceso = i.getStringExtra("proceso");
        String idMascota = i.getStringExtra("idMascota");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        seleccionarFragment(proceso, idMascota);


    }

    private void seleccionarFragment(String valor, String idMasc) {
        if (valor.equalsIgnoreCase("inicio")) {
            Bundle bundle = new Bundle();
            String idCuenta = firebaseUser != null ? firebaseUser.getUid() : null;
            bundle.putString("idCuenta", idCuenta);
            bundle.putString("idMascota", idMasc);
            IniciarAdopcionFragment fragment = new IniciarAdopcionFragment();
            fragment.setArguments(bundle);
            controladorUtilidades.reemplazarFragments(R.id.fLFragmentProcesoAdopcion, getSupportFragmentManager(), fragment);
        }
    }

}