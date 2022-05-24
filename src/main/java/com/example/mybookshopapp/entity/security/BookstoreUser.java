package com.example.mybookshopapp.entity.security;

import com.example.mybookshopapp.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class BookstoreUser implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_user", sequenceName = "seq_user", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user")
    private Integer id;

    private String hash;

    @Column(name = "reg_time", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime regTime;

    @Column(columnDefinition="INT default '0'")
    private Integer balance = 0;

    private String name;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserContact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<BookReview> bookReviews = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BookReviewLike> bookReviewLikes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BookRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BookUser> bookUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BalanceTransaction> balanceTransactions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<FileDownload> fileDownloads = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

    public BookstoreUser() {
        this.regTime = LocalDateTime.now();
    }

    @JsonProperty("formatTime")
    public String getFormatTime() {
        return regTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }

    @JsonProperty("roleString")
    public String getRolesString() {
        return roles.toString().replaceAll("[\\[\\]]", "");
    }

    @PreRemove
    private void preRemove() {
        messages.forEach(message -> message.setUser(null));
        bookReviews.forEach(review -> review.setUser(null));
    }
}
