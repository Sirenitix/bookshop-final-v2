package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.BalanceTransaction;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.repository.BalanceTransactionRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BalanceTransactionServiceTest {

    @MockBean
    private BalanceTransactionRepository balanceTransactionRepository;

    private final BalanceTransactionService balanceTransactionService;

    @Autowired
    BalanceTransactionServiceTest(BalanceTransactionService balanceTransactionService) {
        this.balanceTransactionService = balanceTransactionService;
    }

    @Test
    void getTransactionsByUserPage() {
        BookstoreUser user = new BookstoreUser();
        List<BalanceTransaction> balanceTransactionList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            balanceTransactionList.add(new BalanceTransaction());
        }
        Page<BalanceTransaction> balanceTransactionPage = new PageImpl<>(balanceTransactionList);

        Mockito.doReturn(balanceTransactionPage)
                .when(balanceTransactionRepository)
                .findBalanceTransactionByUser(user, PageRequest.of(0, 20, Sort.by("time").descending()));

        Page<BalanceTransaction> transactionsByUserPage = balanceTransactionService.getTransactionsByUserPage(user, 0, 20);
        assertNotNull(transactionsByUserPage);
        assertFalse(transactionsByUserPage.isEmpty());
        assertEquals(20, balanceTransactionPage.getContent().size());
    }
}