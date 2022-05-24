package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Author;
import com.example.mybookshopapp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Query("SELECT a FROM Author a, BookAuthor ba WHERE ba.author = a AND ba.book IN :books")
    List<Author> findByBookIn(List<Book> books);

    @Query("SELECT a FROM Author a, BookAuthor ba WHERE ba.author = a AND ba.book = :book ORDER BY ba.sortIndex")
    List<Author> findByBook(Book book);

    Author findBySlug(String slug);

    Author findByName(String name);
}
