package com.example.tic_pv.Vista;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.CatalogoMascotaAdopcionAdaptador;
import com.example.tic_pv.Adaptadores.ListaMascotasAdopcionAdaptador;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Modelo.CatalogoMascotas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivityCatalogoMascotasBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CatalogoMascotasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ArrayList <Mascota> listaMascotas;
    private CatalogoMascotas catalogoMascotas;
    private CatalogoMascotaAdopcionAdaptador listaMascotasAdopcAdaptador;
    private ControladorMascota controladorMascota = new ControladorMascota();
    private ActivityCatalogoMascotasBinding binding;
    private Dialog requisitosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCatalogoMascotasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Crear la lista de mascotas en adopcion
        catalogoMascotas = new CatalogoMascotas();

        //Crear el Dialog para el popup de los requisitos
        requisitosDialog = new Dialog(this);

//        listaMascotas = new ArrayList<>();

        //Definir el layout manager de la Recycler View para tener dos columnas
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recyViewCatalogoAdopcion.setLayoutManager(gridLayoutManager);

        //Configurar la aparición de los filtros al presionar el botón
        binding.ivFiltroCatalogoMascotas.setOnClickListener(v -> {
            switch (binding.gLMostrarFiltrosCatalogoMascotas.getVisibility()) {
                case View.VISIBLE:
                    binding.gLMostrarFiltrosCatalogoMascotas.setVisibility(View.GONE);
                    break;
                case View.GONE:
                    binding.gLMostrarFiltrosCatalogoMascotas.setVisibility(View.VISIBLE);
                    break;
                case View.INVISIBLE:
                    break;
            }
        });

        binding.lLBotonVerRequisitosCatalogo.setOnClickListener(v -> {
            requisitosDialog.setContentView(R.layout.popup_requisitos_adopcion);
            Objects.requireNonNull(requisitosDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageView btnSalirRequisitos = requisitosDialog.findViewById(R.id.iVSalirRequisitosAdopcion);

            btnSalirRequisitos.setOnClickListener(view -> {
                requisitosDialog.dismiss();
            });

            requisitosDialog.create();
            requisitosDialog.show();
        });

        configurarFiltros(binding.tVCachorros);
        configurarFiltros(binding.tVAdultos);
        configurarFiltros(binding.tVCaninos);
        configurarFiltros(binding.tVFelinos);
        configurarFiltros(binding.tVMachos);
        configurarFiltros(binding.tVHembras);
        configurarFiltros(binding.tVVacunados);
        configurarFiltros(binding.tVEsterilizados);
        configurarFiltros(binding.tVQuitarFiltrosCatalogoMascotas);
        binding.searchViewCatalogoMascotas.setOnQueryTextListener(this);
    }

    // Se recarga al volver al frente: una mascota puede haberse adoptado o retirado del
    // catálogo mientras el usuario estaba en otra pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarMascotasAdopcion();
    }

    private void cargarMascotasAdopcion() {
        catalogoMascotas.obtenerListaMascotasAdopcion(this, new CatalogoMascotas.Callback<List<Mascota>>() {
            @Override
            public void onComplete(List<Mascota> result) {
                catalogoMascotas.getListaAnimalesAdopcion().clear();
                catalogoMascotas.getListaAnimalesAdopcion().addAll(result);

                listaMascotasAdopcAdaptador = new CatalogoMascotaAdopcionAdaptador(catalogoMascotas.getListaAnimalesAdopcion());
                binding.recyViewCatalogoAdopcion.setAdapter(listaMascotasAdopcAdaptador);

                if (catalogoMascotas.getListaAnimalesAdopcion().isEmpty()) {
                    binding.tVNoHayResultados.setVisibility(View.VISIBLE);
                } else {
                    binding.tVNoHayResultados.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CatalogoMascotasActivity.this, "Error al obtener la mascotas desde la base de datos", Toast.LENGTH_SHORT).show();
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

        listaMascotasAdopcAdaptador.filtrar("Nombre", newText, binding.tVNoHayResultados);
        return false;
    }

    private void configurarFiltros(TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listaMascotasAdopcAdaptador == null) {
                    return;
                }

                ocultarTeclado();
                listaMascotasAdopcAdaptador.filtrar(tv.getText().toString(), tv.getText().toString(), binding.tVNoHayResultados);
            }
        });
    }

    private void ocultarTeclado () {
        // Obtener el servicio InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Ocultar el teclado
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}
