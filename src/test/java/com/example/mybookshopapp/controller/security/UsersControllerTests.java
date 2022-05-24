package com.example.mybookshopapp.controller.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@Sql(value = {"/scripts-test/test-data-before-auth_user.sql", "/scripts-test/userControllerTestScripts/test-data-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts-test/test-data-after.sql", "/scripts-test/userControllerTestScripts/test-data-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("bogoke1616@eyeremind.com")
class UsersControllerTests {

    @Value("e4e09ed6ee8ca9c5cc773081f7ef0736781d1e9805cb13a3f436a6a1d7skt64h")
    String hashOfUser;

    @Value("Tester Test")
    String nameOfUser;

    private final MockMvc mockMvc;

    @Autowired
    UsersControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void usersPage() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"))
                .andExpect(xpath("/html/body/div/div/main/table").exists())
                .andExpect(xpath("//*[@id=\"list-users\"]/tr").nodeCount(20));

    }

    @Test
    void userSlugPageOk() throws Exception {
        mockMvc.perform(get("/users/" + hashOfUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users/slug"))
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/h1").string(nameOfUser))
                .andExpect(xpath("/html/body/div/div/main/div/div[6]/div[1]").exists())
                .andExpect(model().attributeExists("paidBooks", "recommendBooks", "user", "contacts"));
    }

    @Test
    void userSlugPageRemove() throws Exception {
        mockMvc.perform(get("/users/" + "asd78g8w9r8b980d87"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users/slug"))
                .andExpect(xpath("/html/body/div/h1").string("User removed"))
                .andExpect(model().attribute("warnMessage", "User removed"));
    }

    @Test
    void removeUser() throws Exception {
        mockMvc.perform(get("/users/remove/" + hashOfUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users/slug"))
                .andExpect(xpath("/html/body/div/h1").string("User removed"))
                .andExpect(model().attribute("warnMessage", "User removed"));
    }

    @Test
    void removeRoleAdmin() throws Exception {
        mockMvc.perform(get("/users/" + hashOfUser + "/remove/role"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + hashOfUser))
                .andExpect(flash().attribute("message", "Role ADMIN removed from user " + nameOfUser));
    }

    @Test
    void addRoleAdmin() throws Exception {
        String hash = "f86e5c89ab23c78676d086293fdcf0a72c6be34c083229e97b0b941856dcaf09";
        mockMvc.perform(get("/users/" + hash + "/add/role"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + hash))
                .andExpect(flash().attribute("message", "Role ADMIN added to user Harbert Fydoe"));
    }

    @Test
    void getUsersList() throws Exception {
        mockMvc.perform(get("/users/list")
                        .param("offset", "1")
                        .param("limit", "20")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .contains("id", "hash", "regTime", "balance", "name", "roles"));
    }

    @Test
    void blockAccessUser() throws Exception {
        mockMvc.perform(get("/users/" + hashOfUser + "/block/" + 3))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + hashOfUser));
    }

    @Test
    void giveBookToUser() throws Exception {
        String slugOgBook = "book-rdn-842";
        mockMvc.perform(get("/users/" + hashOfUser + "/give/" + slugOgBook))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + hashOfUser))
                .andExpect(flash().attributeExists("message"));
    }
}