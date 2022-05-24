package com.example.mybookshopapp.service;

import com.example.mybookshopapp.dto.GenreDto;
import com.example.mybookshopapp.entity.Genre;
import com.example.mybookshopapp.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class GenreServiceTest {

    @Value("genre-OxD-482")
    String slugOfGenre;

    private final GenreService genreService;

    @Autowired
    GenreServiceTest(GenreService genreService) {
        this.genreService = genreService;
    }

    @Test
    void getListGenreDto() {
        List<GenreDto> listGenreDto = genreService.getListGenreDto();
        assertNotNull(listGenreDto);
        assertFalse(listGenreDto.isEmpty());
    }

    @Test
    void getGenresWithoutParent() {
        List<Genre> genreList = genreService.getGenresWithoutParent();
        assertNotNull(genreList);
        assertFalse(genreList.isEmpty());
        for (Genre genre : genreList) {
            assertNull(genre.getParentId());
        }
    }

    @Test
    void getGenreById() {
        Genre genreById = genreService.getGenreById(3);
        assertNotNull(genreById);
        assertEquals(3, genreById.getId());
    }

    @Test
    void getGenreBySlug() {
        Genre genreBySlug = genreService.getGenreBySlug(slugOfGenre);
        assertNotNull(genreBySlug);
        assertEquals(slugOfGenre, genreBySlug.getSlug());
    }

    @Test
    void getGenresForNewBook() {
        String genresString = "genreOne, genreTwo, genreThree";
        Set<Genre> genres = genreService.getGenresForNewBook(genresString);
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        assertEquals(3, genres.size());
    }
}