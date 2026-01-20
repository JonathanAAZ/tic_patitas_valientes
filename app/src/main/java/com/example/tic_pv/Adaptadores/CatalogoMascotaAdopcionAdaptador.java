package com.example.tic_pv.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.ProcesoAdopcionActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CatalogoMascotaAdopcionAdaptador extends RecyclerView.Adapter <CatalogoMascotaAdopcionAdaptador.MascotaViewHolder> {

    private ArrayList <Mascota> listaMascotasAdopcion;
    private ArrayList <Mascota> listaMascotasAdopcionOriginal;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Mascota> getListaMascotasAdopcion() {
        return listaMascotasAdopcion;
    }

    public void setListaMascotasAdopcion(ArrayList<Mascota> listaMascotasAdopcion) {
        this.listaMascotasAdopcion = listaMascotasAdopcion;
    }

    public ArrayList<Mascota> getListaMascotasAdopcionOriginal() {
        return listaMascotasAdopcionOriginal;
    }

    public void setListaMascotasAdopcionOriginal(ArrayList<Mascota> listaMascotasAdopcionOriginal) {
        this.listaMascotasAdopcionOriginal = listaMascotasAdopcionOriginal;
    }

    public CatalogoMascotaAdopcionAdaptador(ArrayList<Mascota> listaMascotasAdopcion) {
        this.listaMascotasAdopcion = listaMascotasAdopcion;

        listaMascotasAdopcionOriginal = new ArrayList<>();
        listaMascotasAdopcionOriginal.addAll(listaMascotasAdopcion);
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_catalogo_adopcion, null, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(listaMascotasAdopcion.get(position).getFotoMascota())
                .override(200, 200)
                .fitCenter()
                .into(holder.iVFotoMascotaCatalogo);
        holder.tVNombreMascotaCatalogo.setText(listaMascotasAdopcion.get(position).getNombreMascota());
        holder.tVEdadMascotaCatalogo.setText(listaMascotasAdopcion.get(position).getEdadMascota());
        holder.tVEspecieMascotaCatalogo.setText(listaMascotasAdopcion.get(position).getEspecieMascota());
        holder.tVSexoMascotaCatalogo.setText(listaMascotasAdopcion.get(position).getSexoMascota());

        verificarAdopcionesProceso(listaMascotasAdopcion.get(position).getId(),
                holder.tVMensajeAdopcionProceso, holder.btnIniciarProcesoAdopcion);

    }

    @Override
    public int getItemCount() {
        return listaMascotasAdopcion.size();
    }

    public class MascotaViewHolder extends RecyclerView.ViewHolder {
        ImageView iVFotoMascotaCatalogo;
        LinearLayout btnIniciarProcesoAdopcion;
        TextView tVNombreMascotaCatalogo, tVEdadMascotaCatalogo, tVEspecieMascotaCatalogo, tVSexoMascotaCatalogo, tVMensajeAdopcionProceso;
        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            iVFotoMascotaCatalogo = itemView.findViewById(R.id.iVFotoMascotaCatalogo);
            tVNombreMascotaCatalogo = itemView.findViewById(R.id.tVNombreMascotaCatalogo);
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tVNombreMascotaCatalogo, 12, 20, 2, TypedValue.COMPLEX_UNIT_SP);
            tVEdadMascotaCatalogo = itemView.findViewById(R.id.tVEdadMascotaCatalogo);
            tVEspecieMascotaCatalogo = itemView.findViewById(R.id.tVEspecieMascotaCatalogo);
            tVSexoMascotaCatalogo = itemView.findViewById(R.id.tVSexoMascotaCatalogo);
            btnIniciarProcesoAdopcion = itemView.findViewById(R.id.lLBotonIniciarAdopcion);
            tVMensajeAdopcionProceso = itemView.findViewById(R.id.tVMensajeAdopcionProceso);


            btnIniciarProcesoAdopcion.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent i = new Intent(context, ProcesoAdopcionActivity.class);
                i.putExtra("idMascota", listaMascotasAdopcion.get(getBindingAdapterPosition()).getId());
                i.putExtra("proceso", "inicio");
                context.startActivity(i);
            });

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filtrar (String filtro, String criterio, TextView tVNoExiste) {
        String busqueda = criterio.trim();
        ControladorMascota controladorMascota = new ControladorMascota();
        controladorMascota.filtrarMascotas(filtro, busqueda, listaMascotasAdopcionOriginal, listaMascotasAdopcion, tVNoExiste);
        notifyDataSetChanged();
    }

    public void verificarAdopcionesProceso (String idMascota, TextView text, LinearLayout button) {
        String estadoRechazada = EstadosCuentas.RECHAZADA.toString();

        db.collection("Adopciones")
                .whereEqualTo("mascotaAdopcion", idMascota)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // filtra en cliente solo lo necesario
                    long count = queryDocumentSnapshots.getDocuments()
                            .stream()
                            .filter(doc -> !estadoRechazada.equalsIgnoreCase(doc.getString("estado")))
                            .count();

                    boolean disponible = count == 0;

                    text.setVisibility(disponible ? View.GONE : View.VISIBLE);
                    button.setVisibility(disponible ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "No se pudo obtener la lista de solicitudes pendientes: " + e.getMessage());
                });
    }
}
