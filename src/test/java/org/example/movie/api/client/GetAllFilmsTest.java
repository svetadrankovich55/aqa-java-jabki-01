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

public class GetAllFilmsTest {

    private MovieApiClient movieApiClient;

    @BeforeEach
    public void setUp() {
        movieApiClient = new MovieApiClient();
    }

    @Test
    @DisplayName("Успешное получение всех фильмов")
    void getAllFilmsTest() {
        Set<Movie> movies = movieApiClient.getAllFilms();

        Assertions.assertEquals(3, movies.size());
        Assertions.assertTrue(movies.stream().anyMatch(movie ->
                movie.id().equals(1L) &&
                        movie.title().equals("Пролетая над гнездом кукушки") &&
                        movie.durationInMinutes().equals(100L) &&
                        movie.genres().equals(Set.of(Genre.DRAMA)) &&
                        movie.releaseDate().equals(LocalDate.of(1990, 10, 12))));

        Assertions.assertTrue(movies.stream().anyMatch(movie ->
                movie.id().equals(2L) &&
                        movie.title().equals("Зеленая миля") &&
                        movie.durationInMinutes().equals(132L) &&
                        movie.genres().equals(Set.of(Genre.DRAMA, Genre.THRILLER)) &&
                        movie.releaseDate().equals(LocalDate.of(1999, 5, 3))));

        Assertions.assertTrue(movies.stream().anyMatch(movie ->
                movie.id().equals(3L) &&
                        movie.title().equals("Оно") &&
                        movie.durationInMinutes().equals(142L) &&
                        movie.genres().equals(Set.of(Genre.HORROR, Genre.THRILLER)) &&
                        movie.releaseDate().equals(LocalDate.of(2017, 9, 5))));
    }
}