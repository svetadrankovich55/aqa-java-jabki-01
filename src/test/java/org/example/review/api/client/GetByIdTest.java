package org.example.review.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.ReviewApiClient;
import ru.dpd.edu.model.Movie;
import ru.dpd.edu.model.Review;
import ru.dpd.edu.model.ReviewRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class GetByIdTest {
    private MovieApiClient movieApiClient;
    private ReviewApiClient reviewApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
        reviewApiClient = new ReviewApiClient(movieApiClient);
    }

    @Test
    @DisplayName("Проверка на получение отзыва по ID")
    public void getByFilmIdInitialReviews() {
        Review review = reviewApiClient.getById(1L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, review.id()),
                () -> Assertions.assertEquals(1L, review.filmId()),
                () -> Assertions.assertEquals("Вдохновляющий фильм по книге Кена Кизи.\nСмысл немного изменен в отличие от книги и повествование построено по-другому.\n", review.text()),
                () -> Assertions.assertEquals("Арсений Евсеевич", review.reviewerName()),
                () -> Assertions.assertTrue(review.like()));
    }

    @Test
    @DisplayName("Проверка получения отзыва после создания")
    public void getByFilmIdMultipleReviews() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long movieId = movies.getFirst().id();

        ReviewRequest request = ReviewRequest.builder()
                .filmId(movieId)
                .text("Новый отзыв")
                .like(false)
                .reviewerName("Новый автор")
                .build();
        Review createdReview = reviewApiClient.createReview(request);
        Long newId = createdReview.id();

        Review resultReview = reviewApiClient.getById(newId);

        Assertions.assertAll(
                () -> Assertions.assertEquals(newId, resultReview.id()),
                () -> Assertions.assertEquals(movieId, resultReview.filmId()),
                () -> Assertions.assertEquals("Новый отзыв", resultReview.text()),
                () -> Assertions.assertEquals("Новый автор", resultReview.reviewerName()),
                () -> Assertions.assertFalse(resultReview.like()),
                () -> Assertions.assertNotNull(resultReview.createdAt()));
    }

    @Test
    @DisplayName("Получение отзывов по несуществующему Id")
    public void getByFilmIdNonExistingFilmId() {
        Set<Long> existingIds = reviewApiClient.getAll().stream()
                .map(Review::id)
                .collect(Collectors.toSet());

        long nonExistingId;
        if (existingIds.isEmpty()) {
            nonExistingId = 1L;
        } else {
            nonExistingId = Collections.max(existingIds) + 1;
        }

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.getById(nonExistingId));

        Assertions.assertEquals(String.format("Ревью по ID = %d не найден", nonExistingId), exception.getMessage());
    }
}
