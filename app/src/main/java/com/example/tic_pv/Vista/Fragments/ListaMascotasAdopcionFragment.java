package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.GestionarMascotaActivity;
import com.example.tic_pv.databinding.FragmentListaMascotasAdopcionBinding;

import java.util.ArrayList;
import java.util.List;

public class ListaMascotasAdopcionFragment extends Fragment implements SearchView.OnQueryTextListener{
    private ArrayList <Mascota> listaMascotas;
    private ListaMascotasAdopcionAdaptador listaMascotasAdopcAdaptador;
    private FragmentListaMascotasAdopcionBinding binding;
    private ControladorMascota controladorMascota = new ControladorMascota();
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListaMascotasAdopcionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment

        listaMascotas = new ArrayList<>();
//        listaMascotasAdopcAdaptador = new ListaMascotasAdopcionAdaptador(listaMascotas);

        binding.recyViewListaMascotasAdopcion.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.recyViewListaMascotasAdopcion.setAdapter(listaMascotasAdopcAdaptador);

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

        controladorMascota.obtenerListaMascotasAdopcion(getContext(), new ControladorMascota.Callback<List<Mascota>>() {
            @Override
            public void onComplete(List<Mascota> result) {
                listaMascotas.clear();
                listaMascotas.addAll(result);

                listaMascotasAdopcAdaptador = new ListaMascotasAdopcionAdaptador(listaMascotas);
                binding.recyViewListaMascotasAdopcion.setAdapter(listaMascotasAdopcAdaptador);

                if (listaMascotas.size() <= 0) {
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


//        listaMascotasAdopcAdaptador.notifyDataSetChanged();

//        listaMascotas = new ArrayList<>();
//
//        controladorMascota.obtenerListaMascotasAdopcion(listaMascotas, view.getContext());
//
//        binding.recyViewListaMascotasAdopcion.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        listaMascotasAdopcAdaptador = new ListaMascotasAdopcionAdaptador(listaMascotas);
//        binding.recyViewListaMascotasAdopcion.setAdapter(listaMascotasAdopcAdaptador);
        return view;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listaMascotasAdopcAdaptador.filtrar("Nombre", newText, binding.tVNoHayResultadosListaMascotas);
        return false;
    }

    private void configurarFiltros(TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaMascotasAdopcAdaptador.filtrar(tv.getText().toString(), tv.getText().toString(), binding.tVNoHayResultadosListaMascotas);
//                Toast.makeText(getContext(), "FILTRO SELECCIONADO " + tv.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}