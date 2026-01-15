package com.example.tic_pv.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorHistorialMedico;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Desparasitacion;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.EditarHistorialMedicoActivity;

import java.util.ArrayList;

public class ListaDesparasitacionesAdaptador extends RecyclerView.Adapter<ListaDesparasitacionesAdaptador.DesparasitacionViewHolder> {

    private ArrayList<Desparasitacion> listaDesparasitaciones;
    private String idMascota;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorHistorialMedico controladorHistorialMedico = new ControladorHistorialMedico();

    public ArrayList<Desparasitacion> getListaDesparasitaciones() {
        return listaDesparasitaciones;
    }

    public void setListaDesparasitaciones(ArrayList<Desparasitacion> listaDesparasitaciones) {
        this.listaDesparasitaciones = listaDesparasitaciones;
    }

    public String getIdMascota() {
        return idMascota;
    }

public void setIdMascota(String idMascota) {
        this.idMascota = idMascota;
    }

    public ListaDesparasitacionesAdaptador(ArrayList<Desparasitacion> listaDesparasitaciones, String idMascota) {
        this.listaDesparasitaciones = listaDesparasitaciones;
        this.idMascota = idMascota;
    }

    @Override
    public DesparasitacionViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_desparasitacion, null, false);
        return new DesparasitacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DesparasitacionViewHolder holder, int position) {
        holder.tVNombreDesparasitacion.setText(listaDesparasitaciones.get(position).getNombre());
        holder.tVFechaColocacionDesparasitacion.setText(listaDesparasitaciones.get(position).getFechaColocacion());
        holder.tVFechaProximaDesparasitacion.setText(listaDesparasitaciones.get(position).getFechaProxima());
        holder.tVCantidadDesparasitante.setText(String.valueOf(listaDesparasitaciones.get(position).getCantidadDesparasitante()));
    }

    @Override
    public int getItemCount() {
        return listaDesparasitaciones.size();
    }

    public class DesparasitacionViewHolder extends RecyclerView.ViewHolder {
        TextView tVNombreDesparasitacion, tVFechaColocacionDesparasitacion, tVFechaProximaDesparasitacion, tVCantidadDesparasitante;
        LinearLayout btnEditarDesparasitacion, btnEliminarDesparasitacion, btnAceptar, btnCancelar;
        Dialog dialog;
        public DesparasitacionViewHolder(@NonNull View itemView) {
            super(itemView);

            tVNombreDesparasitacion = itemView.findViewById(R.id.tVNombreDesparasitacion);
            tVFechaColocacionDesparasitacion = itemView.findViewById(R.id.tVFechaDesparasitacion);
            tVFechaProximaDesparasitacion = itemView.findViewById(R.id.tVProximaDesparasitacion);
            tVCantidadDesparasitante = itemView.findViewById(R.id.tVCantidadDesparasitacion);
            btnEditarDesparasitacion = itemView.findViewById(R.id.lLEditarDesparasitacion);
            btnEliminarDesparasitacion = itemView.findViewById(R.id.lLEliminarDesparasitacion);

            dialog = controladorUtilidades.crearAlertaPersonalizada("MENSAJE DE CONFIRMACIÓN",
                    "¿Seguro/a que desea eliminar esta desparasitación?",
                    itemView.getContext());

            btnAceptar = dialog.findViewById(R.id.lLbtnAceptar);
            btnCancelar = dialog.findViewById(R.id.lLbtnCancelar);
            btnAceptar.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);

            btnAceptar.setOnClickListener(v -> {
                controladorHistorialMedico.eliminarHistorial(listaDesparasitaciones.get(getBindingAdapterPosition()).getId(), idMascota, new ControladorHistorialMedico.Callback<Boolean>() {
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

            btnEditarDesparasitacion.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent i = new Intent(context, EditarHistorialMedicoActivity.class);
                i.putExtra("idVacuna", listaDesparasitaciones.get(getBindingAdapterPosition()).getId());
                i.putExtra("idMascota", idMascota);
                i.putExtra("isVacuna", false);
                context.startActivity(i);
            });

            btnEliminarDesparasitacion.setOnClickListener(v -> {
                dialog.create();
                dialog.show();
            });
        }
    }

    public void eliminarItem(int position) {
        listaDesparasitaciones.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaDesparasitaciones.size());
    }
}
