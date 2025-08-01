package org.example.movie.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GetByIdTest {
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное получение фильма по id")
    public void getByIdTest() {

        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);
        Movie randomMovie = movies.getFirst();

        Movie result = movieApiClient.getById(randomMovie.id());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(randomMovie.title(), result.title()),
                () -> Assertions.assertEquals(randomMovie.durationInMinutes(), result.durationInMinutes()),
                () -> Assertions.assertEquals(randomMovie.genres(), result.genres()),
                () -> Assertions.assertEquals(randomMovie.releaseDate(), result.releaseDate()),
                () -> Assertions.assertNotNull(result.createdAt(), "Дата создания не должна быть пустой"),
                () -> Assertions.assertNotNull(result.updatedAt(), "Дата редактирования не должна быть пустой"));
    }

    @Test
    @DisplayName("Проверка ошибки для несуществующего id")
    public void getByNotExistIdTest() {
        Set<Long> existingIds = movieApiClient.getAllFilms().stream()
                .map(Movie::id)
                .collect(Collectors.toSet());

        long nonExistentId;
        if (existingIds.isEmpty()) {
            nonExistentId = 1L;
        } else {
            nonExistentId = Collections.max(existingIds) + 1;
        }

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.getById(nonExistentId));

        Assertions.assertEquals(String.format("Фильм по ID = %d не найден", nonExistentId), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка различия фильмов с разными ID")
    public void getByDifferentIdTest() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Movie movie1 = movies.getFirst();
        Movie movie2 = movies.get(1);

        Assertions.assertNotEquals(movie1.title(), movie2.title());
    }

    @Test
    @DisplayName("Проверка возвращения одного и тот же объекта для одинакового ID")
    public void getBySameIdTest() {
        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        Collections.shuffle(movies);

        Movie firstCall = movies.getFirst();
        Movie secondCall = movies.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(firstCall, secondCall),
                () -> Assertions.assertSame(firstCall, secondCall));
    }
}