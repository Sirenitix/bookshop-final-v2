package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class GenreRepositoryTest {

    @Value("genre-OxD-482")
    String slugOfGenre;

    @Value("Thriller")
    String nameOfGenre;

    private final GenreRepository genreRepository;

    @Autowired
    GenreRepositoryTest(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Test
    void findGenresByParentId() {
        Integer parentId = 1;
        List<Genre> genresByParentId = genreRepository.findGenresByParentId(parentId);
        assertNotNull(genresByParentId);
        assertFalse(genresByParentId.isEmpty());
        for (Genre genre : genresByParentId) {
            assertEquals(parentId, genre.getParentId());
        }
    }

    @Test
    void findBySlug() {
        Genre genreBuSlug = genreRepository.findBySlug(slugOfGenre);
        assertNotNull(genreBuSlug);
        assertEquals(slugOfGenre, genreBuSlug.getSlug());
    }

    @Test
    void findByName() {
        Genre genreByName = genreRepository.findByName(nameOfGenre);
        assertNotNull(genreByName);
        assertEquals(nameOfGenre, genreByName.getName());
    }
}