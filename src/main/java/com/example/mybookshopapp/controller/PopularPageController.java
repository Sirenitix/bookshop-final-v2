package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BookWithAuthorsDto;
import com.example.mybookshopapp.dto.BooksPageDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PopularPageController {

    private final BookService bookService;

    @Autowired
    public PopularPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("popularBooks")
    public List<BookWithAuthorsDto> popularBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                 @CookieValue(value = "userHash", required = false) String userHash) {
        List<Book> popularBooks;
        if (userDetails != null) {
            popularBooks = bookService.getPageOfPopularBooksForUser(userDetails.getBookstoreUser(), 0, 20);
        } else {
            popularBooks = bookService.getPageOfPopularBooksForNotAuthenticatedUser(userHash, 0, 20);
        }
        return bookService.getBookWithAuthorDtoList(popularBooks);
    }

    @GetMapping(value = "/books/popular", produces = MediaType.TEXT_HTML_VALUE)
    public String getPopularPage() {
        return "books/popular";
    }

    @GetMapping(value = "/books/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getPopularBooksPage(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                            @RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit,
                                            @CookieValue(value = "userHash", required = false) String userHash) {
        List<Book> books;
        if (userDetails != null) {
            books = bookService.getPageOfPopularBooksForUser(userDetails.getBookstoreUser(), offset, limit);
        } else {
            books = bookService.getPageOfPopularBooksForNotAuthenticatedUser(userHash, offset, limit);
        }
        return new BooksPageDto(bookService.getBookWithAuthorDtoList(books));
    }
}