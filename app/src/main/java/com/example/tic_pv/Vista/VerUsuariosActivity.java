package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Adaptadores.ListaUsuariosAdaptador;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerUsuariosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView listaUsuariosRV;
    private TextView noHayResultados;
    private ArrayList <Usuario> listaArrayUsuarios;
    private ArrayList <CuentaUsuario> listaArrayCuentas;
    private String fotoURL, rolCuenta;
    private SearchView searchView;
    private ListaUsuariosAdaptador adaptadorListaUsuarios;
    private Map<String, CuentaUsuario> cuentasMap = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_usuarios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Button btnAgregarVoluntario = findViewById(R.id.btnAgregarVoluntario);
        LinearLayout lYBtnAgregarVoluntario = findViewById(R.id.lYBtnAgregarVoluntario);
        searchView = findViewById(R.id.searchViewVerUsuarios);
        noHayResultados = findViewById(R.id.tVNoHayResultadosListaUsuarios);
//        Button btnRefrescar = findViewById(R.id.btnRefrescarVerUsuarios);

        listaUsuariosRV = findViewById(R.id.recyViewListaUsuarios);
        listaUsuariosRV.setLayoutManager(new LinearLayoutManager(this));

        listaArrayUsuarios = new ArrayList<>();
        listaArrayCuentas = new ArrayList<>();

        obtenerListaUsuarios();

//        btnAgregarVoluntario.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VerUsuariosActivity.this, CrearVoluntarioActivity.class);
//                startActivity(i);
//            }
//        });

        lYBtnAgregarVoluntario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerUsuariosActivity.this, CrearVoluntarioActivity.class);
                startActivity(i);
            }
        });

        searchView.setOnQueryTextListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

//    public void obtenerListaUsuarios() {
//
//        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    Usuario usuario = null;
//                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                        usuario = new Usuario();
//                        usuario.setNombre(documentSnapshot.getString("nombre"));
//                        usuario.setCedula(documentSnapshot.getString("cedula"));
//                        usuario.setCuenta(documentSnapshot.getString("cuentaUsuario"));
//                        listaArrayUsuarios.add(usuario);
//                    }
//
//                    obtenerCuentas();
//
//                } else {
//                    Toast.makeText(VerUsuariosActivity.this, "Error al obtener documentos", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    public void obtenerCuentas () {
//
//        db.collection("Cuentas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                        CuentaUsuario cuenta = new CuentaUsuario();
//                        cuenta.setIdCuenta(documentSnapshot.getId());
//                        cuenta.setFotoPerfil(documentSnapshot.getString("fotoPerfil"));
//                        cuenta.setRol(documentSnapshot.getString("rol"));
//                        cuenta.setEstadoCuenta(documentSnapshot.getString("estado"));
//                        cuentasMap.put(cuenta.getIdCuenta(), cuenta);
//                    }
//                    sincronizarUsuariosCuentas();
//
//                } else {
//                    Toast.makeText(VerUsuariosActivity.this, "Error al obtener documentos", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public void obtenerListaUsuarios() {

        db.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Usuario usuario = null;
                    listaArrayUsuarios.clear(); // Limpia la lista antes de llenarla

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        usuario = new Usuario();
                        usuario.setNombre(documentSnapshot.getString("nombre"));
                        usuario.setCedula(documentSnapshot.getString("cedula"));
                        usuario.setCuenta(documentSnapshot.getString("cuentaUsuario"));
                        listaArrayUsuarios.add(usuario);
                    }

                    obtenerCuentas();

                } else {
                    Toast.makeText(VerUsuariosActivity.this, "Error al obtener documentos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void obtenerCuentas() {

        db.collection("Cuentas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    cuentasMap.clear(); // Limpia el mapa antes de llenarlo

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        CuentaUsuario cuenta = new CuentaUsuario();
                        cuenta.setIdCuenta(documentSnapshot.getId());
                        cuenta.setFotoPerfil(documentSnapshot.getString("fotoPerfil"));
                        cuenta.setRol(documentSnapshot.getString("rol"));
                        cuenta.setEstadoCuenta(documentSnapshot.getString("estado"));
                        cuentasMap.put(cuenta.getIdCuenta(), cuenta);
                    }

                    sincronizarUsuariosCuentas();

                } else {
                    Toast.makeText(VerUsuariosActivity.this, "Error al obtener documentos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void sincronizarUsuariosCuentas() {
//        for (Usuario usuario : listaArrayUsuarios) {
//            if (cuentasMap.containsKey(usuario.getCuenta())) {
//                listaArrayCuentas.add(cuentasMap.get(usuario.getCuenta()));
//            }
//        }
//
//        adaptadorListaUsuarios = new ListaUsuariosAdaptador(listaArrayUsuarios, listaArrayCuentas);
//        listaUsuariosRV.setAdapter(adaptadorListaUsuarios);
//    }

    private void sincronizarUsuariosCuentas() {
        ArrayList<Usuario> usuariosFiltrados = new ArrayList<>();
        ArrayList<CuentaUsuario> cuentasFiltradas = new ArrayList<>();

        for (Usuario usuario : listaArrayUsuarios) {
            // Validar si la cuenta existe en el mapa
            if (cuentasMap.containsKey(usuario.getCuenta())) {
                CuentaUsuario cuenta = cuentasMap.get(usuario.getCuenta());

                // Filtrar usuarios cuyo rol NO sea "Administrador"
                if (!"Administrador".equalsIgnoreCase(cuenta.getRol())) {
                    usuariosFiltrados.add(usuario);
                    cuentasFiltradas.add(cuenta);
                }
            }
        }

        // Actualizar las listas con los datos filtrados
        listaArrayUsuarios.clear();
        listaArrayUsuarios.addAll(usuariosFiltrados);

        listaArrayCuentas.clear();
        listaArrayCuentas.addAll(cuentasFiltradas);

        // Configurar el adaptador con las listas filtradas
        adaptadorListaUsuarios = new ListaUsuariosAdaptador(listaArrayUsuarios, listaArrayCuentas);
        listaUsuariosRV.setAdapter(adaptadorListaUsuarios);

        if (listaArrayUsuarios.size() <= 0 && listaArrayCuentas.size() <= 0) {
            noHayResultados.setVisibility(View.VISIBLE);
        } else {
            noHayResultados.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adaptadorListaUsuarios.filtrar(newText);
        return false;
    }
}