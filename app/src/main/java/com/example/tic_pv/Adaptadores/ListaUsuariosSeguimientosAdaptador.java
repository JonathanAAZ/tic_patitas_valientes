package com.example.tic_pv.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.SeguimientosVoluntarioActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ListaUsuariosSeguimientosAdaptador extends RecyclerView.Adapter<ListaUsuariosSeguimientosAdaptador.UsuarioViewHolder> {
    private EstadosCuentas estadosCuentas;
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<CuentaUsuario> listaCuentas;
    private ArrayList<Usuario> listaUsuariosOriginal;
    private ArrayList<CuentaUsuario> listaCuentasOriginal;
    private HashMap<String, Integer> cantidadSeguimientos;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

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

    public ListaUsuariosSeguimientosAdaptador(ArrayList<Usuario> listaUsuarios, ArrayList<CuentaUsuario> listaCuentas,
                                              HashMap<String, Integer> cantidadSeguimientos) {
        this.listaUsuarios = listaUsuarios;
        this.listaCuentas = listaCuentas;
        this.cantidadSeguimientos = cantidadSeguimientos != null ? cantidadSeguimientos : new HashMap<>();
        listaUsuariosOriginal = new ArrayList<>();
        listaUsuariosOriginal.addAll(listaUsuarios);

        listaCuentasOriginal = new ArrayList<>();
        listaCuentasOriginal.addAll(listaCuentas);
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Con el parent, para que el ítem respete los LayoutParams del RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_item_voluntarios_seguimiento, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {

        CuentaUsuario cuenta = listaCuentas.get(position);

        // La cantidad viene contada de una sola vez desde el controlador. Antes se consultaba
        // Firestore aquí, lo que suponía una lectura por fila en cada scroll y datos cruzados
        // cuando la respuesta llegaba después de reciclarse la fila.
        Integer cantidad = cantidadSeguimientos.get(cuenta.getIdCuenta());
        holder.tVNroSeguimientos.setText(String.valueOf(cantidad == null ? 0 : cantidad));

        holder.tVNombreVoluntario.setText(listaUsuarios.get(position).getNombre());
        holder.tVCedulaVoluntario.setText(listaUsuarios.get(position).getCedula());

        String fotoPerfil = cuenta.getFotoPerfil();
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            controladorUtilidades.insertarImagenDesdeBDD(fotoPerfil,
                    holder.iVFotoVoluntario,
                    holder.itemView.getContext());
        } else {
            holder.iVFotoVoluntario.setImageResource(R.drawable.logo_patitas_valientes);
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {
        ImageView iVFotoVoluntario;
        TextView tVNombreVoluntario, tVCedulaVoluntario, tVNroSeguimientos;
        LinearLayout btnVerSeguimientos;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            Context context = itemView.getContext();

            iVFotoVoluntario = itemView.findViewById(R.id.ivFotoListaVoluntario);
            tVNombreVoluntario = itemView.findViewById(R.id.tvNombreListaVoluntario);
            tVCedulaVoluntario = itemView.findViewById(R.id.tvCedulaListaVoluntario);
            tVNroSeguimientos = itemView.findViewById(R.id.tvNroSeguiListaVoluntario);
            btnVerSeguimientos = itemView.findViewById(R.id.lLVerSeguimientos);

            btnVerSeguimientos.setOnClickListener(v -> {
                Intent i = new Intent(context, SeguimientosVoluntarioActivity.class);
                i.putExtra("idVoluntario", listaCuentas.get(getBindingAdapterPosition()).getIdCuenta());
                i.putExtra("nombreVoluntario", listaUsuarios.get(getBindingAdapterPosition()).getNombre());
                context.startActivity(i);
            });
        }
    }

}
