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

public class GetAllTest {
    private MovieApiClient movieApiClient;
    private ReviewApiClient reviewApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
        reviewApiClient = new ReviewApiClient(movieApiClient);
    }

    @Test
    @DisplayName("Проверка на получение всех отзывов (начальное состояние)")
    public void getAlLInitialReviews() {
        Set<Review> reviews = reviewApiClient.getAll();

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
    public void getAlLMultipleReviews() {
        ReviewRequest request1 = ReviewRequest.builder().filmId(1L).text("Первый отзыв").like(true).reviewerName("Первый автор").build();
        ReviewRequest request2 = ReviewRequest.builder().filmId(1L).text("Второй отзыв").like(false).reviewerName("Второй автор").build();
        ReviewRequest request3 = ReviewRequest.builder().filmId(1L).text("Третий отзыв").like(false).reviewerName("Третий автор").build();

        int initialSize = reviewApiClient.getAll().size();
        reviewApiClient.createReview(request1);
        reviewApiClient.createReview(request2);
        reviewApiClient.createReview(request3);
        int resultReviewStorage = reviewApiClient.getAll().size();
        Set<Review> reviews = reviewApiClient.getAll();

        Assertions.assertEquals(initialSize + 3, resultReviewStorage);
        Assertions.assertTrue(reviews.stream().anyMatch(r ->
                r.id().equals(2L) &&
                        r.filmId().equals(1L) &&
                        r.text().equals("Первый отзыв") &&
                        r.reviewerName().equals("Первый автор")));

        Assertions.assertTrue(reviews.stream().anyMatch(r ->
                r.id().equals(3L) &&
                        r.filmId().equals(1L) &&
                        r.text().equals("Второй отзыв") &&
                        r.reviewerName().equals("Второй автор")));

        Assertions.assertTrue(reviews.stream().anyMatch(r ->
                r.id().equals(4L) &&
                        r.filmId().equals(1L) &&
                        r.text().equals("Третий отзыв") &&
                        r.reviewerName().equals("Третий автор")));
    }
}