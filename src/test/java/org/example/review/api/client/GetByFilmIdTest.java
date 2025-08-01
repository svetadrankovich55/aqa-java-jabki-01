package org.example.review.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.ReviewApiClient;
import ru.dpd.edu.model.Genre;
import ru.dpd.edu.model.Movie;
import ru.dpd.edu.model.MovieRequest;
import ru.dpd.edu.model.Review;
import ru.dpd.edu.model.ReviewRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GetByFilmIdTest {
    private MovieApiClient movieApiClient;
    private ReviewApiClient reviewApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
        reviewApiClient = new ReviewApiClient(movieApiClient);
    }

    @Test
    @DisplayName("Проверка на получение отзывов по ID фильмов (начальное состояние)")
    public void getByFilmIdInitialReviews() {
        Set<Review> reviews = reviewApiClient.getByFilmId(1L);

        Review initialReview = reviews.iterator().next();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, reviews.size()),
                () -> Assertions.assertEquals(1L, initialReview.id()),
                () -> Assertions.assertEquals(1L, initialReview.filmId()),
                () -> Assertions.assertEquals("Вдохновляющий фильм по книге Кена Кизи.\nСмысл немного изменен в отличие от книги и повествование построено по-другому.\n", initialReview.text()),
                () -> Assertions.assertEquals("Арсений Евсеевич", initialReview.reviewerName()),
                () -> Assertions.assertTrue(initialReview.like()));
    }

    @Test
    @DisplayName("Проверка получения нескольких отзывов для одного фильма")
    public void getByFilmIdMultipleReviews() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long movieId = movies.getFirst().id();

        ReviewRequest request1 = ReviewRequest.builder()
                .filmId(movieId)
                .text("Первый отзыв")
                .like(true)
                .reviewerName("Первый автор")
                .build();
        ReviewRequest request2 = ReviewRequest.builder()
                .filmId(movieId)
                .text("Второй отзыв")
                .like(false)
                .reviewerName("Второй автор")
                .build();
        ReviewRequest request3 = ReviewRequest.builder()
                .filmId(movieId)
                .text("Третий отзыв")
                .like(false)
                .reviewerName("Третий автор")
                .build();
        int initialSize;

        try {
            initialSize = reviewApiClient.getByFilmId(movieId).size();
        } catch (RuntimeException e) {
            initialSize = 0;
        }

        Review review1 = reviewApiClient.createReview(request1);
        Review review2 = reviewApiClient.createReview(request2);
        Review review3 = reviewApiClient.createReview(request3);
        int resultReviewStorage = reviewApiClient.getByFilmId(movieId).size();
        int finalInitialSize = initialSize;

        Assertions.assertAll(
                () -> Assertions.assertNotEquals(review1.id(), review2.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(review2.id(), review3.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(movieId).contains(review1)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(movieId).contains(review2)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(movieId).contains(review3)),
                () -> Assertions.assertEquals(finalInitialSize + 3, resultReviewStorage));
    }

    @Test
    @DisplayName("Получение отзывов по несуществующему filmId")
    public void getByFilmIdNonExistingFilmId() {
        Long nonExistingFilmId = 999L;

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.getByFilmId(nonExistingFilmId));

        Assertions.assertEquals("Отзывы по фильму ID = 999 не найдены", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения нескольких отзывов для разных фильма")
    public void getByDifferentFilmIdMultipleReviews() {
        MovieRequest request1 = new MovieRequest(
                "Inception",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );
        Movie testMovie1 = movieApiClient.createMovie(request1);
        Long testMovie1Id = testMovie1.id();

        MovieRequest request2 = new MovieRequest(
                "Inception 2.0",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );
        Movie testMovie2= movieApiClient.createMovie(request2);
        Long testMovie2Id = testMovie2.id();

        MovieRequest request3 = new MovieRequest(
                "Inception 3.0",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );
        Movie testMovie3= movieApiClient.createMovie(request3);
        Long testMovie3Id = testMovie3.id();

        ReviewRequest requestFor1Film = ReviewRequest.builder()
                .filmId(testMovie1Id)
                .text("Первый отзыв для первого фильма")
                .like(true)
                .reviewerName("Первый автор")
                .build();
        ReviewRequest requestFor2Film = ReviewRequest.builder()
                .filmId(testMovie2Id)
                .text("Первый отзыв для второго фильма")
                .like(false)
                .reviewerName("Второй автор")
                .build();
        ReviewRequest request2For2Film = ReviewRequest.builder()
                .filmId(testMovie2Id)
                .text("Второй отзыв для второго фильма")
                .like(false)
                .reviewerName("Третий автор")
                .build();
        ReviewRequest requestFor3Film = ReviewRequest.builder()
                .filmId(testMovie3Id)
                .text("Первый отзыв для третьего фильма")
                .like(false)
                .reviewerName("Третий автор")
                .build();

        Review review1 = reviewApiClient.createReview(requestFor1Film);
        Review review2 = reviewApiClient.createReview(requestFor2Film);
        Review review3 = reviewApiClient.createReview(request2For2Film);
        Review review4 = reviewApiClient.createReview(requestFor3Film);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, reviewApiClient.getByFilmId(testMovie1Id).size()),
                () -> Assertions.assertEquals(2, reviewApiClient.getByFilmId(testMovie2Id).size()),
                () -> Assertions.assertEquals(1, reviewApiClient.getByFilmId(testMovie3Id).size()),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(testMovie1Id).contains(review1)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(testMovie2Id).contains(review2)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(testMovie2Id).contains(review3)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(testMovie3Id).contains(review4)));
    }
}
