package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUser;
import com.example.mybookshopapp.entity.BookUserType;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface BookUserRepository extends JpaRepository<BookUser, Integer> {

    BookUser findByBookAndUserAndType(Book book, BookstoreUser user, BookUserType type);

    BookUser findByBook(Book book);

    BookUser findByBookAndUser(Book book, BookstoreUser user);

    @Transactional
    @Modifying
    void deleteByUserAndTypeAndTimeBefore(BookstoreUser user, BookUserType type, LocalDateTime time);

    @Query("FROM BookUser bu WHERE bu.user = :user AND bu.type.code = :type")
    List<BookUser> findByUserAndType(BookstoreUser user, TypeBookToUser type);

    @Transactional
    @Modifying
    @Query("UPDATE BookUser bu SET bu.type = :type WHERE bu IN :list")
    void updateStatusForBookUserOn(List<BookUser> list, BookUserType type);
}
