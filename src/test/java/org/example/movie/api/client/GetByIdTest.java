package org.example.movie.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.model.Genre;
import ru.dpd.edu.model.Movie;

import java.time.LocalDate;
import java.util.Set;

public class GetByIdTest {
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное получение фильма по id")
    public void getByIdTest() {
        Movie movie = movieApiClient.getById(1L);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(movie),
                () -> Assertions.assertEquals("Пролетая над гнездом кукушки", movie.title()),
                () -> Assertions.assertEquals(100L, movie.durationInMinutes()),
                () -> Assertions.assertEquals(Set.of(Genre.DRAMA), movie.genres()),
                () -> Assertions.assertEquals(LocalDate.of(1990, 10, 12), movie.releaseDate()),
                () -> Assertions.assertNotNull(movie.createdAt()),
                () -> Assertions.assertNotNull(movie.updatedAt()));
    }

    @Test
    @DisplayName("Проверка ошибки для несуществующего id")
    public void getByNotExistIdTest() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.getById(5L));

        Assertions.assertEquals("Фильм по ID = 5 не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка различия фильмов с разными ID")
    public void getByDifferentIdTest() {

        Movie movie1 = movieApiClient.getById(1L);
        Movie movie2 = movieApiClient.getById(2L);

        Assertions.assertNotEquals(movie1.title(), movie2.title());
    }

    @Test
    @DisplayName("Проверка возвращения одного и тот же объекта для одинакового ID")
    public void getBySameIdTest() {
        Movie firstCall = movieApiClient.getById(3L);
        Movie secondCall = movieApiClient.getById(3L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(firstCall, secondCall),
                () -> Assertions.assertSame(firstCall, secondCall));
    }
}