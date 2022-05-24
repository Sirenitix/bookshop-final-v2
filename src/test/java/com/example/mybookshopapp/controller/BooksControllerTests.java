package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/scripts-test/test-data-before-auth_user.sql", "/scripts-test/bookControllerTestScripts/test-data-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts-test/bookControllerTestScripts/test-data-after.sql", "/scripts-test/test-data-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BooksControllerTests {

    private final MockMvc mockMvc;

    @Value("book-uni-888")
    String slug;

    @Value("Southern Comfort")
    String title;

    @Value("sdfga233jsfs4r34253df")
    String hash;

    @Autowired
    BooksControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void bookPage() throws Exception {
        mockMvc.perform(get("/books/{slug}", slug))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/books/slug"))
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/div[2]/div[1]/div[1]/a")
                        .string("Chrysa Allender"));
    }

    @Test
    void saveNewBookImage() throws Exception {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", "image.png", "img/png", "example picture".getBytes());
        mockMvc.perform(multipart("/books/{slug}/img/save/", slug).file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));

    }

    @Test
    @Sql(value = "/scripts-test/test-data-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("bogoke1616@eyeremind.com")
    void bookFile() throws Exception {
        mockMvc.perform(get("/books/download/{hash}", hash))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("bogoke1616@eyeremind.com")
    void addReview() throws Exception {
        String payload = "{\"bookId\":\"4001\",\"text\":\" add review\"}";
        mockMvc.perform(post("/books/bookReview", slug)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }

    @Test
    @WithUserDetails("bogoke1616@eyeremind.com")
    void rateBookReview() throws Exception {
        String payload = "{\"reviewid\":\"4004\",\"value\":\"1\"}";
        mockMvc.perform(post("/books/{slug}/rateBookReview", slug)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }

    @Test
    @WithUserDetails("bogoke1616@eyeremind.com")
    void toRatingBook() throws Exception {
        String payload = "{\"bookId\":\"4001\",\"value\":\"3\"}";
        mockMvc.perform(post("/books/toRating")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(payload))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }

    @Test
    void removeBook() throws Exception {
        mockMvc.perform(get("/books/remove/{slug}", slug))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }

    @Test
    void editDescriptionOfBook() throws Exception {
        mockMvc.perform(post("/books/edit/{slug}", slug)
                .param("editText", "edit description"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }

    @Test
    @WithUserDetails("bogoke1616@eyeremind.com")
    void newBookPage() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/books/new"))
                .andExpect(model().attributeExists("newBook"))
                .andExpect(xpath("/html/body/div/div/main/div/form").exists())
                .andExpect(xpath("/html/body/div/div/main/div/h2").string("Добавить новую книгу"));
    }

    @Test
    void newBookPageFail() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    void removeReview() throws Exception {
        Integer id = 4004;
        mockMvc.perform(get("/books/{slug}/review/remove/{id}", slug, id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }
}