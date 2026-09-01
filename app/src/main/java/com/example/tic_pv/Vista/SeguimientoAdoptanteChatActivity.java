package com.example.tic_pv.Vista;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.ListaMensajesAdaptador;
import com.example.tic_pv.Adaptadores.DeslizarParaResponderCallback;
import com.example.tic_pv.Controlador.ControladorNotificaciones;
import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mensaje;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivitySeguimientoAdoptanteChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.ListenerRegistration;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SeguimientoAdoptanteChatActivity extends AppCompatActivity {
    private Seguimiento seguimiento;
    private ListaMensajesAdaptador listaMensajesAdaptador;
    private ArrayList<Mensaje> listaMensajes;
    private DatabaseReference mensajesRef;
    private ActivitySeguimientoAdoptanteChatBinding binding;
    private boolean accionTomarFoto, esParaFoto, reproduciendo;
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();
    private final ControladorNotificaciones controladorNotificaciones = new ControladorNotificaciones();
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
    private Dialog dialogVisualizarMultimedia;
    private ImageView visualizarFoto, iVReproducirVideo;
    private VideoView visualizarVideo;
    private FrameLayout frameLayoutVisualizarMultimedia;
    private Uri videoUri;
    private File archivoFoto;
    private LinearLayout enviarMultimedia;
    private ProgressBar barraProgresoSubirMultimedia;
    // Mensaje al que se está respondiendo, null si es un mensaje normal
    private Mensaje mensajeRespondido;
    // Gesto de deslizar para responder, se suelta cuando el seguimiento se cierra
    private ItemTouchHelper deslizarParaResponder;
    // Escucha del estado del seguimiento, para enterarse de un retiro en el momento
    private ListenerRegistration escuchaEstadoSeguimiento;
    private boolean seguimientoCerrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySeguimientoAdoptanteChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // El padding inferior sigue al teclado para que la barra de escritura no quede tapada
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    Math.max(systemBars.bottom, teclado.bottom));
            return insets;
        });

        Intent intent = getIntent();
        seguimiento = intent.getParcelableExtra("seguimiento");

        if (seguimiento == null) {
            Toast.makeText(this, "No se pudo abrir el seguimiento", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listaMensajes = new ArrayList<>();
        listaMensajesAdaptador = new ListaMensajesAdaptador(listaMensajes, this);
        binding.recyclerViewChatSeguiAdop.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewChatSeguiAdop.setAdapter(listaMensajesAdaptador);

        // Deslizar un mensaje hacia la derecha lo cita para responderlo
        listaMensajesAdaptador.setOnResponderMensajeListener(this::mostrarPanelRespuesta);
        deslizarParaResponder = new ItemTouchHelper(
                new DeslizarParaResponderCallback(binding.recyclerViewChatSeguiAdop, listaMensajesAdaptador));
        deslizarParaResponder.attachToRecyclerView(binding.recyclerViewChatSeguiAdop);

        binding.iVCancelarRespuesta.setOnClickListener(v -> cancelarRespuesta());

        // Al abrirse el teclado el RecyclerView se encoge, así que volvemos al último mensaje
        binding.recyclerViewChatSeguiAdop.addOnLayoutChangeListener(
                (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (bottom < oldBottom) {
                        desplazarAlUltimoMensaje();
                    }
                });

        // Configurar dialog para visualizar multimedia
        dialogVisualizarMultimedia = new Dialog(this);
        dialogVisualizarMultimedia.setContentView(R.layout.dialog_visualizar_multimedia);
        Objects.requireNonNull(dialogVisualizarMultimedia.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        visualizarFoto = dialogVisualizarMultimedia.findViewById(R.id.iVFotoMultimedia);
        visualizarVideo = dialogVisualizarMultimedia.findViewById(R.id.videoViewVisualizar);
        frameLayoutVisualizarMultimedia = dialogVisualizarMultimedia.findViewById(R.id.frameVideoMultimedia);
        iVReproducirVideo = dialogVisualizarMultimedia.findViewById(R.id.iVReproducirVideoMultimedia);
        enviarMultimedia = dialogVisualizarMultimedia.findViewById(R.id.lLEnviarMultimedia);
        barraProgresoSubirMultimedia = dialogVisualizarMultimedia.findViewById(R.id.barraProgresoEnviarMultimedia);

        // Configurar el VideoView
        configurarVideoView();

        // Las fotos de perfil se consultan una sola vez y se reutilizan en cada mensaje
        controladorSeguimiento.obtenerFotosPerfilChat(seguimiento, new ControladorSeguimiento.Callback<HashMap<String, String>>() {
            @Override
            public void onComplete(HashMap<String, String> result) {
                listaMensajesAdaptador.setFotosPerfil(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIREBASE", "Error al obtener las fotos de perfil del chat.");
            }
        });

        // Inicializar la lista de mensajes
        controladorSeguimiento.obtenerOManejarMensajes(seguimiento.getListaMensajes(), new ControladorSeguimiento.Callback<ArrayList<Mensaje>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(ArrayList<Mensaje> result) {
                // Actualizamos la lista y el adaptador
                listaMensajes.clear();
                listaMensajes.addAll(result);
                listaMensajesAdaptador.notifyDataSetChanged();

                // Desplazamos al último mensaje si hay mensajes en la lista
                if (!result.isEmpty()) {
                    binding.recyclerViewChatSeguiAdop.scrollToPosition(result.size() - 1);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIREBASE", "Error al obtener la lista de mensajes.");
            }
        });

        mensajesRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(seguimiento.getListaMensajes());

        mensajesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Este método se llama cada vez que se inserta un nuevo mensaje
                Mensaje mensaje = snapshot.getValue(Mensaje.class);
                if (mensaje != null) {
                    listaMensajes.add(mensaje); // Agregar mensaje nuevo
                    listaMensajesAdaptador.notifyItemInserted(listaMensajes.size() - 1); // Actualizar adaptador
                    binding.recyclerViewChatSeguiAdop.scrollToPosition(listaMensajes.size() - 1); // Desplazar al último
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Este método captura actualizaciones en los nodos hijos (ediciones)
                Mensaje mensajeActualizado = snapshot.getValue(Mensaje.class);
                String mensajeId = snapshot.getKey();  // Obtenemos el ID del mensaje

                if (mensajeId != null && mensajeActualizado != null) {
                    for (int i = 0; i < listaMensajes.size(); i++) {
                        if (listaMensajes.get(i).getId().equals(mensajeId)) {  // Buscar por ID
                            listaMensajes.set(i, mensajeActualizado);  // Actualizar el mensaje en la lista
                            listaMensajesAdaptador.notifyItemChanged(i);  // Notificar que el mensaje cambió
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Este método captura eliminaciones de nodos
                String mensajeId = snapshot.getKey();  // Obtener el ID del mensaje eliminado

                if (mensajeId != null) {
                    for (int i = 0; i < listaMensajes.size(); i++) {
                        if (listaMensajes.get(i).getId().equals(mensajeId)) {  // Buscar por ID
                            listaMensajes.remove(i);  // Eliminar el mensaje de la lista
                            listaMensajesAdaptador.notifyItemRemoved(i);  // Notificar al adaptador
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error al escuchar los mensajes: " + error.getMessage());
            }
        });

        // Manejo del botón de regresar
        binding.iVRegresarSeguimientoAdoptanteChat.setOnClickListener(v -> finish());

        // Manejo del botón de enviar mensaje
        binding.lLEnviarMensaje.setOnClickListener(v -> {
            String contenidoMensaje = binding.eTEscribirMensaje.getText().toString().trim();
            enviarMensajeTexto(contenidoMensaje);
        });

        binding.iVAdjuntarArchivo.setOnClickListener(v -> {
            if (binding.cardOpcionesAdjuntar.getVisibility() == View.GONE) {
                binding.cardOpcionesAdjuntar.setVisibility(View.VISIBLE);
            } else {
                binding.cardOpcionesAdjuntar.setVisibility(View.GONE);
            }
        });

        binding.lLOpcionTomarFoto.setOnClickListener(v -> {
            dialogVisualizarMultimedia.create();
            accionTomarFoto = true;
            esParaFoto = true;
            verificarPermisoCamara(true);
            binding.cardOpcionesAdjuntar.setVisibility(View.GONE);
            visualizarFoto.setVisibility(View.VISIBLE);
            visualizarVideo.setVisibility(View.GONE);
        });

        binding.lLOpcionGrabarVideo.setOnClickListener(v -> {
            dialogVisualizarMultimedia.create();
            accionTomarFoto = false;
            esParaFoto = false;
            verificarPermisoCamara(false);
            binding.cardOpcionesAdjuntar.setVisibility(View.GONE);
            visualizarFoto.setVisibility(View.GONE);
            visualizarVideo.setVisibility(View.VISIBLE);
        });

        binding.lLOpcionSubirFoto.setOnClickListener(v -> {
            dialogVisualizarMultimedia.create();
            accionTomarFoto = false;
            esParaFoto = true;
            verificarPermisoGaleria(true);
            binding.cardOpcionesAdjuntar.setVisibility(View.GONE);
            visualizarFoto.setVisibility(View.VISIBLE);
            visualizarVideo.setVisibility(View.GONE);
        });

        binding.lLOpcionSubirVideo.setOnClickListener(v -> {
            dialogVisualizarMultimedia.create();
            accionTomarFoto = false;
            esParaFoto = false;
            verificarPermisoGaleria(false);
            binding.cardOpcionesAdjuntar.setVisibility(View.GONE);
            visualizarFoto.setVisibility(View.GONE);
            visualizarVideo.setVisibility(View.VISIBLE);
        });

        enviarMultimedia.setOnClickListener(v -> {
            if (esParaFoto || accionTomarFoto) {
                controladorSeguimiento.subirFotoChat(videoUri,
                        seguimiento,
                        dialogVisualizarMultimedia,
                        mensajesRef,
                        enviarMultimedia,
                        barraProgresoSubirMultimedia,
                        false,
                        mensajeRespondido);
            } else {
                controladorSeguimiento.subirVideoChat(videoUri,
                        seguimiento,
                        dialogVisualizarMultimedia,
                        mensajesRef,
                        enviarMultimedia,
                        barraProgresoSubirMultimedia,
                        false,
                        mensajeRespondido);
            }
            cancelarRespuesta();
        });

        // Un seguimiento retirado llega ya cerrado desde la lista
        aplicarEstadoSeguimiento(seguimiento.getEstado());

        // Y si el voluntario lo cierra con el chat abierto, la barra de escritura se bloquea
        // en el momento, sin necesidad de salir y volver a entrar
        escuchaEstadoSeguimiento = controladorSeguimiento.escucharEstadoSeguimiento(seguimiento.getId(),
                new ControladorSeguimiento.Callback<String>() {
                    @Override
                    public void onComplete(String estado) {
                        seguimiento.setEstado(estado);
                        aplicarEstadoSeguimiento(estado);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("FIREBASE", "Error al escuchar el estado del seguimiento");
                    }
                });
    }

    // Solo un seguimiento activo admite mensajes nuevos. Cerrado se puede leer, pero no
    // responder: la conversación queda como constancia del motivo del retiro.
    private void aplicarEstadoSeguimiento(String estado) {
        boolean activo = estado != null && estado.equalsIgnoreCase(EstadosCuentas.ACTIVO.toString());

        if (activo || seguimientoCerrado) {
            return;
        }

        seguimientoCerrado = true;

        cancelarRespuesta();
        binding.cardOpcionesAdjuntar.setVisibility(View.GONE);
        binding.lLOpcionesMensajeria.setVisibility(View.GONE);
        binding.lLSeguimientoCerrado.setVisibility(View.VISIBLE);

        // Sin el ItemTouchHelper el gesto de responder deja de existir
        if (deslizarParaResponder != null) {
            deslizarParaResponder.attachToRecyclerView(null);
        }

        // El teclado puede estar abierto justo cuando llega el cierre
        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (teclado != null) {
            teclado.hideSoftInputFromWindow(binding.main.getWindowToken(), 0);
        }

        if (dialogVisualizarMultimedia != null && dialogVisualizarMultimedia.isShowing()) {
            dialogVisualizarMultimedia.dismiss();
        }
    }

    // Muestra sobre el campo de texto el mensaje que se va a responder
    private void mostrarPanelRespuesta(Mensaje mensaje) {
        mensajeRespondido = mensaje;

        String emisor = usuarioActual != null && usuarioActual.getUid().equalsIgnoreCase(mensaje.getIdEmisor())
                ? "Tú"
                : mensaje.getEmisor();

        binding.tVRespondiendoEmisor.setText(emisor);
        binding.tVRespondiendoContenido.setText(controladorUtilidades.describirContenidoMensaje(mensaje.getContenido()));
        binding.lLRespondiendoMensaje.setVisibility(View.VISIBLE);

        binding.eTEscribirMensaje.requestFocus();
    }

    private void cancelarRespuesta() {
        mensajeRespondido = null;
        binding.lLRespondiendoMensaje.setVisibility(View.GONE);
    }

    // Desplaza la lista hasta el último mensaje recibido
    private void desplazarAlUltimoMensaje() {
        if (!listaMensajes.isEmpty()) {
            binding.recyclerViewChatSeguiAdop.post(() ->
                    binding.recyclerViewChatSeguiAdop.scrollToPosition(listaMensajes.size() - 1));
        }
    }

    // Envía un mensaje de texto al chat del seguimiento, con el adoptante como emisor
    private void enviarMensajeTexto(String contenidoMensaje) {
        if (contenidoMensaje.isEmpty()) {
            return;
        }

        // El seguimiento pudo cerrarse con el mensaje ya escrito
        if (seguimientoCerrado) {
            Toast.makeText(this, "Este seguimiento está cerrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique Firebase key for the message
        DatabaseReference mensajeRef = mensajesRef.push(); // Generate a node with a unique ID
        String mensajeId = mensajeRef.getKey(); // Get the key (ID)

        // Create the new message object and assign the Firebase key as ID
        Mensaje nuevoMensaje = new Mensaje(
                mensajeId,  // Use the key generated by Firebase as the ID
                seguimiento.getNombreAdoptante(),
                seguimiento.getIdAdoptante(),
                contenidoMensaje,
                seguimiento.getNombreVoluntario(),
                seguimiento.getIdVoluntario(),
                System.currentTimeMillis()
        );

        // Si se está respondiendo, se copia la cita dentro del mensaje
        if (mensajeRespondido != null) {
            nuevoMensaje.setIdMensajeRespondido(mensajeRespondido.getId());
            nuevoMensaje.setEmisorRespondido(mensajeRespondido.getEmisor());
            nuevoMensaje.setContenidoRespondido(mensajeRespondido.getContenido());
        }

        // Save the message in Firebase using the generated key
        mensajeRef.setValue(nuevoMensaje).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.eTEscribirMensaje.setText(""); // Clear the input field
                cancelarRespuesta();
                controladorNotificaciones.enviarNotificacionMensajeChat(seguimiento, false, contenidoMensaje);
            } else {
                Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mientras el chat esté abierto no se reciben notificaciones de este chat
        if (seguimiento != null) {
            controladorSeguimiento.marcarPresenciaChat(seguimiento.getListaMensajes(), seguimiento.getIdAdoptante());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (seguimiento != null) {
            controladorSeguimiento.quitarPresenciaChat(seguimiento.getListaMensajes(), seguimiento.getIdAdoptante());
        }

        // Liberar todos los players cuando la actividad se pausa
        if (listaMensajesAdaptador != null) {
            listaMensajesAdaptador.releaseAllPlayers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listaMensajesAdaptador != null) {
            listaMensajesAdaptador.releaseAllPlayers();
        }

        if (escuchaEstadoSeguimiento != null) {
            escuchaEstadoSeguimiento.remove();
        }
    }

    private void configurarVideoView() {
        //Asegurarse de que el vídeo esté preparado para poder reproducirlo y evitar errores de sincronización
        visualizarVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                iVReproducirVideo.setVisibility(View.INVISIBLE);
                visualizarVideo.start();
            }
        });

        //Código para reproducir vídeo al tocar el VideoView
        frameLayoutVisualizarMultimedia.setOnClickListener(v -> {
            if (videoUri != null) {
                if (reproduciendo) {
                    visualizarVideo.pause();
                    iVReproducirVideo.setVisibility(View.VISIBLE);
                    iVReproducirVideo.setImageResource(R.drawable.ic_reproducir);
                } else {
                    visualizarVideo.start();
                    iVReproducirVideo.setVisibility(View.INVISIBLE);
                }
                reproduciendo = !reproduciendo;
            } else {
                Toast.makeText(this, "No se ha grabado un video", Toast.LENGTH_SHORT).show();
            }
        });

        // Establecer el listener para cuando el video termine
        visualizarVideo.setOnCompletionListener(mediaPlayer -> {
            reproduciendo = !reproduciendo;
            // Cambiar la imagen a "play" cuando el video termine
            iVReproducirVideo.setVisibility(View.VISIBLE);
            iVReproducirVideo.setImageResource(R.drawable.ic_reproducir);
        });
    }

    private void verificarPermisoCamara(boolean paraFoto) {
        String permisoNecesario = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(this, permisoNecesario) == PackageManager.PERMISSION_GRANTED) {
            if (paraFoto) {
                abrirCamara(); // Llamar al método para tomar foto
            } else {
                abrirCamaraParaVideo(); // Llamar al método para grabar video
            }
        } else {
            // Solicitar el permiso necesario
            solicitarPermisosCamaraLauncher.launch(permisoNecesario);
        }
    }

    private final ActivityResultLauncher<String> solicitarPermisosCamaraLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    if (esParaFoto) { // Es para tomar una foto
                        abrirCamara();
                    } else { // Es para grabar un video
                        abrirCamaraParaVideo();
                    }
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
                }
            }
    );

    private void abrirCamara() {
        // Crear un archivo temporal para guardar la foto
        archivoFoto = new File(getCacheDir(), "foto_" + System.currentTimeMillis() + ".jpg");

        // Crear la URI del archivo para usar con FileProvider
        videoUri = FileProvider.getUriForFile(this, "com.example.tic_pv.fileprovider", archivoFoto);

        // Intent para capturar la imagen
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri); // Guardar en archivo directamente
        seleccionarFotoLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> seleccionarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Mostrar la foto en alta calidad (usar ZoomageView o ImageView)
                    dialogVisualizarMultimedia.show();
                    frameLayoutVisualizarMultimedia.setVisibility(View.GONE);
                    visualizarVideo.setVisibility(View.GONE);
                    visualizarFoto.setVisibility(View.VISIBLE);
                    visualizarFoto.setImageURI(videoUri); // Mostrar la foto capturada
                }
            }
    );

    private void abrirCamaraParaVideo() {
        // Crear un archivo temporal para el video
        archivoFoto = new File(getCacheDir(), "video_" + System.currentTimeMillis() + ".mp4");

        // Crear la URI del archivo para usar con FileProvider
        videoUri = FileProvider.getUriForFile(this, "com.example.tic_pv.fileprovider", archivoFoto);

        // Intent para grabar el video
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri); // Guardamos el video en el lugar especificado
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15); // Límite de tiempo para el video en segundos
        grabarVideoLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> grabarVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    dialogVisualizarMultimedia.show();
                    videoUri = result.getData().getData(); // URI del video grabado
                    visualizarFoto.setVisibility(View.GONE);
                    frameLayoutVisualizarMultimedia.setVisibility(View.VISIBLE);
                    visualizarVideo.setVisibility(View.VISIBLE);
                    visualizarVideo.setVideoURI(videoUri); // Mostrar el video grabado
                    visualizarVideo.start(); // Inicia la reproducción del video
                }
            }
    );

    private void verificarPermisoGaleria(boolean paraFoto) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33 (Android 13) y superior
            String permisoNecesario = paraFoto ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_MEDIA_VIDEO;

            if (ContextCompat.checkSelfPermission(this, permisoNecesario) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria(paraFoto); // Abrir galería según la función seleccionada
            } else {
                solicitarPermisosGaleriaLauncher.launch(permisoNecesario);
            }
        } else {
            // Android 12 y versiones anteriores
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria(paraFoto);
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void abrirGaleria(boolean paraFoto) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Configurar el tipo de contenido según sea necesario
        intent.setType(paraFoto ? "image/*" : "video/*");
        seleccionarGaleriaLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> seleccionarGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData(); // Obtener la URI del archivo seleccionado

                    if (videoUri != null) {
                        if (esParaFoto) { // Es una foto
                            // Mostrar la imagen seleccionada
                            dialogVisualizarMultimedia.show();
                            frameLayoutVisualizarMultimedia.setVisibility(View.GONE);
                            visualizarVideo.setVisibility(View.GONE);
                            visualizarFoto.setVisibility(View.VISIBLE);
                            visualizarFoto.setImageURI(videoUri);
                        } else { // Es un video
                            // Configurar el VideoView para reproducir el video seleccionado
                            dialogVisualizarMultimedia.show();
                            visualizarFoto.setVisibility(View.GONE);
                            frameLayoutVisualizarMultimedia.setVisibility(View.VISIBLE);
                            visualizarVideo.setVisibility(View.VISIBLE);
                            visualizarVideo.setVideoURI(videoUri);
                            visualizarVideo.start();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> solicitarPermisosGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirGaleria(esParaFoto); // Abrir la galería después de otorgar el permiso
                } else {
                    Toast.makeText(this, "Permiso para acceder a la galería denegado", Toast.LENGTH_LONG).show();
                }
            }
    );
}
