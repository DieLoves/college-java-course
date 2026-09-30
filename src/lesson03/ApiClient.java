package lesson03;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_SECONDS = 1;

    private final HttpClient httpClient;

    public ApiClient() {
        this(HttpClient.newHttpClient());
    }

    public ApiClient(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    public CompletableFuture<HttpResponse<String>> get(String url) {
        HttpRequest request = createRequest(url)
                .GET()
                .build();

        return send(request);
    }

    public CompletableFuture<HttpResponse<String>> post(String url, String body) {
        HttpRequest request = createRequest(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        return send(request);
    }

    public CompletableFuture<HttpResponse<String>> put(String url, String body) {
        HttpRequest request = createRequest(url)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        return send(request);
    }

    public CompletableFuture<HttpResponse<String>> delete(String url) {
        HttpRequest request = createRequest(url)
                .DELETE()
                .build();

        return send(request);
    }

    public CompletableFuture<HttpResponse<String>> getWithRetry(String url) {
        HttpRequest request = createRequest(url)
                .GET()
                .build();

        return sendWithRetry(request, MAX_RETRIES);
    }

    private HttpRequest.Builder createRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url));
    }

    private CompletableFuture<HttpResponse<String>> send(HttpRequest request) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private CompletableFuture<HttpResponse<String>> sendWithRetry(HttpRequest request, int retriesLeft) {
        return send(request).thenCompose(response -> {
            if (isServerError(response.statusCode()) && retriesLeft > 0) {
                System.out.printf(
                        "Получен статус %d. Повтор через %d секунду, осталось попыток: %d%n",
                        response.statusCode(),
                        RETRY_DELAY_SECONDS,
                        retriesLeft
                );

                return delay().thenCompose(ignored -> sendWithRetry(request, retriesLeft - 1));
            }

            return CompletableFuture.completedFuture(response);
        });
    }

    private boolean isServerError(int statusCode) {
        return statusCode >= 500 && statusCode < 600;
    }

    private CompletableFuture<Void> delay() {
        return CompletableFuture.runAsync(
                () -> {
                },
                CompletableFuture.delayedExecutor(RETRY_DELAY_SECONDS, TimeUnit.SECONDS)
        );
    }
}
