package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class GenrePageControllerTest {

    private final MockMvc mockMvc;

    @Value("genre-feX-498")
    String slug;

    @Autowired
    GenrePageControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getGenrePage() throws Exception {
        mockMvc.perform(get("/genres"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("genres/index"))
                .andExpect(model().attributeExists("genresDto", "genresRoot"))
                .andExpect(xpath("/html/body/div/div/main/h1").string("Жанры"))
                .andExpect(xpath("/html/body/div/div/main/div/div/div/div[2]").exists());
    }

    @Test
    void getGenreSlugPage() throws Exception {
        mockMvc.perform(get("/genres/{slug}", slug))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("genres/slug"))
                .andExpect(model().attributeExists("firstParent", "booksOfGenre", "genre"))
                .andExpect(xpath("/html/body/div/div/main/header/h1").string("Classic"))
                .andExpect(xpath("/html/body/div/div/main/div/div/div").exists());
    }

    @Test
    void getBooksByGenre() throws Exception {
        Integer id = 13;
        mockMvc.perform(get("/books/genre/{id}", id)
                        .param("offset", "1")
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", notNullValue()))
                .andExpect(jsonPath("$.books", notNullValue()));
    }
}