package com.example.mybookshopapp.entity;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "book2user")
public class BookUser implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_book_user", sequenceName = "seq_book_user", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_book_user")
    private Integer id;

    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private BookUserType type;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookstoreUser user;

    public BookUser(BookUserType type, Book book, BookstoreUser user) {
        this();
        this.type = type;
        this.book = book;
        this.user = user;
    }

    public BookUser() {
        this.time = LocalDateTime.now();
    }
}
