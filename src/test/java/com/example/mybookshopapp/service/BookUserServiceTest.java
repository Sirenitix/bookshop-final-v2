package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUser;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.BookRepository;
import com.example.mybookshopapp.repository.BookUserRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookUserServiceTest {

    private BookstoreUser user;
    private BookstoreUser userHash;
    private BookstoreUserDetails userDetails;

    @MockBean
    private BookstoreUserRepository bookstoreUserRepositoryMock;

    @MockBean
    private BookRepository bookRepositoryMock;

    @MockBean
    private BookUserRepository bookUserRepositoryMock;

    @Value("anrw345gfg57gdf")
    String hash;

    private final BookUserService bookUserService;

    @Autowired
    BookUserServiceTest(BookUserService bookUserService) {
        this.bookUserService = bookUserService;
    }

    @BeforeEach
    void setUp() {
        user = new BookstoreUser();
        user.setName("tester");
        user.setHash("hashTest");
        user.setBalance(1000);
        userDetails = new BookstoreUserDetails(user, new UserContact());
        userHash = new BookstoreUser();
        userHash.setHash(hash);
    }

    @AfterEach
    void tearDown() {
        userDetails = null;
        user = null;
        userHash = null;
    }

    @Test
    void moveBooksFromUserHashToCurrentUser() {
        List<Book> bookList = Collections.singletonList(new Book());
        Mockito.doReturn(userHash).when(bookstoreUserRepositoryMock).findBookstoreUserByHash(hash);
        Mockito.doReturn(bookList).when(bookRepositoryMock).findBooksByUser(userHash);
        Mockito.doReturn(new BookUser()).when(bookUserRepositoryMock).findByBookAndUser(bookList.get(0), userHash);

        bookUserService.moveBooksFromUserHashToCurrentUser(hash, userDetails, new MockHttpServletResponse());

        Mockito.verify(bookUserRepositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1)).delete(Mockito.any());
    }

    @Test
    void changeBookStatusToViewedForUser() {
        Book book = new Book();
        Mockito.doReturn(book).when(bookRepositoryMock).findBookBySlug("slug");
        Mockito.doReturn(null).when(bookUserRepositoryMock).findByBookAndUser(book, user);

        bookUserService.changeBookStatusToViewedForUser("slug", user);

        Mockito.verify(bookUserRepositoryMock, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void changeBookStatusForUser() {
        Book book = new Book();
        Mockito.doReturn(book).when(bookRepositoryMock).findBookBySlug("slug");
        Mockito.doReturn(null).when(bookUserRepositoryMock).findByBookAndUser(book, user);

        bookUserService.changeBookStatusForUser("slug", user, TypeBookToUser.PAID);

        Mockito.verify(bookUserRepositoryMock, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void changeStatusForAllPostponedBooksToCart() {
        bookUserService.changeStatusForAllPostponedBooksToCart(userDetails, hash);
        Mockito.verify(bookUserRepositoryMock, Mockito.times(1)).findByUserAndType(Mockito.any(), Mockito.any());
        Mockito.verify(bookUserRepositoryMock, Mockito.times(1)).updateStatusForBookUserOn(Mockito.any(), Mockito.any());
    }
}