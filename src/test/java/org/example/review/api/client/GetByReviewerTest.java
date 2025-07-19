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
        ReviewRequest request = ReviewRequest.builder().filmId(2L).text("Новый отзыв").like(false).reviewerName("Арсений Евсеевич").build();
        ReviewRequest request1 = ReviewRequest.builder().filmId(2L).text("Первый отзыв").like(true).reviewerName("Арсений Евсеевич").build();
        ReviewRequest request2 = ReviewRequest.builder().filmId(1L).text("Второй отзыв").like(false).reviewerName("Арсений Евсеевич").build();
        ReviewRequest request3 = ReviewRequest.builder().filmId(3L).text("Третий отзыв").like(false).reviewerName("Арсений Евсеевич").build();

        int initialSize = reviewApiClient.getByReviewer("Арсений Евсеевич").size();
        Review createdReview = reviewApiClient.createReview(request);
        Review createdReview1 = reviewApiClient.createReview(request1);
        Review createdReview2 = reviewApiClient.createReview(request2);
        Review createdReview3 = reviewApiClient.createReview(request3);
        int resultSize = reviewApiClient.getByReviewer("Арсений Евсеевич").size();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, createdReview.id()),
                () -> Assertions.assertEquals(3L, createdReview1.id()),
                () -> Assertions.assertEquals(4L, createdReview2.id()),
                () -> Assertions.assertEquals(5L, createdReview3.id()),
                () -> Assertions.assertTrue(reviewApiClient.getByReviewer("Арсений Евсеевич").contains(createdReview)),
                () -> Assertions.assertTrue(reviewApiClient.getByReviewer("Арсений Евсеевич").contains(createdReview1)),
                () -> Assertions.assertTrue(reviewApiClient.getByReviewer("Арсений Евсеевич").contains(createdReview2)),
                () -> Assertions.assertTrue(reviewApiClient.getByReviewer("Арсений Евсеевич").contains(createdReview3)),
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
