package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Integer> {
}
