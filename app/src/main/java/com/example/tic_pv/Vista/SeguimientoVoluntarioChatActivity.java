package com.example.tic_pv.Vista;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tic_pv.Adaptadores.ListaMensajesAdaptador;
import com.example.tic_pv.Controlador.ControladorSeguimiento;
import com.example.tic_pv.Modelo.Mensaje;
import com.example.tic_pv.Modelo.Seguimiento;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivitySeguimientoVoluntarioChatBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.UUID;

public class SeguimientoVoluntarioChatActivity extends AppCompatActivity {
    private Seguimiento seguimiento;
    private ListaMensajesAdaptador listaMensajesAdaptador;
    private ArrayList<Mensaje> listaMensajes;
    private DatabaseReference mensajesRef;
    private ActivitySeguimientoVoluntarioChatBinding binding;
    private final ControladorSeguimiento controladorSeguimiento = new ControladorSeguimiento();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimiento_voluntario_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        seguimiento = intent.getParcelableExtra("seguimiento");

        binding = ActivitySeguimientoVoluntarioChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listaMensajes = new ArrayList<>();
        listaMensajesAdaptador = new ListaMensajesAdaptador(listaMensajes);
        binding.recyclerViewChatSeguiVol.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewChatSeguiVol.setAdapter(listaMensajesAdaptador);

        // Inicializar la lista de mensajes
        assert seguimiento != null;
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
                    binding.recyclerViewChatSeguiVol.scrollToPosition(result.size() - 1);
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
                    binding.recyclerViewChatSeguiVol.scrollToPosition(listaMensajes.size() - 1); // Desplazar al último
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Este método captura actualizaciones en los nodos hijos (ediciones)
                Mensaje mensajeActualizado = snapshot.getValue(Mensaje.class);
                String mensajeId = snapshot.getKey();  // Obtenemos el ID del mensaje

                System.out.println("onChildChanged Triggered");
                System.out.println("Mensaje ID: " + mensajeId);

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

        // Manejo del botón de enviar mensaje
        binding.lLEnviarMensaje.setOnClickListener(v -> {
            String contenidoMensaje = binding.eTEscribirMensaje.getText().toString().trim();
            if (!contenidoMensaje.isEmpty()) {
                // Generate a unique Firebase key for the message
                DatabaseReference mensajeRef = mensajesRef.push(); // Generate a node with a unique ID
                String mensajeId = mensajeRef.getKey(); // Get the key (ID)

                // Create the new message object and assign the Firebase key as ID
                Mensaje nuevoMensaje = new Mensaje(
                        mensajeId,  // Use the key generated by Firebase as the ID
                        seguimiento.getNombreVoluntario(),
                        seguimiento.getIdVoluntario(),
                        contenidoMensaje,
                        seguimiento.getNombreAdoptante(),
                        seguimiento.getIdAdoptante(),
                        System.currentTimeMillis()
                );

                // Save the message in Firebase using the generated key
                mensajeRef.setValue(nuevoMensaje).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.eTEscribirMensaje.setText(""); // Clear the input field
                    } else {
                        Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}