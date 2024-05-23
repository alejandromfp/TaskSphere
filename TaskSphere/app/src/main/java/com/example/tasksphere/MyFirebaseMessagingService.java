package com.example.tasksphere;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Aquí manejas las notificaciones entrantes cuando la app está en primer plano
        if (remoteMessage.getNotification() != null) {
            // Obtener datos de la notificación
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Mostrar la notificación
            mostrarNotificacion(title, body);
        }
    }

    private void mostrarNotificacion(String title, String body) {
        // Obtener el servicio de notificación
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        // Crear el canal de notificación (solo necesario en Android 8.0 y superior)
        createNotificationChannel();

        // Intent para abrir la actividad principal al hacer clic en la notificación
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent para abrir la actividad principal
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono de notificación
                .setContentTitle(title) // Título de la notificación
                .setContentText(body) // Contenido de la notificación
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Permitir que la notificación se cierre al hacer clic en ella

        // Mostrar la notificación
        notificationManager.notify(23232, builder.build());
    }

    private void createNotificationChannel() {
        // Verificar si el dispositivo está en Android 8.0 (Oreo) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear el canal de notificación
            CharSequence name = "Nombre del canal";
            String description = "Descripción del canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            // Registrar el canal en el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}