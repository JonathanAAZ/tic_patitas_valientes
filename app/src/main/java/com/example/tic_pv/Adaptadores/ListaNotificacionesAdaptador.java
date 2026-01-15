package com.example.tic_pv.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Modelo.Notificacion;
import com.example.tic_pv.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaNotificacionesAdaptador extends RecyclerView.Adapter<ListaNotificacionesAdaptador.NotificacionViewHolder> {

    private ArrayList <Notificacion> listaNotificaciones;
    private ArrayList <Notificacion> listaNotificacionesOrdenada;

    public ArrayList<Notificacion> getListaNotificaciones() {
        return listaNotificaciones;
    }

    public void setListaNotificaciones(ArrayList<Notificacion> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    public ArrayList<Notificacion> getListaNotificacionesOrdenada() {
        return listaNotificacionesOrdenada;
    }

    public void setListaNotificacionesOrdenada(ArrayList<Notificacion> listaNotificacionesOrdenada) {
        this.listaNotificacionesOrdenada = listaNotificacionesOrdenada;
    }

    public ListaNotificacionesAdaptador(ArrayList<Notificacion> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_notificaciones, null, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.logo_patitas_valientes)
                .into(holder.iconoNotificacion);

        holder.tVTituloNotificacion.setText(listaNotificaciones.get(position).getTitulo());
        holder.tVCuerpoNotificacion.setText(listaNotificaciones.get(position).getCuerpo());
        holder.tVFechaNotificacion.setText(listaNotificaciones.get(position).getFechaNotificacion());
        holder.tVHoraNotificacion.setText(listaNotificaciones.get(position).getHoraNotificacion());
    }

    @Override
    public int getItemCount() { return listaNotificaciones.size(); }

    public class NotificacionViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout rLContenedorNotificacion;
        CircleImageView iconoNotificacion;
        TextView tVTituloNotificacion, tVCuerpoNotificacion, tVFechaNotificacion, tVHoraNotificacion;
        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);

            rLContenedorNotificacion = itemView.findViewById(R.id.rLContenedorNotificacion);
            iconoNotificacion = itemView.findViewById(R.id.cIVLogoNotificacion);
            tVTituloNotificacion = itemView.findViewById(R.id.tVTituloNotificacion);
            tVCuerpoNotificacion = itemView.findViewById(R.id.tVCuerpoNotificacion);
            tVFechaNotificacion = itemView.findViewById(R.id.tVFechaNotificacion);
            tVHoraNotificacion = itemView.findViewById(R.id.tVHoraNotificacion);
        }
    }
}
