package com.example.tic_pv.Vista;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.ListaMisMascotasAdaptador;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.SessionManager;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivityMisMascotasBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MisMascotasActivity extends AppCompatActivity {

    private ActivityMisMascotasBinding binding;
    private ArrayList <Mascota> listaMisMascotas;
    private ListaMisMascotasAdaptador adaptadorMisMascotas;
    private String rol;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser authUsuario = auth.getCurrentUser();
    private final ControladorMascota controladorMascota = new ControladorMascota();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_mascotas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);

        binding = ActivityMisMascotasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String idUsuario = authUsuario.getUid();
        rol = sessionManager.getUserRole();
        listaMisMascotas = new ArrayList<>();
        binding.recyViewMisMascotas.setLayoutManager(new LinearLayoutManager(this));

        if (rol.equalsIgnoreCase("Adoptante")) {
            binding.tVMisMascotas.setText("Mis mascotas");
            binding.tVSubtituloMisMascotas.setText("Visualiza y gestiona el historial médico de tus mascotas.");
            // Obtener la lista de mascotas del usuario
            controladorMascota.obtenerListaMisMascotas(idUsuario, new ControladorMascota.Callback<List<Mascota>>() {
                @Override
                public void onComplete(List<Mascota> result) {
                    listaMisMascotas.clear();
                    listaMisMascotas.addAll(result);

                    adaptadorMisMascotas = new ListaMisMascotasAdaptador(listaMisMascotas);
                    binding.recyViewMisMascotas.setAdapter(adaptadorMisMascotas);

                    if (listaMisMascotas.size() <= 0) {
                        binding.tVNoHayMascotas.setVisibility(View.VISIBLE);
                        binding.recyViewMisMascotas.setVisibility(View.GONE);
                    } else {
                        binding.tVNoHayMascotas.setVisibility(View.GONE);
                        binding.recyViewMisMascotas.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("ERROR", "No se pudo obtener la lista de mascotas");
                }
            });
        } else if (rol.equalsIgnoreCase("Voluntario")) {
            binding.tVMisMascotas.setText("Lista de mascotas");
            binding.tVSubtituloMisMascotas.setText("Visualiza y gestiona el historial médico de las mascotas registradas en el sistema.");
            controladorMascota.obtenerListaMascotas(new ControladorMascota.Callback<List<Mascota>>() {
                @Override
                public void onComplete(List<Mascota> result) {
                    listaMisMascotas.clear();
                    listaMisMascotas.addAll(result);

                    adaptadorMisMascotas = new ListaMisMascotasAdaptador(listaMisMascotas);
                    binding.recyViewMisMascotas.setAdapter(adaptadorMisMascotas);

                    if (listaMisMascotas.size() <= 0) {
                        binding.tVNoHayMascotas.setVisibility(View.VISIBLE);
                        binding.recyViewMisMascotas.setVisibility(View.GONE);
                    } else {
                        binding.tVNoHayMascotas.setVisibility(View.GONE);
                        binding.recyViewMisMascotas.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("ERROR", "No se pudo obtener la lista de mascotas");
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        if (adaptadorMisMascotas != null) {
            adaptadorMisMascotas.notifyDataSetChanged();
        }
        super.onResume();
    }
}