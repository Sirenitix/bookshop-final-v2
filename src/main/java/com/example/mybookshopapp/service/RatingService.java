package com.example.mybookshopapp.service;

import com.example.mybookshopapp.dto.RatingBookDto;
import com.example.mybookshopapp.dto.ReviewLikeDto;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookRating;
import com.example.mybookshopapp.entity.BookReview;
import com.example.mybookshopapp.entity.BookReviewLike;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.repository.BookRatingRepository;
import com.example.mybookshopapp.repository.BookReviewLikeRepository;
import com.example.mybookshopapp.repository.BookReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookRatingRepository bookRatingRepository;

    public RatingService(BookReviewLikeRepository bookReviewLikeRepository, BookReviewRepository bookReviewRepository, BookRatingRepository bookRatingRepository) {
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookRatingRepository = bookRatingRepository;
    }

    public Integer getRatingByUser(BookstoreUser user) {
        List<BookReviewLike> likesAndDislikesByUser = bookReviewLikeRepository.findByUser(user);
        long countLikes = likesAndDislikesByUser.stream().filter(ld -> ld.getValue().equals(1)).count();
        long countDislikes = likesAndDislikesByUser.stream().filter(ld -> ld.getValue().equals(-1)).count();
        if (countLikes == 0 && countDislikes == 0) {
            return 100;
        }
        return Math.toIntExact(100 * countLikes / (countLikes + countDislikes));
    }

    public void rateBookReview(BookstoreUser user, ReviewLikeDto payload) {
        BookReview bookReview = bookReviewRepository.getOne(payload.getReviewid());

        BookReviewLike likeByReviewAndUser = bookReviewLikeRepository.findByBookReviewAndUser(bookReview, user);
        if (likeByReviewAndUser != null) {
            if (!likeByReviewAndUser.getValue().equals(payload.getValue())) {
                BookReviewLike newBookReviewLike = new BookReviewLike(user, bookReview, payload.getValue());
                bookReviewLikeRepository.save(newBookReviewLike);
            }
            bookReviewLikeRepository.delete(likeByReviewAndUser);
        } else {
            BookReviewLike bookReviewLike = new BookReviewLike(user, bookReview, payload.getValue());
            bookReviewLikeRepository.save(bookReviewLike);
        }
    }

    public void rateBook(BookstoreUser user, Book book, RatingBookDto payload) {
        BookRating ratingByBookAndUser = bookRatingRepository.findByBookAndUser(book, user);
        if (ratingByBookAndUser != null) {
            ratingByBookAndUser.setValue(payload.getValue());
            bookRatingRepository.save(ratingByBookAndUser);
        } else {
            BookRating newBookRating = new BookRating(user, book, payload.getValue());
            bookRatingRepository.save(newBookRating);
        }
    }
}
