package com.example.mybookshopapp.entity.security;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_contact")
public class UserContact implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_bok_contact", sequenceName = "seq_bok_contact", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bok_contact")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BookstoreUser user;

    @Enumerated(EnumType.STRING)
    private ContactType type;

    @Column(columnDefinition = "SMALLINT default '0'")
    private Integer approved = 0;

    private String code;

    @Column(name = "code_trials")
    private Integer codeTrials;

    @Column(name = "code_time", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime codeTime;

    private String contact;

    public UserContact(BookstoreUser user, String contact, Integer approved, ContactType type) {
        this.user = user;
        this.type = type;
        this.approved = approved;
        this.contact = contact;
    }

    public UserContact(String code, String contact) {
        this();
        this.code = code;
        this.contact = contact;
    }

    public UserContact() {
        this.codeTime = LocalDateTime.now();
    }
}
