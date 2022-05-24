package com.example.mybookshopapp.service;


import com.example.mybookshopapp.dto.GenreDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Genre;
import com.example.mybookshopapp.repository.GenreRepository;
import com.example.mybookshopapp.util.GeneratorSlug;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final GeneratorSlug generatorSlug;

    public GenreService(GenreRepository genreRepository, GeneratorSlug generatorSlug) {
        this.genreRepository = genreRepository;
        this.generatorSlug = generatorSlug;
    }

    public List<GenreDto> getListGenreDto() {
        return genreRepository.findAll().stream()
                .filter(genre -> genre.getParentId() != null)
                .map(genre -> new GenreDto(genre,
                        genreRepository.findGenresByParentId(genre.getId()),
                        genreRepository.getOne(genre.getParentId())))
                .sorted((o1, o2) -> o2.getCountBooks() - o1.getCountBooks())
                .collect(Collectors.toList());
    }

    public List<Genre> getGenresWithoutParent() {
        List<Genre> genres = genreRepository.findGenresByParentId(null);
        genres.sort((o1, o2) -> o2.getBooks().size() - o1.getBooks().size());
        return genres;
    }

    public Genre getGenreById(Integer id) {
        return genreRepository.getOne(id);
    }

    public Genre getGenreBySlug(String slug) {
        return genreRepository.findBySlug(slug);
    }

    public List<Genre> findByBooksIn(List<Book> bookListByUser) {
        return genreRepository.findByBooksIn(bookListByUser);
    }

    public Set<Genre> getGenresForNewBook(String genresString) {
        Set<Genre> genresSet = new HashSet<>();

        String[] genresName = genresString.split(",");

        for (String name : genresName) {
            Genre genre = null;
            name = name.trim();
            if (name.isEmpty()) {
                continue;
            }
            genre = genreRepository.findByName(name);
            if (genre == null) {
                genre = new Genre();
                genre.setName(name);
                genre.setSlug(generatorSlug.generateSlug("genre"));
                genre = genreRepository.save(genre);
            }
            genresSet.add(genre);
        }
        return genresSet;
    }
}
