package com.example.mybookshopapp.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Getter
@Setter
public class BookNewDto {

    @NotNull(message = "image must be chosen")
    private MultipartFile image;

    @NotNull(message = "file must be chosen")
    private MultipartFile fileBook;

    @NotBlank(message = "title must not be empty")
    private String title;

    @NotBlank(message = "author must not be empty")
    @Pattern(regexp = "([а-яА-Яa-zA-Z]+[\\s]?(,\\s)?)+$", message = "invalid characters, authors must be separated by comma with space")
    private String author;

    @NotBlank(message = "genre must not be empty")
    @Pattern(regexp = "([а-яА-Яa-zA-Z]+[\\s]?(,\\s)?)+$", message = "invalid characters, genres must be separated by comma with space")
    private String genre;

    @NotBlank(message = "tag must not be empty")
    @Pattern(regexp = "([а-яА-Яa-zA-Z]+[\\s]?(,\\s)?)+$", message = "invalid characters, tags must be separated by comma with space")
    private String tag;

    @NotNull(message = "price must be set")
    @Min(value = 10, message = "price must be between 10 and 5000")
    @Max(value = 5000, message = "price must be between 10 and 5000")
    private Integer price;

    @Min(value = 0, message = "discount must be between 0 and 99")
    @Max(value = 90, message = "discount must be between 0 and 99")
    private Integer discount;

    @NotBlank(message = "description must not be empty")
    private String description;
}
