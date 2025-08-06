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


public class CreateReviewTest {
    private MovieApiClient movieApiClient;
    private ReviewApiClient reviewApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
        reviewApiClient = new ReviewApiClient(movieApiClient);
    }

    @Test
    @DisplayName("Успешное создание отзыва")
    public void createReviewWithValidRequest() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Long movieId = movies.getFirst().id();

        ReviewRequest validRequest = ReviewRequest.builder()
                .filmId(movieId)
                .text("Шедевр")
                .like(true)
                .reviewerName("Критик")
                .build();

        int initialSize = reviewApiClient.getAll().size();
        Review createdReview = reviewApiClient.createReview(validRequest);
        Set<Review> reviewsForFilm1L = reviewApiClient.getByFilmId(movieId);
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(createdReview),
                () -> Assertions.assertEquals(validRequest.filmId(), createdReview.filmId()),
                () -> Assertions.assertEquals(validRequest.text(), createdReview.text()),
                () -> Assertions.assertEquals(validRequest.like(), createdReview.like()),
                () -> Assertions.assertEquals(validRequest.reviewerName(), createdReview.reviewerName()),
                () -> Assertions.assertNotNull(createdReview.createdAt()),
                () -> Assertions.assertTrue(reviewApiClient.getAll().contains(createdReview)),
                () -> Assertions.assertTrue(reviewsForFilm1L.stream().allMatch(review -> review.filmId().equals(movieId))),
                () -> Assertions.assertEquals(initialSize + 1, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании ревью с пустыми полями")
    public void createReviewWithNullRequest() {
        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(null));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните данные отзыва", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании отзыва с Null ID фильма")
    public void createReviewWithNullFilmId() {
        ReviewRequest request = ReviewRequest.builder()
                .filmId(null)
                .text("типа шедевр,епта!")
                .like(false)
                .reviewerName("Диванный Критик")
                .build();

        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(request));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните ID фильма для отзыва", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании отзыва с Null текстом")
    public void createReviewWithNullText() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Long movieId = movies.getFirst().id();

        ReviewRequest request = ReviewRequest.builder()
                .filmId(movieId)
                .text(null)
                .like(true)
                .reviewerName("Диванный Критик")
                .build();

        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(request));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните текст для отзыва", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании отзыва с пустым текстом")
    public void createReviewWithBlankText() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Long movieId = movies.getFirst().id();

        ReviewRequest request = ReviewRequest.builder()
                .filmId(movieId)
                .text("  ")
                .like(true)
                .reviewerName("Диванный Критик")
                .build();

        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(request));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Текст отзыва пуст", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании отзыва с Null like")
    public void createReviewWithNullLike() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Long movieId = movies.getFirst().id();

        ReviewRequest request = ReviewRequest.builder()
                .filmId(movieId)
                .text("типа шедевр,епта!")
                .like(null)
                .reviewerName("Диванный Критик")
                .build();

        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(request));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Поставьте лайк или дизлайк отзыва", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании отзыва с Null like")
    public void createReviewWithNullReviewerName() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Long movieId = movies.getFirst().id();

        ReviewRequest request = ReviewRequest.builder()
                .filmId(movieId)
                .text("типа шедевр,епта!")
                .like(false)
                .reviewerName(null)
                .build();

        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(request));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните имя ревьюера для отзыва", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка ошибки при создании отзыва с Null like")
    public void createReviewWithBlankReviewerName() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Long movieId = movies.getFirst().id();

        ReviewRequest request = ReviewRequest.builder()
                .filmId(movieId)
                .text("типа шедевр,епта!")
                .like(false)
                .reviewerName("    ")
                .build();

        int initialSize = reviewApiClient.getAll().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> reviewApiClient.createReview(request));
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Имя ревьюера отзыва пустое", exception.getMessage()),
                () -> Assertions.assertEquals(initialSize, resultReviewStorage));
    }

    @Test
    @DisplayName("Проверка создания нескольких отзывов для одного фильма")
    public void createMultipleReviewWithValidRequest() {
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

        int initialSize = reviewApiClient.getAll().size();
        Review review1 = reviewApiClient.createReview(request1);
        Review review2 = reviewApiClient.createReview(request2);
        int resultReviewStorage = reviewApiClient.getAll().size();

        Assertions.assertAll(
                () -> Assertions.assertNotEquals(review1.id(), review2.id(), "ID отзывов должны быть уникальны"),
                () -> Assertions.assertTrue(reviewApiClient.getAll().contains(review1)),
                () -> Assertions.assertTrue(reviewApiClient.getAll().contains(review2)),
                () -> Assertions.assertEquals(initialSize + 2, resultReviewStorage));
    }
}