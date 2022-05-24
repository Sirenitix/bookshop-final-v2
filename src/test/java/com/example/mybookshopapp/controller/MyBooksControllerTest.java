package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUser;
import com.example.mybookshopapp.repository.BookRepository;
import com.example.mybookshopapp.repository.BookUserRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/scripts-test/test-data-before-auth_user.sql", "/scripts-test/myBooksControllerTestScripts/test-data-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts-test/myBooksControllerTestScripts/test-data-after.sql", "/scripts-test/test-data-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("bogoke1616@eyeremind.com")
class MyBooksControllerTest {

    private final MockMvc mockMvc;
    private final BookUserRepository bookUserRepository;
    private final BookRepository bookRepository;

    @Autowired
    MyBooksControllerTest(MockMvc mockMvc, BookUserRepository bookUserRepository, BookRepository bookRepository) {
        this.mockMvc = mockMvc;
        this.bookUserRepository = bookUserRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    void handleMy() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("my"))
                .andExpect(model().attributeExists("paidBooks", "archivedBooks"))
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/h1").string("Мои книги"))
                .andExpect(xpath("/html/body/div/div/main/div/div[2]/div").exists());
    }

    @Test
    void handleMyArchive() throws Exception {
        mockMvc.perform(get("/myarchive"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("myarchive"))
                .andExpect(model().attributeExists("paidBooks", "archivedBooks"))
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/h1").string("Мои книги"))
                .andExpect(xpath("/html/body/div/div/main/div/div[2]/div").exists());
    }

    @Test
    void handleChangeBookStatusArchive() throws Exception {
        String slug = "book-eas-004";
        Integer idArchivedBook = 4;
        String payload = "{\"booksIds\":\"book-eas-004\",\"status\":\"ARCHIVED\"}";
        mockMvc.perform(post("/changeBookStatus/archive/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .content(payload))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));

        Book book = bookRepository.findBookBySlug(slug);
        BookUser bookUser = bookUserRepository.findByBook(book);

        assertEquals(idArchivedBook, bookUser.getType().getId());
    }

    @Test
    void testHandleChangeBookStatusArchive() throws Exception {
        String slug = "book-xxx-975";
        Integer idPaidBook = 3;
        mockMvc.perform(post("/changeBookStatus/archived/paid")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                        .param("slug", slug))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));

        Book book = bookRepository.findBookBySlug(slug);
        BookUser bookUser = bookUserRepository.findByBook(book);

        assertEquals(idPaidBook, bookUser.getType().getId());
    }
}