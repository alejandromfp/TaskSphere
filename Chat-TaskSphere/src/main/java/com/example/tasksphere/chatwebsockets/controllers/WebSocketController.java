package com.example.tasksphere.chatwebsockets.controllers;

import com.example.tasksphere.chatwebsockets.modelo.entidad.ChatMessage;
import com.example.tasksphere.chatwebsockets.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebSocketController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat")
    public ChatMessage sendMessage(ChatMessage message) {
        chatService.saveMessage(message);
        return message;
    }

    @MessageMapping("/chat.getAllMessages")
    @SendTo("/topic/history")
    public List<ChatMessage> getAllMessages() {
        return chatService.getAllMessages();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/chat/history")
    public List<ChatMessage> getChatHistory() {
        return chatService.getAllMessages();
    }
}