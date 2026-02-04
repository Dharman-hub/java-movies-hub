package ru.practicum.moviehub.http;

public class NewMovieRequest {
    private String title;
    private int year;
    private String genre;

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