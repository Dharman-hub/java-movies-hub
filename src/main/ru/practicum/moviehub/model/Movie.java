package ru.practicum.moviehub.model;

public class Movie {
    private final long id;
    private final String title;
    private final int year;
    private final String genre;

    public Movie(long id, String title, int year, String genre) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genre = genre;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }
}