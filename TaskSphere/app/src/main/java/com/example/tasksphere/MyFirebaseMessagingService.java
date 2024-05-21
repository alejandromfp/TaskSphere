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
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construir y mostrar la notificación

        android.app.Notification.Style style = new android.app.Notification.BigTextStyle().bigText(body);
        Notification.Builder builder = new Notification.Builder(this, "channel_id")
                .setSmallIcon(android.R.drawable.ic_dialog_info) //CAMBIAR AQUI POR EL ICONO DE LA APP
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(style)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        notificationManager.notify(23232, builder.build());
    }

}