package com.example.tasksphere.WebSocketService;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WebSocketManager {
    private WebSocket webSocket;
    public void connectWebSocket(String url, ChatWebSocketListener listener) {
        OkHttpClient client = new OkHttpClient();
        Log.d("WebSocket", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("WebSocket", request.url().toString());
        webSocket = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }
}
