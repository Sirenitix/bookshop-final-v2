package com.example.mybookshopapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusPayload {

    private String booksIds;

    private String status;
}
