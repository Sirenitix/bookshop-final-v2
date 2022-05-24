package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUserType;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookUserTypeRepository extends JpaRepository<BookUserType, Integer> {

    BookUserType findByCode(TypeBookToUser typeBookToUser);

    @Query("FROM BookUserType t, BookUser bu WHERE bu.type = t AND bu.user = :user AND bu.book = :book")
    BookUserType findByBookAndUser(Book book, BookstoreUser user);
}
