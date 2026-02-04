package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;

public class MoviesHandler extends BaseHttpHandler {
    private final MoviesStore store;
    private final Gson gson = new Gson();

    public MoviesHandler(MoviesStore store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();

        if (method.equalsIgnoreCase("GET")) {
            String json = gson.toJson(store.getAll());
            sendJson(ex, 200, json);
            return;
        }

        if (method.equalsIgnoreCase("POST")) {

            try {

                String body = readBody(ex).trim();
                if (body.isEmpty()) {
                    sendError(ex, 400, "Пустое тело запроса");
                    return;
                }

                NewMovieRequest req = gson.fromJson(body, NewMovieRequest.class);
                if (req == null || req.getTitle() == null || req.getTitle().isBlank()
                        || req.getGenre().isBlank() || req.getGenre() == null || req.getYear() <= 0) {
                    sendError(ex, 400, "Введены некорректные данные");
                    return;
                }

                Movie created = store.add(req.getTitle().trim(), req.getYear(), req.getGenre().trim());
                sendJson(ex, 201, gson.toJson(created));
                return;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        sendError(ex, 405, "Метод не поддерживается");
    }
}