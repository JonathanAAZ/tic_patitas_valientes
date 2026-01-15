package com.example.tic_pv.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.EditarMascotaActivity;

import java.util.ArrayList;

public class ListaMascotasAdopcionAdaptador extends RecyclerView.Adapter<ListaMascotasAdopcionAdaptador.MascotaViewHolder> {

    private EstadosCuentas estadosCuentas;
    private ArrayList <Mascota> listaMascotasAdopcion;
    private ArrayList <Mascota> listaMascotasAdopcionOriginal;

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

    public ListaMascotasAdopcionAdaptador(ArrayList<Mascota> listaMascotasAdopcion) {
        this.listaMascotasAdopcion = listaMascotasAdopcion;

        listaMascotasAdopcionOriginal = new ArrayList<>();
        listaMascotasAdopcionOriginal.addAll(listaMascotasAdopcion);
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_mascota_adopcion, null, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .load(listaMascotasAdopcion.get(position).getFotoMascota())
                .placeholder(R.drawable.ic_huella_mascota)
                .override(200, 200)
                .fitCenter()
                .into(holder.fotoMascotaAdopc);
        holder.nombreMascota.setText(listaMascotasAdopcion.get(position).getNombreMascota());
        if (listaMascotasAdopcion.get(position).isMascotaVacunada()) {
            holder.vacunacionMascota.setText("Sí");
        } else {
            holder.vacunacionMascota.setText("No");
        }
        if (listaMascotasAdopcion.get(position).isMascotaDesparasitada()) {
            holder.desparasitacionMascota.setText("Sí");
        } else {
            holder.desparasitacionMascota.setText("No");
        }
        if (listaMascotasAdopcion.get(position).isMascotaEsterilizada()) {
            holder.esterilizacionMascota.setText("Sí");
        } else {
            holder.esterilizacionMascota.setText("No");
        }
        holder.estadoMascotaAdopc.setText(listaMascotasAdopcion.get(position).getEstadoMascota());

        if (listaMascotasAdopcion.get(position).getEstadoMascota().equals(estadosCuentas.ACTIVO.toString())) {
            holder.iVEliminarMascotaAdopc.setBackgroundResource(R.color.red);
            holder.iVEliminarMascotaAdopc.setImageResource(R.drawable.ic_huella_android);
            holder.tvActivarDesactivarMascota.setText("Desactivar");
        } else {
            holder.iVEliminarMascotaAdopc.setBackgroundResource(R.color.light_green);
            holder.iVEliminarMascotaAdopc.setImageResource(R.drawable.ic_huella_android);
            holder.tvActivarDesactivarMascota.setText("Activar");
        }
    }

    @Override
    public int getItemCount() {
        return listaMascotasAdopcion.size();
    }

    public class MascotaViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoMascotaAdopc, iVEditarMascotaAdopc, iVEliminarMascotaAdopc;
        TextView nombreMascota, vacunacionMascota, desparasitacionMascota, esterilizacionMascota, estadoMascotaAdopc, tvActivarDesactivarMascota;
        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Confirmar cambio de estado");
//            builder.setMessage("¿Está seguro/a de que desea activar/desactivar esta mascota?");

//            if (estadoActual.equals(estadosCuentas.ACTIVO.toString())) {
//                builder.setMessage("¿Estás seguro de que quieres desactivar esta mascota?");
//            } else {
//                builder.setMessage("¿Estás seguro de que quieres activar esta mascota?");
//            }

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Context context = itemView.getContext();
                    FragmentActivity activity = (FragmentActivity) context;
                    String idMascota = listaMascotasAdopcion.get(getAdapterPosition()).getId();
                    ControladorMascota controladorMascota = new ControladorMascota();
                    controladorMascota.actualizarEstadoMascota(context, idMascota, activity.getSupportFragmentManager());
//                    controladorUsuario.setIdCuenta(listaMascotasAdopcion.get(getAdapterPosition()).getIdCuenta());
//                    controladorUsuario.actualizarEstadoUsuario(context);
//                    Toast.makeText(context, "NOMBRE CLASE " + context.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            fotoMascotaAdopc = itemView.findViewById(R.id.ivFotoListaMascotaAdopcion);
            nombreMascota = itemView.findViewById(R.id.tVNombreMascotaAdopc);
            vacunacionMascota = itemView.findViewById(R.id.tVVacunacionMascotaAdopc);
            desparasitacionMascota = itemView.findViewById(R.id.tVDesparasitacionMascotaAdopc);
            esterilizacionMascota = itemView.findViewById(R.id.tVEsterilizMascotaAdopc);
            estadoMascotaAdopc = itemView.findViewById(R.id.tVEstadoMascotaAdopc);
            iVEditarMascotaAdopc = itemView.findViewById(R.id.ivEditarMascotaLista);
            iVEliminarMascotaAdopc = itemView.findViewById(R.id.ivEliminarMascotaLista);
            tvActivarDesactivarMascota = itemView.findViewById(R.id.tvActivarDesactivarMascota);

            iVEditarMascotaAdopc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context, EditarMascotaActivity.class);
                    i.putExtra("id", listaMascotasAdopcion.get(getBindingAdapterPosition()).getId());
                    context.startActivity(i);
                }
            });

            iVEliminarMascotaAdopc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String estadoActual = listaMascotasAdopcion.get(getAdapterPosition()).getEstadoMascota();
                    if (estadoActual.equals(estadosCuentas.ACTIVO.toString())) {
                        builder.setMessage("¿Está seguro/a de que desea desactivar esta mascota?");
                    } else {
                        builder.setMessage("¿Está seguro/a de que desea activar esta mascota?");
                    }
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    public void filtrar(String filtro, String criterio, TextView tVNoExisten) {
        String busqueda = criterio.trim();
        ControladorMascota controladorMascota = new ControladorMascota();
        controladorMascota.filtrarMascotas(filtro, busqueda, listaMascotasAdopcionOriginal, listaMascotasAdopcion, tVNoExisten);
        notifyDataSetChanged();
    }
}
