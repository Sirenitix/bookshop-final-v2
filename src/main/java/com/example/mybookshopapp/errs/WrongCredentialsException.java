package com.example.mybookshopapp.errs;

public class WrongCredentialsException extends Exception {
    public WrongCredentialsException(String message) {
        super(message);
    }
}
