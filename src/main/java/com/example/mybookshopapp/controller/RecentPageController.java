package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BookWithAuthorsDto;
import com.example.mybookshopapp.dto.BooksPageDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
public class RecentPageController {

    private final BookService bookService;

    @Autowired
    public RecentPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recentBooks")
    public List<BookWithAuthorsDto> recentBooks() {
        List<Book> recentBooks = bookService.getPageOfRecentBooks(0, 20).getContent();
        return bookService.getBookWithAuthorDtoList(recentBooks);
    }

    @GetMapping( value = "/books/recent", produces = MediaType.TEXT_HTML_VALUE)
    public String getRecentPage() {
        return "books/recent";
    }

    @GetMapping(value = "/books/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getRecentBooksPage(@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
                                           @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date to,
                                           @RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) {
        List<Book> books = bookService.getPageOfRecentBooks(from, to, offset, limit);
        return new BooksPageDto(bookService.getBookWithAuthorDtoList(books));
    }

}
