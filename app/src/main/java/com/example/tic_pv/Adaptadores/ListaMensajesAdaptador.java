package com.example.tic_pv.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorUtilidades;
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
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

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

        // Limpiar el VideoView antes de reutilizarlo
        if (holder.viewVideoMensaje.isPlaying()) {
            holder.viewVideoMensaje.stopPlayback();
        }
        holder.viewVideoMensaje.setVideoURI(null);

        // Obtener los parámetros del contenedor del texto
        if (usuarioActual.getUid().equalsIgnoreCase(mensaje.getIdEmisor())) {
            holder.tVEmisor.setText("Tú"); // Texto del emisor
        } else {
            holder.tVEmisor.setText(mensaje.getEmisor()); // Texto del emisor
        }
        // Configurar el contenido del mensaje
        if (controladorUtilidades.esImagen(listaMensajes.get(position).getContenido())) {
            holder.tVContenido.setVisibility(View.GONE);
            holder.fLVideoMensaje.setVisibility(View.GONE);
            holder.iVFotoMensaje.setVisibility(View.VISIBLE);
            controladorUtilidades.insertarImagenDesdeBDD(
                    mensaje.getContenido(),
                    holder.iVFotoMensaje,
                    holder.itemView.getContext()
            );

        } else if (controladorUtilidades.esVideo(listaMensajes.get(position).getContenido())) {
            holder.tVContenido.setVisibility(View.GONE);
            holder.iVFotoMensaje.setVisibility(View.GONE);
            holder.fLVideoMensaje.setVisibility(View.VISIBLE);
            controladorUtilidades.insertarVideoDesdeBDD(
                    mensaje.getContenido(),
                    holder.viewVideoMensaje,
                    holder.barraProgresoVideoMensaje,
                    holder.iVReproducirVideoMensaje
            );

            MediaController mediaController = new MediaController(holder.itemView.getContext());
            mediaController.setAnchorView(holder.viewVideoMensaje);
            holder.viewVideoMensaje.setMediaController(mediaController);
        } else {
            holder.tVContenido.setVisibility(View.VISIBLE);
            holder.iVFotoMensaje.setVisibility(View.GONE);
            holder.fLVideoMensaje.setVisibility(View.GONE);
            holder.tVContenido.setText(mensaje.getContenido());
        }


    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tVEmisor, tVContenido;
        ImageView iVFotoMensaje, iVReproducirVideoMensaje;
        FrameLayout fLVideoMensaje;
        VideoView viewVideoMensaje;
        ProgressBar barraProgresoVideoMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            tVEmisor = itemView.findViewById(R.id.tVEmisor);
            tVContenido = itemView.findViewById(R.id.tVContenido);
            iVFotoMensaje = itemView.findViewById(R.id.iVFotoMensaje);
            iVReproducirVideoMensaje = itemView.findViewById(R.id.iVReproducirVideoMensaje);
            fLVideoMensaje = itemView.findViewById(R.id.fLVideoMensaje);
            viewVideoMensaje = itemView.findViewById(R.id.viewVideoMensaje);
            barraProgresoVideoMensaje = itemView.findViewById(R.id.barraProgresoVideoMensaje);
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

    @Override
    public void onViewRecycled(@androidx.annotation.NonNull MensajeViewHolder holder) {
        super.onViewRecycled(holder);

        // Liberar recursos del VideoView cuando se recicla
        if (holder.viewVideoMensaje != null) {
            holder.viewVideoMensaje.stopPlayback();
            holder.viewVideoMensaje.setVideoURI(null);
        }

    }
}
