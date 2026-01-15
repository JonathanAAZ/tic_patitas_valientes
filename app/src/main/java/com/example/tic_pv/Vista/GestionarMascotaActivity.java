package com.example.tic_pv.Vista;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.AgregarMascotaFragment;
import com.example.tic_pv.Vista.Fragments.InicioAdministradorFragment;
import com.example.tic_pv.Vista.Fragments.NotificacionesFragment;
import com.example.tic_pv.Vista.Fragments.PerfilFragment;
import com.example.tic_pv.databinding.ActivityGestionarMascotaBinding;

public class GestionarMascotaActivity extends AppCompatActivity {

    private ActivityGestionarMascotaBinding binding;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestionar_mascota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Ajustar el padding del contenedor principal
            int bottomPadding = imeInsets.bottom > 0 ? imeInsets.bottom : systemBars.bottom;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            // Verificar si hay un EditText enfocado
            View focusedView = getCurrentFocus();
            if (focusedView instanceof EditText && imeInsets.bottom > 0) {
                int[] location = new int[2];
                focusedView.getLocationInWindow(location); // Cambiar a `getLocationInWindow` para mayor precisión

                // Altura disponible para el contenido visible (pantalla - teclado)
                int availableHeight = getResources().getDisplayMetrics().heightPixels - imeInsets.bottom;

                // Asegurar que el campo no sea cubierto por el teclado
                int focusedViewBottom = location[1] + focusedView.getHeight();
                int scrollDistance = Math.max(0, focusedViewBottom - availableHeight); // Evitar valores negativos

                if (scrollDistance > 0) {
                    // Limitar el desplazamiento para evitar que el EditText desaparezca en la parte superior
                    ScrollView scrollView = findViewById(R.id.scrollCrearCuenta);
                    if (scrollView != null) {
                        int maxScroll = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                        int currentScrollY = scrollView.getScrollY();
                        int finalScroll = Math.min(currentScrollY + scrollDistance + 20, maxScroll);

                        scrollView.post(() -> scrollView.smoothScrollTo(0, finalScroll));
                    }
                }
            }

            return insets;
        });

        binding = ActivityGestionarMascotaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controladorUtilidades.reemplazarFragments(R.id.fLFragmentGestionarMascota, getSupportFragmentManager(), new AgregarMascotaFragment());

    }

//    private void seleccionarFragment(int id) {
////        binding.bottomNavigationViewMenu.setSelectedItemId(id); // Actualizar selección visual en el BottomNavigationView
//
//        // Cargar el fragmento correspondiente
//        switch (id) {
//            case 1:
//                controladorUtilidades.reemplazarFragments(R.id.fLFragmentGestionarMascota, getSupportFragmentManager(), new AgregarMascotaFragment());
//            case 2:
//
//        }
//        if (id == 1) {
//            controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new InicioAdministradorFragment());
//        } else if (id == R.id.bMPerfil) {
//            Bundle bundle = new Bundle();
//            String idCuenta = firebaseUser != null ? firebaseUser.getUid() : null;
//            bundle.putString("idCuenta", idCuenta);
//            PerfilFragment fragment = new PerfilFragment();
//            fragment.setArguments(bundle);
//            controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), fragment);
//        } else if (id == R.id.bMNotificaciones) {
//            controladorUtilidades.reemplazarFragments(R.id.flFragment, getSupportFragmentManager(), new NotificacionesFragment());
//        }
//    }
}