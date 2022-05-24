package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.BalanceTransaction;
import com.example.mybookshopapp.repository.BalanceTransactionRepository;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BalanceTransactionService {

    private final BalanceTransactionRepository balanceTransactionRepository;

    @Autowired
    public BalanceTransactionService(BalanceTransactionRepository balanceTransactionRepository) {
        this.balanceTransactionRepository = balanceTransactionRepository;
    }

    public Page<BalanceTransaction> getTransactionsByUserPage(BookstoreUser user, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by("time").descending());
        return balanceTransactionRepository.findBalanceTransactionByUser(user, nextPage);
    }
}
