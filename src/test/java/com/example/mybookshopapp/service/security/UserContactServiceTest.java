package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.errs.security.NotFoundUserWithContactException;
import com.example.mybookshopapp.errs.security.WrongCodeLoginException;
import com.example.mybookshopapp.errs.security.WrongCodeRegException;
import com.example.mybookshopapp.repository.UserContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserContactServiceTest {

    @Value("testContact")
    String contact;

    @Value("testCode")
    String code;

    private UserContact userContact;

    @MockBean
    private UserContactRepository userContactRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final UserContactService userContactService;

    @Autowired
    UserContactServiceTest(UserContactService userContactService) {
        this.userContactService = userContactService;
    }

    @BeforeEach
    void setUp() {
        userContact = new UserContact();
        userContact.setContact(contact);
        userContact.setCode(code);
        userContact.setApproved(1);
        userContact.setCodeTime(LocalDateTime.now());
        userContact.setCodeTrials(0);
        userContact.setType(ContactType.EMAIL);
    }

    @AfterEach
    void tearDown() {
        userContact = null;
    }

    @Test
    void generateCode() {
        String code = userContactService.generateCode();
        assertNotNull(code);
        assertFalse(code.isEmpty());
        assertEquals(7, code.length());
    }

    @Test
    void saveNewCode() throws WrongCodeLoginException, NotFoundUserWithContactException {
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(Mockito.any());
        userContactService.saveNewCode(code, contact);
        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test()
    void saveNewCodeThrowNotFoundUserWithContactException() {
        Mockito.doReturn(null).when(userContactRepository).findByContact(Mockito.any());
        assertThrows(NotFoundUserWithContactException.class,
                () -> userContactService.saveNewCode(code, contact), "User with specified contact is not exists");
    }

    @Test()
    void saveNewCodeThrowWrongCodeLoginException() {
        userContact.setCodeTime(LocalDateTime.now().plusHours(1));
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(Mockito.any());
        assertThrows(WrongCodeLoginException.class,
                () -> userContactService.saveNewCode(code, contact), "You were blocked. Try to log in later");
    }

    @Test
    void saveNewCodeForReg() throws WrongCodeRegException {
        Mockito.doReturn(new UserContact()).when(userContactRepository).findByContact(Mockito.any());
        userContactService.saveNewCodeForReg(code, contact);
        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void saveNewCodeForRegWrongCodeRegException() {
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(Mockito.any());
        assertThrows(WrongCodeRegException.class,
                () -> userContactService.saveNewCodeForReg(code, contact), "Contact already approved");
    }

    @Test
    void verifyCode() {
        Mockito.doReturn(userContact).when(userContactRepository).findByUserAndContact(Mockito.any(), Mockito.any());
        assertTrue(userContactService.verifyCode(Mockito.any(), Mockito.any(), code));
    }

    @Test
    void verifyCodeFail() {
        Mockito.doReturn(null).when(userContactRepository).findByUserAndContact(Mockito.any(), Mockito.any());
        assertFalse(userContactService.verifyCode(Mockito.any(), Mockito.any(), "test"));
    }

    @Test
    void getByEmail() {
        userContact.setType(ContactType.EMAIL);
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertEquals(userContact, userContactService.getByEmail(contact));
    }

    @Test
    void getByEmailFail() {
        Mockito.doReturn(null).when(userContactRepository).findByContact(contact);
        assertNull(userContactService.getByEmail(contact));
    }

    @Test
    void getByPhone() {
        userContact.setType(ContactType.PHONE);
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertEquals(userContact, userContactService.getByPhone(contact));
    }

    @Test
    void getByPhoneFail() {
        userContact.setType(ContactType.EMAIL);
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertNull(userContactService.getByPhone(contact));
    }

    @Test
    void verifyCodeReg() throws WrongCodeRegException {
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertTrue(userContactService.verifyCodeReg(contact, code));
    }

    @Test
    void verifyCodeRegWrongCodeRegExceptionFailTime() {
        userContact.setCodeTime(LocalDateTime.now().plusHours(1));
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertThrows(WrongCodeRegException.class, () -> userContactService.verifyCodeReg(contact, code));
    }

    @Test
    void verifyCodeRegWrongCodeRegExceptionFailWrongCode() {
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertThrows(WrongCodeRegException.class, () -> userContactService.verifyCodeReg(contact, "code"));
    }

    @Test
    void verifyCodeRegWrongCodeRegExceptionFailWrongCodeTimes() {
        userContact.setCodeTrials(2);
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertThrows(WrongCodeRegException.class,
                () -> userContactService.verifyCodeReg(contact, "code"), "Число попыток подтверждения превышено, повторите попытку через 5 минут");
    }

    @Test
    void verifyCodeLogin() throws WrongCodeLoginException {
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        Mockito.doReturn(true).when(passwordEncoder).matches(code, userContact.getCode());
        userContactService.verifyCodeLogin(code, contact);
        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void verifyCodeLoginWrongCodeLoginExceptionFailTime() {
        userContact.setCodeTime(LocalDateTime.now().plusHours(1));
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertThrows(WrongCodeLoginException.class, () -> userContactService.verifyCodeLogin(code, contact));
    }

    @Test
    void verifyCodeLoginWrongCodeLoginExceptionFailWrongCode() {
        Mockito.doReturn(userContact).when(userContactRepository).findByContact(contact);
        assertThrows(WrongCodeLoginException.class, () -> userContactService.verifyCodeLogin(code, contact));
    }
}