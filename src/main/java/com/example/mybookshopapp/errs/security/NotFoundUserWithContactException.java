package com.example.mybookshopapp.errs.security;

public class NotFoundUserWithContactException extends Exception {
    public NotFoundUserWithContactException(String message) {
        super(message);
    }
}
