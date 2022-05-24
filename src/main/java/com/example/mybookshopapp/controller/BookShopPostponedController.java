package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BookWithAuthorsDto;
import com.example.mybookshopapp.dto.ChangeStatusPayload;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import com.example.mybookshopapp.service.BookService;
import com.example.mybookshopapp.service.BookUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookShopPostponedController {

    private static final String REDIRECT_POSTPONED_URL = "redirect:/postponed";

    private final BookstoreUserRepository bookstoreUserRepository;
    private final BookUserService bookUserService;
    private final BookService bookService;

    @ModelAttribute(name = "bookPostponed")
    public List<BookWithAuthorsDto> bookCart() {
        return new ArrayList<>();
    }

    @Autowired
    public BookShopPostponedController(BookstoreUserRepository bookstoreUserRepository, BookUserService bookUserService, BookService bookService) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.bookUserService = bookUserService;
        this.bookService = bookService;
    }

    @GetMapping("/postponed")
    public String handlePostponeRequest(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                        @CookieValue(value = "userHash", required = false) String userHash,
                                        HttpServletResponse response,
                                        Model model) {
        List<Book> booksPostponed;

        if (userDetails != null) {
            bookUserService.moveBooksFromUserHashToCurrentUser(userHash, userDetails, response);
            booksPostponed = bookService.getPostponedBooksForUser(userDetails.getBookstoreUser());
        } else if (userHash != null && !userHash.equals("")) {
            BookstoreUser bookstoreUserByHash = bookstoreUserRepository.findBookstoreUserByHash(userHash);
            booksPostponed = bookService.getPostponedBooksForUser(bookstoreUserByHash);
        } else {
            model.addAttribute("isPostponedEmpty", true);
            return "postponed";
        }

        model.addAttribute("isPostponedEmpty", booksPostponed.isEmpty());
        model.addAttribute("bookPostponed", bookService.getBookWithAuthorDtoList(booksPostponed));
        return "postponed";
    }

    @PostMapping("/changeBookStatus/postpone/")
    public String handleChangeBookStatusPostpone(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                 @RequestBody ChangeStatusPayload payload,
                                                 @CookieValue(name = "userHash", required = false) String userHash,
                                                 HttpServletResponse response) {
        bookUserService.changeBookStatusToPostponedForUser(payload, userDetails, userHash, response);
        return "redirect:/books/" + payload.getBooksIds();
    }

    @PostMapping("/changeBookStatus/postponed/remove/")
    public String handleRemoveBookFromPostponeRequest(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                      @RequestBody ChangeStatusPayload payload,
                                                      @CookieValue(name = "userHash", required = false) String userHash) {

        TypeBookToUser type = TypeBookToUser.getTypeByString(payload.getStatus());
        if (userDetails != null) {
            bookUserService.changeBookStatusForUser(payload.getBooksIds(), userDetails.getBookstoreUser(), type);
        } else {
            BookstoreUser bookstoreUserByHash = bookstoreUserRepository.findBookstoreUserByHash(userHash);
            bookUserService.changeBookStatusForUser(payload.getBooksIds(), bookstoreUserByHash, type);
        }
        return REDIRECT_POSTPONED_URL;
    }

    @PostMapping("/changeBookStatus/postponed/cart/")
    public String handleCartBookFromPostponeRequest(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                    @RequestBody ChangeStatusPayload payload,
                                                    @CookieValue(name = "userHash", required = false) String userHash,
                                                    HttpServletResponse response) {
        bookUserService.changeBookStatus(payload, userDetails, userHash, response);
        return REDIRECT_POSTPONED_URL;
    }

    @PostMapping("/changeBookStatus/postponed/cartAll")
    public String handleCartAllBookFromPostponeRequest(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                                       @CookieValue(name = "userHash", required = false) String userHash) {
        bookUserService.changeStatusForAllPostponedBooksToCart(userDetails, userHash);
        return REDIRECT_POSTPONED_URL;
    }
}
