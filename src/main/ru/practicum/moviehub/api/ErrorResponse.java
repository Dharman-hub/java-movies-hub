package ru.practicum.moviehub.api;


public class ErrorResponse {
    private final int statusCode;
    private final String error;

    public ErrorResponse(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}