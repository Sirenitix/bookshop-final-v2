package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.BalanceTransaction;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceTransactionRepository extends JpaRepository<BalanceTransaction, Integer> {

    Page<BalanceTransaction> findBalanceTransactionByUser(BookstoreUser user, Pageable nextPage);
}
