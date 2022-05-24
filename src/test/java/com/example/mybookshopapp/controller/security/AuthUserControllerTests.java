package com.example.mybookshopapp.controller.security;

import com.example.mybookshopapp.errs.security.WrongCodeLoginException;
import com.example.mybookshopapp.errs.security.WrongCodeRegException;
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

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class AuthUserControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    AuthUserControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void handleSignIn() throws Exception {
        mockMvc.perform(get("/signin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("signin"))
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label").exists());
    }

    @Test
    void handleSignUp() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(xpath("/html/body/div/div[1]/div/h1").exists());
    }

    @Test
    void handleUserRegistration() throws Exception {
        mockMvc.perform(post("/reg")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param("name", "Tester")
                        .param("phone", "+7 (123) 123-12-31")
                        .param("phoneCode", "123 123")
                        .param("email", "tester@mail.com")
                        .param("mailCode", "123 123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("signin"));
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("bogoke1616@eyeremind.com")
    void handleMy() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("my"))
                .andExpect(xpath("/html/body/header/div[1]/div/div/div[3]/div/a[4]/span[1]")
                        .string("Rob Coast"));
    }

    @Test
    void accessProfileFail() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleRequestContactConfirmation() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\"}";
        mockMvc.perform(post("/requestContactConfirmation")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("true")));
    }

    @Test
    @Sql(value = {"/scripts-test/test-data-before.sql", "/scripts-test/authUserControllerTestScripts/test-data-before-fail_time_contact.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleRequestContactConfirmationFail() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\"}";
        mockMvc.perform(post("/requestContactConfirmation")
                .content(payload)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass())
                        .isEqualTo(WrongCodeLoginException.class));
    }

    @Test
    void handleRequestEmailConfirmation() throws Exception {
        String payload = "{\"contact\":\"test@email.com\"}";
        mockMvc.perform(post("/requestEmailConfirmation")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("true")));
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleRequestEmailConfirmationFail() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\"}";
        mockMvc.perform(post("/requestEmailConfirmation")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass())
                        .isEqualTo(WrongCodeRegException.class));
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleApproveContact() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\", \"code\":\"371 997\"}";
        mockMvc.perform(post("/approveContact")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("true")));
    }

    @Test
    @Sql(value = {"/scripts-test/test-data-before.sql", "/scripts-test/authUserControllerTestScripts/test-data-before-fail_time_contact.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleApproveContactFail() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\", \"code\":\"371 997\"}";
        mockMvc.perform(post("/approveContact")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass())
                        .isEqualTo(WrongCodeRegException.class));
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleLogin() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\", \"code\":\"371 997\"}";
        mockMvc.perform(post("/login")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(notNullValue())));
    }

    @Test
    @Sql(value = {"/scripts-test/test-data-before-auth_user.sql", "/scripts-test/authUserControllerTestScripts/test-data-before-fail_time_contact.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleLoginFail() throws Exception {
        String payload = "{\"contact\":\"bogoke1616@eyeremind.com\", \"code\":\"371 997\"}";
        mockMvc.perform(post("/login")
                        .content(payload)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass())
                        .isEqualTo(WrongCodeLoginException.class));
    }
}