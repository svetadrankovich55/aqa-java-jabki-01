package org.example.review.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.ReviewApiClient;
import ru.dpd.edu.model.Review;
import ru.dpd.edu.model.ReviewRequest;

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
        ReviewRequest request1 = ReviewRequest.builder().filmId(1L).text("Первый отзыв").like(true).reviewerName("Первый автор").build();
        ReviewRequest request2 = ReviewRequest.builder().filmId(1L).text("Второй отзыв").like(false).reviewerName("Второй автор").build();
        ReviewRequest request3 = ReviewRequest.builder().filmId(1L).text("Третий отзыв").like(false).reviewerName("Третий автор").build();

        int initialSize = reviewApiClient.getByFilmId(1L).size();
        Review review1 = reviewApiClient.createReview(request1);
        Review review2 = reviewApiClient.createReview(request2);
        Review review3 = reviewApiClient.createReview(request3);
        int resultReviewStorage = reviewApiClient.getByFilmId(1L).size();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, review1.id()),
                () -> Assertions.assertEquals(3L, review2.id()),
                () -> Assertions.assertEquals(4L, review3.id()),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(1L).contains(review1)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(1L).contains(review2)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(1L).contains(review3)),
                () -> Assertions.assertEquals(initialSize + 3, resultReviewStorage));
    }

    @Test
    @DisplayName("Получение отзывов по несуществующему filmId")
    public void getByFilmIdNonExistingFilmId() {
        Long nonExistingFilmId = 777L;

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.getByFilmId(nonExistingFilmId));

        Assertions.assertEquals("Отзывы по фильму ID = 777 не найдены", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения нескольких отзывов для разных фильма")
    public void getByDifferentFilmIdMultipleReviews() {
        ReviewRequest requestFor1Film = ReviewRequest.builder().filmId(1L).text("Первый отзыв для первого фильма").like(true).reviewerName("Первый автор").build();
        ReviewRequest requestFor2Film = ReviewRequest.builder().filmId(2L).text("Первый отзыв для второго фильма").like(false).reviewerName("Второй автор").build();
        ReviewRequest request2For2Film = ReviewRequest.builder().filmId(2L).text("Второй отзыв для второго фильма").like(false).reviewerName("Третий автор").build();
        ReviewRequest requestFor3Film = ReviewRequest.builder().filmId(3L).text("Первый отзыв для третьего фильма").like(false).reviewerName("Третий автор").build();

        Review review1 = reviewApiClient.createReview(requestFor1Film);
        Review review2 = reviewApiClient.createReview(requestFor2Film);
        Review review3 = reviewApiClient.createReview(request2For2Film);
        Review review4 = reviewApiClient.createReview(requestFor3Film);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, reviewApiClient.getByFilmId(1L).size()),
                () -> Assertions.assertEquals(2, reviewApiClient.getByFilmId(2L).size()),
                () -> Assertions.assertEquals(1, reviewApiClient.getByFilmId(3L).size()),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(1L).contains(review1)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(2L).contains(review2)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(2L).contains(review3)),
                () -> Assertions.assertTrue(reviewApiClient.getByFilmId(3L).contains(review4)));
    }
}
