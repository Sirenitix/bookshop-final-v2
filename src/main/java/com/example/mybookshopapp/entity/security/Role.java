package com.example.mybookshopapp.entity.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    USER,
    ANONYMOUS;

    @Override
    public String getAuthority() {
        return name();
    }
}
