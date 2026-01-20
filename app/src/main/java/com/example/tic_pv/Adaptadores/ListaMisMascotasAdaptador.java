package com.example.tic_pv.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorHistorialMedico;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.VerMiMascotaActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class  ListaMisMascotasAdaptador extends RecyclerView.Adapter<ListaMisMascotasAdaptador.MascotaViewHolder> {
    private ArrayList <Mascota> listaMisMascotas;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorHistorialMedico controladorHistorialMedico = new ControladorHistorialMedico();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Mascota> getListaMisMascotas() {
        return listaMisMascotas;
    }

    public void setListaMisMascotas(ArrayList<Mascota> listaMisMascotas) {
        this.listaMisMascotas = listaMisMascotas;
    }

    public ListaMisMascotasAdaptador(ArrayList<Mascota> listaMisMascotas) {
        this.listaMisMascotas = listaMisMascotas;
    }


    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_mis_mascotas, null, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        controladorUtilidades.insertarImagenDesdeBDD(listaMisMascotas.get(position).getFotoMascota(),
                holder.iVFotoMiMascota, holder.iVFotoMiMascota.getContext());

        holder.nombreMiMascota.setText(listaMisMascotas.get(position).getNombreMascota());

        controladorHistorialMedico.obtenerFechaUltimaVacuna(listaMisMascotas.get(position).getId(),
                holder.fechaUltimaVacuna);
        controladorHistorialMedico.obtenerFechaUltimaDesparasitacion(listaMisMascotas.get(position).getId(),
                holder.fechaUltimaDesp);

//        holder.fechaUltimaVacuna.setText(listaMisMascotas.get(position).getSexoMascota());
//        holder.fechaUltimaDesp.setText(listaMisMascotas.get(position).getSexoMascota());
    }
    @SuppressLint("NotifyDataSetChanged")
    private void actualizarAdaptador() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaMisMascotas.size();
    }

    public class MascotaViewHolder extends RecyclerView.ViewHolder{

        ImageView iVFotoMiMascota;
        TextView nombreMiMascota, fechaUltimaVacuna, fechaUltimaDesp;
        LinearLayout btnVerInformacion;
        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);

            iVFotoMiMascota = itemView.findViewById(R.id.ivFotoListaMiMascota);
            nombreMiMascota = itemView.findViewById(R.id.tVNombreMiMascota);
            fechaUltimaVacuna = itemView.findViewById(R.id.tVFechaUltimaVacuna);
            fechaUltimaDesp = itemView.findViewById(R.id.tVFechaUltimaDesparasitacion);
            btnVerInformacion = itemView.findViewById(R.id.lLBtnVerMiMascota);

            btnVerInformacion.setOnClickListener(v -> {
                Context context = v.getContext();

                Intent i = new Intent(context, VerMiMascotaActivity.class);
                i.putExtra("idMascota", listaMisMascotas.get(getBindingAdapterPosition()).getId());
                context.startActivity(i);
            });
        }
    }
}
