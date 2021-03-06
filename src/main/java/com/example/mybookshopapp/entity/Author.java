package com.example.mybookshopapp.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "authors")
@ApiModel(description = "data model of author entity")
public class Author implements Serializable {

    @Id
    @SequenceGenerator(name = "seq_author_id", sequenceName = "seq_author_id", initialValue = 1001, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_author_id")
    @ApiModelProperty(value = "author id generated by bd", position = 1)
    private Integer id;

    @ApiModelProperty(value = "name of author", example = "Bob Blaskovits", position = 2)
    private String name;

    @ApiModelProperty(value = "link to photo of author", example = "Bob Blaskovits", position = 3)
    private String photo;

    @ApiModelProperty(value = "mnemonic identifier of author", position = 5)
    private String slug;

    @ApiModelProperty(value = "description (biography, characteristics) of author", position = 4)
    @Column(columnDefinition = "TEXT")
    private String description;

    public Author(List<String> authors) {
        if (authors != null) {
            this.name = authors.toString();
        }
    }

    public Author() {
    }

    @Override
    public String toString() {
        return name;
    }


}
