package com.example.mybookshopapp.errs;

public class EmptySearchException extends Exception {
    public EmptySearchException(String message) {
        super(message);
    }
}
