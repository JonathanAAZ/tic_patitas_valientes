package com.example.tic_pv.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuAdoptanteActivity extends AppCompatActivity {

    private Button btnVerInformacionPersonal;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser authUsuario = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_adoptante);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        btnVerInformacionPersonal = findViewById(R.id.btnVerPerfilAdop);
        btnVerInformacionPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuAdoptanteActivity.this, VerInformacionPerfilActivity.class);
                String id = authUsuario.getUid();
                i.putExtra("id", id);
                startActivity(i);
            }
        });
    }
}