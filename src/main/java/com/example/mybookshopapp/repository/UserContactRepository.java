package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserContactRepository extends JpaRepository<UserContact, Integer> {

    UserContact findByContact(String contact);

    UserContact findByUserAndContact(BookstoreUser bookstoreUser, String contact);

    UserContact findByUserAndType(BookstoreUser user, ContactType type);

    List<UserContact> findByUser(BookstoreUser user);
}
