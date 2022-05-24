package com.example.mybookshopapp.dto;

import com.example.mybookshopapp.entity.BalanceTransaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BalanceTransactionDto {

    private Integer count;
    private List<BalanceTransaction> transactions;

    public BalanceTransactionDto(List<BalanceTransaction> transactions) {
        this.transactions = transactions;
        this.count = transactions.size();
    }
}
