package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Author;
import com.example.mybookshopapp.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class AuthorRepositoryTest {

    @Value("Chrysa Allender")
    String nameOfAuthor;

    @Value("author-ceh-320")
    String slugOfAuthor;

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    AuthorRepositoryTest(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    void findByName() {
        Author authorByName = authorRepository.findByName(nameOfAuthor);
        assertNotNull(authorByName);
        assertEquals(nameOfAuthor, authorByName.getName());
    }

    @Test
    void findByBookIn() {
        List<Book> booksList = bookRepository.findBooksByAuthorNameContaining(nameOfAuthor);
        List<Author> authorsByBooks = authorRepository.findByBookIn(booksList);
        assertNotNull(authorsByBooks);
        assertFalse(authorsByBooks.isEmpty());
    }

    @Test
    void findByBook() {
        Book book = bookRepository.findBooksByAuthorNameContaining(nameOfAuthor).get(0);
        List<Author> authorsByBook = authorRepository.findByBook(book);
        assertNotNull(authorsByBook);
        assertFalse(authorsByBook.isEmpty());
    }

    @Test
    void findBySlug() {
        Author authorBySlug = authorRepository.findBySlug(slugOfAuthor);
        assertNotNull(authorBySlug);
        assertEquals(slugOfAuthor, authorBySlug.getSlug());
    }
}