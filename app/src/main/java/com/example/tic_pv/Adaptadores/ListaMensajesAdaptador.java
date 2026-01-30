package com.example.tic_pv.Adaptadores;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, null, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);

        // Obtener los parámetros del contenedor del texto
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.contenedorTexto.getLayoutParams();

        if (usuarioActual.getUid().equalsIgnoreCase(mensaje.getIdEmisor())) {
            // Mensaje del usuario actual: alinearlo a la derecha (END)
            params.gravity = Gravity.END; // Posicionar el contenedor completo al END
            holder.contenedorTexto.setLayoutParams(params); // Aplicar cambios dinámicos
            holder.tVEmisor.setText("Tú"); // Texto del emisor
        } else {
            // Mensaje del receptor: alinearlo a la izquierda (START)
            params.gravity = Gravity.START; // Posicionar el contenedor completo al START
            holder.contenedorTexto.setLayoutParams(params); // Aplicar cambios dinámicos
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

            contenedorTexto = itemView.findViewById(R.id.contenedorTexto);
            tVEmisor = itemView.findViewById(R.id.tVEmisor);
            tVContenido = itemView.findViewById(R.id.tVContenido);
        }
    }
}
