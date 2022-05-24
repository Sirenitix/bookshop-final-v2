package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.dto.RegistrationForm;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private RegistrationForm registrationForm;

    @MockBean
    private BookstoreUserRepository bookstoreUserRepositoryMock;

    @MockBean
    private UserContactService userContactServiceMock;

    @Autowired
    BookstoreUserRegisterTests(BookstoreUserRegister userRegister) {
        this.userRegister = userRegister;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    void registerNewUser() {
        Mockito.doReturn(new UserContact()).when(userContactServiceMock).getByEmail(registrationForm.getEmail());
        Mockito.doReturn(new UserContact()).when(userContactServiceMock).getByPhone(registrationForm.getPhone());
        Boolean result = userRegister.registerNewUser(registrationForm);
        assertTrue(result);
        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .save(Mockito.any(BookstoreUser.class));
    }

    @Test
    void registerNewUserFail() {
        Mockito.doReturn(new BookstoreUser())
                .when(bookstoreUserRepositoryMock)
                .findBookstoreUserByContact(registrationForm.getEmail());

        Boolean result = userRegister.registerNewUser(registrationForm);
        assertFalse(result);
    }
}