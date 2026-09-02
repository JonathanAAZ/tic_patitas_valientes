package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Adaptadores.ListaDesparasitacionesAdaptador;
import com.example.tic_pv.Adaptadores.ListaVacunasAdaptador;
import com.example.tic_pv.Controlador.ControladorHistorialMedico;
import com.example.tic_pv.Modelo.Desparasitacion;
import com.example.tic_pv.Modelo.HistorialMedico;
import com.example.tic_pv.Vista.AgregarHistorialMedicoActivity;
import com.example.tic_pv.databinding.FragmentHistorialMedicoBinding;

import java.util.ArrayList;
import java.util.List;

public class HistorialMedicoFragment extends Fragment {

    private FragmentHistorialMedicoBinding binding;
    private ArrayList<HistorialMedico> listaVacunas;
    private ListaVacunasAdaptador listaVacunasAdaptador;
    private ArrayList<Desparasitacion> listaDesparasitaciones;
    private ListaDesparasitacionesAdaptador listaDesparasitacionesAdaptador;
    private final ControladorHistorialMedico controladorHistorialMedico = new ControladorHistorialMedico();
    private String idMascota;
    private boolean isVacuna;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHistorialMedicoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        idMascota = requireArguments().getString("idMascota");
        isVacuna = requireArguments().getBoolean("isVacuna");

        listaVacunas = new ArrayList<>();
        listaDesparasitaciones = new ArrayList<>();

        binding.recyViewListaVacunas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyViewListaDesparasitaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        // Solo se muestra la sección que corresponde al botón con el que se entró
        if (isVacuna) {
            binding.lLAgregarVacuna.setVisibility(View.VISIBLE);
            binding.recyViewListaVacunas.setVisibility(View.VISIBLE);

            binding.lLAgregarDesparasitacion.setVisibility(View.GONE);
            binding.recyViewListaDesparasitaciones.setVisibility(View.GONE);
        } else {
            binding.lLAgregarVacuna.setVisibility(View.GONE);
            binding.recyViewListaVacunas.setVisibility(View.GONE);

            binding.lLAgregarDesparasitacion.setVisibility(View.VISIBLE);
            binding.recyViewListaDesparasitaciones.setVisibility(View.VISIBLE);
        }

        // Configurar botones para ver Vacunas o Desparasitaciones
        binding.lLAgregarVacuna.setOnClickListener(v -> {
            iniciarActivityAgregarVacuna(view);
        });

        binding.lLAgregarDesparasitacion.setOnClickListener(v -> {
            iniciarActivityAgregarVacuna(view);
        });

        return view;
    }

    // Se recarga al volver al frente: es lo que hace aparecer la vacuna o la
    // desparasitación que se acaba de registrar
    @Override
    public void onResume() {
        super.onResume();

        if (isVacuna) {
            cargarVacunas();
        } else {
            cargarDesparasitaciones();
        }
    }

    private void cargarVacunas() {
        binding.shimmerRecyListaVacunas.setVisibility(View.VISIBLE);
        binding.shimmerRecyListaDesparasitacion.setVisibility(View.GONE);
        binding.shimmerRecyListaVacunas.startShimmer();

        controladorHistorialMedico.obtenerListaVacunas(idMascota, new ControladorHistorialMedico.Callback<List<HistorialMedico>>() {
            @Override
            public void onComplete(List<HistorialMedico> result) {
                if (binding == null) {
                    return;
                }

                listaVacunas.clear();
                listaVacunas.addAll(result);

                listaVacunasAdaptador = new ListaVacunasAdaptador(listaVacunas, idMascota);
                binding.recyViewListaVacunas.setAdapter(listaVacunasAdaptador);

                binding.shimmerRecyListaVacunas.stopShimmer();
                binding.shimmerRecyListaVacunas.setVisibility(View.GONE);
                binding.recyViewListaVacunas.setVisibility(View.VISIBLE);

                if (listaVacunas.isEmpty()) {
                    binding.tVNoHayResultadosListaVacunas.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultadosListaVacunas.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener la lista de vacunas");
            }
        });
    }

    private void cargarDesparasitaciones() {
        binding.shimmerRecyListaVacunas.setVisibility(View.GONE);
        binding.shimmerRecyListaDesparasitacion.setVisibility(View.VISIBLE);
        binding.shimmerRecyListaDesparasitacion.startShimmer();

        controladorHistorialMedico.obtenerListaDesparasitaciones(idMascota, new ControladorHistorialMedico.Callback<List<Desparasitacion>>() {
            @Override
            public void onComplete(List<Desparasitacion> result) {
                if (binding == null) {
                    return;
                }

                listaDesparasitaciones.clear();
                listaDesparasitaciones.addAll(result);

                listaDesparasitacionesAdaptador = new ListaDesparasitacionesAdaptador(listaDesparasitaciones, idMascota);
                binding.recyViewListaDesparasitaciones.setAdapter(listaDesparasitacionesAdaptador);

                binding.shimmerRecyListaDesparasitacion.stopShimmer();
                binding.shimmerRecyListaDesparasitacion.setVisibility(View.GONE);
                binding.recyViewListaDesparasitaciones.setVisibility(View.VISIBLE);

                if (listaDesparasitaciones.isEmpty()) {
                    binding.tVNoHayResultadosListaDesparasitaciones.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultadosListaDesparasitaciones.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener la lista de desparasitaciones");
            }
        });
    }

    private void iniciarActivityAgregarVacuna(View view) {
        Intent intent = new Intent(view.getContext(), AgregarHistorialMedicoActivity.class);
        intent.putExtra("idMascota", idMascota);
        intent.putExtra("isVacuna", isVacuna);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
