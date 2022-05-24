package com.example.mybookshopapp.repository.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRepositoryTests {

    @Value("e4e09ed6ee8ca9c5cc773081f7ef0736781d1e9805cb13a3f436a6a1d7bf051d")
    String hashOfUser;

    @Value("atreble0@skyrock.com")
    String contactOfUser;

    private final BookstoreUserRepository bookstoreUserRepository;


    @Autowired
    BookstoreUserRepositoryTests(BookstoreUserRepository bookstoreUserRepository) {
        this.bookstoreUserRepository = bookstoreUserRepository;
    }


    @Test
    void testAddNewUser() {
        BookstoreUser user = new BookstoreUser();
        user.setHash(UUID.randomUUID().toString());
        user.setName("Tester");

        assertNotNull(bookstoreUserRepository.save(user));
    }

    @Test
    void findBookstoreUserByHash() {
        BookstoreUser userByHash = bookstoreUserRepository.findBookstoreUserByHash(hashOfUser);
        assertNotNull(userByHash);
        assertEquals(hashOfUser, userByHash.getHash());
    }

    @Test
    void findBookstoreUserByContact() {
        BookstoreUser userByContact = bookstoreUserRepository.findBookstoreUserByContact(contactOfUser);
        assertNotNull(userByContact);
    }
}