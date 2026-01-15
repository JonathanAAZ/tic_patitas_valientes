package com.example.tic_pv.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.ResponderSolicitudAdopcionActivity;
import com.example.tic_pv.Vista.UltimosRequisitosActivity;
import com.example.tic_pv.Vista.VerInformacionPerfilActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SolicitudesPendientesAdaptador extends RecyclerView.Adapter<SolicitudesPendientesAdaptador.AdopcionViewHolder> {

    private ArrayList<Adopcion> listaAdopcionesPendientes;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String estadoAdopcion;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    public ArrayList<Adopcion> getListaAdopcionesPendientes() {
        return listaAdopcionesPendientes;
    }

    public void setListaAdopcionesPendientes(ArrayList<Adopcion> listaAdopcionesPendientes) {
        this.listaAdopcionesPendientes = listaAdopcionesPendientes;
    }

    public SolicitudesPendientesAdaptador(ArrayList<Adopcion> listaAdopcionesPendientes) {
        this.listaAdopcionesPendientes = listaAdopcionesPendientes;
    }

    @NonNull
    @Override
    public AdopcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_solicitudes_pendientes, null, false);
        return new AdopcionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdopcionViewHolder holder, int position) {
//        holder.tVSolicitante.setText(listaAdopcionesPendientes.get(position).getAdoptante());
//        holder.tVCedulaSolicitante.setText(listaAdopcionesPendientes.get(position).getCed());
        String idCuenta = listaAdopcionesPendientes.get(position).getAdoptante();
        String idMascota = listaAdopcionesPendientes.get(position).getMascotaAdopcion();
        estadoAdopcion = listaAdopcionesPendientes.get(position).getEstadoAdopcion();

        holder.tVEstadoSoli.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_green));

        //Condición utilizada para que el adaptador pueda ser aplicado en dos listas
        if (estadoAdopcion.equalsIgnoreCase(EstadosCuentas.PENDIENTE.toString())) {
            holder.lLBtnVerSolicitudPend.setVisibility(View.VISIBLE);
            holder.lLBtnConfirmarAdopcion.setVisibility(View.GONE);
        } else if (estadoAdopcion.equalsIgnoreCase(EstadosCuentas.EN_REVISION.toString())) {
            estadoAdopcion = controladorUtilidades.reemplazarPalabraString(estadoAdopcion, "_", " ");
            estadoAdopcion = controladorUtilidades.reemplazarPalabraString(estadoAdopcion, "O", "Ó");
            holder.lLBtnVerSolicitudPend.setVisibility(View.GONE);
            holder.lLBtnConfirmarAdopcion.setVisibility(View.VISIBLE);
        } else if (estadoAdopcion.equalsIgnoreCase(EstadosCuentas.ACEPTADA.toString())) {
            holder.lLBtnVerSolicitudPend.setVisibility(View.GONE);
            holder.lLBtnConfirmarAdopcion.setVisibility(View.VISIBLE);
        }else {
            holder.lLBtnVerSolicitudPend.setVisibility(View.GONE);
            holder.lLBtnConfirmarAdopcion.setVisibility(View.GONE);
            holder.tVEstadoSoli.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }

        db.collection("Usuarios").whereEqualTo("cuentaUsuario", idCuenta).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        String nombreUsuario = documento.getString("nombre");
                        String cedulaUsuario = documento.getString("cedula");

