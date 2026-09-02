package com.example.tic_pv.Adaptadores;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.SeguimientoAdoptanteChatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaSeguimientosAdoptanteAdaptador extends RecyclerView.Adapter<ListaSeguimientosAdoptanteAdaptador.SeguimientoAdoptanteViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Seguimiento> listaSeguimientos;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();

    public ArrayList<Seguimiento> getListaSeguimientos() {
        return listaSeguimientos;
    }

    public void setListaSeguimientos(ArrayList<Seguimiento> listaSeguimientos) {
        this.listaSeguimientos = listaSeguimientos;
    }

    public ListaSeguimientosAdoptanteAdaptador(ArrayList<Seguimiento> listaSeguimientos) {
        this.listaSeguimientos = listaSeguimientos;
    }

    @NonNull
    @Override
    public SeguimientoAdoptanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_seguimiento_adoptante, parent, false);
        return new SeguimientoAdoptanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoAdoptanteViewHolder holder, int position) {
        Seguimiento seguimiento = listaSeguimientos.get(position);

        // Los nombres ya vienen desnormalizados en el seguimiento
        holder.tVNombreVoluntario.setText(seguimiento.getNombreVoluntario());
        holder.tVNombreMascota.setText(seguimiento.getNombreMascota());

        // Un seguimiento cerrado solo se puede leer, así que el botón lo anuncia
        holder.tVEstado.setText(controladorSeguimiento.describirEstadoSeguimiento(seguimiento.getEstado()));

        if (controladorSeguimiento.esSeguimientoCerrado(seguimiento.getEstado())) {
            holder.tVResponderSeguimiento.setText("Ver conversación");
            holder.btnResponderSeguimiento.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_2)));
        } else {
            holder.tVResponderSeguimiento.setText("Responder seguimiento");
            holder.btnResponderSeguimiento.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_green)));
        }

        // El fallback se pinta ya: si la vista viene reciclada no se queda con la foto
        // del ítem anterior mientras llega la consulta
        holder.cIVFotoPerfilVoluntario.setImageResource(R.drawable.logo_patitas_valientes);
        holder.cIVFotoMascota.setImageResource(R.drawable.logo_patitas_valientes);

        // Las fotos sí hay que consultarlas porque no se guardan en el seguimiento
        db.collection("Cuentas").document(seguimiento.getIdVoluntario()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    controladorUtilidades.insertarImagenDesdeBDD(document.getString("fotoPerfil"),
                            holder.cIVFotoPerfilVoluntario,
                            holder.itemView.getContext());
                }
            }
        });

        db.collection("Mascotas").document(seguimiento.getIdMascota()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot docMascota = task.getResult();
                if (docMascota.exists()) {
                    controladorUtilidades.insertarImagenDesdeBDD(docMascota.getString("fotoMascota"),
                            holder.cIVFotoMascota,
                            holder.itemView.getContext());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSeguimientos.size();
    }

    public class SeguimientoAdoptanteViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cIVFotoPerfilVoluntario, cIVFotoMascota;
        TextView tVNombreVoluntario, tVNombreMascota, tVEstado, tVResponderSeguimiento;
        LinearLayout btnResponderSeguimiento;

        public SeguimientoAdoptanteViewHolder(View itemView) {
            super(itemView);
            cIVFotoPerfilVoluntario = itemView.findViewById(R.id.ivFotoVoluntarioSeguiAdop);
            cIVFotoMascota = itemView.findViewById(R.id.ivFotoMascotaSeguiAdop);
            tVNombreVoluntario = itemView.findViewById(R.id.tVNombreVoluntarioSeguiAdoptante);
            tVNombreMascota = itemView.findViewById(R.id.tVNombreMascotaSeguiAdoptante);
            tVEstado = itemView.findViewById(R.id.tVEstadoSeguiAdoptante);
            btnResponderSeguimiento = itemView.findViewById(R.id.lLResponderSeguiAdoptante);
            tVResponderSeguimiento = itemView.findViewById(R.id.tVResponderSeguiAdoptante);

            btnResponderSeguimiento.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), SeguimientoAdoptanteChatActivity.class);
                intent.putExtra("seguimiento", listaSeguimientos.get(getAbsoluteAdapterPosition()));
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
