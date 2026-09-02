package com.example.tic_pv.Vista;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.SolicitudesEnviadasAdaptador;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.databinding.ActivityVerSolicitudesAdopcionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class VerSolicitudesAdopcionActivity extends AppCompatActivity {

    private ActivityVerSolicitudesAdopcionBinding binding;
    private ArrayList<Adopcion> listaSolicitudesEnviadas;
    private SolicitudesEnviadasAdaptador solicitudesEnviadasAdaptador;
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser usuarioActual = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityVerSolicitudesAdopcionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaSolicitudesEnviadas = new ArrayList<>();

        binding.rVSolicitudesAdopcionEnviadas.setLayoutManager(new LinearLayoutManager(this));
    }

    // Se recarga al volver al frente: la solicitud pudo aceptarse o rechazarse mientras
    // el usuario estaba en otra pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarSolicitudesEnviadas();
    }

    private void cargarSolicitudesEnviadas() {
        controladorAdopcion.obtenerListaSolicitudesEnviadas(usuarioActual.getUid(), new ControladorAdopcion.Callback<List<Adopcion>>() {
            @Override
            public void onComplete(List<Adopcion> result) {
                listaSolicitudesEnviadas.clear();
                listaSolicitudesEnviadas.addAll(result);

                solicitudesEnviadasAdaptador = new SolicitudesEnviadasAdaptador(listaSolicitudesEnviadas);
                binding.rVSolicitudesAdopcionEnviadas.setAdapter(solicitudesEnviadasAdaptador);

                if (listaSolicitudesEnviadas.isEmpty()) {
                    binding.tVNoHayResultadosSolcitudesEnviadas.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultadosSolcitudesEnviadas.setVisibility(View.GONE);
                }

                Log.d("CANTIDAD DE OBJETOS", "Cantidad: " + listaSolicitudesEnviadas.size());
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(VerSolicitudesAdopcionActivity.this, "No se pudieron obtener las solicitudes enviadas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
