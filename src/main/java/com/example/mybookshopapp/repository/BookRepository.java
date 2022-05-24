package com.example.mybookshopapp.repository;

import com.example.mybookshopapp.entity.*;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

//    @Query("SELECT b FROM Book b WHERE  b.author.name LIKE :authorName")
    @Query("SELECT b FROM Book b, BookAuthor ba WHERE ba.book = b AND ba.author.name = :authorName")
    List<Book> findBooksByAuthorNameContaining(String authorName);

    @Query("SELECT b FROM Book b, BookAuthor ba WHERE b = ba.book AND ba.author = :author")
    List<Book> findBooksByAuthor(Author author);

    @Query("SELECT b FROM Book b, BookAuthor ba WHERE b = ba.book AND ba.author = :author")
    List<Book> findBooksByAuthor(Author author, Pageable nextPage);

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceOldBetween(Integer min, Integer max);

    List<Book> findBooksByPriceOldIs(Integer price);

    @Query("FROM Book WHERE isBestseller = 1")
    List<Book> getBestsellers();

    @Query(value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books)", nativeQuery = true)
    List<Book> getBooksWithMaxDiscount();

    Page<Book> findBookByTitleContaining(String bookTitle, Pageable nextPage);

    Book findBookBySlug(String slug);

    List<Book> findBooksBySlugIn(String[] slugs);

    @Query("SELECT b FROM Book b, BookUser bu, BookUserType t " +
            "WHERE b.id = bu.book.id AND bu.user = :user AND bu.type.id = t.id AND t.code = :type")
    List<Book> findBooksByUserAndType(BookstoreUser user, TypeBookToUser type);

    @Query("SELECT b FROM Book b, BookUser bu, BookUserType t " +
            "WHERE b.id = bu.book.id AND bu.user = :user AND bu.type.id = t.id AND t.code = :type")
    List<Book> findBooksByUserAndType(BookstoreUser user, TypeBookToUser type, Pageable nextPage);

    @Query("SELECT b FROM Book b, BookUser bu, BookUserType t " +
            "WHERE b.id = bu.book.id AND bu.user = :user AND bu.type.id = t.id AND t.code IN :types")
    List<Book> findBooksByUserAndTypeIn(BookstoreUser user, TypeBookToUser[] types);

    @Query("SELECT b FROM Book b, BookUser bu WHERE b.id = bu.book.id AND bu.user = :user")
    List<Book> findBooksByUser(BookstoreUser user);

    @Query(value = "SELECT b.id, description, image, is_bestseller, discount, price, pub_date, slug, title " +
            "        FROM books b " +
            "             INNER JOIN book2tag bt ON b.id = bt.book_id " +
            "             INNER JOIN tags t ON t.id = bt.tag_id " +
            "       WHERE t.id = :tagId", nativeQuery = true)
    Page<Book> findBooksByTagId(@Param(value = "tagId") Integer id, Pageable nextPage);

    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :id")
    List<Book> findBooksByGenreId(@Param("id") Integer id, Pageable nextPage);

    @Query(value = "SELECT * FROM books" +
            "                INNER JOIN " +
            "                      (SELECT b.id book_id, ROUND(AVG(br.value)) rating " +
            "                         FROM books b INNER JOIN book_rating br ON b.id = br.book_id " +
            "                       GROUP BY b.id) as tab_1 ON books.id = tab_1.book_id " +
            "        ORDER BY rating DESC, books.pub_date DESC", nativeQuery = true)
    List<Book> findRecommendedBooksSortRatingAndRecent(Pageable nextPage);

    @Query(value =
            "SELECT * FROM books INNER JOIN " +
            "       (SELECT b.id bid, COUNT(*) n FROM books b " +
            "               INNER JOIN book2author ba ON ba.book_id = b.id " +
            "               INNER JOIN authors a     ON ba.author_id = a.id " +
            "               INNER JOIN book2genre bg ON b.id = bg.book_id " +
            "               INNER JOIN genres g      ON bg.genre_id = g.id " +
            "               INNER JOIN book2tag bt   ON bt.book_id = b.id " +
            "               INNER JOIN tags t        ON t.id = bt.tag_id " +
            "         WHERE b.id NOT IN (SELECT bi.id FROM books bi " +
            "                                         INNER JOIN book2user bu        ON bi.id = bu.book_id " +
            "                                         INNER JOIN book2user_type b2ut ON bu.type_id = b2ut.id " +
            "                             WHERE bu.user_id = :userId AND b2ut.code IN ('CART', 'PAID', 'KEPT')) " +
            "                    AND (a.id IN :authors OR g.id IN :genres OR t.id IN :tags) " +
            "         GROUP BY 1) tab_1 ON books.id = tab_1.bid " +
            " ORDER BY n DESC, pub_date DESC", nativeQuery = true)
    Page<Book> findBooksByAuthorInOrGenresInOrTagsInAndBookUserNotIn(List<Author> authors, List<Genre> genres, List<Tag> tags, Integer userId, Pageable nextPage);

    @Query(value =
            "SELECT id, description, image, is_bestseller, discount, price, pub_date, slug, title " +
            "  FROM books " +
            "       INNER JOIN " +
            "       (SELECT book_id, SUM(popul) AS popular " +
            "          FROM (SELECT book_id, " +
            "                       b2ut.code, " +
            "                       CASE " +
            "                       WHEN b2ut.code = 'PAID' THEN COUNT(*) " +
            "                       WHEN b2ut.code = 'CART' THEN COUNT(*) * 0.7 " +
            "                       WHEN b2ut.code = 'KEPT' THEN COUNT(*) * 0.4 " +
            "                       ELSE 0 END as popul " +
            "                  FROM book2user bu INNER JOIN book2user_type b2ut ON bu.type_id = b2ut.id GROUP BY 1, 2) AS tab1 " +
            "         GROUP BY 1 ORDER BY 2 DESC) AS tab2 " +
            "       ON books.id = tab2.book_id " +
            " ORDER BY popular DESC", nativeQuery = true)
    List<Book> findPopularBooks(Pageable nextPage);

    @Query(value =
            "WITH tab_view_for_user AS " +
                    "(SELECT b2u.book_id as book_id " +
                    "   FROM book2user b2u" +
                    "        INNER JOIN book2user_type b2ut ON b2u.type_id = b2ut.id " +
                    "  WHERE b2ut.code = 'VIEWED' AND b2u.user_id = :user) " +
                    "SELECT id, description, image, is_bestseller, discount, price, pub_date, slug, title " +
                    "  FROM books " +
                    "       INNER JOIN (SELECT book_id, " +
                    "                          CASE WHEN book_id IN (SELECT book_id FROM tab_view_for_user) " +
                    "                          THEN (pop * 1.5 + 1) " +
                    "                          ELSE pop END as popular " +
                    "                     FROM (SELECT book_id, SUM(popul) AS pop " +
                    "                             FROM (SELECT book_id, " +
                    "                                          b2ut.code, " +
                    "                                          CASE " +
                    "                                          WHEN b2ut.code = 'PAID' THEN COUNT(*) " +
                    "                                          WHEN b2ut.code = 'CART' THEN COUNT(*) * 0.7 " +
                    "                                          WHEN b2ut.code = 'KEPT' THEN COUNT(*) * 0.4 " +
                    "                                          ELSE 0 END as popul " +
                    "                                     FROM book2user bu INNER JOIN book2user_type b2ut ON bu.type_id = b2ut.id " +
                    "                                    GROUP BY 1, 2) AS tab_popular " +
                    "                    GROUP BY 1 ORDER BY 2 DESC) as tab_popular_all) as tab_popular_user " +
                    "        ON books.id = tab_popular_user.book_id " +
                    "  ORDER BY popular DESC",
            nativeQuery = true)
    List<Book> findPopularBooksForUser(BookstoreUser user, Pageable nextPage);

    List<Book> findBooksByPubDateGreaterThanEqualAndPubDateLessThanEqual(Date from, Date to, Pageable nextPage);

    List<Book> findBooksByPubDateLessThanEqual(Date to, Pageable nextPage);

    List<Book> findBooksByPubDateGreaterThanEqual(Date from, Pageable nextPage);

    @Transactional
    @Modifying
    void deleteBySlug(String slug);
}
