package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookRating;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRatingRepositoryTest {

    private final BookRatingRepository bookRatingRepository;
    private final BookRepository bookRepository;
    private final BookstoreUserRepository bookstoreUserRepository;


    @Autowired
    BookRatingRepositoryTest(BookRatingRepository bookRatingRepository, BookRepository bookRepository, BookstoreUserRepository bookstoreUserRepository) {
        this.bookRatingRepository = bookRatingRepository;
        this.bookRepository = bookRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Test
    void findByBookAndUser() {
        Book book = bookRepository.getOne(136);
        BookstoreUser user = bookstoreUserRepository.getOne(81);
        BookRating bookRating = bookRatingRepository.findByBookAndUser(book, user);

        assertNotNull(bookRating);
        assertEquals(book.getId(), bookRating.getBook().getId());
        assertEquals(user.getId(), bookRating.getUser().getId());
    }
}