package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.BookFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookFileRepositoryTest {

    @Value("08f236a07a097414def2cae700e19e7f689286f7")
    String hashOfFile;

    private final BookFileRepository bookFileRepository;

    @Autowired
    BookFileRepositoryTest(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    @Test
    void findBookFileByHash() {
        BookFile bookFile = bookFileRepository.findBookFileByHash(hashOfFile);
        assertNotNull(bookFile);
        assertEquals(hashOfFile, bookFile.getHash());
    }
}