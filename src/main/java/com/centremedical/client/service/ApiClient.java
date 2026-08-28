package com.centremedical.client.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Enveloppe simple autour de java.net.http.HttpClient pour dialoguer
 * avec l'API REST du backend Spring Boot.
 */
public class ApiClient {

    // Adaptez le port si vous changez server.port dans application.properties
    public static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String get(String path) throws IOException, InterruptedException, ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return envoyer(request);
    }

    public String post(String path, String jsonBody) throws IOException, InterruptedException, ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return envoyer(request);
    }

    public String put(String path, String jsonBody) throws IOException, InterruptedException, ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return envoyer(request);
    }

    public void delete(String path) throws IOException, InterruptedException, ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        envoyer(request);
    }

    private String envoyer(HttpRequest request) throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return response.body();
        }
        throw new ApiException("Erreur HTTP " + status + " : " + response.body());
    }

    /** Exception levée en cas de réponse non 2xx de l'API. */
    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
    }
}
