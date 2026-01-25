package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.R;
import com.example.tic_pv.Vista.MisMascotasActivity;
import com.example.tic_pv.Vista.MisSeguimientosActivity;
import com.example.tic_pv.databinding.FragmentInicioAdoptanteBinding;
import com.example.tic_pv.databinding.FragmentInicioVoluntarioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioVoluntarioFragment extends Fragment {

    private FragmentInicioVoluntarioBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser usuarioActual = mAuth.getCurrentUser();

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

        binding.iVBotonMisSeguimientos.setOnClickListener( v -> {
            Intent i = new Intent(getActivity(), MisSeguimientosActivity.class);
            i.putExtra("idVoluntario", usuarioActual.getUid());
            startActivity(i);
        });

        return view;
    }
}