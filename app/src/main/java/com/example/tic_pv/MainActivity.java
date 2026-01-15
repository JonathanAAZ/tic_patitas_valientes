package com.example.tic_pv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.example.tic_pv.Controlador.ControladorContrato;
import com.example.tic_pv.Interfaces.ApiService;
import com.example.tic_pv.Modelo.NotificacionRequest;
import com.example.tic_pv.Modelo.RetrofitClient;
import com.example.tic_pv.Vista.BottomNavigationMenu;
import com.example.tic_pv.Vista.CrearCuentaActivity;
import com.example.tic_pv.Vista.CrearVoluntarioActivity;
import com.example.tic_pv.Vista.IniciarSesionActivity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button siguiente, irIS;
    private ControladorContrato controladorContrato = new ControladorContrato();
    private static final String TAG = "Notificaciones";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewCrearCuenta), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        crearCanalNotificacion();
        despertarServidor();

        pedirPermisosNotificaciones();

        limpiarFotosViejas();

        configuracionInicialCloudinary();

//        generarContratoGeneral();

        siguiente=(Button) findViewById(R.id.btnTest);
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, CrearCuentaActivity.class);
                startActivity(i);
//                enviarNotificacion();
            }
        });

        irIS = (Button) findViewById(R.id.btnIrIS);
        irIS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, IniciarSesionActivity.class);
                startActivity(i);
            }
        });

//        Log.d("MENSAJE", "Aplicacion iniciada correctamente");


    }

    private void configuracionInicialCloudinary() {
        Map <String, String> config = new HashMap<>();
        config.put("cloud_name", "de3pikkwa");
        config.put("api_key", "176417194926829");
        config.put("api_secret", "LITvQ_VpkeqIZcvbZwrS2JAl0as");
//        config.put("secure", true);
        MediaManager.init(this, config);
    }

    private void despertarServidor() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.despertarServidor().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Servidor", "Servidor activado correctamente");
                } else {
                    Log.e("Servidor", "Error al activar el servidor" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("Servidor", "Fallo al conectar con el servidor" + throwable.getMessage());
            }
        });
    }

    private void pedirPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Mayor a API 33
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                Log.d("PERMISOS", "Solicitando permiso de notificaciones...");

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                Log.d("PERMISOS", "El permiso de notificaciones ya fue concedido.");
            }
        } else {
            Log.d("PERMISOS", "El dispositivo no requiere permisos de notificación (Android < 13).");
        }
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String canalId = "canal_notificaciones_v3"; // Debe coincidir con el que usas al crear la notificación
            String nombre = "Notificaciones Generales V3";
            String descripcion = "Canal para notificaciones de la app";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel canal = new NotificationChannel(canalId, nombre, importancia);
            canal.setDescription(descripcion);
            canal.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

//    private void enviarNotificacion() {
//        // Token del usuario al que se enviará la notificación
//        String token = "ef2NDYXZSSW_NisMwYFAs7:APA91bEMqUnQmkL6ARj-PFJ5-BqlQClr2NjlPgN925KJHsWdiNFVbko5mURF-MwFAMVvZv1_jc2vNLEEsaSMQI2IU_nVo_PEqoBn_GktBSKv7ynk2Inrgp8";
//
//        NotificacionRequest request = new NotificacionRequest(token, "Hola!", "Este es un mensaje de prueba");
//        ApiService apiService = RetrofitClient.getApiService();
//
//        apiService.enviarNotificacion(request).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "Notificación enviada!", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        String errorBody = response.errorBody().string(); // Obtener respuesta del servidor
//                        Log.e("Notificaciones", "Error en la respuesta: " + errorBody);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(MainActivity.this, "Error al enviar notificación", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e(TAG, "Error: " + t.getMessage());
//            }
//        });
//    }

    private void limpiarFotosViejas() {
        File directorioFotos = getCacheDir(); // O getExternalFilesDir(null) si no son temporales
        if (directorioFotos != null && directorioFotos.isDirectory()) {
            File[] archivos = directorioFotos.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    long tiempoCreacion = archivo.lastModified();
                    long tiempoActual = System.currentTimeMillis();
//                    long tiempoLimite = 24 * 60 * 60 * 1000; // 24 horas
                    long tiempoLimite = 5 * 60 * 1000; // 24 horas

                    if (tiempoActual - tiempoCreacion > tiempoLimite) {
                        if (archivo.delete()) {
                            Log.d("Limpieza", "Archivo eliminado: " + archivo.getName());
                        } else {
                            Log.e("Limpieza", "No se pudo eliminar: " + archivo.getName());
                        }
                    }
                }
            }
        }
    }

    private void generarContratoGeneral() {
        controladorContrato.configurarClausulasContrato();
    }

}