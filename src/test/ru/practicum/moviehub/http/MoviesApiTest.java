package ru.practicum.moviehub.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.store.MoviesStore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesApiTest {
    private static final String BASE = "http://localhost:8080";

    private static MoviesServer server;
    private static HttpClient client;
    private static MoviesStore store;

    @BeforeAll
    static void beforeAll() {
        store = new MoviesStore();
        server = new MoviesServer(store, 8080);
        server.start();

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @BeforeEach
    void beforeEach() {
        store.clear();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(200, resp.statusCode(), "GET /movies должен вернуть 200");

        String contentTypeHeaderValue =
                resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json; charset=UTF-8", contentTypeHeaderValue,
                "Content-Type должен содержать формат данных и кодировку");

        String body = resp.body().trim();
        assertTrue(body.startsWith("[") && body.endsWith("]"),
                "Ожидается JSON-массив");
    }

    @Test
    void getMovies_whenNotEmpty_returnsMoviesArray() throws Exception {
        store.add("Oppenheimer", 2023, "Biopic");
        store.add("Barbie", 2023, "Comedy");

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(200, resp.statusCode(), "GET /movies должен вернуть 200");

        String body = resp.body();

        assertTrue(body.startsWith("[") && body.endsWith("]"),
                "Ожидается JSON-массив");
        assertTrue(body.contains("\"title\":\"Oppenheimer\""));
        assertTrue(body.contains("\"year\":2023"));
        assertTrue(body.contains("\"genre\":\"Comedy\""));
    }

    @Test
    void postMovies_createsMovie() throws Exception {
        String json = """
{
   "title": "Oppenheimer",
   "year": 2023,
   "genre": "Biopic"
}
""";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(201, resp.statusCode(), "GET /movies должен вернуть 201");

        String contentType = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json; charset=UTF-8", contentType);

        String body = resp.body();
        assertTrue(body.contains("\"id\":"));
        assertTrue(body.contains("\"title\":\"Oppenheimer\""));
        assertTrue(body.contains("\"year\":2023"));
        assertTrue(body.contains("\"genre\":\"Biopic\""));
    }

    @Test
    void postMovies_whenEmptyBody() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("", StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(400, resp.statusCode(), "Пустое тело должно давать 400");

        String contentType = resp.headers().firstValue("Content-Type").orElse("");
        assertEquals("application/json; charset=UTF-8", contentType);

        String body = resp.body();
        assertTrue(body.contains("\"statusCode\":400"));
        assertTrue(body.contains("\"error\""));
    }

    @Test
    void getMovieById_whenExists() throws Exception {
        long id = store.add("Oppenheimer", 2023, "Biopic").getId();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/" + id))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(200, resp.statusCode());

        String body = resp.body();
        assertTrue(body.contains("\"id\":" + id));
        assertTrue(body.contains("\"title\":\"Oppenheimer\""));
        assertTrue(body.contains("\"year\":2023"));
        assertTrue(body.contains("\"genre\":\"Biopic\""));
    }

    @Test
    void getMovieById_whenNotFound() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/999"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(404, resp.statusCode());

        String body = resp.body();
        assertTrue(body.contains("\"statusCode\":404"));
        assertTrue(body.contains("\"error\""));
    }

    @Test
    void deleteMovieById_whenExists() throws Exception {
        long id = store.add("Oppenheimer", 2023, "Biopic").getId();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/" + id))
                .DELETE()
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(204, resp.statusCode());
    }

    @Test
    void deleteMovieById_whenNotFound() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies/99999"))
                .DELETE()
                .build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        assertEquals(404, resp.statusCode());

        String body = resp.body();
        assertTrue(body.contains("\"statusCode\":404"));
        assertTrue(body.contains("\"error\""));
    }
}