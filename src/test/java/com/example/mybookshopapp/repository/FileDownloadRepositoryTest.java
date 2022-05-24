package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.FileDownload;
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
class FileDownloadRepositoryTest {

    private final FileDownloadRepository fileDownloadRepository;
    private final BookRepository bookRepository;
    private final BookstoreUserRepository bookstoreUserRepository;

    @Autowired
    FileDownloadRepositoryTest(FileDownloadRepository fileDownloadRepository, BookRepository bookRepository, BookstoreUserRepository bookstoreUserRepository) {
        this.fileDownloadRepository = fileDownloadRepository;
        this.bookRepository = bookRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Test
    void findByUserAndBook() {
        Book book = bookRepository.getOne(794);
        BookstoreUser user = bookstoreUserRepository.getOne(6);

        FileDownload fileDownload = fileDownloadRepository.findByUserAndBook(user, book);

        assertNotNull(fileDownload);
        assertEquals(book.getId(), fileDownload.getBook().getId());
        assertEquals(user.getId(), fileDownload.getUser().getId());
    }
}