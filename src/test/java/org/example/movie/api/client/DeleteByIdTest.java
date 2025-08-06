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

public class DeleteByIdTest {
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное удаление фильма по ID")
    public void deleteMovieValidId() {

        List<Movie> movies = new ArrayList<>(movieApiClient.getAllFilms());
        Assertions.assertFalse(movies.isEmpty(), "Список не должен быть пустым");
        int initialSize = movies.size();
        Collections.shuffle(movies);
        Movie randomMovie = movies.getFirst();

        Movie deletedMovie = movieApiClient.deleteById(randomMovie.id());
        int resultMovieStorage = movieApiClient.getAllFilms().size();
        Set<Movie> remainingMovies = movieApiClient.getAllFilms();

        Assertions.assertAll(
                () -> Assertions.assertEquals(resultMovieStorage, initialSize - 1),
                () -> Assertions.assertFalse(remainingMovies.contains(deletedMovie), "Удаленный фильм не должен оставаться в хранилище"),
                () -> Assertions.assertThrows(RuntimeException.class, () -> movieApiClient.getById(deletedMovie.id())),
                () -> Assertions.assertEquals(randomMovie, deletedMovie));
    }
}