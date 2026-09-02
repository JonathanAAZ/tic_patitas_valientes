package com.example.tic_pv.Vista.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tic_pv.Adaptadores.SolicitudesPendientesAdaptador;
import com.example.tic_pv.Controlador.ControladorAdopcion;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.databinding.FragmentSolicitudesAdopcionPendientesBinding;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesAdopcionPendientesFragment extends Fragment {

    private FragmentSolicitudesAdopcionPendientesBinding binding;
    private ArrayList <Adopcion> solicitudesPendientes;
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private SolicitudesPendientesAdaptador solicitudesPendientesAdaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolicitudesAdopcionPendientesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        solicitudesPendientes = new ArrayList<>();

        binding.recyViewSolicitudesPendientes.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    // Se recarga al volver al frente: una solicitud puede haberse aceptado o rechazado
    // desde la pantalla de detalle
    @Override
    public void onResume() {
        super.onResume();
        cargarSolicitudesPendientes();
    }

    private void cargarSolicitudesPendientes() {
        controladorAdopcion.obtenerListaSolicitudesPendientes(requireContext(), new ControladorAdopcion.Callback<List<Adopcion>>() {
            @Override
            public void onComplete(List<Adopcion> result) {
                if (binding == null) {
                    return;
                }

                solicitudesPendientes.clear();
                solicitudesPendientes.addAll(result);

                solicitudesPendientesAdaptador = new SolicitudesPendientesAdaptador(solicitudesPendientes);
                binding.recyViewSolicitudesPendientes.setAdapter(solicitudesPendientesAdaptador);
                Log.d("LISTA_MASCOTAS", "Tamaño: " + solicitudesPendientes.size());

                // Validar si la lista de solicitudes está vacia para presentar el mensaje
                if (solicitudesPendientes.isEmpty()) {
                    binding.tVNoHaySolicitudesPendientes.setVisibility(View.VISIBLE);
                    binding.recyViewSolicitudesPendientes.setVisibility(View.GONE);
                } else {
                    binding.tVNoHaySolicitudesPendientes.setVisibility(View.GONE);
                    binding.recyViewSolicitudesPendientes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(requireContext(), "No se pudo obtener la lista de solicitudes pendientes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
