package com.example.mybookshopapp.service;

import com.example.mybookshopapp.dto.ChangeStatusPayload;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUser;
import com.example.mybookshopapp.entity.BookUserType;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.repository.BookRepository;
import com.example.mybookshopapp.repository.BookUserRepository;
import com.example.mybookshopapp.repository.BookUserTypeRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookUserService {

    private final BookUserRepository bookUserRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final BookUserTypeRepository bookUserTypeRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookUserService(BookUserRepository bookUserRepository, BookstoreUserRepository bookstoreUserRepository, BookUserTypeRepository bookUserTypeRepository, BookRepository bookRepository) {
        this.bookUserRepository = bookUserRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.bookUserTypeRepository = bookUserTypeRepository;
        this.bookRepository = bookRepository;
    }

    /* moves books from user retrieved by hash to authenticated user and delete user away with hash from cookie */
    public void moveBooksFromUserHashToCurrentUser(String userHash, BookstoreUserDetails user, HttpServletResponse response) {
        BookstoreUser bookstoreUserByHash = bookstoreUserRepository.findBookstoreUserByHash(userHash);
        if (userHash != null && !userHash.equals("") && bookstoreUserByHash != null) {
            List<Book> booksFromCookieUser = bookRepository.findBooksByUser(bookstoreUserByHash);
            for (Book book : booksFromCookieUser) {
                BookUser bookUserFromCookieUser = bookUserRepository.findByBookAndUser(book, bookstoreUserByHash);
                BookUser bookUserFromCurrentUser = bookUserRepository.findByBookAndUser(book, user.getBookstoreUser());
                if (bookUserFromCurrentUser != null) {
                    bookUserRepository.delete(bookUserFromCookieUser);
                } else {
                    bookUserFromCookieUser.setUser(user.getBookstoreUser());
                    bookUserRepository.save(bookUserFromCookieUser);
                }
            }
            bookstoreUserRepository.delete(bookstoreUserByHash);
            Cookie cookie = new Cookie("userHash", "");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    /* deletes items from BookUser for user where typeBookToUser = VIEWED and time more then a month */
    public void removeBookStatusViewedForUserLongerThanMonth(BookstoreUser user) {
        BookUserType type = bookUserTypeRepository.findByCode(TypeBookToUser.VIEWED);
        bookUserRepository.deleteByUserAndTypeAndTimeBefore(user, type, LocalDateTime.now().minusMonths(1));
    }

    /* if book does not have status, changes status of the book to viewed */
    public void changeBookStatusToViewedForUser(String slug, BookstoreUser user) {
        Book book = bookRepository.findBookBySlug(slug);
        BookUser bookUser = bookUserRepository.findByBookAndUser(book, user);
        if (bookUser == null) {
            bookUser = new BookUser(bookUserTypeRepository.findByCode(TypeBookToUser.VIEWED), book, user);
            bookUserRepository.save(bookUser);
        }
    }

    /* if book already has status 'KEPT' then sets status 'VIEWED'
       else sets status 'KEPT' */
    public void changeBookStatusToPostponedForUser(ChangeStatusPayload payload,
                                                   BookstoreUserDetails userDetails,
                                                   String userHash,
                                                   HttpServletResponse response) {
        BookUserType type;
        BookstoreUser currentUser;
        Book book = bookRepository.findBookBySlug(payload.getBooksIds());
        if (userDetails != null) {
            currentUser = userDetails.getBookstoreUser();
        } else {
            currentUser = bookstoreUserRepository.findBookstoreUserByHash(userHash);
        }
        type = bookUserTypeRepository.findByBookAndUser(book, currentUser);
        if (type.getCode().equals(TypeBookToUser.getTypeByString(payload.getStatus()))) {
            changeBookStatusForUser(book.getSlug(), currentUser, TypeBookToUser.VIEWED);
        } else {
            changeBookStatus(payload, userDetails, userHash, response);
        }
    }

    /* if book is not linked to user, then creates link with specified type
   else sets specified type */
    public void changeBookStatusForUser(String slug, BookstoreUser user, TypeBookToUser type) {
        Book book = bookRepository.findBookBySlug(slug);
        BookUser bookUser = bookUserRepository.findByBookAndUser(book, user);
        if (bookUser == null) {
            bookUser = new BookUser(bookUserTypeRepository.findByCode(type), book, user);
        } else {
            bookUser.setType(bookUserTypeRepository.findByCode(type));
        }
        bookUserRepository.save(bookUser);
    }

    /* if user is authenticated or user with hash from cookie exists then invokes changeBookStatusForUser() for him
       else creates new user and add him to cookie and invokes changeBookStatusForUser() for him */
    public void changeBookStatus(ChangeStatusPayload payload,
                                 BookstoreUserDetails userDetails,
                                 String userHash,
                                 HttpServletResponse response) {
        TypeBookToUser type = TypeBookToUser.getTypeByString(payload.getStatus());
        if (userDetails != null) {
            changeBookStatusForUser(payload.getBooksIds(), userDetails.getBookstoreUser(), type);
        } else if (userHash != null && !userHash.equals("")) {
            BookstoreUser bookstoreUserByHash = bookstoreUserRepository.findBookstoreUserByHash(userHash);
            changeBookStatusForUser(payload.getBooksIds(), bookstoreUserByHash, type);
        } else {
            BookstoreUser defaultUser = new BookstoreUser();
            defaultUser.setHash(UUID.randomUUID().toString());
            defaultUser = bookstoreUserRepository.save(defaultUser);
            changeBookStatusForUser(payload.getBooksIds(), defaultUser, type);

            Cookie cookie = new Cookie("userHash", defaultUser.getHash());
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    /* updates status all user's books with status 'KEPT' to 'CART' */
    public void changeStatusForAllPostponedBooksToCart(BookstoreUserDetails userDetails, String userHash) {
        BookstoreUser user;
        if (userDetails != null) {
            user = userDetails.getBookstoreUser();
        } else {
            user = bookstoreUserRepository.findBookstoreUserByHash(userHash);
        }
        BookUserType type = bookUserTypeRepository.findByCode(TypeBookToUser.CART);
        List<BookUser> bookUserListByUserAndStatus = bookUserRepository.findByUserAndType(user, TypeBookToUser.KEPT);
        bookUserRepository.updateStatusForBookUserOn(bookUserListByUserAndStatus, type);
    }

    public TypeBookToUser getStatusOfBookForUser(Book book, BookstoreUser user) {
        BookUserType type = bookUserTypeRepository.findByBookAndUser(book, user);
        return type.getCode();
    }
}
