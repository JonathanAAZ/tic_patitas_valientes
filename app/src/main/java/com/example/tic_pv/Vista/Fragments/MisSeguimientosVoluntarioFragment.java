package com.example.tic_pv.Vista.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Adaptadores.ListaSeguimientosVoluntarioAdaptador;
import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.databinding.FragmentMisSeguimientosVoluntarioBinding;

import java.util.ArrayList;

public class MisSeguimientosVoluntarioFragment extends Fragment {

    private ArrayList<Seguimiento> listaSeguimientos;
    private ListaSeguimientosVoluntarioAdaptador listaSeguimientosVoluntarioAdaptador;
    String idVoluntario;
    private FragmentMisSeguimientosVoluntarioBinding binding;
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMisSeguimientosVoluntarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        listaSeguimientos = new ArrayList<>();

        binding.recyViewListaMisSeguimientosVoluntario.setLayoutManager(new LinearLayoutManager(getContext()));

        idVoluntario = requireArguments().getString("idVoluntario");

        return view;
    }

    // Se recarga cada vez que la pantalla vuelve al frente: al regresar del chat el
    // seguimiento puede haber quedado cerrado y la lista tiene que reflejarlo
    @Override
    public void onResume() {
        super.onResume();
        cargarSeguimientos();
    }

    private void cargarSeguimientos() {
        controladorSeguimiento.obtenerSeguimientosVoluntario(idVoluntario, new ControladorSeguimiento.CallbackSeguimientosVol<ArrayList<Seguimiento>>() {
            @Override
            public void onComplete(ArrayList<Seguimiento> result) {
                if (binding == null) {
                    return;
                }

                listaSeguimientos.clear();
                listaSeguimientos.addAll(result);

                listaSeguimientosVoluntarioAdaptador = new ListaSeguimientosVoluntarioAdaptador(listaSeguimientos);
                binding.recyViewListaMisSeguimientosVoluntario.setAdapter(listaSeguimientosVoluntarioAdaptador);

                // Mostramos el mensaje de lista vacía solo cuando no hay seguimientos
                if (listaSeguimientos.isEmpty()) {
                    binding.tVNoHayResultadosListaMisSeguimientosVoluntario.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultadosListaMisSeguimientosVoluntario.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIREBASE", "Error al obtener la lista de seguimientos.");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
