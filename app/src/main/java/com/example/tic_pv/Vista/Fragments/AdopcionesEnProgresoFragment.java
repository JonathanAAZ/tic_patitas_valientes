package com.example.tic_pv.Vista.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Adaptadores.SolicitudesPendientesAdaptador;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.databinding.FragmentAdopcionesEnProgresoBinding;

import java.util.ArrayList;
import java.util.List;

public class AdopcionesEnProgresoFragment extends Fragment {
    private FragmentAdopcionesEnProgresoBinding binding;
    private ArrayList <Adopcion> adopcionesAceptadas, adopcionesPorRevisar;
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private SolicitudesPendientesAdaptador adaptadorPorRevisar, adaptadorAceptadas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdopcionesEnProgresoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        adopcionesPorRevisar = new ArrayList<>();
        adopcionesAceptadas = new ArrayList<>();

        binding.recyclerViewAdopcionesPorRevisar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewAdopcionesAceptadas.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    // Se recarga al volver al frente: una adopción puede haber avanzado de etapa desde
    // la pantalla de detalle
    @Override
    public void onResume() {
        super.onResume();
        cargarAdopcionesPorRevisar();
        cargarAdopcionesAceptadas();
    }

    private void cargarAdopcionesPorRevisar() {
        controladorAdopcion.obtenerListaAdopcionesPorRevisar(requireContext(), new ControladorAdopcion.Callback<List<Adopcion>>() {
            @Override
            public void onComplete(List<Adopcion> result) {
                if (binding == null) {
                    return;
                }

                adopcionesPorRevisar.clear();
                adopcionesPorRevisar.addAll(result);

                adaptadorPorRevisar = new SolicitudesPendientesAdaptador(adopcionesPorRevisar);
                binding.recyclerViewAdopcionesPorRevisar.setAdapter(adaptadorPorRevisar);

                if (adopcionesPorRevisar.isEmpty()) {
                    binding.tVNoHayAdopcionesPorRevisar.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayAdopcionesPorRevisar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener la lista de adopciones por revisar");
            }
        });
    }

    private void cargarAdopcionesAceptadas() {
        controladorAdopcion.obtenerListaAdopcionesAceptadas(requireContext(), new ControladorAdopcion.Callback<List<Adopcion>>() {
            @Override
            public void onComplete(List<Adopcion> result) {
                if (binding == null) {
                    return;
                }

                adopcionesAceptadas.clear();
                adopcionesAceptadas.addAll(result);

                adaptadorAceptadas = new SolicitudesPendientesAdaptador(adopcionesAceptadas);
                binding.recyclerViewAdopcionesAceptadas.setAdapter(adaptadorAceptadas);

                if (adopcionesAceptadas.isEmpty()) {
                    binding.tVAdopcionesAceptadas.setVisibility(View.GONE);
                    binding.viewAdopcionesAceptadas.setVisibility(View.GONE);
                    binding.tVNoHayAdopcionesAceptadas.setVisibility(View.VISIBLE);
                } else {
                    binding.viewAdopcionesAceptadas.setVisibility(View.VISIBLE);
                    binding.tVAdopcionesAceptadas.setVisibility(View.VISIBLE);
                    binding.tVNoHayAdopcionesAceptadas.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener la lista de adopciones aceptadas");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
