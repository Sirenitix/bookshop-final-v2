package com.example.mybookshopapp.entity;

import com.example.mybookshopapp.dto.MessageDto;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_message", sequenceName = "seq_message", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_message")
    private Integer id;

    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime time;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private BookstoreUser user;

    private String email;

    private String name;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String text;

    public Message(BookstoreUser user, MessageDto messageDto, String email) {
        this();
        this.user = user;
        this.name = user.getName();
        this.email = email;
        this.subject = messageDto.getSubject();
        this.text = messageDto.getText();
    }

    public Message(MessageDto messageDto) {
        this();
        this.name = messageDto.getName();
        this.email = messageDto.getEmail();
        this.subject = messageDto.getSubject();
        this.text = messageDto.getText();
    }

    public Message() {
        time = LocalDateTime.now();
    }
}
