package com.example.tic_pv.Adaptadores;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaSeguimientosAdaptador extends RecyclerView.Adapter<ListaSeguimientosAdaptador.SeguimientoViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Seguimiento> listaSeguimientos;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorUsuario controladorUsuario = new ControladorUsuario();
    private String idVoluntario, nombreVoluntario;
    private ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();

    public ArrayList<Seguimiento> getListaSeguimientos() {
        return listaSeguimientos;
    }

    public void setListaSeguimientos(ArrayList<Seguimiento> listaSeguimientos) {
        this.listaSeguimientos = listaSeguimientos;
    }

    public ListaSeguimientosAdaptador(ArrayList<Seguimiento> listaSeguimientos, String idVoluntario, String nombreVoluntario) {
        this.listaSeguimientos = listaSeguimientos;
        this.idVoluntario = idVoluntario;
        this.nombreVoluntario = nombreVoluntario;
    }

    @NonNull
    @Override
    public SeguimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_seguimientos, null, false);
        return new SeguimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoViewHolder holder, int position) {

        db.collection("Cuentas").document(listaSeguimientos.get(position).getIdAdoptante()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    controladorUtilidades.insertarImagenDesdeBDD(document.getString("fotoPerfil"),
                            holder.cVFotoAdoptante,
                            holder.itemView.getContext());

                    holder.tVNombreAdoptante.setText(listaSeguimientos.get(position).getNombreAdoptante());
                    holder.tVMascotaAdoptante.setText(listaSeguimientos.get(position).getNombreMascota());

                    if (listaSeguimientos.get(position).getIdVoluntario().isEmpty()) {
                        holder.btnAsignar.setVisibility(View.VISIBLE);
                        holder.btnQuitar.setVisibility(View.GONE);
                    } else {
                        holder.btnAsignar.setVisibility(View.GONE);
                        holder.btnQuitar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaSeguimientos.size();
    }

    public class SeguimientoViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cVFotoAdoptante;
        TextView tVNombreAdoptante, tVMascotaAdoptante;
        LinearLayout btnAsignar, btnQuitar;
        String titulo = "ALERTA";
        String mensaje = "¿Seguro que desea asignar este seguimiento a " + nombreVoluntario + "?";
        Dialog reasignarVoluntario, dialogConfirmacion, dialog;
        public SeguimientoViewHolder(@NonNull View itemView) {
            super(itemView);

            cVFotoAdoptante = itemView.findViewById(R.id.ivFotoListaAdoptante);
            tVNombreAdoptante = itemView.findViewById(R.id.tvNombreListaAdoptante);
            tVMascotaAdoptante = itemView.findViewById(R.id.tvMascotaListaAdoptante);
            btnAsignar = itemView.findViewById(R.id.lLAsignarSeguimiento);
            btnQuitar = itemView.findViewById(R.id.lLQuitarSeguimiento);
            reasignarVoluntario = new Dialog(itemView.getContext());

            // Configurar dialog para asignar seguimiento al voluntario
            dialog = controladorUtilidades.crearAlertaPersonalizada(titulo,
                    mensaje,
                    itemView.getContext());

            LinearLayout btnAceptar = dialog.findViewById(R.id.lLbtnAceptar);
            LinearLayout btnRechazar = dialog.findViewById(R.id.lLbtnCancelar);

            btnAceptar.setVisibility(View.VISIBLE);
            btnRechazar.setVisibility(View.VISIBLE);

            btnAceptar.setOnClickListener(v -> {
                controladorSeguimiento.asignarSeguimientoVoluntario(itemView.getContext(),
                        listaSeguimientos.get(getBindingAdapterPosition()).getId(),
                        idVoluntario,
                        nombreVoluntario,
                        new ControladorSeguimiento.Callback<ArrayList<Seguimiento>>() {
                            @Override
                            public void onComplete(ArrayList<Seguimiento> result) {
                                listaSeguimientos.clear();
                                listaSeguimientos.addAll(result);
                                actualizarAdaptador();
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("ERROR", "No se pudo actualizar la lista");
                            }
                        });

            });

            btnRechazar.setOnClickListener(v -> {
                dialog.dismiss();
            });

            btnAsignar.setOnClickListener(v -> {
                dialog.create();
                dialog.show();
            });

            btnQuitar.setOnClickListener(v -> {
                // Configurar el dialog para reasignar
                configurarDialogReasignar(reasignarVoluntario,
                        itemView.getContext(),
                        listaSeguimientos.get(getBindingAdapterPosition()).getId());
                reasignarVoluntario.create();
                reasignarVoluntario.show();
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void actualizarAdaptador() {
        notifyDataSetChanged();
    }

    private void configurarDialogReasignar(Dialog dialog, Context context, String idSeguimiento) {

        // Configurar dialog para reasignar
        dialog.setContentView(R.layout.dialog_asignar_otro_voluntario);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView btnSalirReasignar = dialog.findViewById(R.id.iVSalirAsignarOtroVolun);
        TextView textoReasignar = dialog.findViewById(R.id.tVEncAsignarOtroVoluntario);
        textoReasignar.setText("Por favor, seleccione un voluntario para reasignar el seguimiento actual.");
        Spinner voluntarios = dialog.findViewById(R.id.spVoluntarioReasignar);
        controladorUsuario.cargarVoluntariosSpinnerSegui(voluntarios, textoReasignar);
        LinearLayout btnReasingar = dialog.findViewById(R.id.lLAsignarOtroVoluntario);

        btnSalirReasignar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnReasingar.setOnClickListener(v -> {
            controladorSeguimiento.reasignarSeguimientoVoluntario(context,
                    idVoluntario,
                    idSeguimiento,
                    controladorUsuario.obtenerUsuarioSeleccionado().getCuenta(),
                    controladorUsuario.obtenerUsuarioSeleccionado().getNombre(),
                    new ControladorSeguimiento.CallbackSeguimientosVol<ArrayList<Seguimiento>>() {
                        @Override
                        public void onComplete(ArrayList<Seguimiento> result) {
                            listaSeguimientos.clear();
                            listaSeguimientos.addAll(result);
                            actualizarAdaptador();
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        });
    }
}
