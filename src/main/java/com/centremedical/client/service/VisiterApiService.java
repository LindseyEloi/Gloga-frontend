package com.centremedical.client.service;

import com.centremedical.client.model.Visiter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VisiterApiService {

    private final ApiClient apiClient = new ApiClient();

    public List<Visiter> listerToutes() throws IOException, InterruptedException, ApiClient.ApiException {
        String json = apiClient.get("/visites");
        JsonArray tableau = JsonParser.parseString(json).getAsJsonArray();
        List<Visiter> resultat = new ArrayList<>();
        for (JsonElement element : tableau) {
            resultat.add(depuisJson(element.getAsJsonObject()));
        }
        return resultat;
    }

    public Visiter creer(String codemed, String codepat, LocalDate date) throws IOException, InterruptedException, ApiClient.ApiException {
        String corps = construireCorps(codemed, codepat, date);
        String json = apiClient.post("/visites", corps);
        return depuisJson(JsonParser.parseString(json).getAsJsonObject());
    }

    public Visiter modifier(String codemed, String codepat, LocalDate ancienneDate, LocalDate nouvelleDate)
            throws IOException, InterruptedException, ApiClient.ApiException {
        String corps = construireCorps(codemed, codepat, nouvelleDate);
        String json = apiClient.put("/visites/" + codemed + "/" + codepat + "/" + ancienneDate, corps);
        return depuisJson(JsonParser.parseString(json).getAsJsonObject());
    }

    public void supprimer(String codemed, String codepat, LocalDate date) throws IOException, InterruptedException, ApiClient.ApiException {
        apiClient.delete("/visites/" + codemed + "/" + codepat + "/" + date);
    }

    private String construireCorps(String codemed, String codepat, LocalDate date) {
        JsonObject objet = new JsonObject();
        objet.addProperty("codemed", codemed);
        objet.addProperty("codepat", codepat);
        objet.addProperty("date", date.toString()); // yyyy-MM-dd
        return objet.toString();
    }

    private Visiter depuisJson(JsonObject objet) {
        JsonObject id = objet.getAsJsonObject("id");
        JsonObject medecin = objet.getAsJsonObject("medecin");
        JsonObject patient = objet.getAsJsonObject("patient");

        String codemed = id.get("codemed").getAsString();
        String codepat = id.get("codepat").getAsString();
        LocalDate date = LocalDate.parse(id.get("date").getAsString());

        String nomMedecin = medecin != null ? medecin.get("nom").getAsString() : codemed;
        String nomPatient = patient != null ? patient.get("nom").getAsString() : codepat;

        return new Visiter(codemed, codepat, date, nomMedecin, nomPatient);
    }
}
