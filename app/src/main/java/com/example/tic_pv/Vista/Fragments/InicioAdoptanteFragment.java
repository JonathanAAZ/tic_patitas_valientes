package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.R;
import com.example.tic_pv.Vista.CatalogoMascotasActivity;
import com.example.tic_pv.Vista.MisMascotasActivity;
import com.example.tic_pv.Vista.VerSolicitudesAdopcionActivity;
import com.example.tic_pv.databinding.FragmentInicioAdoptanteBinding;

public class InicioAdoptanteFragment extends Fragment {

    private FragmentInicioAdoptanteBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInicioAdoptanteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.iVBotonCatalogoMascotas.setOnClickListener(v -> {
//            AgregarMascotaFragment fragmentB = new AgregarMascotaFragment();
//            controladorUtilidades.reemplazarEntreFragments(R.id.flFragment, requireActivity().getSupportFragmentManager(),fragmentB);
            Intent i = new Intent(getActivity(), CatalogoMascotasActivity.class);
            startActivity(i);
//            reemplazarFragment(fragmentB);
        });

        binding.iVBotonVerSolicitudesEnviadas.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), VerSolicitudesAdopcionActivity.class);
            startActivity(i);
        });

        binding.iVBotonVerMisMascotas.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), MisMascotasActivity.class);
            startActivity(i);
        });


        // Inflate the layout for this fragment
        return view;
    }
}