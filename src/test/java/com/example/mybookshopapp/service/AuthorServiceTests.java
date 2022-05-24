package com.example.mybookshopapp.service;


import com.example.mybookshopapp.entity.Author;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.repository.AuthorRepository;
import com.example.mybookshopapp.repository.BookAuthorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class AuthorServiceTests {

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private BookAuthorRepository bookAuthorRepository;

    private final AuthorService authorService;

    @Autowired
    AuthorServiceTests(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Test
    void getAuthorsMap() {
        List<Author> authorList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Author author = new Author();
            char ch = 'A';
            author.setName(ch + "name");
            authorList.add(author);
            ch++;
        }
        Mockito.doReturn(authorList).when(authorRepository).findAll();
        Map<String, List<Author>> authorsMap = authorService.getAuthorsMap();
        assertNotNull(authorsMap);
        assertFalse(authorsMap.isEmpty());
        for (Map.Entry<String, List<Author>> entry : authorsMap.entrySet()) {
            for (Author author : entry.getValue()) {
                assertThat(author.getName()).startsWith(entry.getKey());
            }
        }
    }

    @Test
    void getAuthorsForNewBook() {
        StringBuilder authorsStrings = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            authorsStrings.append("author").append(i).append(" ,");
        }
        List<Author> authorList = authorService.getAuthorsForNewBook(authorsStrings.toString());

        Mockito.verify(authorRepository, Mockito.times(5)).findByName(Mockito.any());
        Mockito.verify(authorRepository, Mockito.times(5)).save(Mockito.any());
        assertNotNull(authorList);
        assertFalse(authorList.isEmpty());
        assertEquals(5, authorList.size());

    }

    @Test
    void setBookAuthors() {
        List<Author> authorList = Arrays.asList(new Author(), new Author(), new Author());
        authorService.setBookAuthors(new Book(), authorList);
        Mockito.verify(bookAuthorRepository, Mockito.times(3)).save(Mockito.any());
    }
}