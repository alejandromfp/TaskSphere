package com.example.tasksphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewTreeObserver;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.tasksphere.WebSocketService.ChatApi;
import com.example.tasksphere.WebSocketService.ChatWebSocketListener;
import com.example.tasksphere.WebSocketService.WebSocketManager;
import com.example.tasksphere.adapter.FichajesAdapter;
import com.example.tasksphere.adapter.MensajesAdapter;
import com.example.tasksphere.modelo.entidad.Message;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.StompClient;


import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatActivity extends AppCompatActivity {

    private final String API_URL = "https://tasksphere-chat.zeabur.app";

    private final String WEBSOCKET_URL = "ws://tasksphere-chat.zeabur.app/chat";

    private Gson mGson = new GsonBuilder().create();
    RecyclerView recyclerMensajes;

    EditText chatInput;

    Button sendMessage, backButton;

    ProgressDialog progressDialog;

    private StompClient stompClient;
    MensajesAdapter adapter;


    SharedPreferences sharedPreferences;
    User usuario;
    List<Message> chatHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        obtenerDatosDeUsuario();

        recyclerMensajes  = findViewById(R.id.recyclerContainer);
        adapter = new MensajesAdapter(this, chatHistory, usuario.getUserId());
        recyclerMensajes.setAdapter(adapter);
        backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        chatInput = findViewById(R.id.chat_input);
        chatInput.setOnClickListener(v -> {
            if(chatHistory.size()>0)
                recyclerMensajes.smoothScrollToPosition(chatHistory.size()-1);
        });
        sendMessage = findViewById(R.id.sendbutton);
        sendMessage.setOnClickListener(v -> {
            enviarMensaje();
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatApi api = retrofit.create(ChatApi.class);
        Call<List<Message>> call = api.getChatHistory();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando chats...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL);
        stompClient.connect();

        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    Log.d("121212", "FUNCIONE");
                    List<Message> chatHistorial = response.body();
                    chatHistory.addAll(chatHistorial);
                    Log.d("121212", String.valueOf(chatHistory.size()));
                    adapter.notifyDataSetChanged();
                    if(chatHistory.size()>0)
                        recyclerMensajes.smoothScrollToPosition(chatHistory.size()-1);
                    progressDialog.dismiss();
                    escucharWebSocket();
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.d("121212", "FALLE");
                Log.d("121212", t.getMessage());
                t.printStackTrace();
            }
        });

    }

    public void enviarMensaje(){

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
           Message newMessage = new Message();
           newMessage.setUserId(usuario.getUserId());
           newMessage.setUsername(usuario.getNombre());
           newMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
           newMessage.setContent(chatInput.getText().toString());
           chatInput.setText("");
           Log.d("sendMessage", gson.toJson(newMessage));
           stompClient.send("/app/chat.sendMessage", gson.toJson(newMessage)).subscribe();
    }

    @SuppressLint("CheckResult")
    public void escucharWebSocket() {
        stompClient.topic("/topic/chat").subscribe(topicMessage -> {
            runOnUiThread(() -> {
                Log.d("121212", "Received " + topicMessage.getPayload());
                Message newMessage = mGson.fromJson(topicMessage.getPayload(), Message.class);
                chatHistory.add(newMessage);
                adapter.notifyDataSetChanged();
                if(chatHistory.size()>0)
                    recyclerMensajes.smoothScrollToPosition(chatHistory.size()-1);
            });
        }, throwable -> {
            Log.e("121212", "Error on subscribe topic", throwable);
        });
    }

    private void obtenerDatosDeUsuario(){
        sharedPreferences = this.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", "uwu");
        Log.d("JSON", userJson);
        if(userJson != null){
            Gson gson = new Gson();
            usuario = gson.fromJson(userJson, User.class);
        }
    }




}