//                        binding.tvBienvenidaVP.setText("Información personal y de domicilio");
                        holder.tVSolicitante.setText(nombreUsuario);

                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "No se encontraron documentos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Error al obtener los documentos", Toast.LENGTH_LONG).show();
            }
        });

        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String nombreMascota = documentSnapshot.getString("nombre");
                        holder.tVMascotaPend.setText(nombreMascota);
                    } else {
                        Log.e("ERROR", "No existe el documento");
                    }
                }
            }
        });

        holder.tVEstadoSoli.setText(estadoAdopcion);
        holder.tVFechaEmision.setText(listaAdopcionesPendientes.get(position).getFechaEmision());

    }

    @Override
    public int getItemCount() {
        return listaAdopcionesPendientes.size();
    }

    public class AdopcionViewHolder extends RecyclerView.ViewHolder {
        TextView tVSolicitante, tVEstadoSoli, tVMascotaPend, tVFechaEmision;
        LinearLayout lLBtnVerSolicitudPend, lLBtnConfirmarAdopcion;
        Dialog alertaCompletarAdopcion;

        public AdopcionViewHolder(@NonNull View itemView) {
            super(itemView);

            tVSolicitante = itemView.findViewById(R.id.tVNombreSolicitantePend);
            tVEstadoSoli = itemView.findViewById(R.id.tVEstadoSoliPend);
            tVMascotaPend = itemView.findViewById(R.id.tVMascotaPend);
            tVFechaEmision = itemView.findViewById(R.id.tVFechaEmisionPend);
            lLBtnVerSolicitudPend = itemView.findViewById(R.id.lLBotonVerSolicitudPendiente);
            lLBtnConfirmarAdopcion = itemView.findViewById(R.id.lLBotonConfirmarAdopcion);

            alertaCompletarAdopcion = controladorUtilidades
                    .crearAlertaPersonalizada("Información",
                            "El adoptante aún no ha completado los requisitos faltantes",
                            itemView.getContext());

//            alertaCompletarAdopcion.setContentView(R.layout.dialog_alerta_general);
//            Objects.requireNonNull(alertaCompletarAdopcion.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            LinearLayout btnAceptar, btnCancelar;
//            TextView tVTitulo, tVMensaje;

            btnAceptar = alertaCompletarAdopcion.findViewById(R.id.lLbtnAceptar);
//            btnCancelar = alertaCompletarAdopcion.findViewById(R.id.lLbtnCancelar);
//            tVTitulo = alertaCompletarAdopcion.findViewById(R.id.tVTituloAlerta);
//            tVMensaje = alertaCompletarAdopcion.findViewById(R.id.tVMensajeAlerta);
//
//            tVTitulo.setText("Información");
//            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tVTitulo, 12, 18, 2, TypedValue.COMPLEX_UNIT_SP);
//            tVMensaje.setText("El adoptante aún no ha completado los requisitos faltantes");
//            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tVMensaje, 12, 18, 2, TypedValue.COMPLEX_UNIT_SP);

            btnAceptar.setOnClickListener(v -> {
                alertaCompletarAdopcion.dismiss();
            });

            lLBtnVerSolicitudPend.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent i = new Intent(context, ResponderSolicitudAdopcionActivity.class);
                i.putExtra("idSolicitud", listaAdopcionesPendientes.get(getBindingAdapterPosition()).getId());
                context.startActivity(i);
            });

            lLBtnConfirmarAdopcion.setOnClickListener(v -> {
                btnAceptar.setVisibility(View.VISIBLE);
                alertaCompletarAdopcion.create();

                String estado = listaAdopcionesPendientes.get(getBindingAdapterPosition()).getEstadoAdopcion();

                if (estado.equalsIgnoreCase(EstadosCuentas.ACEPTADA.toString())) {
                    alertaCompletarAdopcion.show();
                } else {
                    Context context = v.getContext();
                    Intent i = new Intent(context, UltimosRequisitosActivity.class);
                    i.putExtra("adopcion", listaAdopcionesPendientes.get(getBindingAdapterPosition()));
//                    i.putExtra("idContrato", listaAdopcionesPendientes.get(getBindingAdapterPosition()).getContratoAdopcion());
//                    i.putExtra("idCuenta", listaAdopcionesPendientes.get(getBindingAdapterPosition()).getAdoptante());
//                    i.putExtra("idMascota", listaAdopcionesPendientes.get(getBindingAdapterPosition()).getMascotaAdopcion());
                    context.startActivity(i);

//                    System.out.println("ID ADOPCION" + listaAdopcionesPendientes.get(getBindingAdapterPosition()).getId());

                }

            });

        }
    }
}
