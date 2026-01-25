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
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.FragmentMisSeguimientosVoluntarioBinding;

import java.util.ArrayList;

public class MisSeguimientosVoluntarioFragment extends Fragment {

    private ArrayList <Seguimiento> listaSeguimientos;
    private ListaSeguimientosVoluntarioAdaptador listaSeguimientosVoluntarioAdaptador;
    String idVoluntario;
    private FragmentMisSeguimientosVoluntarioBinding binding;
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMisSeguimientosVoluntarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        listaSeguimientos = new ArrayList<>();

        binding.recyViewListaMisSeguimientosVoluntario.setLayoutManager(new LinearLayoutManager(getContext()));

        assert getArguments() != null;
        idVoluntario = getArguments().getString("idVoluntario");
        controladorSeguimiento.obtenerSeguimientosVoluntario(idVoluntario, new ControladorSeguimiento.CallbackSeguimientosVol<ArrayList<Seguimiento>>() {
            @Override
            public void onComplete(ArrayList<Seguimiento> result) {
                listaSeguimientos.clear();
                listaSeguimientos.addAll(result);
                listaSeguimientosVoluntarioAdaptador = new ListaSeguimientosVoluntarioAdaptador(listaSeguimientos);
                binding.recyViewListaMisSeguimientosVoluntario.setAdapter(listaSeguimientosVoluntarioAdaptador);
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIREBASE", "Error al obtener la lista de seguimientos.");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}