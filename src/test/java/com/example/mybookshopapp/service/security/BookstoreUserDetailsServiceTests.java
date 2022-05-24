package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserDetailsServiceTests {

    private BookstoreUser user;
    private UserContact userContact;

    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    @MockBean
    BookstoreUserRepository bookstoreUserRepository;

    @Autowired
    BookstoreUserDetailsServiceTests(BookstoreUserDetailsService bookstoreUserDetailsService) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
    }

    @BeforeEach
    void setUp() {
        user = new BookstoreUser();
        userContact = new UserContact();
        user.setId(1);
        user.setName("TestUser");
        userContact.setUser(user);
        userContact.setContact("test@email.org");
        userContact.setApproved(1);
    }

    @Test
    void loadUserByUsername() {
        Mockito.doReturn(user)
                .when(bookstoreUserRepository)
                .findBookstoreUserByContact(userContact.getContact());
        BookstoreUserDetails userByEmail = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(userContact.getContact());
        assertNotNull(userByEmail);
        assertThat(userByEmail.getBookstoreUser()).isEqualTo(user);
    }
}