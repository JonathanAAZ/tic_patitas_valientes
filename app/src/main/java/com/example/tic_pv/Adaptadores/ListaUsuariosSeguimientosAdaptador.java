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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListaUsuariosSeguimientosAdaptador extends RecyclerView.Adapter<ListaUsuariosSeguimientosAdaptador.UsuarioViewHolder> {
    private EstadosCuentas estadosCuentas;
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<CuentaUsuario> listaCuentas;
    private ArrayList<Usuario> listaUsuariosOriginal;
    private ArrayList<CuentaUsuario> listaCuentasOriginal;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public ListaUsuariosSeguimientosAdaptador(ArrayList<Usuario> listaUsuarios, ArrayList<CuentaUsuario> listaCuentas) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_voluntarios_seguimiento, null, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {

        db.collection("Seguimientos")
                .whereEqualTo("idVoluntario", listaCuentas.get(position).getIdCuenta())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int cantidad = querySnapshot.size();
                    controladorUtilidades.insertarImagenDesdeBDD(listaCuentas.get(position).getFotoPerfil(),
                            holder.iVFotoVoluntario,
                            holder.itemView.getContext());
                    holder.tVNombreVoluntario.setText(listaUsuarios.get(position).getNombre());
                    holder.tVCedulaVoluntario.setText(listaUsuarios.get(position).getCedula());
                    holder.tVNroSeguimientos.setText(String.valueOf(cantidad));

                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", "Error al contar seguimientos: " + e.getMessage());
                });


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
