package com.example.tic_pv.Adaptadores;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorHistorialMedico;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.HistorialMedico;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.EditarHistorialMedicoActivity;
import com.example.tic_pv.Vista.EditarMascotaActivity;

import java.util.ArrayList;

public class ListaVacunasAdaptador extends RecyclerView.Adapter<ListaVacunasAdaptador.VacunaViewHolder> {

    private ArrayList<HistorialMedico> listaVacunas;
    private String idMascota;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorHistorialMedico controladorHistorialMedico = new ControladorHistorialMedico();

    public ArrayList<HistorialMedico> getListaVacunas() {
        return listaVacunas;
    }

    public void setListaVacunas(ArrayList<HistorialMedico> listaVacunas) {
        this.listaVacunas = listaVacunas;
    }

    public String getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(String idMascota) {
        this.idMascota = idMascota;
    }

    public ListaVacunasAdaptador(ArrayList<HistorialMedico> listaVacunas, String idMascota) {
        this.listaVacunas = listaVacunas;
        this.idMascota = idMascota;
    }

    @NonNull
    @Override
    public VacunaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_vacuna, null, false);
        return new VacunaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacunaViewHolder holder, int position) {
        holder.tVNombreVacuna.setText(listaVacunas.get(position).getNombre());
        holder.tVFechaColocacion.setText(listaVacunas.get(position).getFechaColocacion());
        holder.tVFechaProxima.setText(listaVacunas.get(position).getFechaProxima());
    }

    @Override
    public int getItemCount() {
        return listaVacunas.size();
    }

    public class VacunaViewHolder extends RecyclerView.ViewHolder {

        TextView tVNombreVacuna, tVFechaColocacion, tVFechaProxima;
        LinearLayout btnEditarVacuna, btnEliminarVacuna, btnAceptar, btnCancelar;
        Dialog dialog;

        public VacunaViewHolder(@NonNull View itemView) {
            super(itemView);

            tVNombreVacuna = itemView.findViewById(R.id.tVNombreVacuna);
            tVFechaColocacion = itemView.findViewById(R.id.tVFechaVacuna);
            tVFechaProxima = itemView.findViewById(R.id.tVProximaVacuna);
            btnEditarVacuna = itemView.findViewById(R.id.lLEditarVacuna);
            btnEliminarVacuna = itemView.findViewById(R.id.lLEliminarVacuna);

            dialog = controladorUtilidades.crearAlertaPersonalizada("MENSAJE DE CONFIRMACIÓN",
                    "¿Seguro/a que desea eliminar esta vacuna?",
                    itemView.getContext());

            btnAceptar = dialog.findViewById(R.id.lLbtnAceptar);
            btnCancelar = dialog.findViewById(R.id.lLbtnCancelar);
            btnAceptar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);

            btnAceptar.setOnClickListener(v -> {
                controladorHistorialMedico.eliminarHistorial(listaVacunas.get(getBindingAdapterPosition()).getId(), idMascota, new ControladorHistorialMedico.Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        dialog.dismiss();
                        eliminarItem(getBindingAdapterPosition());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("REALTIME", "Error al eliminar el historial médico");
                    }
                });
            });

            btnCancelar.setOnClickListener(v -> {
                dialog.dismiss();
            });

            btnEditarVacuna.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent i = new Intent(context, EditarHistorialMedicoActivity.class);
                i.putExtra("idVacuna", listaVacunas.get(getBindingAdapterPosition()).getId());
                i.putExtra("idMascota", idMascota);
                i.putExtra("isVacuna", true);
                context.startActivity(i);
            });

            btnEliminarVacuna.setOnClickListener(v -> {
                dialog.create();
                dialog.show();
            });

        }
    }

    public void eliminarItem(int position) {
        listaVacunas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaVacunas.size());
    }
}
