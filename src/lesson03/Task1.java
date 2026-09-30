package lesson03;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Task1 {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();

        CompletableFuture<HttpResponse<String>> postRequest =
                apiClient.get("https://jsonplaceholder.typicode.com/posts/1");
        CompletableFuture<HttpResponse<String>> userRequest =
                apiClient.get("https://jsonplaceholder.typicode.com/users/1");
        CompletableFuture<HttpResponse<String>> todoRequest =
                apiClient.get("https://jsonplaceholder.typicode.com/todos/1");

        CompletableFuture.allOf(postRequest, userRequest, todoRequest).join();

        printResponse("Публикация", postRequest.join());
        printResponse("Пользователь", userRequest.join());
        printResponse("Задача", todoRequest.join());
    }

    private static void printResponse(String title, HttpResponse<String> response) {
        System.out.printf("%n%s (HTTP %d):%n%s%n", title, response.statusCode(), response.body());
    }
}
