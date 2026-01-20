package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.R;
import com.example.tic_pv.Vista.MisMascotasActivity;
import com.example.tic_pv.databinding.FragmentInicioAdoptanteBinding;
import com.example.tic_pv.databinding.FragmentInicioVoluntarioBinding;

public class InicioVoluntarioFragment extends Fragment {

    private FragmentInicioVoluntarioBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInicioVoluntarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.iVBotonListaMascotas.setOnClickListener( v -> {
            Intent i = new Intent(getActivity(), MisMascotasActivity.class);
            startActivity(i);
        });

        return view;
    }
}