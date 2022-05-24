package com.example.mybookshopapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithContactsDto {

    private String hash;

    private Integer balance;

    private String name;

    private String email;

    private String phone;

    public UserWithContactsDto(String hash, Integer balance, String name, String email, String phone) {
        this.hash = hash;
        this.balance = balance;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
