package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.BookReview;
import com.example.mybookshopapp.entity.BookReviewLike;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookReviewLikeRepositoryTest {

    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final BookReviewRepository bookReviewRepository;

    @Autowired
    BookReviewLikeRepositoryTest(BookReviewLikeRepository bookReviewLikeRepository, BookstoreUserRepository bookstoreUserRepository, BookReviewRepository bookReviewRepository) {
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.bookReviewRepository = bookReviewRepository;
    }

    @Test
    void findByBookReviewAndUser() {
        BookstoreUser user = bookstoreUserRepository.getOne(54);
        BookReview bookReview = bookReviewRepository.getOne(972);
        BookReviewLike like = bookReviewLikeRepository.findByBookReviewAndUser(bookReview, user);

        assertNotNull(like);
        assertEquals(user.getId(), like.getUser().getId());
        assertEquals(bookReview.getId(), like.getBookReview().getId());
    }

    @Test
    void findByUser() {
        BookstoreUser user = bookstoreUserRepository.getOne(54);
        List<BookReviewLike> likeList = bookReviewLikeRepository.findByUser(user);
        assertNotNull(likeList);
        assertFalse(likeList.isEmpty());
        for (BookReviewLike like : likeList) {
            assertThat(like.getUser().getId()).isEqualTo(user.getId());
        }
    }
}