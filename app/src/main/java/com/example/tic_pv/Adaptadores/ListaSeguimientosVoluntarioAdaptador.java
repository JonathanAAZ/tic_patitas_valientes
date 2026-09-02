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
import com.example.tic_pv.Vista.SeguimientoVoluntarioChatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaSeguimientosVoluntarioAdaptador extends RecyclerView.Adapter<ListaSeguimientosVoluntarioAdaptador.SeguimientoVoluntarioViewHolder> {
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

    public ListaSeguimientosVoluntarioAdaptador(ArrayList<Seguimiento> listaSeguimientos) {
        this.listaSeguimientos = listaSeguimientos;
    }

    @NonNull
    @Override
    public SeguimientoVoluntarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_seguimiento_voluntario, parent, false);
        return new SeguimientoVoluntarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoVoluntarioViewHolder holder, int position) {
        Seguimiento seguimiento = listaSeguimientos.get(position);

        // Los nombres ya vienen desnormalizados en el seguimiento
        holder.tVNombreAdoptante.setText(seguimiento.getNombreAdoptante());
        holder.tVNombreMascota.setText(seguimiento.getNombreMascota());
        holder.tVEstado.setText(controladorSeguimiento.describirEstadoSeguimiento(seguimiento.getEstado()));

        // Un seguimiento cerrado solo se puede leer, así que el botón lo anuncia
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
        holder.cIVFotoPerfilAdoptante.setImageResource(R.drawable.logo_patitas_valientes);
        holder.cIVFotoMascota.setImageResource(R.drawable.logo_patitas_valientes);

        // Las fotos sí hay que consultarlas porque no se guardan en el seguimiento
        db.collection("Cuentas").document(seguimiento.getIdAdoptante()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    controladorUtilidades.insertarImagenDesdeBDD(document.getString("fotoPerfil"),
                            holder.cIVFotoPerfilAdoptante,
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

    public class SeguimientoVoluntarioViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cIVFotoPerfilAdoptante, cIVFotoMascota;
        TextView tVNombreAdoptante, tVNombreMascota, tVEstado, tVResponderSeguimiento;
        LinearLayout btnResponderSeguimiento;

        public SeguimientoVoluntarioViewHolder(View itemView) {
            super(itemView);
            cIVFotoPerfilAdoptante = itemView.findViewById(R.id.ivFotoAdoptanteSegui);
            cIVFotoMascota = itemView.findViewById(R.id.ivFotoMascotaSegui);
            tVNombreAdoptante = itemView.findViewById(R.id.tVNombreAdoptanteSeguiVoluntario);
            tVNombreMascota = itemView.findViewById(R.id.tVNombreMascotaSeguiVoluntario);
            tVEstado = itemView.findViewById(R.id.tVEstadoSeguiVoluntario);
            btnResponderSeguimiento = itemView.findViewById(R.id.lLResponderSeguiVoluntario);
            tVResponderSeguimiento = itemView.findViewById(R.id.tVResponderSeguiVoluntario);

            btnResponderSeguimiento.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), SeguimientoVoluntarioChatActivity.class);
                intent.putExtra("seguimiento", listaSeguimientos.get(getAbsoluteAdapterPosition()));
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
