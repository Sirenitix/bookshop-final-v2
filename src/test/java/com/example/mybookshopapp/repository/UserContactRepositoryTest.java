package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
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
class UserContactRepositoryTest {

    @Value("atreble0@skyrock.com")
    String contactOfUserContact;

    @Value("1")
    Integer userId;

    private final UserContactRepository userContactRepository;
    private final BookstoreUserRepository bookstoreUserRepository;

    @Autowired
    UserContactRepositoryTest(UserContactRepository userContactRepository, BookstoreUserRepository bookstoreUserRepository) {
        this.userContactRepository = userContactRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Test
    void findByContact() {
        UserContact userContact = userContactRepository.findByContact(contactOfUserContact);
        assertNotNull(userContact);
        assertEquals(contactOfUserContact, userContact.getContact());
    }

    @Test
    void findByUserAndContact() {
        BookstoreUser user = bookstoreUserRepository.getOne(userId);
        UserContact userContact = userContactRepository.findByUserAndContact(user, contactOfUserContact);

        assertNotNull(userContact);
        assertEquals(contactOfUserContact, userContact.getContact());
        assertEquals(user.getId(), userContact.getUser().getId());
    }

    @Test
    void findByUserAndType() {
        BookstoreUser user = bookstoreUserRepository.getOne(userId);
        UserContact userContact = userContactRepository.findByUserAndType(user, ContactType.PHONE);
        assertNotNull(userContact);
        assertEquals(user.getId(), userContact.getUser().getId());
        assertEquals(ContactType.PHONE, userContact.getType());
    }

    @Test
    void findByUser() {
        BookstoreUser user = bookstoreUserRepository.getOne(userId);
        List<UserContact> userContacts = userContactRepository.findByUser(user);

        assertNotNull(userContacts);
        assertFalse(userContacts.isEmpty());

        for (UserContact contact : userContacts) {
            assertEquals(user.getId(), contact.getUser().getId());
        }
    }
}