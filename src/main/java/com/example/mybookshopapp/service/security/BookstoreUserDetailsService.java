package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final BookstoreUserRepository bookstoreUserRepository;
    private final UserContactRepository userContactRepository;

    @Autowired
    public BookstoreUserDetailsService(BookstoreUserRepository bookstoreUserRepository, UserContactRepository userContactRepository) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.userContactRepository = userContactRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String contact) throws UsernameNotFoundException {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByContact(contact);
        if (bookstoreUser == null) {
            throw new UsernameNotFoundException("user not found doh!!!");
        }
        UserContact userContact = userContactRepository.findByUserAndContact(bookstoreUser, contact);
        return new BookstoreUserDetails(bookstoreUser, userContact);
    }
}
