package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class TagRepositoryTest {

    @Value("Horde")
    String nameOfTag;

    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    @Autowired
    TagRepositoryTest(TagRepository tagRepository, BookRepository bookRepository) {
        this.tagRepository = tagRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    void findByBooksIn() {
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            bookList.add(bookRepository.getOne(i));
        }
        List<Tag> tags = tagRepository.findByBooksIn(bookList);

        assertNotNull(tags);
        assertFalse(tags.isEmpty());
    }

    @Test
    void findByName() {
        Tag tagByName = tagRepository.findByName(nameOfTag);
        assertNotNull(tagByName);
        assertEquals(nameOfTag, tagByName.getName());
    }
}