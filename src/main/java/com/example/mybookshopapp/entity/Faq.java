package com.example.mybookshopapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "faq")
public class Faq implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sort_index", columnDefinition="INT default '0'")
    private Integer sortIndex = 0;

    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;
}
