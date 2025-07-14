package org.example.movie.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.model.Genre;
import ru.dpd.edu.model.Movie;
import ru.dpd.edu.model.MovieRequest;

import java.time.LocalDate;
import java.util.Set;

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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        Movie createdMovie = movieApiClient.createMovie(request);
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(createdMovie),
                () -> Assertions.assertEquals(4L, createdMovie.id()),
                () -> Assertions.assertEquals(request.title(), createdMovie.title()),
                () -> Assertions.assertEquals(request.durationInMinutes(), createdMovie.durationInMinutes()),
                () -> Assertions.assertEquals(request.genres(), createdMovie.genres()),
                () -> Assertions.assertEquals(request.releaseDate(), createdMovie.releaseDate()),
                () -> Assertions.assertNotNull(createdMovie.createdAt()),
                () -> Assertions.assertNotNull(createdMovie.updatedAt()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage + 1));
    }

    @Test
    @DisplayName("Проверка ошибки при создании фильма с пустыми полями")
    public void createMovieWithNullRequest() {
        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(null));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните данные фильма", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните заголовок фильма", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заголовок фильма пуст", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните продолжительность фильма", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните жанры фильма", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Список жанров фильма пуст", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Заполните дату релиза фильма", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
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

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.createMovie(request));
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals("Дата релиза не может быть больше текущей", exception.getMessage()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage));
    }
}