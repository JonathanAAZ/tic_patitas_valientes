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

import com.example.tic_pv.Adaptadores.ListaMisMascotasAdaptador;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.SessionManager;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.databinding.ActivityMisMascotasBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MisMascotasActivity extends AppCompatActivity {

    private ActivityMisMascotasBinding binding;
    private ArrayList<Mascota> listaMisMascotas;
    private ListaMisMascotasAdaptador adaptadorMisMascotas;
    private String rol, idUsuario;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser authUsuario = auth.getCurrentUser();
    private final ControladorMascota controladorMascota = new ControladorMascota();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMisMascotasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);

        idUsuario = authUsuario.getUid();
        rol = sessionManager.getUserRole();
        listaMisMascotas = new ArrayList<>();
        binding.recyViewMisMascotas.setLayoutManager(new LinearLayoutManager(this));

        if (rol.equalsIgnoreCase("Adoptante")) {
            binding.tVMisMascotas.setText("Mis mascotas");
            binding.tVSubtituloMisMascotas.setText("Visualiza y gestiona el historial médico de tus mascotas.");
        } else if (rol.equalsIgnoreCase("Voluntario")) {
            binding.tVMisMascotas.setText("Lista de mascotas");
            binding.tVSubtituloMisMascotas.setText("Visualiza y gestiona el historial médico de las mascotas registradas en el sistema.");
        } else {
            Toast.makeText(this, "Su rol no tiene acceso a esta sección", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Se recarga al volver al frente: una mascota puede haberse adoptado o retirado
    // mientras el usuario estaba en otra pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarMascotas();
    }

    private void cargarMascotas() {
        if (rol.equalsIgnoreCase("Adoptante")) {
            // Solo las mascotas que ya están en el hogar del adoptante
            controladorMascota.obtenerListaMisMascotas(idUsuario, new ControladorMascota.Callback<List<Mascota>>() {
                @Override
                public void onComplete(List<Mascota> result) {
                    presentarMascotas(result);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("ERROR", "No se pudo obtener la lista de mascotas");
                }
            });
        } else if (rol.equalsIgnoreCase("Voluntario")) {
            // El voluntario gestiona el historial médico de todas
            controladorMascota.obtenerListaMascotas(new ControladorMascota.Callback<List<Mascota>>() {
                @Override
                public void onComplete(List<Mascota> result) {
                    presentarMascotas(result);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("ERROR", "No se pudo obtener la lista de mascotas");
                }
            });
        }
    }

    private void presentarMascotas(List<Mascota> result) {
        listaMisMascotas.clear();
        listaMisMascotas.addAll(result);

        adaptadorMisMascotas = new ListaMisMascotasAdaptador(listaMisMascotas);
        binding.recyViewMisMascotas.setAdapter(adaptadorMisMascotas);

        if (listaMisMascotas.isEmpty()) {
            binding.tVNoHayMascotas.setVisibility(View.VISIBLE);
            binding.recyViewMisMascotas.setVisibility(View.GONE);
        } else {
            binding.tVNoHayMascotas.setVisibility(View.GONE);
            binding.recyViewMisMascotas.setVisibility(View.VISIBLE);
        }
    }
}
