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
import java.util.Set;

public class GetByReviewerTest {
    private MovieApiClient movieApiClient;
    private ReviewApiClient reviewApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
        reviewApiClient = new ReviewApiClient(movieApiClient);
    }

    @Test
    @DisplayName("Проверка на получение отзыва по имени автора")
    public void getByReviewerInitialReviews() {
        Set<Review> reviews = reviewApiClient.getByReviewer("Арсений Евсеевич");
        Review initialReview = reviews.iterator().next();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, reviews.size()),
                () -> Assertions.assertEquals(1L, initialReview.id()),
                () -> Assertions.assertEquals(1L, initialReview.filmId()),
                () -> Assertions.assertEquals("Вдохновляющий фильм по книге Кена Кизи.\nСмысл немного изменен в отличие от книги и повествование построено по-другому.\n", initialReview.text()),
                () -> Assertions.assertTrue(initialReview.like()));
    }

    @Test
    @DisplayName("Проверка получения отзыва после создания")
    public void getByReviewerMultipleReview() {
        MovieRequest movierequest1 = new MovieRequest(
                "Inception",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );
        Movie testMovie1 = movieApiClient.createMovie(movierequest1);
        Long testMovie1Id = testMovie1.id();

        MovieRequest movierequest2 = new MovieRequest(
                "Inception 2.0",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );
        Movie testMovie2= movieApiClient.createMovie(movierequest2);
        Long testMovie2Id = testMovie2.id();

        MovieRequest movierequest3 = new MovieRequest(
                "Inception 3.0",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );
        Movie testMovie3= movieApiClient.createMovie(movierequest3);
        Long testMovie3Id = testMovie3.id();

        ReviewRequest reviewRequest1 = ReviewRequest.builder()
                .filmId(testMovie1Id)
                .text("Новый отзыв")
                .like(false)
                .reviewerName("Арсений Евсеевич")
                .build();
        ReviewRequest reviewRequest2 = ReviewRequest.builder()
                .filmId(testMovie2Id)
                .text("Первый отзыв")
                .like(true)
                .reviewerName("Арсений Евсеевич")
                .build();
        ReviewRequest reviewRequest3 = ReviewRequest.builder()
                .filmId(testMovie2Id)
                .text("Второй отзыв")
                .like(false)
                .reviewerName("Арсений Евсеевич").build();
        ReviewRequest reviewRequest4 = ReviewRequest.builder()
                .filmId(testMovie3Id)
                .text("Третий отзыв")
                .like(false)
                .reviewerName("Арсений Евсеевич")
                .build();

        int initialSize = reviewApiClient.getByReviewer("Арсений Евсеевич").size();
        Review createdReview1 = reviewApiClient.createReview(reviewRequest1);
        Review createdReview2 = reviewApiClient.createReview(reviewRequest2);
        Review createdReview3 = reviewApiClient.createReview(reviewRequest3);
        Review createdReview4 = reviewApiClient.createReview(reviewRequest4);
        int resultSize = reviewApiClient.getByReviewer("Арсений Евсеевич").size();

        Set<Review> reviewerReviews = reviewApiClient.getByReviewer("Арсений Евсеевич");

        Assertions.assertAll(
                () -> Assertions.assertNotEquals(createdReview1.id(), createdReview2.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(createdReview2.id(), createdReview3.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(createdReview3.id(), createdReview4.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(createdReview1.id(), createdReview3.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(createdReview1.id(), createdReview4.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(createdReview2.id(), createdReview4.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertNotEquals(createdReview3.id(), createdReview4.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertTrue(reviewerReviews.contains(createdReview1)),
                () -> Assertions.assertTrue(reviewerReviews.contains(createdReview2)),
                () -> Assertions.assertTrue(reviewerReviews.contains(createdReview3)),
                () -> Assertions.assertTrue(reviewerReviews.contains(createdReview4)),
                () -> Assertions.assertEquals(initialSize + 4, resultSize));
    }

    @Test
    @DisplayName("Попытка получить отзывы с пустым именем ревьюера")
    public void getByReviewerEmptyReviewerName() {
        Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.getByReviewer(""));
    }

    @Test
    @DisplayName("Попытка получить отзывы для несуществующего ревьюера")
    void getByReviewer_shouldThrowExceptionForNonExistingReviewer() {
        String nonExistingReviewer = "Несуществующий автор";

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.getByReviewer(nonExistingReviewer));

        Assertions.assertEquals(String.format("Отзывы по ревьюеру '%s' не найдены", nonExistingReviewer), exception.getMessage());
    }
}
