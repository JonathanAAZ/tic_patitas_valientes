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
import com.example.tic_pv.R;
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

        assert getArguments() != null;
        idMascota = getArguments().getString("idMascota");
        isVacuna = getArguments().getBoolean("isVacuna");

        listaVacunas = new ArrayList<>();
        listaDesparasitaciones = new ArrayList<>();

        binding.recyViewListaVacunas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyViewListaDesparasitaciones.setLayoutManager(new LinearLayoutManager(getContext()));

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

        // Configurar Recycler View de vacunas
        controladorHistorialMedico.obtenerListaVacunas(idMascota, new ControladorHistorialMedico.Callback<List<HistorialMedico>>() {
            @Override
            public void onComplete(List<HistorialMedico> result) {
                listaVacunas.clear();
                listaVacunas.addAll(result);

                listaVacunasAdaptador = new ListaVacunasAdaptador(listaVacunas, idMascota);
                binding.recyViewListaVacunas.setAdapter(listaVacunasAdaptador);
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener la lista de vacunas");
            }
        });

        // Configurar Recycler View de desparasitaciones
        controladorHistorialMedico.obtenerListaDesparasitaciones(idMascota, new ControladorHistorialMedico.Callback<List<Desparasitacion>>() {
            @Override
            public void onComplete(List<Desparasitacion> result) {
                listaDesparasitaciones.clear();
                listaDesparasitaciones.addAll(result);

                listaDesparasitacionesAdaptador = new ListaDesparasitacionesAdaptador(listaDesparasitaciones, idMascota);
                binding.recyViewListaDesparasitaciones.setAdapter(listaDesparasitacionesAdaptador);
            }

            @Override
            public void onError(Exception e) {

            }
        });


        // Configurar botones para ver Vacunas o Desparasitaciones
        binding.lLAgregarVacuna.setOnClickListener(v -> {
            iniciarActivityAgregarVacuna(view);
        });

        binding.lLAgregarDesparasitacion.setOnClickListener(v -> {
            iniciarActivityAgregarVacuna(view);
        });

        return view;
    }

    private void iniciarActivityAgregarVacuna(View view) {
        Intent intent = new Intent(view.getContext(), AgregarHistorialMedicoActivity.class);
        intent.putExtra("idMascota", idMascota);
        intent.putExtra("isVacuna", isVacuna);
        startActivity(intent);
    }
}