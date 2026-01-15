package com.example.tic_pv.Vista;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.ListaUsuariosAdaptador;
import com.example.tic_pv.Adaptadores.ListaUsuariosSeguimientosAdaptador;
import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivityGestionarSeguimientosBinding;
import com.example.tic_pv.databinding.ActivityGestionarSolicitudesAdopcionBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestionarSeguimientosActivity extends AppCompatActivity {

    private ActivityGestionarSeguimientosBinding binding;
    private final ControladorUsuario controladorUsuario = new ControladorUsuario();
    private ArrayList<Usuario> listaVoluntarios = new ArrayList<>();
    private HashMap<String, CuentaUsuario> hashCuentas = new HashMap<>();
    private ArrayList<CuentaUsuario> listaCuentas = new ArrayList<>();
    private ListaUsuariosSeguimientosAdaptador adaptadorVoluntarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestionar_seguimientos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityGestionarSeguimientosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presentarVoluntarios();
    }

    private void presentarVoluntarios() {
        controladorUsuario.obtenerListaUsuarios(new ControladorUsuario.Callback<ArrayList<Usuario>>() {
            @Override
            public void onComplete(ArrayList<Usuario> result) {
                listaVoluntarios.clear();
                listaVoluntarios.addAll(result);

                controladorUsuario.obtenerCuentas(new ControladorUsuario.CallbackCuentas<Map<String, CuentaUsuario>>() {
                    @Override
                    public void onComplete(Map<String, CuentaUsuario> result) {
                        hashCuentas.clear();
                        hashCuentas.putAll(result);

                        sincronizarUsuariosCuentas();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("ERROR", "Error al obtener la lista de cuentas");
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR", "Error al obtener lista de voluntarios");
            }
        });
    }

    private void sincronizarUsuariosCuentas() {
        ArrayList<Usuario> usuariosFiltrados = new ArrayList<>();
        ArrayList<CuentaUsuario> cuentasFiltradas = new ArrayList<>();

        for (Usuario usuario : listaVoluntarios) {
            // Validar si la cuenta existe en el mapa
            if (hashCuentas.containsKey(usuario.getCuenta())) {
                CuentaUsuario cuenta = hashCuentas.get(usuario.getCuenta());

                // Filtrar usuarios cuyo rol sea "Voluntario"
                assert cuenta != null;
                if ("Voluntario".equalsIgnoreCase(cuenta.getRol()) &&
                        EstadosCuentas.ACTIVO.toString().equalsIgnoreCase(cuenta.getEstadoCuenta())) {
                    usuariosFiltrados.add(usuario);
                    cuentasFiltradas.add(cuenta);
                }
            }
        }

        // Actualizar las listas con los datos filtrados
        listaVoluntarios.clear();
        listaVoluntarios.addAll(usuariosFiltrados);

        listaCuentas.clear();
        listaCuentas.addAll(cuentasFiltradas);


        // Configurar el adaptador con las listas filtradas
        binding.recyViewVoluntarios.setLayoutManager(new LinearLayoutManager(this));

        adaptadorVoluntarios = new ListaUsuariosSeguimientosAdaptador(listaVoluntarios, listaCuentas);
        binding.recyViewVoluntarios.setAdapter(adaptadorVoluntarios);

        if (listaVoluntarios.size() <= 0 && listaCuentas.size() <= 0) {
            binding.tVNoHayVoluntarios.setVisibility(View.VISIBLE);
        } else {
            binding.tVNoHayVoluntarios.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presentarVoluntarios();
    }
}