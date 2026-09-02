package com.example.tic_pv.Vista.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Adaptadores.ListaNotificacionesAdaptador;
import com.example.tic_pv.Controlador.ControladorNotificaciones;
import com.example.tic_pv.Modelo.Notificacion;
import com.example.tic_pv.databinding.FragmentNotificacionesBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    private FragmentNotificacionesBinding binding;
    private ControladorNotificaciones controladorNotificaciones = new ControladorNotificaciones();
    private ArrayList<Notificacion> listaNotificaciones;
    private ListaNotificacionesAdaptador listaNotificacionesAdaptador;
    private String idCuenta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNotificacionesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        listaNotificaciones = new ArrayList<>();

        //Obtener el id del usuario
        idCuenta = requireArguments().getString("idCuenta");

        binding.recyViewListaNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    // Se recarga al volver al frente para no mostrar notificaciones ya atendidas
    @Override
    public void onResume() {
        super.onResume();
        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        controladorNotificaciones.obtenerListaNotificaciones(idCuenta, new ControladorNotificaciones.CallBackGenerico<List<Notificacion>>() {
            @Override
            public void onComplete(List<Notificacion> result) {
                if (binding == null) {
                    return;
                }

                listaNotificaciones.clear();
                listaNotificaciones.addAll(result);

                listaNotificacionesAdaptador = new ListaNotificacionesAdaptador(listaNotificaciones);
                binding.recyViewListaNotificaciones.setAdapter(listaNotificacionesAdaptador);

                if (listaNotificaciones.isEmpty()) {
                    binding.tVNoHayResultadosNotificaciones.setVisibility(View.VISIBLE);
                    binding.recyViewListaNotificaciones.setVisibility(View.GONE);
                } else {
                    binding.tVNoHayResultadosNotificaciones.setVisibility(View.GONE);
                    binding.recyViewListaNotificaciones.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener las notificaciones");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
