package com.example.tic_pv.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaAdoptantesSeguimientosAdaptador extends RecyclerView.Adapter<ListaAdoptantesSeguimientosAdaptador.UsuarioViewHolder> {

    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<CuentaUsuario> listaCuentas;
    private ArrayList<Usuario> listaUsuariosOriginal;
    private ArrayList<CuentaUsuario> listaCuentasOriginal;
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

    public ArrayList<Usuario> getListaUsuariosOriginal() {
        return listaUsuariosOriginal;
    }

    public void setListaUsuariosOriginal(ArrayList<Usuario> listaUsuariosOriginal) {
        this.listaUsuariosOriginal = listaUsuariosOriginal;
    }

    public ArrayList<CuentaUsuario> getListaCuentasOriginal() {
        return listaCuentasOriginal;
    }

    public void setListaCuentasOriginal(ArrayList<CuentaUsuario> listaCuentasOriginal) {
        this.listaCuentasOriginal = listaCuentasOriginal;
    }

    public ListaAdoptantesSeguimientosAdaptador(ArrayList<Usuario> listaUsuarios, ArrayList<CuentaUsuario> listaCuentas) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_seguimientos, null);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        controladorUtilidades.insertarImagenDesdeBDD(listaCuentas.get(position).getFotoPerfil(),
                holder.cVFotoAdoptante,
                holder.itemView.getContext());

        holder.tVNombreAdoptante.setText(listaUsuarios.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder{
        CircleImageView cVFotoAdoptante;
        TextView tVNombreAdoptante;
        LinearLayout btnAsignar, btnQuitar;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            cVFotoAdoptante = itemView.findViewById(R.id.ivFotoListaAdoptante);
            tVNombreAdoptante = itemView.findViewById(R.id.tvNombreListaAdoptante);
            btnAsignar = itemView.findViewById(R.id.lLAsignarSeguimiento);
            btnQuitar = itemView.findViewById(R.id.lLQuitarSeguimiento);
        }
    }
}
