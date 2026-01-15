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

public class MenuAdministradorActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser authUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        authUsuario = auth.getCurrentUser();

        Button btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
        Button btnVerInformacion = findViewById(R.id.btnVerInformaciónAdmin);

        btnVerUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuAdministradorActivity.this, VerUsuariosActivity.class);
                startActivity(i);
            }
        });

        btnVerInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuAdministradorActivity.this, VerInformacionPerfilActivity.class);
                String id = authUsuario.getUid();
                i.putExtra("id", id);
                startActivity(i);
            }
        });
    }
}