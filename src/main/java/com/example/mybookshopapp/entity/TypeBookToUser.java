package com.example.mybookshopapp.entity;

public enum TypeBookToUser {
    KEPT,
    CART,
    PAID,
    ARCHIVED,
    VIEWED;

    public static TypeBookToUser getTypeByString(String status) {
        switch (status) {
            case "KEPT":
                return KEPT;
            case "CART":
                return CART;
            case "PAID":
                return PAID;
            case "ARCHIVED":
                return ARCHIVED;
            default:
                return VIEWED;
        }
    }
}
