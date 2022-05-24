package com.example.mybookshopapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "document")
public class Document implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sort_index", columnDefinition="INT default '0'")
    private Integer sortIndex = 0;

    private String slug;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;
}
