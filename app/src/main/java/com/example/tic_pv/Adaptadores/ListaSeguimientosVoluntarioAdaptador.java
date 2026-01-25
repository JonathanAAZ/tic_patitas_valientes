package com.example.tic_pv.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListaSeguimientosVoluntarioAdaptador extends RecyclerView.Adapter<ListaSeguimientosVoluntarioAdaptador.SeguimientoVoluntarioViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Seguimiento> listaSeguimientos;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    public ListaSeguimientosVoluntarioAdaptador(ArrayList<Seguimiento> listaSeguimientos) {
        this.listaSeguimientos = listaSeguimientos;
    }

    @Override
    public SeguimientoVoluntarioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_seguimiento_voluntario, parent, false);
        return new SeguimientoVoluntarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SeguimientoVoluntarioViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listaSeguimientos.size();
    }

    public static class SeguimientoVoluntarioViewHolder extends RecyclerView.ViewHolder {
        public SeguimientoVoluntarioViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }
}
