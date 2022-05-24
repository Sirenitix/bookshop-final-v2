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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookShopCartControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    BookShopCartControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("bogoke1616@eyeremind.com")
    void handleCartRequest() throws Exception {
        mockMvc.perform(get("/cart"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("bookCart", "allPrice", "allPriceOld", "isCartEmpty"))
                .andExpect(xpath("/html/body/div/div/main/h1").string("Корзина"));
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("bogoke1616@eyeremind.com")
    void handleChangeBookStatus() throws Exception {
        String payload = "{\"bookIds\":\"book-xhk-845\",\"status\":\"CART\"}";
        mockMvc.perform(post("/changeBookStatus/cart")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(payload))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}