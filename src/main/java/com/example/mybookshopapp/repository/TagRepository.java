package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @EntityGraph(value = "tag.books", type = EntityGraph.EntityGraphType.FETCH)
    List<Tag> findAll();

    List<Tag> findByBooksIn(List<Book> books);

    Tag findByName(String name);
}
