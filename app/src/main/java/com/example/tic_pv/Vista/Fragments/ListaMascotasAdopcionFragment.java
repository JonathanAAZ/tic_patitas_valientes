package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tic_pv.Adaptadores.ListaMascotasAdopcionAdaptador;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.Vista.GestionarMascotaActivity;
import com.example.tic_pv.databinding.FragmentListaMascotasAdopcionBinding;

import java.util.ArrayList;
import java.util.List;

public class ListaMascotasAdopcionFragment extends Fragment implements SearchView.OnQueryTextListener{
    private ArrayList <Mascota> listaMascotas;
    private ListaMascotasAdopcionAdaptador listaMascotasAdopcAdaptador;
    private FragmentListaMascotasAdopcionBinding binding;
    private ControladorMascota controladorMascota = new ControladorMascota();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListaMascotasAdopcionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        listaMascotas = new ArrayList<>();

        binding.recyViewListaMascotasAdopcion.setLayoutManager(new LinearLayoutManager(getContext()));

        //Botón para agregar mascota
        binding.lYBtnAgregarMascota.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), GestionarMascotaActivity.class);
            startActivity(i);
        });

        binding.ivFiltro.setOnClickListener(v -> {

            switch (binding.lYMostrarFiltrosMascotasAdopcion.getVisibility()) {
                case View.VISIBLE:
                    binding.lYMostrarFiltrosMascotasAdopcion.setVisibility(View.GONE);
                    break;
                case View.GONE:
                    binding.lYMostrarFiltrosMascotasAdopcion.setVisibility(View.VISIBLE);
                    break;
                case View.INVISIBLE:
                    break;
            }

        });

        configurarFiltros(binding.tVActivos);
        configurarFiltros(binding.tVInactivos);
        configurarFiltros(binding.tVVacunados);
        configurarFiltros(binding.tVNoVacunados);
        configurarFiltros(binding.tVEsterilizados);
        configurarFiltros(binding.tVNoEsterilizados);
        configurarFiltros(binding.tVDesparasitados);
        configurarFiltros(binding.tVNoDesparasitados);
        configurarFiltros(binding.tVQuitarFiltros);
        binding.searchViewMascotasAdopcion.setOnQueryTextListener(this);

        return view;
    }

    // Se recarga al volver al frente: es lo que refleja la mascota que se acaba de
    // registrar o editar
    @Override
    public void onResume() {
        super.onResume();
        cargarMascotas();
    }

    private void cargarMascotas() {
        controladorMascota.obtenerListaMascotasAdopcion(getContext(), new ControladorMascota.Callback<List<Mascota>>() {
            @Override
            public void onComplete(List<Mascota> result) {
                if (binding == null) {
                    return;
                }

                listaMascotas.clear();
                listaMascotas.addAll(result);

                listaMascotasAdopcAdaptador = new ListaMascotasAdopcionAdaptador(listaMascotas);
                binding.recyViewListaMascotasAdopcion.setAdapter(listaMascotasAdopcAdaptador);

                if (listaMascotas.isEmpty()) {
                    binding.tVNoHayResultadosListaMascotas.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultadosListaMascotas.setVisibility(View.GONE);
                }
                Log.d("LISTA_MASCOTAS", "Tamaño: " + listaMascotas.size());
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error al obtener mascotas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // El adaptador todavía no existe mientras la primera consulta está en curso
        if (listaMascotasAdopcAdaptador == null) {
            return false;
        }

        listaMascotasAdopcAdaptador.filtrar("Nombre", newText, binding.tVNoHayResultadosListaMascotas);
        return false;
    }

    private void configurarFiltros(TextView tv) {
        tv.setOnClickListener(v -> {
            if (listaMascotasAdopcAdaptador == null) {
                return;
            }

            listaMascotasAdopcAdaptador.filtrar(tv.getText().toString(), tv.getText().toString(),
                    binding.tVNoHayResultadosListaMascotas);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
