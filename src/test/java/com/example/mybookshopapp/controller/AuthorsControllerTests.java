package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = "/scripts-test/authorControllerTestScripts/test-data-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/scripts-test/authorControllerTestScripts/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthorsControllerTests {

    @Value("author-aut-999")
    String slugOfAuthor;

    @Value("Author Tester")
    String nameOfAuthor;

    private final MockMvc mockMvc;

    @Autowired
    AuthorsControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void authorsPage() throws Exception {
        mockMvc.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/authors/index"))
                .andExpect(xpath("/html/body/div/div/main/div/div/div[2]/div/div[1]/a")
                        .string("Addy Whiteford"));
    }

    @Test
    void authorsSlugPage() throws Exception {
        mockMvc.perform(get("/authors/{slugOfAuthor}", slugOfAuthor))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("authors/slug"))
                .andExpect(xpath("/html/body/div/div/main/h1").string(nameOfAuthor))
                .andExpect(xpath("/html/body/div/div/main/div/div/div/div/div/div[1]/div").exists())
                .andExpect(model().attributeExists("author", "booksOfAuthor"));
    }

    @Test
    void booksOfAuthorPage() throws Exception {
        mockMvc.perform(get("/books/author/{slugOfAuthor}", slugOfAuthor)
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("books/author"))
                .andExpect(model().attributeExists("author", "booksOfAuthor"))
                .andExpect(xpath("/html/body/div/div/main/ul/li[3]/span").string(nameOfAuthor))
                .andExpect(xpath("/html/body/div/div/main/div/div/div").exists());
    }

    @Test
    void getBooksByAuthor() throws Exception {
        mockMvc.perform(get("/books/author/{slugOfAuthor}", slugOfAuthor)
                        .param("offset", "1")
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", notNullValue()))
                .andExpect(jsonPath("$.books", notNullValue()));

    }

    @Test
    void editDescriptionOfAuthor() throws Exception {
        mockMvc.perform(post("/authors/edit/{slugOfAuthor}", slugOfAuthor)
                        .param("editText", "update description"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors/" + slugOfAuthor));
    }

    @Test
    void saveNewAuthorPhoto() throws Exception {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", "image.png", "img/png", "example picture".getBytes());
        mockMvc.perform(multipart("/authors/{slugOfAuthor}/photo/save", slugOfAuthor).file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors/" + slugOfAuthor));
    }
}