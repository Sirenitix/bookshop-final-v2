package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BookWithAuthorsDto;
import com.example.mybookshopapp.dto.BooksPageDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.service.BookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ViewedPageController {

    private final BookService bookService;

    public ViewedPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("viewedBooks")
    public List<BookWithAuthorsDto> viewedBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails) {
        List<Book> viewedBooks = bookService.getPageOfViewedBooksByUser(userDetails.getBookstoreUser(), 0, 20);
        return bookService.getBookWithAuthorDtoList(viewedBooks);
    }

    @GetMapping("/viewed")
    public String viewedPage() {
        return "books/viewed";
    }

    @GetMapping("/books/viewed")
    @ResponseBody
    public BooksPageDto getViewedBooksPage(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                           @RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) {
        List<Book> books = bookService.getPageOfViewedBooksByUser(userDetails.getBookstoreUser(), offset, limit);
        return new BooksPageDto(bookService.getBookWithAuthorDtoList(books));
    }
}
