package com.centremedical.client.service;

import com.centremedical.client.model.Medecin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MedecinApiService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    public List<Medecin> listerTous() throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.get("/medecins");
        Type type = new TypeToken<List<Medecin>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public Medecin creer(Medecin medecin) throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.post("/medecins", gson.toJson(medecin));
        return gson.fromJson(json, Medecin.class);
    }

    public Medecin modifier(String codemed, Medecin medecin) throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.put("/medecins/" + codemed, gson.toJson(medecin));
        return gson.fromJson(json, Medecin.class);
    }

    public void supprimer(String codemed) throws IOException, InterruptedException, ApiClient.ApiException {
        apiClient.delete("/medecins/" + codemed);
    }
}
