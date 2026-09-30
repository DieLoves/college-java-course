package lesson03;

import java.net.http.HttpResponse;

public class Task2 {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();

        HttpResponse<String> response = apiClient
                .getWithRetry("https://httpbin.org/status/500")
                .join();

        System.out.println("Итоговый статус: " + response.statusCode());
    }
}
