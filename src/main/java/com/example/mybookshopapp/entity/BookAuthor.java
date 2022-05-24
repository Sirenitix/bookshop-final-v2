package com.example.mybookshopapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book2author")
public class BookAuthor implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_book2author_id", sequenceName = "seq_book2author_id", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_book2author_id")
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private Integer sortIndex = 0;
}
