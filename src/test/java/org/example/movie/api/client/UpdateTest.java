package org.example.movie.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.model.Genre;
import ru.dpd.edu.model.Movie;
import ru.dpd.edu.model.UpdateMovieRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateTest {

    private static final Long NON_EXISTING_ID = 999L;
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление всех полей")
    public void updateMovieWithValidRequest() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long existingId = movies.getFirst().id();

        UpdateMovieRequest request = new UpdateMovieRequest(
                existingId,
                "Начало",
                150L,
                LocalDate.of(2020, 1, 1),
                Set.of(Genre.DRAMA)
        );

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        Movie updateMovie = movieApiClient.update(request);
        Movie updateMovie1 = movieApiClient.getById(existingId);
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(updateMovie),
                () -> Assertions.assertEquals(existingId, updateMovie.id()),
                () -> Assertions.assertEquals(request.title(), updateMovie.title()),
                () -> Assertions.assertEquals(request.durationInMinutes(), updateMovie.durationInMinutes()),
                () -> Assertions.assertEquals(request.genres(), updateMovie.genres()),
                () -> Assertions.assertEquals(request.releaseDate(), updateMovie.releaseDate()),
                () -> Assertions.assertEquals(resultMovieStorage, sizeMovieStorage),
                () -> Assertions.assertSame(updateMovie, updateMovie1));
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление только названия фильма")
    public void updateOnlyTitle() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long existingId = movies.getFirst().id();

        Long originalDuration = movieApiClient.getById(existingId).durationInMinutes();
        LocalDate originalReleaseDate = movieApiClient.getById(existingId).releaseDate();
        Set<Genre> originalGenre = movieApiClient.getById(existingId).genres();

        UpdateMovieRequest request = new UpdateMovieRequest(
                existingId,
                "Начало",
                null,
                null,
                null
        );

        Movie updatedMovie = movieApiClient.update(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(request.title(), updatedMovie.title()),
                () -> Assertions.assertEquals(originalDuration, updatedMovie.durationInMinutes()),
                () -> Assertions.assertEquals(originalReleaseDate, updatedMovie.releaseDate()),
                () -> Assertions.assertEquals(originalGenre, updatedMovie.genres()));
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление только длительности фильма")
    public void updateOnlyDuration() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long existingId = movies.getFirst().id();

        String originalTitle = movieApiClient.getById(existingId).title();
        LocalDate originalReleaseDate = movieApiClient.getById(existingId).releaseDate();
        Set<Genre> originalGenre = movieApiClient.getById(existingId).genres();

        UpdateMovieRequest request = new UpdateMovieRequest(
                existingId,
                null,
                150L,
                null,
                null
        );

        Movie updatedMovie = movieApiClient.update(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(originalTitle, updatedMovie.title()),
                () -> Assertions.assertEquals(request.durationInMinutes(), updatedMovie.durationInMinutes()),
                () -> Assertions.assertEquals(originalReleaseDate, updatedMovie.releaseDate()),
                () -> Assertions.assertEquals(originalGenre, updatedMovie.genres()));
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление только даты релиза фильма")
    public void updateOnlyReleaseDate() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long existingId = movies.getFirst().id();

        Movie movie = movieApiClient.getById(existingId);

        UpdateMovieRequest request = new UpdateMovieRequest(
                existingId,
                null,
                null,
                LocalDate.of(2020, 1, 1),
                null
        );

        Movie updatedMovie = movieApiClient.update(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(movie.title(), updatedMovie.title()),
                () -> Assertions.assertEquals(movie.durationInMinutes(), updatedMovie.durationInMinutes()),
                () -> Assertions.assertEquals(request.releaseDate(), updatedMovie.releaseDate()),
                () -> Assertions.assertEquals(movie.genres(), updatedMovie.genres()));
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление только жанра фильма")
    public void updateOnlyGenre() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Long existingId = movies.getFirst().id();

        Movie movie = movieApiClient.getById(existingId);

        UpdateMovieRequest request = new UpdateMovieRequest(
                existingId,
                null,
                null,
                null,
                Set.of(Genre.DRAMA)
        );

        Movie updatedMovie = movieApiClient.update(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(movie.title(), updatedMovie.title()),
                () -> Assertions.assertEquals(movie.durationInMinutes(), updatedMovie.durationInMinutes()),
                () -> Assertions.assertEquals(movie.releaseDate(), updatedMovie.releaseDate()),
                () -> Assertions.assertEquals(request.genres(), updatedMovie.genres()));
    }

    @Test
    @DisplayName("Проверка ошибки при редактирование фильма с несуществующим id")
    public void updateWithNullId() {
        UpdateMovieRequest request = new UpdateMovieRequest(
                null,
                "Начало",
                150L,
                LocalDate.of(2020, 1, 1),
                Set.of(Genre.DRAMA)
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.update(request));

        Assertions.assertEquals("Заполните ID фильма для обновления", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при редактирование фильма с NULL запросом")
    public void updateWithNullRequest() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> movieApiClient.update(null));

        Assertions.assertEquals("Заполните данные для обновления фильма", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при редактирование фильма с несуществующим ID")
    public void updateForNonExistingId() {
        UpdateMovieRequest request = new UpdateMovieRequest(
                NON_EXISTING_ID,
                "Название",
                100L,
                LocalDate.now(),
                Set.of(Genre.DRAMA)
        );

        Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.update(request));
    }
}