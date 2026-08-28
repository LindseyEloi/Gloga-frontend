package com.centremedical.client.ui;

import com.centremedical.client.model.Medecin;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.MedecinApiService;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedecinPanel extends JPanel {

    private final MedecinApiService service = new MedecinApiService();

    private final JTextField champCode = ComposantsUI.creerChamp(10);
    private final JTextField champNom = ComposantsUI.creerChamp(15);
    private final JTextField champPrenom = ComposantsUI.creerChamp(15);
    private final JTextField champGrade = ComposantsUI.creerChamp(15);

    private final JLabel labelMode = ComposantsUI.creerBadgeMode("Nouveau médecin", IconesUI.plus(16, Color.WHITE), Theme.PRIMAIRE);
    private final BoutonArrondi btnEnregistrer = new BoutonArrondi("Ajouter", Theme.PRIMAIRE, Color.WHITE);
    private final BoutonArrondi btnSupprimer = new BoutonArrondi("Supprimer", IconesUI.corbeille(16, Color.WHITE), Theme.DANGER, Color.WHITE);
    private final BoutonArrondi btnNouveau = new BoutonArrondi("Nouveau", IconesUI.plus(16, Color.WHITE), Theme.ACCENT, Color.WHITE);
    private final BoutonArrondi btnActualiser = new BoutonArrondi("Actualiser", IconesUI.actualiser(16, Color.WHITE), Theme.GRIS, Color.WHITE);

    private final DefaultTableModel modeleTable = new DefaultTableModel(
            new Object[]{"Code", "Nom", "Prénom", "Grade"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(modeleTable);

    private boolean enModification = false;

    public MedecinPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.FOND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel nord = new JPanel(new BorderLayout());
        nord.setOpaque(false);
        nord.add(ComposantsUI.creerBandeau("Gestion des médecins", "Ajoutez, modifiez ou supprimez les médecins du centre"), BorderLayout.NORTH);
        nord.add(construireFormulaire(), BorderLayout.CENTER);
        add(nord, BorderLayout.NORTH);

        ComposantsUI.styliserTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(Theme.BORDURE, 1, true));
        add(scroll, BorderLayout.CENTER);

        add(construireBoutons(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int ligne = table.getSelectedRow();
            if (ligne >= 0) {
                passerEnModeModification(
                        String.valueOf(modeleTable.getValueAt(ligne, 0)),
                        String.valueOf(modeleTable.getValueAt(ligne, 1)),
                        String.valueOf(modeleTable.getValueAt(ligne, 2)),
                        String.valueOf(modeleTable.getValueAt(ligne, 3)));
            }
        });

        for (JTextField champ : new JTextField[]{champCode, champNom, champPrenom, champGrade}) {
            champ.addActionListener(e -> enregistrer());
        }

        passerEnModeAjout();
        rafraichir();
    }

    private JPanel construireFormulaire() {
        JPanel carte = ComposantsUI.creerCarte();
        carte.setLayout(new BorderLayout(0, 12));

        JPanel ligneBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ligneBadge.setOpaque(false);
        ligneBadge.add(labelMode);
        carte.add(ligneBadge, BorderLayout.NORTH);

        JPanel champs = new JPanel(new GridBagLayout());
        champs.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; champs.add(ComposantsUI.creerLabelChamp("Code"), c);
        c.gridx = 1; champs.add(ComposantsUI.creerLabelChamp("Nom"), c);
        c.gridx = 2; champs.add(ComposantsUI.creerLabelChamp("Prénom"), c);
        c.gridx = 3; champs.add(ComposantsUI.creerLabelChamp("Grade"), c);

        c.gridy = 1;
        c.gridx = 0; champs.add(champCode, c);
        c.gridx = 1; champs.add(champNom, c);
        c.gridx = 2; champs.add(champPrenom, c);
        c.gridx = 3; champs.add(champGrade, c);

        carte.add(champs, BorderLayout.CENTER);
        return carte;
    }

    private JPanel construireBoutons() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panneau.setOpaque(false);

        btnEnregistrer.addActionListener(e -> enregistrer());
        btnSupprimer.addActionListener(e -> supprimer());
        btnNouveau.addActionListener(e -> passerEnModeAjout());
        btnActualiser.addActionListener(e -> rafraichir());

        panneau.add(btnNouveau);
        panneau.add(btnEnregistrer);
        panneau.add(btnSupprimer);
        panneau.add(btnActualiser);
        return panneau;
    }

    private void passerEnModeAjout() {
        enModification = false;
        champCode.setText("");
        champNom.setText("");
        champPrenom.setText("");
        champGrade.setText("");
        champCode.setEditable(true);
        champCode.setBackground(Color.WHITE);
        table.clearSelection();

        labelMode.setText("Nouveau médecin");
        labelMode.setIcon(IconesUI.plus(16, Color.WHITE));
        labelMode.setBackground(Theme.PRIMAIRE);
        btnEnregistrer.setText("Ajouter");
        btnSupprimer.setEnabled(false);
        champNom.requestFocusInWindow();
    }

    private void passerEnModeModification(String code, String nom, String prenom, String grade) {
        enModification = true;
        champCode.setText(code);
        champNom.setText(nom);
        champPrenom.setText(prenom);
        champGrade.setText(grade);
        champCode.setEditable(false);
        champCode.setBackground(new Color(240, 240, 240));

        labelMode.setText("Modification — " + code);
        labelMode.setIcon(IconesUI.crayon(16, Color.WHITE));
        labelMode.setBackground(Theme.MODIFICATION);
        btnEnregistrer.setText("Enregistrer les modifications");
        btnSupprimer.setEnabled(true);
        champNom.requestFocusInWindow();
    }

    private void enregistrer() {
        if (champCode.getText().isBlank() || champNom.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Le code et le nom sont obligatoires.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean modification = enModification;
        try {
            Medecin medecin = new Medecin(champCode.getText().trim(), champNom.getText().trim(),
                    champPrenom.getText().trim(), champGrade.getText().trim());
            if (modification) {
                service.modifier(medecin.getCodemed(), medecin);
            } else {
                service.creer(medecin);
            }
            passerEnModeAjout();
            rafraichir();
            afficherSucces(modification ? "Médecin modifié avec succès !" : "Médecin ajouté avec succès !");
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void supprimer() {
        if (!enModification) return;
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Confirmer la suppression du médecin " + champCode.getText() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        try {
            service.supprimer(champCode.getText().trim());
            passerEnModeAjout();
            rafraichir();
            afficherSucces("Médecin supprimé avec succès !");
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void rafraichir() {
        try {
            List<Medecin> medecins = service.listerTous();
            modeleTable.setRowCount(0);
            for (Medecin m : medecins) {
                modeleTable.addRow(new Object[]{m.getCodemed(), m.getNom(), m.getPrenom(), m.getGrade()});
            }
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void afficherSucces(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès",
                JOptionPane.PLAIN_MESSAGE, IconesUI.coche(32, new Color(34, 197, 94)));
    }

    private void afficherErreur(Exception ex) {
        String message = ex instanceof ApiClient.ApiException ? ex.getMessage() : "Erreur de connexion à l'API : " + ex.getMessage();
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}