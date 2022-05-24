package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class DocumentRepositoryTest {

    @Value("doc-pui-141")
    String slugOfDocument;

    private final DocumentRepository documentRepository;

    @Autowired
    DocumentRepositoryTest(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Test
    void findBySlug() {
        Document document = documentRepository.findBySlug(slugOfDocument);
        assertNotNull(document);
        assertEquals(slugOfDocument, document.getSlug());
    }
}