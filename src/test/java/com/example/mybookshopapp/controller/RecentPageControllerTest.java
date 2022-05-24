package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class RecentPageControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    RecentPageControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getRecentPage() throws Exception {
        mockMvc.perform(get("/books/recent")
                        .accept(MediaType.TEXT_HTML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("books/recent"))
                .andExpect(model().attributeExists("recentBooks"))
                .andExpect(xpath("/html/body/div[1]/div/main/ul/li[3]/span").string("Новинки"))
                .andExpect(xpath("/html/body/div[1]/div/main/div/div[2]/div[1]").exists());
    }

    @Test
    void getRecentBooksPage() throws Exception {
        mockMvc.perform(get("/books/recent")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("offset", "1")
                        .param("limit", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", notNullValue()))
                .andExpect(jsonPath("$.books", notNullValue()));

    }
}