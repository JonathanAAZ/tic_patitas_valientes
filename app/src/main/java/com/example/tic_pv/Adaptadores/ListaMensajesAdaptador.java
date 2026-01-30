package com.example.tic_pv.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Modelo.Mensaje;
import com.example.tic_pv.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ListaMensajesAdaptador extends RecyclerView.Adapter<ListaMensajesAdaptador.MensajeViewHolder> {

    private ArrayList<Mensaje> listaMensajes;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser usuarioActual = mAuth.getCurrentUser();

    public ListaMensajesAdaptador(ArrayList<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_emisor, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_receptor, parent, false);
        }
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);

        // Obtener los parámetros del contenedor del texto
        if (usuarioActual.getUid().equalsIgnoreCase(mensaje.getIdEmisor())) {
            holder.tVEmisor.setText("Tú"); // Texto del emisor
        } else {
            holder.tVEmisor.setText(mensaje.getEmisor()); // Texto del emisor
        }
        // Configurar el contenido del mensaje
        holder.tVContenido.setText(mensaje.getContenido());
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tVEmisor, tVContenido;
        LinearLayout contenedorTexto;
        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            tVEmisor = itemView.findViewById(R.id.tVEmisor);
            tVContenido = itemView.findViewById(R.id.tVContenido);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (usuarioActual.getUid().equalsIgnoreCase(listaMensajes.get(position).getIdEmisor())) {
            return 1; // Tipo de vista para mensajes enviados por el usuario actual
        } else {
            return -1; // Tipo de vista para mensajes recibidos
        }
    }
}
