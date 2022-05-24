package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTests {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTests(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void findBooksByAuthorNameContaining() {
        String token = "Evin Dorset";
        List<Book> bookListByAuthorFirstName = bookRepository.findBooksByAuthorNameContaining(token);

        assertNotNull(bookListByAuthorFirstName);
        assertFalse(bookListByAuthorFirstName.isEmpty());
    }

    @Test
    void findBooksByTitleContaining() {
        String token = "fish";
        List<Book> bookListByTitleContaining = bookRepository.findBooksByTitleContaining(token);
        assertNotNull(bookListByTitleContaining);
        assertFalse(bookListByTitleContaining.isEmpty());
        for (Book book : bookListByTitleContaining) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.toString());
            assertThat(book.getTitle()).contains(token);
        }
    }

    @Test
    void getBestsellers() {
        List<Book> bestSellersBooks = bookRepository.getBestsellers();
        assertNotNull(bestSellersBooks);
        assertFalse(bestSellersBooks.isEmpty());
        assertThat(bestSellersBooks.size()).isGreaterThan(1);
    }

    @Test
    void findBooksByPriceOldBetween() {
        Integer minPrice = 500;
        Integer maxPrice = 1000;
        List<Book> bookListByPriceOld = bookRepository.findBooksByPriceOldBetween(minPrice, maxPrice);
        assertNotNull(bookListByPriceOld);
        assertFalse(bookListByPriceOld.isEmpty());
        for (Book book : bookListByPriceOld) {
            assertThat(book.getPriceOld()).isGreaterThanOrEqualTo(minPrice);
            assertThat(book.getPriceOld()).isLessThanOrEqualTo(maxPrice);
        }
    }

    @Test
    void findBookBySlug() {
        String token = "book-exa-004";
        Book bookBySlug = bookRepository.findBookBySlug(token);
        assertNotNull(bookBySlug);
        assertEquals(token, bookBySlug.getSlug());
    }

    @Test
    void deleteBySlug() {
        String token = "book-goo-056";
        bookRepository.deleteBySlug(token);
        Book book = bookRepository.findBookBySlug(token);
        assertNull(book);
    }
}