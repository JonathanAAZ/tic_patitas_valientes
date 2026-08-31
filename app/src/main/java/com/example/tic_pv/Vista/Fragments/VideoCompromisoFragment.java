package com.example.tic_pv.Vista.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.FragmentVideoCompromisoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoCompromisoFragment extends Fragment {

    private FragmentVideoCompromisoBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idAdopcion;
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ExoPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoCompromisoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        assert getArguments() != null;
        idAdopcion = getArguments().getString("idAdopcion");


//        Log.d("ID ADOPCION", idAdopcion);

        db.collection("Adopciones").document(idAdopcion).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String videoCompromiso = documentSnapshot.getString("videoCompromiso");
                        player = controladorUtilidades.insertarVideoConExoPlayer(videoCompromiso,
                                binding.videoCompromisoUltimos,
                                binding.barraProgresoVideoCompromisoUltimos);

                    } else {
                        Log.e("ERROR", "El documento no existe");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Sin esto el video sigue sonando al salir de la pantalla
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
            player = null;
        }
        binding = null;
    }
}