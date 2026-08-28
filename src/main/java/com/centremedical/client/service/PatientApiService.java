package com.centremedical.client.service;

import com.centremedical.client.model.Patient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PatientApiService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    public List<Patient> listerTous() throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.get("/patients");
        Type type = new TypeToken<List<Patient>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public Patient creer(Patient patient) throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.post("/patients", gson.toJson(patient));
        return gson.fromJson(json, Patient.class);
    }

    public Patient modifier(String codepat, Patient patient) throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.put("/patients/" + codepat, gson.toJson(patient));
        return gson.fromJson(json, Patient.class);
    }

    public void supprimer(String codepat) throws IOException, InterruptedException, ApiClient.ApiException {
        apiClient.delete("/patients/" + codepat);
    }

    /** Recherche par code (si rechercheParCode = true) ou par nom (sinon). */
    public List<Patient> rechercher(String texte, boolean rechercheParCode) throws IOException, InterruptedException, ApiClient.ApiException {
        String parametre = rechercheParCode ? "code" : "nom";
        String valeur = URLEncoder.encode(texte, StandardCharsets.UTF_8);
        String json = apiClient.get("/patients/recherche?" + parametre + "=" + valeur);
        Type type = new TypeToken<List<Patient>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
