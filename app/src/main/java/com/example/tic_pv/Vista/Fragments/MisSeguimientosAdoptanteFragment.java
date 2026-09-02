package com.example.tic_pv.Vista.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Adaptadores.ListaSeguimientosAdoptanteAdaptador;
import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.databinding.FragmentMisSeguimientosAdoptanteBinding;

import java.util.ArrayList;

public class MisSeguimientosAdoptanteFragment extends Fragment {

    private ArrayList<Seguimiento> listaSeguimientos;
    private ListaSeguimientosAdoptanteAdaptador listaSeguimientosAdoptanteAdaptador;
    String idAdoptante;
    private FragmentMisSeguimientosAdoptanteBinding binding;
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMisSeguimientosAdoptanteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        listaSeguimientos = new ArrayList<>();

        binding.recyViewListaMisSeguimientosAdoptante.setLayoutManager(new LinearLayoutManager(getContext()));

        idAdoptante = requireArguments().getString("idAdoptante");

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
        controladorSeguimiento.obtenerSeguimientosAdoptante(idAdoptante, new ControladorSeguimiento.CallbackSeguimientosVol<ArrayList<Seguimiento>>() {
            @Override
            public void onComplete(ArrayList<Seguimiento> result) {
                if (binding == null) {
                    return;
                }

                listaSeguimientos.clear();
                listaSeguimientos.addAll(result);

                listaSeguimientosAdoptanteAdaptador = new ListaSeguimientosAdoptanteAdaptador(listaSeguimientos);
                binding.recyViewListaMisSeguimientosAdoptante.setAdapter(listaSeguimientosAdoptanteAdaptador);

                // Mostramos el mensaje de lista vacía solo cuando no hay seguimientos
                if (listaSeguimientos.isEmpty()) {
                    binding.tVNoHayResultadosListaMisSeguimientosAdoptante.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultadosListaMisSeguimientosAdoptante.setVisibility(View.GONE);
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
