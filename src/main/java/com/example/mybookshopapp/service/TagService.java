package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.Tag;
import com.example.mybookshopapp.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Map<Tag, Integer> getTagsAndCount() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .collect(Collectors.toMap(tag -> tag, tag -> tag.getBooks().size()));
    }

    public Tag getById(Integer id) {
        return tagRepository.getOne(id);
    }

    public Integer getMaxCountTagsByBook() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> tag.getBooks().size()).max(Integer::compare).orElse(0);
    }

    public Set<Tag> getTagsForNewBook(String tagsString) {
        Set<Tag> tagsSet = new HashSet<>();

        String[] tagsName = tagsString.split(",");

        for (String name : tagsName) {
            Tag tag = null;
            name = name.trim();
            if (name.isEmpty()) {
                continue;
            }
            tag = tagRepository.findByName(name);
            if (tag == null) {
                tag = new Tag();
                tag.setName(name);
                tag = tagRepository.save(tag);
            }
            tagsSet.add(tag);
        }
        return tagsSet;
    }

    public List<Tag> findByBooksIn(List<Book> bookListByUser) {
        return tagRepository.findByBooksIn(bookListByUser);
    }
}
