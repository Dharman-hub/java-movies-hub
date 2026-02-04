package ru.practicum.moviehub.http;


import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.util.Optional;

public class MovieByIdHandler extends BaseHttpHandler {
    private final MoviesStore store;

    public MovieByIdHandler(MoviesStore store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();

        Long id = parseId(ex);
        if (id == null) {
            sendError(ex, 400, "Некорректный идентификатор фильма");
            return;
        }

        if (method.equalsIgnoreCase("GET")) {
            Optional<Movie> movie = store.findById(id);
            if (movie.isEmpty()) {
                sendError(ex, 404, "Фильм не найден");
                return;
            }
            sendJson(ex, 200, gson.toJson(movie.get()));
            return;
        }

        if (method.equalsIgnoreCase("DELETE")) {
            boolean deleted = store.deleteById(id);
            if (!deleted) {
                sendError(ex, 404, "Фильм не найден");
                return;
            }
            sendNoContent(ex);
            return;
        }

        sendError(ex, 405, "Метод не поддерживается");
    }

    private Long parseId(HttpExchange ex) {
        String path = ex.getRequestURI().getPath();
        String prefix = "/movies/";

        if (!path.startsWith(prefix) || path.length() <= prefix.length()) {
            return null;
        }

        String idString = path.substring(prefix.length());

        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            return null;
        }

    }
}
