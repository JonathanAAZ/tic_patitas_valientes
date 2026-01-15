package com.example.tic_pv.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.CambiarClaveActivity;
import com.example.tic_pv.Vista.CatalogoMascotasActivity;
import com.example.tic_pv.Vista.GestionarMascotaActivity;
import com.example.tic_pv.Vista.GestionarSeguimientosActivity;
import com.example.tic_pv.Vista.GestionarSolicitudesAdopcionActivity;
import com.example.tic_pv.Vista.MenuAdministradorActivity;
import com.example.tic_pv.Vista.VerInformacionPerfilActivity;
import com.example.tic_pv.Vista.VerMascotasActivity;
import com.example.tic_pv.Vista.VerUsuariosActivity;
import com.example.tic_pv.databinding.FragmentInicioAdministradorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioAdministradorFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser authUsuario;
    private FragmentInicioAdministradorBinding binding;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInicioAdministradorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        authUsuario = auth.getCurrentUser();


        binding.iVBotonVerMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), VerMascotasActivity.class);
            startActivity(intent);
        });

        binding.iVBotonVerUsuarios.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), VerUsuariosActivity.class);
            startActivity(i);
        });

        binding.iVBotonCambiarClave.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), CambiarClaveActivity.class);
            startActivity(i);
        });

        binding.iVBotonVerSolicitudes.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), GestionarSolicitudesAdopcionActivity.class);
            startActivity(i);
        });

        binding.iVBotonGestionarSeguimientos.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), GestionarSeguimientosActivity.class);
            startActivity(i);
        });

//        binding.iVBotonVerPerfil.setOnClickListener(v -> {
//            Intent i = new Intent(getContext(), VerInformacionPerfilActivity.class);
//            String id = authUsuario.getUid();
//            i.putExtra("id", id);
//            startActivity(i);
//        });

        // Inflate the layout for this fragment
        return view;
    }

//    private void reemplazarFragment (Fragment fragment) {
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.flFragment, fragment) // fragment_container es el contenedor del fragmento en el layout de la actividad
//                .addToBackStack(null) // para que el usuario pueda volver con el botón de retroceso
//                .commit();
//    }
}