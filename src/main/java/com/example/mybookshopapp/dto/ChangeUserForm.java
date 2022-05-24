package com.example.mybookshopapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserForm {

    private String name;
    private String mail;
    private String phone;

    @Override
    public String toString() {
        return "ChangeUserForm{" +
                "name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
