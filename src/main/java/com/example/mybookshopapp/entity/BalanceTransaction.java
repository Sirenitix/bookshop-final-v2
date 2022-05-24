package com.example.mybookshopapp.entity;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Getter
@Setter
@Entity
@Table(name = "balance_transaction")
public class BalanceTransaction implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_balance", sequenceName = "seq_balance", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_balance")
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private BookstoreUser user;

    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime time;

    private Integer value;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(columnDefinition = "TEXT")
    private String description;

    public BalanceTransaction(BookstoreUser user, Book book, Integer value, String description) {
        this();
        this.user = user;
        this.book = book;
        this.value = value;
        this.description = description;
    }

    public BalanceTransaction() {
        this.time = LocalDateTime.now();
    }

    public String getFormatTime() {
        return time.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
