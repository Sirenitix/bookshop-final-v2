package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.Author;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookAuthor;
import com.example.mybookshopapp.repository.AuthorRepository;
import com.example.mybookshopapp.repository.BookAuthorRepository;
import com.example.mybookshopapp.util.GeneratorSlug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final GeneratorSlug generatorSlug;
    private final BookAuthorRepository bookAuthorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, GeneratorSlug generatorSlug, BookAuthorRepository bookAuthorRepository) {
        this.authorRepository = authorRepository;
        this.generatorSlug = generatorSlug;
        this.bookAuthorRepository = bookAuthorRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream().sorted(Comparator.comparing(Author::getName)).collect(Collectors.groupingBy((Author a) -> a.getName().substring(0, 1)));
    }

    public List<Author> getAuthorsByBook(Book book) {
        return authorRepository.findByBook(book);
    }

    public Author getAuthorBySlug(String slug) {
        return authorRepository.findBySlug(slug);
    }

    public void save(Author author) {
        authorRepository.save(author);
    }

    public List<Author> findByBookIn(List<Book> bookListByUser) {
        return authorRepository.findByBookIn(bookListByUser);
    }

    public List<Author> getAuthorsForNewBook(String authorsString) {
        List<Author> authorsList = new ArrayList<>();

        String[] authorsName = authorsString.split(",");

        for (String name : authorsName) {
            Author author = null;
            name = name.trim();
            if (name.isEmpty()) {
                continue;
            }
            author = authorRepository.findByName(name);
            if (author == null) {
                author = new Author();
                author.setName(name);
                author.setSlug(generatorSlug.generateSlug("author"));
                author.setPhoto("/assets/img/notAuthorImage.jpeg");
                author.setDescription("new author");
                author = authorRepository.save(author);
            }
            authorsList.add(author);
        }
        return authorsList;
    }

    public void setBookAuthors(Book book, List<Author> authorList) {
        int sortIndex = 0;

        for (Author author : authorList) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBook(book);
            bookAuthor.setAuthor(author);
            bookAuthor.setSortIndex(sortIndex++);
            bookAuthorRepository.save(bookAuthor);
        }
    }
}
