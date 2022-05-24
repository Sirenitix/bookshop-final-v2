package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BookWithAuthorsDto;
import com.example.mybookshopapp.dto.BooksPageDto;
import com.example.mybookshopapp.dto.SearchWordDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Tag;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.errs.EmptySearchException;
import com.example.mybookshopapp.service.BookService;
import com.example.mybookshopapp.service.BookUserService;
import com.example.mybookshopapp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;
    private final BookUserService bookUserService;

    @Autowired
    public MainPageController(BookService bookService, TagService tagService, BookUserService bookUserService) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.bookUserService = bookUserService;
    }

    @ModelAttribute("searchResult")
    public List<Book> searchResult() {
        return new ArrayList<>();
    }

    @ModelAttribute("tags")
    public Map<Tag, Integer> tagsOfBooks() {
        return tagService.getTagsAndCount();
    }

    @ModelAttribute("recentBooks")
    public List<BookWithAuthorsDto> recentBooks() {
        List<Book> recentBooks = bookService.getPageOfRecentBooks(0, 6).getContent();
        return bookService.getBookWithAuthorDtoList(recentBooks);
    }

    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal BookstoreUserDetails user,
                           @CookieValue(value = "userHash", required = false) String userHash,
                           HttpServletResponse response,
                           Model model) {
        List<Book> recommendedBooks;
        List<Book> popularBooks;
        if (user != null) {
            bookUserService.moveBooksFromUserHashToCurrentUser(userHash, user, response);
            recommendedBooks = bookService.getPageOfRecommendedBooksForUser(user.getBookstoreUser(), 0, 6);
            popularBooks = bookService.getPageOfPopularBooksForUser(user.getBookstoreUser(), 0, 6);
        } else {
            recommendedBooks = bookService.getPageOfRecommendedBooksForNotAuthenticatedUser(userHash, 0, 6);
            popularBooks = bookService.getPageOfPopularBooksForNotAuthenticatedUser(userHash, 0, 6);
        }
        model.addAttribute("recommendedBooks", bookService.getBookWithAuthorDtoList(recommendedBooks));
        model.addAttribute("popularBooks", bookService.getBookWithAuthorDtoList(popularBooks));
        model.addAttribute("maxBooksByTag", tagService.getMaxCountTagsByBook());
        return "index";
    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getBooksPage(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                     @CookieValue(value = "userHash", required = false) String userHash,
                                     @RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit) {
        List<Book> books;
        if (userDetails != null) {
            books = bookService.getPageOfRecommendedBooksForUser(userDetails.getBookstoreUser(), offset, limit);
        } else {
            books = bookService.getPageOfRecommendedBooksForNotAuthenticatedUser(userHash, offset, limit);
        }
        return new BooksPageDto(bookService.getBookWithAuthorDtoList(books));
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResult(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                  Model model) throws EmptySearchException {
        if (searchWordDto != null) {
            List<BookWithAuthorsDto> bookList = bookService.getPageOfGoogleBooksApiSearchResult(searchWordDto.getExample(), 0, 5);
            model.addAttribute("searchWordDto", searchWordDto);
            model.addAttribute("searchResults", bookList);
            return "/search/index";
        } else {
            throw new EmptySearchException("Поиск по null невозможен");
        }
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) {
        List<BookWithAuthorsDto> books = bookService.getPageOfGoogleBooksApiSearchResult(searchWordDto.getExample(), offset, limit);
        return new BooksPageDto(books);
    }
}
