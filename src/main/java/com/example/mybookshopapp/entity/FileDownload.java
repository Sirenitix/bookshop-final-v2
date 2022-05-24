package com.example.mybookshopapp.entity;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "file_download")
public class FileDownload implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_file_download", sequenceName = "seq_file_download", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_file_download")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookstoreUser user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(columnDefinition = "integer default 1")
    private Integer count = 1;

    public FileDownload(BookstoreUser user, Book book) {
        this.user = user;
        this.book = book;
    }

    public FileDownload() {
    }
}
