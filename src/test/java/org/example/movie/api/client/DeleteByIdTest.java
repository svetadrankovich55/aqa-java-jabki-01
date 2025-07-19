package org.example.movie.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dpd.edu.MovieApiClient;
import ru.dpd.edu.model.Movie;

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

        int initialSize = movieApiClient.getAllFilms().size();
        Movie deletedMovie = movieApiClient.deleteById(1L);
        int resultMovieStorage = movieApiClient.getAllFilms().size();
        Set<Movie> remainingMovies = movieApiClient.getAllFilms();

        Assertions.assertAll(
                () -> Assertions.assertEquals(resultMovieStorage, initialSize - 1),
                () -> Assertions.assertFalse(remainingMovies.contains(deletedMovie), "Удаленный фильм не должен оставаться в хранилище"));
    }
}