package com.example.mybookshopapp.entity.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class BookstoreUserDetails implements UserDetails {

    private final BookstoreUser bookstoreUser;

    private final UserContact userContact;

    public BookstoreUserDetails(BookstoreUser bookstoreUser, UserContact userContact) {
        this.bookstoreUser = bookstoreUser;
        this.userContact = userContact;
    }

    public BookstoreUser getBookstoreUser() {
        return bookstoreUser;
    }


    public UserContact getUserContact() {
        return userContact;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return bookstoreUser.getRoles();
    }

    @Override
    public String getPassword() {
        return userContact.getCode();
    }

    @Override
    public String getUsername() {
        return userContact.getContact();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
