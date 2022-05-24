package com.example.mybookshopapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BooksPageDto {

    private Integer count;
    private List<BookWithAuthorsDto> books;

    public BooksPageDto(List<BookWithAuthorsDto> books) {
        this.books = books;
        this.count = books.size();
    }
}
