package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BookWithAuthorsDto;
import com.example.mybookshopapp.dto.ChangeStatusPayload;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.service.BookService;
import com.example.mybookshopapp.service.BookUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class MyBooksController {

    private final BookService bookService;
    private final BookUserService bookUserService;

    public MyBooksController(BookService bookService, BookUserService bookUserService) {
        this.bookService = bookService;
        this.bookUserService = bookUserService;
    }

    @ModelAttribute("paidBooks")
    public List<BookWithAuthorsDto> paidBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails) {
        List<Book> paidBooks = bookService.getPaidBooksForUser(userDetails.getBookstoreUser());
        return bookService.getBookWithAuthorDtoList(paidBooks);
    }

    @ModelAttribute("archivedBooks")
    public List<BookWithAuthorsDto> archivedBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails) {
        List<Book> archivedBooks = bookService.getArchivedBooksForUser(userDetails.getBookstoreUser());
        return bookService.getBookWithAuthorDtoList(archivedBooks);
    }

    @GetMapping("/my")
    public String handleMy(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                           @CookieValue(value = "userHash", required = false) String userHash,
                           HttpServletResponse response) {
        bookUserService.moveBooksFromUserHashToCurrentUser(userHash, userDetails, response);
        return "my";
    }

    @GetMapping("/myarchive")
    public String handleMyArchive() {
        return "myarchive";
    }

    @PostMapping("/changeBookStatus/archive/")
    public String handleChangeBookStatusArchive(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                @RequestBody ChangeStatusPayload payload) {
        bookUserService.changeBookStatusForUser(payload.getBooksIds(), userDetails.getBookstoreUser(), TypeBookToUser.ARCHIVED);
        return "redirect:/books/" + payload.getBooksIds();
    }

    @PostMapping("/changeBookStatus/archived/paid")
    public String handleChangeBookStatusArchive(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                @RequestParam("slug") String slug) {
        bookUserService.changeBookStatusForUser(slug, userDetails.getBookstoreUser(), TypeBookToUser.PAID);
        return "redirect:/books/" + slug;
    }
}
