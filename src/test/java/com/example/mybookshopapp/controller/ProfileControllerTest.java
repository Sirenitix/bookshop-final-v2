package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("bogoke1616@eyeremind.com")
class ProfileControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    ProfileControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void handleProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("transactions", "ratingUser"))
                .andExpect(xpath("//*[@id=\"basic\"]/div/form").exists());
    }

    @Test
    void getNextPageTransactions() throws Exception {
        mockMvc.perform(get("/transactions")
                        .param("offset", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", notNullValue()))
                .andExpect(jsonPath("$.transactions", notNullValue()));
    }

    @Test
    void handlePay() throws Exception {
        mockMvc.perform(get("/pay"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my"));
    }

    @Test
    void handlePayment() throws Exception {
        String payload = "{\"sum\":\"500\",\"time\":\"1628194557497\"}";
        mockMvc.perform(post("/payment")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void approveCredentials() throws Exception {
        Integer currentUserId = 5005;
        Integer updateUserId = 2;
        String code = "185_962";
        mockMvc.perform(get("/changeCredentials/{updateUserId}/{currentUserId}/{code}", updateUserId, currentUserId, code))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/profile"))
                .andExpect(model().attribute("credentialsSuccess", "Профиль успешно сохранен"))
                .andExpect(model().attributeExists("transactions", "ratingUser"))
                .andExpect(xpath("//*[@id=\"basic\"]/div/form").exists());
    }
}