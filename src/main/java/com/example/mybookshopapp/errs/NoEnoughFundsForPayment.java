package com.example.mybookshopapp.errs;

public class NoEnoughFundsForPayment extends Exception {
    public NoEnoughFundsForPayment(String message) {
        super(message);
    }
}
