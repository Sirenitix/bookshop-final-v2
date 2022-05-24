package com.example.mybookshopapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "genres")
@NamedEntityGraph(name = "Genre.books", attributeNodes = @NamedAttributeNode(value = "books"))
public class Genre implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_genre_id", sequenceName = "seq_genre_id", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_genre_id")
    private Integer id;

    @Column(name = "parent_id")
    private Integer parentId;

    private String slug;

    private String name;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "book2genre", joinColumns = @JoinColumn(name = "genre_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> books = new HashSet<>();

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", parent_id=" + parentId +
                ", name='" + name + '\'' +
                '}';
    }
}
