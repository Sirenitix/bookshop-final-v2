package com.example.mybookshopapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class FooterControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    FooterControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void documentsPage() throws Exception {
        mockMvc.perform(get("/documents"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("documents/index"))
                .andExpect(model().attributeExists("documents"))
                .andExpect(xpath("/html/body/div/div/main/h1").string("Документы"))
                .andExpect(xpath("/html/body/div/div/main/div/div/div/a[1]").exists());
    }

    @Test
    void documentSlugPage() throws Exception {
        String slug = "doc-pui-141";
        mockMvc.perform(get("/documents/{slug}", slug))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("documents/slug"))
                .andExpect(model().attributeExists("document"))
                .andExpect(xpath("/html/body/div/div[2]/main/ul/li[3]").string("Losartan Potassium"));
    }

    @Test
    void aboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(xpath("/html/body/div/div/main/ul/li[2]/span").string("О компании"));
    }

    @Test
    void faqPage() throws Exception {
        mockMvc.perform(get("/faq"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("faq"))
                .andExpect(model().attributeExists("faqs"))
                .andExpect(xpath("/html/body/div/div/main/ul/li[2]/span").string("Помощь"))
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/header").exists());
    }

    @Test
    void contactsPage() throws Exception {
        mockMvc.perform(get("/contacts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("contacts"))
                .andExpect(model().attributeExists("messageDto"))
                .andExpect(xpath("/html/body/div/div[2]/main/ul/li[2]/span").string("Контакты"))
                .andExpect(xpath("/html/body/div/div[2]/main/div/div/div[2]/div/form").exists());
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails("bogoke1616@eyeremind.com")
    void handleSendMessage() throws Exception {
        mockMvc.perform(post("/contacts")
                        .param("subject", "sub")
                        .param("text", "text"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contacts"));
    }
}