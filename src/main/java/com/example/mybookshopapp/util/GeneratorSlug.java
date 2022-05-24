package com.example.mybookshopapp.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GeneratorSlug {

    private final Random random = new Random();

    public String generateSlug(String entity) {
        StringBuilder sb = new StringBuilder(entity + "-");
        for (int i = 0; i < 3; i++) {
            sb.append((char)(random.nextInt(26) + 'a'));
        }
        sb.append("-");
        for (int i = 0; i < 3; i++) {
            sb.append(random.nextInt(9));
        }
        return sb.toString();
    }
}
