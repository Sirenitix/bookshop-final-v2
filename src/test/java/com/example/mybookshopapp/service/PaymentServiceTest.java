package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.*;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.errs.NoEnoughFundsForPayment;
import com.example.mybookshopapp.repository.BalanceTransactionRepository;
import com.example.mybookshopapp.repository.BookRepository;
import com.example.mybookshopapp.repository.BookUserRepository;
import com.example.mybookshopapp.repository.BookUserTypeRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class PaymentServiceTest {

    @MockBean
    private BookstoreUserRepository bookstoreUserRepositoryMock;

    @MockBean
    private BookUserRepository bookUserRepositoryMock;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookUserTypeRepository bookUserTypeRepositoryMock;

    @MockBean
    private BalanceTransactionRepository balanceTransactionRepositoryMock;

    private final PaymentService paymentService;

    @Autowired
    PaymentServiceTest(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    private BookstoreUser user;
    private BookUserType bookUserType;
    private List<Book> bookList;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new BookstoreUser();
        bookUserType = new BookUserType();
        user.setBalance(5000);
        bookList = new ArrayList<>();
        book = new Book();
        book.setTitle("title");
        book.setPriceOld(500);
        book.setPrice(10.0);
        bookList.add(book);
    }

    @AfterEach
    void tearDown() {
        user = null;
        bookUserType = null;
        bookList = null;
        book = null;
    }

    @Test
    void buyBooksByUser() throws NoEnoughFundsForPayment {
        BalanceTransaction balanceTransaction = new BalanceTransaction(user, book, 450, "mess");

        Mockito.doReturn(bookList).when(bookRepository).findBooksByUserAndType(user, TypeBookToUser.CART);
        Mockito.doReturn(bookUserType).when(bookUserTypeRepositoryMock).findByCode(TypeBookToUser.CART);
        Mockito.doReturn(balanceTransaction).when(balanceTransactionRepositoryMock)
                .save(Mockito.any());
        Mockito.doReturn(new BookUser()).when(bookUserRepositoryMock).findByBookAndUserAndType(book, user, bookUserType);

        Boolean resultActual = paymentService.buyBooksByUser(user);

        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(bookUserRepositoryMock, Mockito.times(1)).save(Mockito.any());
        assertTrue(resultActual);
    }

    @Test
    void buyBooksByUserFailNoEnoughFundsForPayment() {
        user.setBalance(50);
        BalanceTransaction balanceTransaction = new BalanceTransaction(user, book, 450, "mess");
        Mockito.doReturn(bookList).when(bookRepository).findBooksByUserAndType(user, TypeBookToUser.CART);
        Mockito.doReturn(balanceTransaction).when(balanceTransactionRepositoryMock)
                .save(Mockito.any());

        assertThrows(NoEnoughFundsForPayment.class, () -> paymentService.buyBooksByUser(user), "No Enough Funds, make deposit to the account");
    }

    @Test
    void deposit() throws NoSuchAlgorithmException {
        BookstoreUserDetails userDetails = new BookstoreUserDetails(user, null);
        BalanceTransaction balanceTransaction = new BalanceTransaction(userDetails.getBookstoreUser(), null, 100, "Depositing funds through Robokassa");
        balanceTransaction.setId(9999);
        Mockito.doReturn(balanceTransaction).when(balanceTransactionRepositoryMock)
                .save(Mockito.any());

        String paymentUrl = paymentService.deposit(userDetails, 100);

        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1)).save(Mockito.any());
        assertNotNull(paymentUrl);
    }
}