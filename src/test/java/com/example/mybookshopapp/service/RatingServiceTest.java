package com.example.mybookshopapp.service;

import com.example.mybookshopapp.dto.RatingBookDto;
import com.example.mybookshopapp.dto.ReviewLikeDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookReviewLike;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.repository.BookRatingRepository;
import com.example.mybookshopapp.repository.BookReviewLikeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class RatingServiceTest {

    @MockBean
    private BookReviewLikeRepository bookReviewLikeRepositoryMock;

    @MockBean
    private BookRatingRepository bookRatingRepository;

    private BookstoreUser user;
    private ReviewLikeDto reviewLikeDto;
    private RatingBookDto ratingBookDto;

    private final RatingService ratingService;

    @Autowired
    RatingServiceTest(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @BeforeEach
    void setUp() {
        user = new BookstoreUser();
        reviewLikeDto = new ReviewLikeDto();
        reviewLikeDto.setReviewid(35);
        reviewLikeDto.setValue(1);
        ratingBookDto = new RatingBookDto();
        ratingBookDto.setBookId(21);
        ratingBookDto.setValue(1);
    }

    @AfterEach
    void rearDown() {
        user = null;
        reviewLikeDto = null;
    }

    @Test
    void rateBookReview() {
        ratingService.rateBookReview(user, reviewLikeDto);
        Mockito.verify(bookReviewLikeRepositoryMock, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void rateBook() {
        ratingService.rateBook(user, new Book(), ratingBookDto);
        Mockito.verify(bookRatingRepository, Mockito.times(1)).save(Mockito.any());
    }
}