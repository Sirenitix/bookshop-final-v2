package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    Document findBySlug(String slug);
}
