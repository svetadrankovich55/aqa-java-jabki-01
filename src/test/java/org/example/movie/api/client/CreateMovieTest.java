package org.example.movie.api.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.model.Genre;
import ru.dpd.edu.model.Movie;
import ru.dpd.edu.model.MovieRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreateMovieTest {
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное создание фильма")
    public void createMovieValidRequestReturnsCreatedMovie() {
        MovieRequest request = new MovieRequest(
                "Inception",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );

        Movie createdMovie = movieApiClient.createMovie(request);

        assertAll(
                () -> assertNotNull(createdMovie),
                () -> assertEquals(4L, createdMovie.id()),
                () -> assertEquals(request.title(), createdMovie.title()),
                () -> assertEquals(request.durationInMinutes(), createdMovie.durationInMinutes()),
                () -> assertEquals(request.genres(), createdMovie.genres()),
                () -> assertEquals(request.releaseDate(), createdMovie.releaseDate()),
                () -> assertNotNull(createdMovie.createdAt()),
                () -> assertNotNull(createdMovie.updatedAt()));
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с пустыми полями")
    public void createMovieWithNullRequest() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(null));

        assertEquals("Заполните данные фильма", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с Null заголовком")
    public void createMovieWithNullTitleRequest() {
        MovieRequest request = new MovieRequest(
                null,
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Заполните заголовок фильма", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с пустым заголовком")
    public void createMovieWithBlankTitleRequest() {
        MovieRequest request = new MovieRequest(
                "   ",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Заголовок фильма пуст", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с пустой длительностью")
    public void createMovieWithNullDurationInMinutesRequest() {
        MovieRequest request = new MovieRequest(
                "Inception",
                null,
                LocalDate.of(2010, 7, 16),
                Set.of(Genre.THRILLER)
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Заполните продолжительность фильма", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с null жанрами")
    public void createMovieWithNullGenresRequest() {
        MovieRequest request = new MovieRequest(
                "Inception",
                148L,
                LocalDate.of(2010, 7, 16),
                null
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Заполните жанры фильма", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с пустыми жанрами")
    public void createMovieWithEmptyGenresRequest() {
        MovieRequest request = new MovieRequest(
                "Inception",
                148L,
                LocalDate.of(2010, 7, 16),
                Set.of()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Список жанров фильма пуст", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с пустой датой релиза")
    public void createMovieWithNullReleaseDateRequest() {
        MovieRequest request = new MovieRequest(
                "Inception",
                148L,
                null,
                Set.of(Genre.THRILLER)
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Заполните дату релиза фильма", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с датой релиза больше текущей")
    public void createMovieWithFutureReleaseDateRequest() {
        MovieRequest request = new MovieRequest(
                "Inception",
                148L,
                LocalDate.now().plusDays(1),
                Set.of(Genre.THRILLER)
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));

        assertEquals("Дата релиза не может быть больше текущей", exception.getMessage());
    }
}