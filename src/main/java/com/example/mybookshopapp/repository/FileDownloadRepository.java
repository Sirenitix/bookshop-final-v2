package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.FileDownload;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDownloadRepository extends JpaRepository<FileDownload, Integer> {

    FileDownload findByUserAndBook(BookstoreUser user, Book book);
}
