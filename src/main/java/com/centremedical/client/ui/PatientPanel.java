package com.centremedical.client.ui;

import com.centremedical.client.model.Patient;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.PatientApiService;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {

    private final PatientApiService service = new PatientApiService();

    private final JTextField champCode = ComposantsUI.creerChamp(10);
    private final JTextField champNom = ComposantsUI.creerChamp(15);
    private final JTextField champPrenom = ComposantsUI.creerChamp(15);
    private final JComboBox<String> champSexe = new JComboBox<>(new String[]{"M", "F"});
    private final JTextField champAdresse = ComposantsUI.creerChamp(20);

    private final JTextField champRecherche = ComposantsUI.creerChamp(15);
    private final JRadioButton radioParCode = new JRadioButton("Par code", true);
    private final JRadioButton radioParNom = new JRadioButton("Par nom");

    private final JLabel labelMode = ComposantsUI.creerBadgeMode("Nouveau patient", IconesUI.plus(16, Color.WHITE), Theme.PRIMAIRE);
    private final BoutonArrondi btnEnregistrer = new BoutonArrondi("Ajouter", Theme.PRIMAIRE, Color.WHITE);
    private final BoutonArrondi btnSupprimer = new BoutonArrondi("Supprimer", IconesUI.corbeille(16, Color.WHITE), Theme.DANGER, Color.WHITE);
    private final BoutonArrondi btnNouveau = new BoutonArrondi("Nouveau", IconesUI.plus(16, Color.WHITE), Theme.ACCENT, Color.WHITE);
    private final BoutonArrondi btnActualiser = new BoutonArrondi("Actualiser", IconesUI.actualiser(16, Color.WHITE), Theme.GRIS, Color.WHITE);
    private final BoutonArrondi btnRechercher = new BoutonArrondi("Rechercher", IconesUI.recherche(16, Color.WHITE), Theme.PRIMAIRE_FONCE, Color.WHITE);
    private final BoutonArrondi btnReinitialiser = new BoutonArrondi("Réinitialiser", Theme.GRIS, Color.WHITE);

    private final DefaultTableModel modeleTable = new DefaultTableModel(
            new Object[]{"Code", "Nom", "Prénom", "Sexe", "Adresse"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(modeleTable);

    private boolean enModification = false;

    public PatientPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.FOND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel nord = new JPanel();
        nord.setLayout(new BoxLayout(nord, BoxLayout.Y_AXIS));
        nord.setOpaque(false);
        // === BANDEAU TITRE PERSONNALISÉ (aligné à gauche) ===
        JPanel panneauTitre = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panneauTitre.setOpaque(false);
        panneauTitre.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel texteBandeau = new JPanel();
        texteBandeau.setLayout(new BoxLayout(texteBandeau, BoxLayout.Y_AXIS));
        texteBandeau.setOpaque(false);

        JLabel titre = new JLabel("Gestion des patients");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sousTitre = new JLabel("Ajoutez, modifiez, supprimez ou recherchez un patient");
        sousTitre.setFont(Theme.POLICE_NORMALE);
        sousTitre.setForeground(Theme.TEXTE_CLAIR);
        sousTitre.setAlignmentX(Component.LEFT_ALIGNMENT);

        texteBandeau.add(titre);
        texteBandeau.add(Box.createVerticalStrut(2));
        texteBandeau.add(sousTitre);
        panneauTitre.add(texteBandeau);
        nord.add(panneauTitre);
// ==================================================
        nord.add(construireFormulaire());
        nord.add(Box.createVerticalStrut(12));
        nord.add(construireBarreRecherche());
        add(nord, BorderLayout.NORTH);

        ComposantsUI.styliserTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(Theme.BORDURE, 1, true));
        add(scroll, BorderLayout.CENTER);

        add(construireBoutons(), BorderLayout.SOUTH);

        ButtonGroup groupe = new ButtonGroup();
        groupe.add(radioParCode);
        groupe.add(radioParNom);
        radioParCode.setOpaque(false);
        radioParNom.setOpaque(false);
        radioParCode.setFocusable(false);
        radioParNom.setFocusable(false);
        radioParCode.setFont(Theme.POLICE_NORMALE);
        radioParNom.setFont(Theme.POLICE_NORMALE);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int ligne = table.getSelectedRow();
            if (ligne >= 0) {
                passerEnModeModification(
                        String.valueOf(modeleTable.getValueAt(ligne, 0)),
                        String.valueOf(modeleTable.getValueAt(ligne, 1)),
                        String.valueOf(modeleTable.getValueAt(ligne, 2)),
                        String.valueOf(modeleTable.getValueAt(ligne, 3)),
                        String.valueOf(modeleTable.getValueAt(ligne, 4)));
            }
        });

        for (JTextField champ : new JTextField[]{champCode, champNom, champPrenom, champAdresse}) {
            champ.addActionListener(e -> enregistrer());
        }
        champRecherche.addActionListener(e -> rechercher());

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
        c.weightx = 1.0; // Permet à tous les champs de s'étendre uniformément

        c.gridx = 0; c.gridy = 0; champs.add(ComposantsUI.creerLabelChamp("Code"), c);
        c.gridx = 1; champs.add(ComposantsUI.creerLabelChamp("Nom"), c);
        c.gridx = 2; champs.add(ComposantsUI.creerLabelChamp("Prénom"), c);
        c.gridx = 3; champs.add(ComposantsUI.creerLabelChamp("Sexe"), c);
        c.gridx = 4; champs.add(ComposantsUI.creerLabelChamp("Adresse"), c);

        c.gridy = 1;
        c.gridx = 0; champs.add(champCode, c);
        c.gridx = 1; champs.add(champNom, c);
        c.gridx = 2; champs.add(champPrenom, c);
        c.gridx = 3;
        // Styliser le JComboBox pour qu'il ait la même apparence
        champSexe.setPreferredSize(new Dimension(80, champCode.getPreferredSize().height));
        champSexe.setFont(Theme.POLICE_NORMALE);
        champSexe.setFocusable(false);
        champSexe.setBackground(Color.WHITE);
        champSexe.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDURE, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        champs.add(champSexe, c);
        c.gridx = 4; champs.add(champAdresse, c);

        carte.add(champs, BorderLayout.CENTER);
        return carte;
    }

    private JPanel construireBarreRecherche() {
        JPanel carte = ComposantsUI.creerCarte();
        carte.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        carte.add(ComposantsUI.creerLabelChamp("Recherche :"));
        carte.add(champRecherche);
        carte.add(radioParCode);
        carte.add(radioParNom);

        btnRechercher.addActionListener(e -> rechercher());
        btnReinitialiser.addActionListener(e -> {
            champRecherche.setText("");
            rafraichir();
        });
        carte.add(btnRechercher);
        carte.add(btnReinitialiser);
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
        champSexe.setSelectedIndex(0);
        champAdresse.setText("");
        champCode.setEditable(true);
        champCode.setBackground(Color.WHITE);
        table.clearSelection();

        labelMode.setText("Nouveau patient");
        labelMode.setIcon(IconesUI.plus(16, Color.WHITE));
        labelMode.setBackground(Theme.PRIMAIRE);
        btnEnregistrer.setText("Ajouter");
        btnSupprimer.setEnabled(false);
        champNom.requestFocusInWindow();
    }

    private void passerEnModeModification(String code, String nom, String prenom, String sexe, String adresse) {
        enModification = true;
        champCode.setText(code);
        champNom.setText(nom);
        champPrenom.setText(prenom);
        champSexe.setSelectedItem(sexe);
        champAdresse.setText(adresse);
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
            Patient patient = new Patient(champCode.getText().trim(), champNom.getText().trim(),
                    champPrenom.getText().trim(), String.valueOf(champSexe.getSelectedItem()), champAdresse.getText().trim());
            if (modification) {
                service.modifier(patient.getCodepat(), patient);
            } else {
                service.creer(patient);
            }
            passerEnModeAjout();
            rafraichir();
            afficherSucces(modification ? "Patient modifié avec succès !" : "Patient ajouté avec succès !");
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void supprimer() {
        if (!enModification) return;
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Confirmer la suppression du patient " + champCode.getText() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        try {
            service.supprimer(champCode.getText().trim());
            passerEnModeAjout();
            rafraichir();
            afficherSucces("Patient supprimé avec succès !");
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void rechercher() {
        if (champRecherche.getText().isBlank()) {
            rafraichir();
            return;
        }
        try {
            remplirTable(service.rechercher(champRecherche.getText().trim(), radioParCode.isSelected()));
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void rafraichir() {
        try {
            remplirTable(service.listerTous());
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void remplirTable(List<Patient> patients) {
        modeleTable.setRowCount(0);
        for (Patient p : patients) {
            modeleTable.addRow(new Object[]{p.getCodepat(), p.getNom(), p.getPrenom(), p.getSexe(), p.getAdresse()});
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