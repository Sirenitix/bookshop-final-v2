package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.SearchWordDto;
import com.example.mybookshopapp.dto.UserWithContactsDto;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import com.example.mybookshopapp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonController {

    private final BookService bookService;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final UserContactRepository userContactRepository;

    @Autowired
    public CommonController(BookService bookService, BookstoreUserRepository bookstoreUserRepository, UserContactRepository userContactRepository) {
        this.bookService = bookService;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.userContactRepository = userContactRepository;
    }


    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("curUsr")
    public UserWithContactsDto getCurrentUser(@AuthenticationPrincipal BookstoreUserDetails userDetails) {
        if (userDetails != null) {
            BookstoreUser user = userDetails.getBookstoreUser();
            UserContact userContactEmail = userContactRepository.findByUserAndType(user, ContactType.EMAIL);
            UserContact userContactPhone = userContactRepository.findByUserAndType(user, ContactType.PHONE);
            return new UserWithContactsDto(user.getHash(), user.getBalance(), user.getName(),
                    userContactEmail.getContact(), userContactPhone.getContact());
        }
        return null;
    }

    @ModelAttribute("countMyBooks")
    public Integer countMyBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails) {
        if (userDetails != null) {
            return bookService.getPaidBooksForUser(userDetails.getBookstoreUser()).size();
        }
        return 0;
    }

    @ModelAttribute("countCartBooks")
    public Integer countCartBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                  @CookieValue(value = "userHash", required = false) String userHash) {
        BookstoreUser currentUser;
        if (userDetails != null) {
            currentUser = userDetails.getBookstoreUser();
        } else if (userHash != null && !userHash.equals("")){
            currentUser = bookstoreUserRepository.findBookstoreUserByHash(userHash);
        } else {
            currentUser = null;
        }
        return currentUser != null ? bookService.getCartBooksForUser(currentUser).size() : 0;
    }

    @ModelAttribute("countPostponedBooks")
    public Integer countPostponedBooks(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                       @CookieValue(value = "userHash", required = false) String userHash) {
        BookstoreUser currentUser;
        if (userDetails != null) {
            currentUser = userDetails.getBookstoreUser();
        } else if (userHash != null && !userHash.equals("")){
            currentUser = bookstoreUserRepository.findBookstoreUserByHash(userHash);
        } else {
            currentUser = null;
        }
        return currentUser != null ? bookService.getPostponedBooksForUser(currentUser).size() : 0;
    }
}
