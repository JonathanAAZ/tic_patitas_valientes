package com.example.tic_pv.Adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public SeguimientoVoluntarioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_seguimiento_voluntario, null, false);
        return new SeguimientoVoluntarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SeguimientoVoluntarioViewHolder holder, int position) {
        holder.tVEstado.setText(listaSeguimientos.get(position).getEstado());
        db.collection("Cuentas").document(listaSeguimientos.get(position).getIdAdoptante()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    controladorUtilidades.insertarImagenDesdeBDD(document.getString("fotoPerfil"),
                            holder.cIVFotoPerfilAdoptante,
                            holder.itemView.getContext());

                    holder.tVNombreAdoptante.setText(listaSeguimientos.get(position).getNombreAdoptante());

                    db.collection("Mascotas").document(listaSeguimientos.get(position).getIdMascota()).get().addOnCompleteListener(command -> {
                       if (task.isSuccessful()) {
                            DocumentSnapshot docMascota = command.getResult();
                            if (docMascota.exists()) {
                                 controladorUtilidades.insertarImagenDesdeBDD(docMascota.getString("fotoMascota"),
                                        holder.cIVFotoMascota,
                                        holder.itemView.getContext());

                                 holder.tVNombreMascota.setText(listaSeguimientos.get(position).getNombreMascota());
                            }
                       }
                    });
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
        TextView tVNombreAdoptante, tVNombreMascota, tVEstado;
        LinearLayout btnResponderSeguimiento;
        public SeguimientoVoluntarioViewHolder(View itemView) {

            super(itemView);
            cIVFotoPerfilAdoptante = itemView.findViewById(R.id.ivFotoAdoptanteSegui);
            cIVFotoMascota = itemView.findViewById(R.id.ivFotoMascotaSegui);
            tVNombreAdoptante = itemView.findViewById(R.id.tVNombreAdoptanteSeguiVoluntario);
            tVNombreMascota = itemView.findViewById(R.id.tVNombreMascotaSeguiVoluntario);
            tVEstado = itemView.findViewById(R.id.tVEstadoSeguiVoluntario);
            btnResponderSeguimiento = itemView.findViewById(R.id.lLResponderSeguiVoluntario);

            btnResponderSeguimiento.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), SeguimientoVoluntarioChatActivity.class);
                intent.putExtra("seguimiento", listaSeguimientos.get(getAbsoluteAdapterPosition()));
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
