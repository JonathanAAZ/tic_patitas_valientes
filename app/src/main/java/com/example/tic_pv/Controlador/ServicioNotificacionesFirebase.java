package com.example.tic_pv.Controlador;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.tic_pv.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class ServicioNotificacionesFirebase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Soporte para mensaje tipo notification
        String titulo = null;
        String cuerpo = null;

        if (message.getNotification() != null) {
            titulo = message.getNotification().getTitle();
            cuerpo = message.getNotification().getBody();
        }

        // Soporte para mensaje tipo data (sobrescribe si viene desde servidor)
        if (!message.getData().isEmpty()) {
            if (message.getData().get("title") != null) {
                titulo = message.getData().get("title");
            }
            if (message.getData().get("body") != null) {
                cuerpo = message.getData().get("body");
            }
        }

        if (titulo == null && cuerpo == null) {
            Log.w("NOTIFICACIONES", "Mensaje recibido sin data ni notification");
            return;
        }

        mostrarNotificacion(titulo, cuerpo);
    }


    private void mostrarNotificacion(String titulo, String mensaje) {

        if (mensaje == null) mensaje = "";

        // Extraer mensaje corto
        String mensajeCorto;
        int indicePunto = mensaje.indexOf(".");

        if (indicePunto != -1) {
            mensajeCorto = mensaje.substring(0, indicePunto + 1);
        } else {
            mensajeCorto = mensaje;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "canal_notificaciones_v3")
                        .setSmallIcon(R.drawable.ic_huella_android)
                        .setContentTitle(titulo)
                        .setContentText(mensajeCorto)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        // Validar permisos en Android 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

            int notificationId = (int) System.currentTimeMillis();
            managerCompat.notify(notificationId, builder.build());

        } else {
            Log.e("PERMISOS", "Notificación bloqueada por falta de permiso POST_NOTIFICATIONS.");
        }
    }
}

