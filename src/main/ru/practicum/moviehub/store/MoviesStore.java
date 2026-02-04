package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MoviesStore {
    private final Map<Long, Movie> movies = new LinkedHashMap<>();
    private long nextId = 1;

    public List<Movie> getAll() {
        return new ArrayList<>(movies.values());
    }

    public Movie add(String title, int year, String genre) {
        long id = nextId++;
        Movie movie = new Movie(id, title, year, genre);
        movies.put(id, movie);
        return movie;
    }

    public Optional<Movie> findById(long id) {
        return Optional.ofNullable(movies.get(id));
    }

    public boolean deleteById(long id) {
        return movies.remove(id) != null;
    }

    public void clear() {
        movies.clear();
        nextId = 1;
    }
}