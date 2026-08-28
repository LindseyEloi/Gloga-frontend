package com.centremedical.client.model;

import java.time.LocalDate;

/**
 * Version "aplatie" cote client de l'entite VISITER.
 * Le backend renvoie un objet imbrique (id / medecin / patient) ;
 * on le convertit ici en champs simples faciles a afficher dans une JTable.
 */
public class Visiter {
    private String codemed;
    private String codepat;
    private LocalDate date;
    private String nomMedecin;
    private String nomPatient;

    public Visiter() {
    }

    public Visiter(String codemed, String codepat, LocalDate date, String nomMedecin, String nomPatient) {
        this.codemed = codemed;
        this.codepat = codepat;
        this.date = date;
        this.nomMedecin = nomMedecin;
        this.nomPatient = nomPatient;
    }

    public String getCodemed() { return codemed; }
    public void setCodemed(String codemed) { this.codemed = codemed; }

    public String getCodepat() { return codepat; }
    public void setCodepat(String codepat) { this.codepat = codepat; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNomMedecin() { return nomMedecin; }
    public void setNomMedecin(String nomMedecin) { this.nomMedecin = nomMedecin; }

    public String getNomPatient() { return nomPatient; }
    public void setNomPatient(String nomPatient) { this.nomPatient = nomPatient; }
}
