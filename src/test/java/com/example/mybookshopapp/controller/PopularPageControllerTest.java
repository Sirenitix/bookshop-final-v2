package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class PopularPageControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    PopularPageControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getPopularPage() throws Exception {
        mockMvc.perform(get("/books/popular")
                        .accept(MediaType.TEXT_HTML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("books/popular"))
                .andExpect(model().attributeExists("popularBooks"))
                .andExpect(xpath("/html/body/div/div/main/ul/li[3]/span").string("Популярное"))
                .andExpect(xpath("/html/body/div/div/main/div/div[2]/div").exists());
    }

    @Test
    void getPopularBooksPage() throws Exception {
        mockMvc.perform(get("/books/popular")
                        .param("offset", "1")
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", notNullValue()))
                .andExpect(jsonPath("$.books", notNullValue()));
    }
}