package com.example.mybookshopapp.dto;

import com.example.mybookshopapp.entity.Author;
import com.example.mybookshopapp.entity.Book;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookWithAuthorsDto {

    private String slug;

    private Integer isBestseller;

    private String title;

    private String image;

    private Integer priceOld;

    private Integer discountPrice;

    private Integer discountPercent;

    private List<Author> authorList;

    public BookWithAuthorsDto(Book book, List<Author> author) {
        this.slug = book.getSlug();
        this.isBestseller = book.getIsBestseller();
        this.title = book.getTitle();
        this.image = book.getImage();
        this.priceOld = book.getPriceOld();
        this.discountPrice = book.discountPrice();
        this.discountPercent = book.getDiscountPercent();
        this.authorList = author;
    }

    public BookWithAuthorsDto() {
    }

    @JsonGetter("authors")
    public String authorsName() {
        if (authorList.isEmpty()) {
            return "unknown";
        }
        return authorList.size() == 1 ?
                authorList.get(0).getName() :
                authorList.get(0).getName() + " и другие";
    }
}
