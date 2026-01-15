package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.InicioAdministradorFragment;
import com.example.tic_pv.Vista.Fragments.InicioAdoptanteFragment;
import com.example.tic_pv.Vista.Fragments.NotificacionesFragment;
import com.example.tic_pv.Vista.Fragments.PerfilFragment;
import com.example.tic_pv.databinding.ActivityBottomNavigationMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class BottomNavigationMenu extends AppCompatActivity {

    private ActivityBottomNavigationMenuBinding binding, bindingBotones;
    private int idBoton;
    private String rol;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ImageView iVBotonAgregarMascota;
    private Mascota mascotaCreada;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_navigation_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityBottomNavigationMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        idBoton = i.getIntExtra("id", R.id.bMInicio);
        rol = i.getStringExtra("rol");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

//        binding.bottomNavigationViewMenu.setBackground(null);

        //Seleccionar el fragment que va a aparecer
        seleccionarFragment(idBoton);
//        controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new InicioAdministradorFragment());

        binding.bottomNavigationViewMenu.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.bMInicio) {
                if (rol.equalsIgnoreCase("Administrador")) {
                    controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new InicioAdministradorFragment());
                } else if (rol.equalsIgnoreCase("Adoptante")) {
                    controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new InicioAdoptanteFragment());
                }
            } else if (menuItem.getItemId() == R.id.bMPerfil) {
                Bundle bundle = new Bundle();
                String id = firebaseUser.getUid();
                bundle.putString("idCuenta", id);
                bundle.putString("rol", rol);
                PerfilFragment fragment = new PerfilFragment();
                fragment.setArguments(bundle);
                controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), fragment);
            } else if (menuItem.getItemId() == R.id.bMNotificaciones) {
                Bundle bundle = new Bundle();
                String id = firebaseUser.getUid();
                bundle.putString("idCuenta", id);
                bundle.putString("rol", rol);
                NotificacionesFragment fragment = new NotificacionesFragment();
                fragment.setArguments(bundle);
                controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), fragment);
            }

            return true;
        });
    }

    private void seleccionarFragment(int id) {
        binding.bottomNavigationViewMenu.setSelectedItemId(id); // Actualizar selección visual en el BottomNavigationView

        // Cargar el fragmento correspondiente
        if (id == R.id.bMInicio) {
            if (rol.equalsIgnoreCase("Administrador")) {
                controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new InicioAdministradorFragment());
            } else if (rol.equalsIgnoreCase("Adoptante")) {
                controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new InicioAdoptanteFragment());
            }
        } else if (id == R.id.bMPerfil) {
            Bundle bundle = new Bundle();
            String idCuenta = firebaseUser != null ? firebaseUser.getUid() : null;
            bundle.putString("idCuenta", idCuenta);
            PerfilFragment fragment = new PerfilFragment();
            fragment.setArguments(bundle);
            controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), fragment);
        } else if (id == R.id.bMNotificaciones) {
            Bundle bundle = new Bundle();
            String idCuenta = firebaseUser.getUid();
            bundle.putString("idCuenta", idCuenta);
            NotificacionesFragment fragment = new NotificacionesFragment();
            fragment.setArguments(bundle);
            controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), fragment);
        }
    }

//    private void reemplazarFragments (Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.flFragment, fragment);
//        fragmentTransaction.commit();
//    }
}