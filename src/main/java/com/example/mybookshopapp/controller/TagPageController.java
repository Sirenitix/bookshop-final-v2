package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BooksPageDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.service.BookService;
import com.example.mybookshopapp.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TagPageController {

    private final TagService tagService;

    private final BookService bookService;

    public TagPageController(TagService tagService, BookService bookService) {
        this.tagService = tagService;
        this.bookService = bookService;
    }

    @GetMapping("/tags")
    public String getTagPage(@RequestParam("tag") Integer id, Model model) {
        List<Book> books = bookService.getBookByTag(0, 20, id);
        model.addAttribute("tag", tagService.getById(id));
        model.addAttribute("booksByTag", bookService.getBookWithAuthorDtoList(books));
        return "tags/index";
    }

    @GetMapping("/books/tag/{id}")
    @ResponseBody
    public BooksPageDto getBooksPage(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit,
                                     @PathVariable("id") Integer id) {
        List<Book> books = bookService.getBookByTag(offset, limit, id);
        return new BooksPageDto(bookService.getBookWithAuthorDtoList(books));
    }
}
