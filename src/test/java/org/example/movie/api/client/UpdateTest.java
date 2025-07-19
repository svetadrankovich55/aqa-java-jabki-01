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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateTest {
    private final Long EXISTING_ID = 1L;
    private final Long NON_EXISTING_ID = 999L;
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление всех полей")
    public void updateMovieWithValidRequest() {
        UpdateMovieRequest request = new UpdateMovieRequest(
                EXISTING_ID,
                "Начало",
                150L,
                LocalDate.of(2020, 1, 1),
                Set.of(Genre.DRAMA)
        );

        int sizeMovieStorage = movieApiClient.getAllFilms().size();
        Movie updateMovie = movieApiClient.update(request);
        Movie updateMovie1 = movieApiClient.getById(EXISTING_ID);
        int resultMovieStorage = movieApiClient.getAllFilms().size();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(updateMovie),
                () -> Assertions.assertEquals(EXISTING_ID, updateMovie.id()),
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
        Long originalDuration = movieApiClient.getById(EXISTING_ID).durationInMinutes();
        LocalDate originalReleaseDate = movieApiClient.getById(EXISTING_ID).releaseDate();
        Set<Genre> originalGenre = movieApiClient.getById(EXISTING_ID).genres();

        UpdateMovieRequest request = new UpdateMovieRequest(
                EXISTING_ID,
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

        String originalTitle = movieApiClient.getById(EXISTING_ID).title();
        LocalDate originalReleaseDate = movieApiClient.getById(EXISTING_ID).releaseDate();
        Set<Genre> originalGenre = movieApiClient.getById(EXISTING_ID).genres();

        UpdateMovieRequest request = new UpdateMovieRequest(
                EXISTING_ID,
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

        String originalTitle = movieApiClient.getById(EXISTING_ID).title();
        Long originalDuration = movieApiClient.getById(EXISTING_ID).durationInMinutes();
        Set<Genre> originalGenre = movieApiClient.getById(EXISTING_ID).genres();

        UpdateMovieRequest request = new UpdateMovieRequest(
                EXISTING_ID,
                null,
                null,
                LocalDate.of(2020, 1, 1),
                null
        );

        Movie updatedMovie = movieApiClient.update(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(originalTitle, updatedMovie.title()),
                () -> Assertions.assertEquals(originalDuration, updatedMovie.durationInMinutes()),
                () -> Assertions.assertEquals(request.releaseDate(), updatedMovie.releaseDate()),
                () -> Assertions.assertEquals(originalGenre, updatedMovie.genres()));
    }

    @Test
    @DisplayName("Успешное редактирование фильма. Обновление только жанра фильма")
    public void updateOnlyGenre() {

        String originalTitle = movieApiClient.getById(EXISTING_ID).title();
        Long originalDuration = movieApiClient.getById(EXISTING_ID).durationInMinutes();
        LocalDate originalReleaseDate = movieApiClient.getById(EXISTING_ID).releaseDate();

        UpdateMovieRequest request = new UpdateMovieRequest(
                EXISTING_ID,
                null,
                null,
                null,
                Set.of(Genre.DRAMA)
        );

        Movie updatedMovie = movieApiClient.update(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(originalTitle, updatedMovie.title()),
                () -> Assertions.assertEquals(originalDuration, updatedMovie.durationInMinutes()),
                () -> Assertions.assertEquals(originalReleaseDate, updatedMovie.releaseDate()),
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