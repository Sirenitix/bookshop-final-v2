package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
