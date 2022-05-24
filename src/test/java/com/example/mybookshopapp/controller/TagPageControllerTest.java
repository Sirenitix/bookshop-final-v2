package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class TagPageControllerTest {

    @Value("7")
    String id;

    private final MockMvc mockMvc;

    @Autowired
    TagPageControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getTagPage() throws Exception {
        mockMvc.perform(get("/tags")
                        .param("tag", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("tags/index"))
                .andExpect(model().attributeExists("booksByTag", "tag"))
                .andExpect(xpath("/html/body/div/div/main/ul/li[2]/span").string("Thunderbolt"))
                .andExpect(xpath("/html/body/div/div/main/div/div[2]/div[1]").exists());
    }

    @Test
    void getBooksPage() throws Exception {

        mockMvc.perform(get("/books/tag/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("offset", "1")
                        .param("limit", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(7)))
                .andExpect(jsonPath("$.books", notNullValue()));
    }
}