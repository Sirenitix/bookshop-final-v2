package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUser;
import com.example.mybookshopapp.entity.BookUserType;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookUserRepositoryTest {

    @Value("451")
    Integer bookId;

    @Value("83")
    Integer userId;

    @Value("1")
    Integer typeId;

    private final BookUserRepository bookUserRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final BookUserTypeRepository bookUserTypeRepository;
    private final BookRepository bookRepository;

    @Autowired
    BookUserRepositoryTest(BookUserRepository bookUserRepository, BookstoreUserRepository bookstoreUserRepository, BookUserTypeRepository bookUserTypeRepository, BookRepository bookRepository) {
        this.bookUserRepository = bookUserRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.bookUserTypeRepository = bookUserTypeRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    void findByBookAndUserAndType() {
        BookUserType type = bookUserTypeRepository.getOne(typeId);
        Book book = bookRepository.getOne(bookId);
        BookstoreUser user = bookstoreUserRepository.getOne(userId);

        BookUser bookUser = bookUserRepository.findByBookAndUserAndType(book, user, type);

        assertNotNull(bookUser);
        assertEquals(type.getId(), bookUser.getType().getId());
        assertEquals(book.getId(), bookUser.getBook().getId());
        assertEquals(user.getId(), bookUser.getUser().getId());
    }

    @Test
    void findByBookAndUser() {
        Book book = bookRepository.getOne(bookId);
        BookstoreUser user = bookstoreUserRepository.getOne(userId);

        BookUser bookUser = bookUserRepository.findByBookAndUser(book, user);

        assertNotNull(bookUser);
        assertEquals(book.getId(), bookUser.getBook().getId());
        assertEquals(user.getId(), bookUser.getUser().getId());
    }

    @Test
    void findByUserAndType() {
        BookstoreUser user = bookstoreUserRepository.getOne(userId);

        List<BookUser> bookUsersList = bookUserRepository.findByUserAndType(user, TypeBookToUser.PAID);

        assertNotNull(bookUsersList);
        assertFalse(bookUsersList.isEmpty());
        for (BookUser bookUser : bookUsersList) {
            assertEquals(user.getId(), bookUser.getUser().getId());
            assertEquals(TypeBookToUser.PAID, bookUser.getType().getCode());
        }
    }
}