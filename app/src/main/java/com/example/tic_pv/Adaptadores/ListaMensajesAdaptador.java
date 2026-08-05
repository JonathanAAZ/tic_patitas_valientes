package com.example.tic_pv.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Mensaje;
import com.example.tic_pv.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaMensajesAdaptador extends RecyclerView.Adapter<ListaMensajesAdaptador.MensajeViewHolder> {

    private ArrayList<Mensaje> listaMensajes;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser usuarioActual = mAuth.getCurrentUser();
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    // Rastrear el player en reproducción
    private ExoPlayer currentPlayer = null;
    private int playingPosition = -1;
    private Context context;

    // Fotos de perfil de los participantes del chat, indexadas por id de cuenta
    private final HashMap<String, String> fotosPerfil = new HashMap<>();

    public ListaMensajesAdaptador(ArrayList<Mensaje> listaMensajes, Context context) {
        this.listaMensajes = listaMensajes;
        this.context = context;
    }

    // Se llama una sola vez al abrir el chat, con las fotos ya consultadas
    @SuppressLint("NotifyDataSetChanged")
    public void setFotosPerfil(HashMap<String, String> fotos) {
        fotosPerfil.clear();
        fotosPerfil.putAll(fotos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_emisor, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_receptor, parent, false);
        }
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);

        // Liberar player anterior si existe
        if (holder.player != null) {
            holder.player.release();
            holder.player = null;
        }

        // Configurar emisor
        if (usuarioActual.getUid().equalsIgnoreCase(mensaje.getIdEmisor())) {
            holder.tVEmisor.setText("Tú");
        } else {
            holder.tVEmisor.setText(mensaje.getEmisor());
        }

        // Foto de perfil de quien envió el mensaje
        String urlFotoPerfil = fotosPerfil.get(mensaje.getIdEmisor());
        if (urlFotoPerfil != null && !urlFotoPerfil.isEmpty()) {
            controladorUtilidades.insertarImagenDesdeBDD(urlFotoPerfil,
                    holder.iVFotoPerfilMensaje,
                    holder.itemView.getContext());
        } else {
            holder.iVFotoPerfilMensaje.setImageResource(R.drawable.logo_patitas_valientes);
        }

        // Configurar el contenido del mensaje
        if (controladorUtilidades.esImagen(mensaje.getContenido())) {
            holder.tVContenido.setVisibility(View.GONE);
            holder.fLVideoMensaje.setVisibility(View.GONE);
            holder.iVFotoMensaje.setVisibility(View.VISIBLE);
            controladorUtilidades.insertarImagenDesdeBDD(
                    mensaje.getContenido(),
                    holder.iVFotoMensaje,
                    holder.itemView.getContext()
            );

        } else if (controladorUtilidades.esVideo(mensaje.getContenido())) {
            holder.tVContenido.setVisibility(View.GONE);
            holder.iVFotoMensaje.setVisibility(View.GONE);
            holder.fLVideoMensaje.setVisibility(View.VISIBLE);

            configurarExoPlayer(holder, mensaje, position);

        } else {
            holder.tVContenido.setVisibility(View.VISIBLE);
            holder.iVFotoMensaje.setVisibility(View.GONE);
            holder.fLVideoMensaje.setVisibility(View.GONE);
            holder.tVContenido.setText(mensaje.getContenido());
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void configurarExoPlayer(MensajeViewHolder holder, Mensaje mensaje, int position) {
        // Crear ExoPlayer
        holder.player = new ExoPlayer.Builder(context).build();
        holder.playerViewMensaje.setPlayer(holder.player);

        // Configurar controles nativos de ExoPlayer
        holder.playerViewMensaje.setControllerAutoShow(true);  // Mostrar controles automáticamente
        holder.playerViewMensaje.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        holder.playerViewMensaje.setUseController(true);  // Usar los controles nativos

        // Mostrar barra de progreso mientras carga
        holder.barraProgresoVideoMensaje.setVisibility(View.VISIBLE);

        // Obtener URL del video desde tu controlador
        controladorUtilidades.obtenerUrlVideoDesdeBDD(mensaje.getContenido(), new ControladorUtilidades.VideoUrlCallback() {
            @Override
            public void onSuccess(String videoUrl) {
                // Crear MediaItem con la URL
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                holder.player.setMediaItem(mediaItem);
                holder.player.prepare();

                // NO auto-reproducir, dejar que el usuario use los controles
                holder.player.setPlayWhenReady(false);

                // Listener para manejar estados
                holder.player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            // Video listo para reproducir
                            holder.barraProgresoVideoMensaje.setVisibility(View.GONE);
                        } else if (playbackState == Player.STATE_BUFFERING) {
                            // Buffering
                            holder.barraProgresoVideoMensaje.setVisibility(View.VISIBLE);
                        } else if (playbackState == Player.STATE_ENDED) {
                            // Video terminado
                            if (playingPosition == position) {
                                playingPosition = -1;
                                currentPlayer = null;
                            }
                        }
                    }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (isPlaying) {
                            // Pausar el video anterior si existe
                            if (currentPlayer != null && currentPlayer != holder.player) {
                                currentPlayer.pause();
                            }
                            playingPosition = position;
                            currentPlayer = holder.player;
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                holder.barraProgresoVideoMensaje.setVisibility(View.GONE);
                Toast.makeText(context, "Error al cargar video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tVEmisor, tVContenido;
        ImageView iVFotoMensaje;
        CircleImageView iVFotoPerfilMensaje;
        FrameLayout fLVideoMensaje;
        PlayerView playerViewMensaje;
        ProgressBar barraProgresoVideoMensaje;
        ExoPlayer player;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            tVEmisor = itemView.findViewById(R.id.tVEmisor);
            tVContenido = itemView.findViewById(R.id.tVContenido);
            iVFotoMensaje = itemView.findViewById(R.id.iVFotoMensaje);
            iVFotoPerfilMensaje = itemView.findViewById(R.id.iVFotoPerfilMensaje);
            fLVideoMensaje = itemView.findViewById(R.id.fLVideoMensaje);
            playerViewMensaje = itemView.findViewById(R.id.viewVideoMensaje);
            barraProgresoVideoMensaje = itemView.findViewById(R.id.barraProgresoVideoMensaje);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (usuarioActual.getUid().equalsIgnoreCase(listaMensajes.get(position).getIdEmisor())) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public void onViewRecycled(@NonNull MensajeViewHolder holder) {
        super.onViewRecycled(holder);

        // Liberar ExoPlayer cuando se recicla la vista
        if (holder.player != null) {
            holder.player.stop();
            holder.player.release();
            holder.player = null;
        }

        if (holder.playerViewMensaje != null) {
            holder.playerViewMensaje.setPlayer(null);
        }
    }

    // Método para liberar todos los recursos
    public void releaseAllPlayers() {
        if (currentPlayer != null) {
            currentPlayer.release();
            currentPlayer = null;
        }
        playingPosition = -1;
    }
}
