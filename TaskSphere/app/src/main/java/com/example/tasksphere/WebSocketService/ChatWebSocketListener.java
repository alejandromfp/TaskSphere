package com.example.tasksphere.WebSocketService;

import android.util.Log;

import com.example.tasksphere.adapter.MensajesAdapter;
import com.example.tasksphere.modelo.entidad.Message;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWebSocketListener extends WebSocketListener {
    List<Message> mensajes;


    MensajesAdapter adapter;
    public ChatWebSocketListener(List<Message> mensajes , MensajesAdapter adapter){
        this.mensajes = mensajes;
        this.adapter = adapter;
    }
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
        super.onOpen(webSocket, response);
        Log.d("121212", "CONEXION ESTABLECIDA CON EL WEBSOCKET");

    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);

        Log.d("WebSocket", "Mensaje recivido");
        try {

            JSONObject jsonMessage = new JSONObject(text);
            Message mensajeNuevo = new Message();
            mensajeNuevo.setMessageId(jsonMessage.getLong("id"));
            mensajeNuevo.setUserId(jsonMessage.getString("userId"));
            mensajeNuevo.setUsername(jsonMessage.getString("username"));
            mensajeNuevo.setContent(jsonMessage.getString("content"));
            //PARSEAR EL STRING A LOCALDATETIME
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(jsonMessage.getString("timestamp"), formatter);
            mensajes.add(mensajeNuevo);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("WebSocket", "Error parsing JSON message: " + e.getMessage());
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        Log.d("WebSocket", "Conexión cerrada: " + reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, okhttp3.Response response) {
        Log.e("WebSocket", "Error en la conexión: " + t.getMessage());
        if (response != null) {
            Log.e("WebSocket", "Respuesta: " + response);
        }
    }

}
