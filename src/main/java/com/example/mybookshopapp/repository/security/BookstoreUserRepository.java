package com.example.mybookshopapp.repository.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookstoreUserRepository extends JpaRepository<BookstoreUser, Integer> {

    BookstoreUser findBookstoreUserByHash(String userHash);

    @Query("SELECT u FROM BookstoreUser u, UserContact uc WHERE uc.user = u AND uc.contact = :contact")
    BookstoreUser findBookstoreUserByContact(String contact);
}
