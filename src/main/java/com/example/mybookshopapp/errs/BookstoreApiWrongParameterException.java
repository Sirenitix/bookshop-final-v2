package com.example.mybookshopapp.errs;

public class BookstoreApiWrongParameterException extends Exception {
    public BookstoreApiWrongParameterException(String message) {
        super(message);
    }
}
