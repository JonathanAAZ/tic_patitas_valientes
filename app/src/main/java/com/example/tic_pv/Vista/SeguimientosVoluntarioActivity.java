package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.ListaAdoptantesSeguimientosAdaptador;
import com.example.tic_pv.Adaptadores.ListaSeguimientosAdaptador;
import com.example.tic_pv.Adaptadores.ListaUsuariosSeguimientosAdaptador;
import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivitySeguimientosVoluntarioBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class SeguimientosVoluntarioActivity extends AppCompatActivity {

    private ActivitySeguimientosVoluntarioBinding binding;
    private final ControladorUsuario controladorUsuario = new ControladorUsuario();
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();
    private ArrayList<Seguimiento> listaSeguimientosDisponibles;
    private ArrayList<Seguimiento> listaSeguimientosVoluntario;
    private ListaSeguimientosAdaptador adaptadorSeguimientos, adaptadorSeguimientosVol;
    private String idVoluntario, nombreVoluntario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimientos_voluntario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaSeguimientosDisponibles = new ArrayList<>();
        listaSeguimientosVoluntario = new ArrayList<>();

        Intent i = getIntent();
        idVoluntario = i.getStringExtra("idVoluntario");
        nombreVoluntario = i.getStringExtra("nombreVoluntario");

        binding = ActivitySeguimientosVoluntarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controladorUtilidades.configurarColoresBotones(binding.lLBtnAsignarSeguimientos, binding.lLBtnSeguimientosAsig);

        // Layout manager de recycler view
        binding.recyViewAsignarSegui.setLayoutManager(new LinearLayoutManager(this));
        binding.recyViewSeguiAsignados.setLayoutManager(new LinearLayoutManager(this));


        presentarSeguimientosDisponibles();

        binding.lLBtnAsignarSeguimientos.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLBtnAsignarSeguimientos, binding.lLBtnSeguimientosAsig);
            presentarSeguimientosDisponibles();
            binding.rLAsignarSeguimientos.setVisibility(View.VISIBLE);
            binding.rLSeguimientosAsignados.setVisibility(View.GONE);
        });

        binding.lLBtnSeguimientosAsig.setOnClickListener(v -> {
            controladorUtilidades.configurarColoresBotones(binding.lLBtnSeguimientosAsig, binding.lLBtnAsignarSeguimientos);
            presentarSeguimientosVoluntario();
            binding.rLAsignarSeguimientos.setVisibility(View.GONE);
            binding.rLSeguimientosAsignados.setVisibility(View.VISIBLE);
        });
    }

    private void presentarSeguimientosDisponibles() {
        controladorSeguimiento.obtenerSeguimientosDisponibles(new ControladorSeguimiento.Callback<ArrayList<Seguimiento>>() {
            @Override
            public void onComplete(ArrayList<Seguimiento> result) {
                listaSeguimientosDisponibles.clear();
                listaSeguimientosDisponibles.addAll(result);

                adaptadorSeguimientos = new ListaSeguimientosAdaptador(listaSeguimientosDisponibles, idVoluntario, nombreVoluntario);
                binding.recyViewAsignarSegui.setAdapter(adaptadorSeguimientos);

                if (adaptadorSeguimientos.getItemCount() <= 0) {
                    binding.tVNoHaySeguimientosDisponibles.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHaySeguimientosDisponibles.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "No se pudieron obtener los seguimientos");
            }
        });
    }

    private void presentarSeguimientosVoluntario() {
        controladorSeguimiento.obtenerSeguimientosVoluntario(idVoluntario, new ControladorSeguimiento.CallbackSeguimientosVol<ArrayList<Seguimiento>>() {
            @Override
            public void onComplete(ArrayList<Seguimiento> result) {
                listaSeguimientosVoluntario.clear();
                listaSeguimientosVoluntario.addAll(result);

                adaptadorSeguimientosVol = new ListaSeguimientosAdaptador(listaSeguimientosVoluntario, idVoluntario, nombreVoluntario);
                binding.recyViewSeguiAsignados.setAdapter(adaptadorSeguimientosVol);

                if (adaptadorSeguimientosVol.getItemCount() <= 0) {
                    binding.tVNoTieneSeguimientos.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoTieneSeguimientos.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

}