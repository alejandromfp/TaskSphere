package com.example.tasksphere.modelo.entidad;

import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;

public class Message {

    @Expose(serialize = false)
    private long id;

    @Expose
    private String content, userId, username, timestamp;

    public Message() {
    }

    public long getId() {
        return id;
    }

    public void setMessageId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
