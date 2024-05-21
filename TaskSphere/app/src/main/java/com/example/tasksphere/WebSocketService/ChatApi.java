package com.example.tasksphere.WebSocketService;

import com.example.tasksphere.modelo.entidad.Message;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ChatApi {
    @GET("/chat/history")
    Call<List<Message>> getChatHistory();

}
