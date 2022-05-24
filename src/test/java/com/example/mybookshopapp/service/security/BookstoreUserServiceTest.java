package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserServiceTest {

    @MockBean
    private BookstoreUserRepository bookstoreUserRepository;

    @MockBean
    private UserContactRepository userContactRepository;

    private final BookstoreUserService bookstoreUserService;

    @Autowired
    BookstoreUserServiceTest(BookstoreUserService bookstoreUserService) {
        this.bookstoreUserService = bookstoreUserService;
    }

    @Test
    void getPageOfUsers() {
        List<BookstoreUser> userListExcepted = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            userListExcepted.add(new BookstoreUser());
        }
        Page<BookstoreUser> userPage = new PageImpl<>(userListExcepted);
        Mockito.doReturn(userPage)
                .when(bookstoreUserRepository).findAll(PageRequest.of(0, 20, Sort.by("regTime")));

        List<BookstoreUser> userList = bookstoreUserService.getPageOfUsers(0, 20);
        assertNotNull(userList);
        assertFalse(userList.isEmpty());
        assertEquals(20, userList.size());
    }

    @Test
    void blockAccessUserFor() {
        UserContact userContact = new UserContact(new BookstoreUser(), "tectContact", 1, ContactType.PHONE);
        List<UserContact> userContacts = Collections.singletonList(userContact);
        BookstoreUser user = new BookstoreUser();
        Mockito.doReturn(user).when(bookstoreUserRepository).findBookstoreUserByHash(Mockito.any());
        Mockito.doReturn(userContacts).when(userContactRepository).findByUser(user);

        bookstoreUserService.blockAccessUserFor("hash", 1);

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any());
    }
}