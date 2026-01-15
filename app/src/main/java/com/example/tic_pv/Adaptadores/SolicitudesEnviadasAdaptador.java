package com.example.tic_pv.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.CompletarProcesoAdopcionActivity;
import com.example.tic_pv.Vista.EditarMascotaActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.kernel.geom.Line;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SolicitudesEnviadasAdaptador extends RecyclerView.Adapter<SolicitudesEnviadasAdaptador.AdopcionViewHolder> {

    private ArrayList<Adopcion> listaAdopcionesEnviadas;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    public SolicitudesEnviadasAdaptador(ArrayList<Adopcion> listaAdopcionesEnviadas) {
        this.listaAdopcionesEnviadas = listaAdopcionesEnviadas;
    }

    public ArrayList<Adopcion> getListaAdopcionesEnviadas() {
        return listaAdopcionesEnviadas;
    }

    public void setListaAdopcionesEnviadas(ArrayList<Adopcion> listaAdopcionesEnviadas) {
        this.listaAdopcionesEnviadas = listaAdopcionesEnviadas;
    }

    @NonNull
    @Override
    public AdopcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_solicitudes_enviadas, null, false);
        return new AdopcionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdopcionViewHolder holder, int position) {
        String idMascota = listaAdopcionesEnviadas.get(position).getMascotaAdopcion();
        String estado = listaAdopcionesEnviadas.get(position).getEstadoAdopcion();

        if (estado.equalsIgnoreCase(EstadosCuentas.RECHAZADA.toString())) {
            holder.btnContinuarProceso.setVisibility(View.GONE);
            holder.btnVerMotivo.setVisibility(View.VISIBLE);
            holder.tVEstadoSolicitud.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        } else if (estado.equalsIgnoreCase(EstadosCuentas.ACEPTADA.toString())) {
            holder.btnContinuarProceso.setVisibility(View.VISIBLE);
            holder.btnVerMotivo.setVisibility(View.GONE);
            holder.tVEstadoSolicitud.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_green));
        } else if (estado.equalsIgnoreCase(EstadosCuentas.EN_REVISION.toString())) {
            holder.btnContinuarProceso.setVisibility(View.GONE);
            holder.btnVerMotivo.setVisibility(View.GONE);
            holder.tVMensajeEnRevision.setVisibility(View.VISIBLE);
            holder.tVEstadoSolicitud.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_green));
        } else {
            holder.btnContinuarProceso.setVisibility(View.GONE);
            holder.btnVerMotivo.setVisibility(View.GONE);
            holder.tVEstadoSolicitud.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dashboard_blue));
        }

        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String nombreMascota = documentSnapshot.getString("nombre");
                        String fotoMascota = documentSnapshot.getString("fotoMascota");
                        holder.tVNombreMacota.setText(nombreMascota);

                        Glide.with(holder.itemView.getContext())
                                .load(fotoMascota)
                                .fitCenter()
                                .placeholder(R.drawable.ic_cargando)
                                .into(holder.iVFotoMascotaSolicitudEnviada);
                    } else {
                        Log.e("ERROR", "No existe el documento");
                    }
                }
            }
        });

        holder.tVFechaEmision.setText(listaAdopcionesEnviadas.get(position).getFechaEmision());
        if (estado.equalsIgnoreCase(EstadosCuentas.EN_REVISION.toString())) {
            estado = controladorUtilidades.reemplazarPalabraString(estado, "_", " ");
            estado = controladorUtilidades.reemplazarPalabraString(estado, "O", "Ó");
        }
        holder.tVEstadoSolicitud.setText(estado);

    }

    @Override
    public int getItemCount() { return listaAdopcionesEnviadas.size(); }

    public class AdopcionViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iVFotoMascotaSolicitudEnviada;
        TextView tVNombreMacota, tVFechaEmision, tVEstadoSolicitud, tVMensajeEnRevision;
        LinearLayout btnContinuarProceso, btnVerMotivo;
        public AdopcionViewHolder(@NonNull View itemView) {
            super(itemView);

            iVFotoMascotaSolicitudEnviada = itemView.findViewById(R.id.cIVFotoMascotaSoliEnv);
            tVNombreMacota = itemView.findViewById(R.id.tVNombreMascotaSoliEnv);
            tVFechaEmision = itemView.findViewById(R.id.tVFechaEmisionSoliEnv);
            tVEstadoSolicitud = itemView.findViewById(R.id.tVEstadoSoliEnv);
            tVMensajeEnRevision = itemView.findViewById(R.id.tVMensajeEnRevision);

            btnContinuarProceso = itemView.findViewById(R.id.lLBtnContinuarProceso);
            btnVerMotivo = itemView.findViewById(R.id.lLBtnVerMotivo);

            btnContinuarProceso.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent i = new Intent(context, CompletarProcesoAdopcionActivity.class);
                i.putExtra("idAdopcion", listaAdopcionesEnviadas.get(getBindingAdapterPosition()).getId());
                i.putExtra("idContrato", listaAdopcionesEnviadas.get(getBindingAdapterPosition()).getContratoAdopcion());
                i.putExtra("idCuenta", listaAdopcionesEnviadas.get(getBindingAdapterPosition()).getAdoptante());
                i.putExtra("idMascota", listaAdopcionesEnviadas.get(getBindingAdapterPosition()).getMascotaAdopcion());

                Log.d("CONTRATOOO", "contrato" + listaAdopcionesEnviadas.get(getBindingAdapterPosition()).getContratoAdopcion());

                context.startActivity(i);
            });

            btnVerMotivo.setOnClickListener(v -> {

                String motivo = listaAdopcionesEnviadas.get(getBindingAdapterPosition()).getObservaciones();
                Dialog dialog = controladorUtilidades.crearAlertaPersonalizada("Motivo de rechazo", motivo, itemView.getContext());

                LinearLayout btnAceptar = dialog.findViewById(R.id.lLbtnAceptar);

                btnAceptar.setOnClickListener(view -> {
                    dialog.dismiss();
                });

                btnAceptar.setVisibility(View.VISIBLE);
                dialog.create();
                dialog.show();
            });
        }
    }
}
