package com.example.tasksphere.chatwebsockets.services;

import com.example.tasksphere.chatwebsockets.modelo.entidad.ChatMessage;
import com.example.tasksphere.chatwebsockets.repo.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    public void saveMessage(ChatMessage message) {
        chatRepository.save(message);
    }

    public List<ChatMessage> getAllMessages() {
        return chatRepository.findAll();
    }
}