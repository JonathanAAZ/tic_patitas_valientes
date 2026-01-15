package com.example.tic_pv.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.VerInformacionPerfilActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListaUsuariosAdaptador extends RecyclerView.Adapter<ListaUsuariosAdaptador.UsuarioViewHolder> {

    private EstadosCuentas estadosCuentas;
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<CuentaUsuario> listaCuentas;
    private ArrayList<Usuario> listaUsuariosOriginal;
    private ArrayList<CuentaUsuario> listaCuentasOriginal;

    public ArrayList<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public ArrayList<CuentaUsuario> getListaCuentas() {
        return listaCuentas;
    }

    public void setListaCuentas(ArrayList<CuentaUsuario> listaCuentas) {
        this.listaCuentas = listaCuentas;
    }

    public ListaUsuariosAdaptador(ArrayList<Usuario> listaUsuarios, ArrayList<CuentaUsuario> listaCuentas) {
        this.listaUsuarios = listaUsuarios;
        this.listaCuentas = listaCuentas;
        listaUsuariosOriginal = new ArrayList<>();
        listaUsuariosOriginal.addAll(listaUsuarios);

        listaCuentasOriginal = new ArrayList<>();
        listaCuentasOriginal.addAll(listaCuentas);
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_usuario, null, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {

//        Picasso.get().load(listaCuentas.get(position).getFotoPerfil()).into(holder.ivFotoPerfil);
        Glide.with(holder.itemView.getContext())
                .load(listaCuentas.get(position).getFotoPerfil())
                .fitCenter()
                .into(holder.ivFotoPerfil);
        holder.vNombre.setText(listaUsuarios.get(position).getNombre());
        holder.vCedula.setText(listaUsuarios.get(position).getCedula());
        holder.vRol.setText(listaCuentas.get(position).getRol());
        holder.vEstado.setText(listaCuentas.get(position).getEstadoCuenta());

        if (listaCuentas.get(position).getEstadoCuenta().equals(estadosCuentas.ACTIVO.toString())) {
            holder.ivEliminarUsuario.setBackgroundResource(R.color.red);
            holder.ivEliminarUsuario.setImageResource(R.drawable.ic_desactivar_usuario);
            holder.vActivarDesactivarUsuario.setText("Desactivar");
        } else {
            holder.ivEliminarUsuario.setBackgroundResource(R.color.light_green);
            holder.ivEliminarUsuario.setImageResource(R.drawable.ic_activar_usuario);
            holder.vActivarDesactivarUsuario.setText("Activar");
        }

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFotoPerfil, ivEditarUsuario, ivEliminarUsuario;
        TextView vNombre, vCedula, vRol, vEstado, vActivarDesactivarUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Confirmar cambio de estado");
//            builder.setMessage("¿Estás seguro de que quieres activar/desactivar este usuario?");

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Context context = itemView.getContext();
                    ControladorUsuario controladorUsuario = new ControladorUsuario();
                    controladorUsuario.setIdCuenta(listaCuentas.get(getAdapterPosition()).getIdCuenta());
                    controladorUsuario.actualizarEstadoUsuario(context);
                    Toast.makeText(itemView.getContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            ivFotoPerfil = itemView.findViewById(R.id.ivFotoListaUsuario);
            vNombre = itemView.findViewById(R.id.tvNombreListaUsuario);
            vCedula = itemView.findViewById(R.id.tvCedulaListaUsuario);
            vRol = itemView.findViewById(R.id.tvRolListaUsuario);
            vEstado = itemView.findViewById(R.id.tvEstadoUsuario);
            vActivarDesactivarUsuario = itemView.findViewById(R.id.tvActivarDesactivarUsuario);
            ivEditarUsuario = itemView.findViewById(R.id.ivEditarUsuarioLista);
            ivEliminarUsuario = itemView.findViewById(R.id.ivEliminarUsuarioLista);

//            ivEditarUsuario.setOnTouchListener();

            ivEditarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context, VerInformacionPerfilActivity.class);
                    i.putExtra("id", listaCuentas.get(getBindingAdapterPosition()).getIdCuenta());
                    i.putExtra("rol", "Administrador");
                    context.startActivity(i);
//                    if (context instanceof Activity) {
//                        ((Activity) context).finish();
//                    }
                }
            });

            ivEliminarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String estadoActual = listaCuentas.get(getAdapterPosition()).getEstadoCuenta();
                    if (estadoActual.equals(estadosCuentas.ACTIVO.toString())) {
                        builder.setMessage("¿Está seguro/a de que desea desactivar este usuario?");
                    } else {
                        builder.setMessage("¿Está seguro/a de que desea activar este usuario?");
                    }
                    AlertDialog dialog = builder.create();
                    dialog.show();
//                    AlertDialog dialog = builder.create();
//                    dialog.show();

//                    Context context = itemView.getContext();
//                    ControladorUsuario controladorUsuario = new ControladorUsuario();
//                    controladorUsuario.setIdCuenta(listaCuentas.get(getAdapterPosition()).getIdCuenta());
//                    controladorUsuario.actualizarEstadoUsuario(context);
//                    Toast.makeText(itemView.getContext(), "Eliminado", Toast.LENGTH_SHORT).show();

//                    Context context = v.getContext();
//                    Intent i = new Intent(context, VerInformacionPerfilActivity.class);
//                    i.putExtra("id", listaCuentas.get(getAdapterPosition()).getIdCuenta());
//                    context.startActivity(i);
                }
            });

        }
    }

    public void filtrar (String filtro) {
        String busqueda = filtro.trim();
        ControladorUsuario controladorUsuario = new ControladorUsuario();
        controladorUsuario.filtrarInformación(busqueda, listaUsuariosOriginal, listaUsuarios, listaCuentasOriginal, listaCuentas);
        notifyDataSetChanged();
    }
}
