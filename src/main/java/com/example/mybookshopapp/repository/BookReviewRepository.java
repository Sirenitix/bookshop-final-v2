package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    List<BookReview> findAllByBookSlug(String slug);
}
