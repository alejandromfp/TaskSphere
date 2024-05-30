package com.example.tasksphere.chatwebsockets.repo;

import com.example.tasksphere.chatwebsockets.modelo.entidad.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
}