package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookShopPostponedControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    BookShopPostponedControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("bogoke1616@eyeremind.com")
    void handlePostponeRequest() throws Exception {
        mockMvc.perform(get("/postponed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("postponed"))
                .andExpect(model().attributeExists("isPostponedEmpty", "bookPostponed"))
                .andExpect(xpath("/html/body/div/div/main/h1").string("Отложенное"));
    }
}