package com.example.mybookshopapp.errs.security;

public class WrongCodeLoginException extends Exception {
    public WrongCodeLoginException(String message) {
        super(message);
    }
}
