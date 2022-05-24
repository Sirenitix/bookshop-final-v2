package com.example.mybookshopapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class ResourceStorageTests {

    @Value("${download.path}")
    String downloadPath;

    @Value("${upload.book-image.path}")
    String uploadPath;

    @Value("fw324dfw234234fwdf221")
    String bookFileHash;

    @Value("/Faust.pdf")
    String bookFilePath;

    private final ResourceStorage resourceStorage;

    @Autowired
    ResourceStorageTests(ResourceStorage resourceStorage) {
        this.resourceStorage = resourceStorage;
    }

    @Test
    void saveNewBookImage() throws IOException {
        String slug = "book-slug";
        String expected = "/book-covers/" + slug + ".png";
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("image", "image.png", "img/png", "example picture".getBytes());
        String actual = resourceStorage.saveNewImage(mockMultipartFile, slug, uploadPath);
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertNull(resourceStorage.saveNewImage(new MockMultipartFile("empty", "".getBytes()), slug, uploadPath));
    }

    @Test
    void getBookFilePath() {
        Path path = Paths.get(bookFilePath);
        Path bookFilePath = resourceStorage.getBookFilePath(bookFileHash);
        assertNotNull(bookFilePath);
        assertEquals(path.toString(), bookFilePath.toString());
    }

    @Test
    void getBookFileMime() {
        String mimeTypeExpected = "application/pdf";
        MediaType mediaType = resourceStorage.getBookFileMime(bookFileHash);
        assertNotNull(mediaType);
        assertEquals(mimeTypeExpected, mediaType.toString());
    }

    @Test
    void getBookFileByteArray() throws IOException {
        String path = downloadPath + bookFilePath;
        byte[] expectedArray = Files.readAllBytes(Paths.get(path));
        byte[] bookFileByteArray = resourceStorage.getBookFileByteArray(bookFileHash);
        assertNotNull(bookFileByteArray);
        assertThat(bookFileByteArray).isEqualTo(expectedArray);
    }
}