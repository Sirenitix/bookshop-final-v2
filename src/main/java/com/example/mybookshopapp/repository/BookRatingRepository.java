package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookRating;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {

    BookRating findByBookAndUser(Book book, BookstoreUser user);
}
