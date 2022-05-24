package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class TagServiceTest {

    private final TagService tagService;

    @Autowired
    TagServiceTest(TagService tagService) {
        this.tagService = tagService;
    }

    @Test
    void getTagsAndCount() {
        Map<Tag, Integer> tagsMap = tagService.getTagsAndCount();
        assertNotNull(tagsMap);
        assertFalse(tagsMap.isEmpty());
    }

    @Test
    void getById() {
        Tag tagById = tagService.getById(1);
        assertNotNull(tagById);
        assertEquals(1, tagById.getId());
    }

    @Test
    void getMaxCountTagsByBook() {
        assertNotNull(tagService.getMaxCountTagsByBook());
    }

    @Test
    void getTagsForNewBook() {
        String tagsString = "tagOne, tagTwo, tagThree";
        Set<Tag> tags = tagService.getTagsForNewBook(tagsString);

        assertNotNull(tags);
        assertFalse(tags.isEmpty());
        assertEquals(3, tags.size());
    }

}