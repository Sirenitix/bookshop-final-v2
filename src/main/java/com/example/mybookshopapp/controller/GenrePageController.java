package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BooksPageDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Genre;
import com.example.mybookshopapp.service.BookService;
import com.example.mybookshopapp.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class GenrePageController {

    private final GenreService genreService;
    private final BookService bookService;

    public GenrePageController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @GetMapping("/genres")
    public String getGenrePage(Model model) {
        model.addAttribute("genresRoot", genreService.getGenresWithoutParent());
        model.addAttribute("genresDto", genreService.getListGenreDto());
        return "genres/index";
    }

    @GetMapping("/genres/{slug}")
    public String getGenreSlugPage(@PathVariable String slug, Model model) {
        Genre genre = genreService.getGenreBySlug(slug);
        Genre firstParent = null;
        Genre secondParent = null;
        if (genre.getParentId() != null) {
            firstParent = genreService.getGenreById(genre.getParentId());
        }
        if (firstParent != null && firstParent.getParentId() != null) {
            secondParent = genreService.getGenreById(firstParent.getParentId());
        }
        List<Book> books = bookService.getBooksByGenreId(0, 20, genre.getId());
        model.addAttribute("genre", genre);
        model.addAttribute("booksOfGenre", bookService.getBookWithAuthorDtoList(books));
        model.addAttribute("firstParent", firstParent);
        model.addAttribute("secondParent", secondParent);
        return "genres/slug";
    }

    @GetMapping("/books/genre/{id}")
    @ResponseBody
    public BooksPageDto getBooksByGenre(@RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit,
                                        @PathVariable("id") Integer id) {
        List<Book> books = bookService.getBooksByGenreId(offset, limit, id);
        return new BooksPageDto(bookService.getBookWithAuthorDtoList(books));
    }
}
