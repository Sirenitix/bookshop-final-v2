package com.example.mybookshopapp.repository;


import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Genre;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    @Override
    @EntityGraph(value = "Genre.books", type = EntityGraph.EntityGraphType.FETCH)
    List<Genre> findAll();

    @EntityGraph(value = "Genre.books", type = EntityGraph.EntityGraphType.FETCH)
    List<Genre> findGenresByParentId(Integer id);

    List<Genre> findByBooksIn(List<Book> books);

    Genre findBySlug(String slug);

    Genre findByName(String name);
}
