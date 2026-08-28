package com.centremedical.client.ui;

import com.centremedical.client.model.Medecin;
import com.centremedical.client.model.Patient;
import com.centremedical.client.model.Visiter;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.MedecinApiService;
import com.centremedical.client.service.PatientApiService;
import com.centremedical.client.service.VisiterApiService;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class VisiterPanel extends JPanel {

    private final VisiterApiService visiterService = new VisiterApiService();
    private final MedecinApiService medecinService = new MedecinApiService();
    private final PatientApiService patientService = new PatientApiService();

    private final JComboBox<Medecin> comboMedecin = new JComboBox<>();
    private final JComboBox<Patient> comboPatient = new JComboBox<>();
    private final JSpinner champDate = new JSpinner(new SpinnerDateModel());

    private final JLabel labelMode = ComposantsUI.creerBadgeMode("Nouvelle visite", IconesUI.plus(16, Color.WHITE), Theme.PRIMAIRE);
    private final BoutonArrondi btnEnregistrer = new BoutonArrondi("Ajouter", Theme.PRIMAIRE, Color.WHITE);
    private final BoutonArrondi btnSupprimer = new BoutonArrondi("Supprimer", IconesUI.corbeille(16, Color.WHITE), Theme.DANGER, Color.WHITE);
    private final BoutonArrondi btnNouveau = new BoutonArrondi("Nouveau", IconesUI.plus(16, Color.WHITE), Theme.ACCENT, Color.WHITE);
    private final BoutonArrondi btnActualiser = new BoutonArrondi("Actualiser", IconesUI.actualiser(16, Color.WHITE), Theme.GRIS, Color.WHITE);

    private boolean enModification = false;
    private String codemedSelectionne;
    private String codepatSelectionne;
    private LocalDate dateSelectionnee;

    private final DefaultTableModel modeleTable = new DefaultTableModel(
            new Object[]{"Code Médecin", "Médecin", "Code Patient", "Patient", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(modeleTable);

    public VisiterPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.FOND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // === STYLISATION DES COMPOSANTS ===
        styliserComboBox(comboMedecin);
        styliserComboBox(comboPatient);
        styliserDateSpinner(champDate);
        // =================================

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

        JLabel titre = new JLabel("Gestion des visites");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sousTitre = new JLabel("Planifiez, modifiez ou annulez une visite");
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
                String codemed = String.valueOf(modeleTable.getValueAt(ligne, 0));
                String codepat = String.valueOf(modeleTable.getValueAt(ligne, 2));
                LocalDate date = LocalDate.parse(String.valueOf(modeleTable.getValueAt(ligne, 4)));
                passerEnModeModification(codemed, codepat, date);
            }
        });

        chargerListesDeroulantes();
        passerEnModeAjout();
        rafraichir();
    }

    // === MÉTHODES DE STYLISATION ===
    private void styliserComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(Theme.POLICE_NORMALE);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(180, 32));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDURE, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        comboBox.setFocusable(false);
        // Supprimer les bordures pointillées
        UIManager.put("ComboBox.focusBorder", BorderFactory.createEmptyBorder());
        UIManager.put("ComboBox.border", BorderFactory.createEmptyBorder());
    }

    private void styliserDateSpinner(JSpinner spinner) {
        // Styliser l'éditeur du spinner
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editor;
            JFormattedTextField textField = dateEditor.getTextField();
            textField.setFont(Theme.POLICE_NORMALE);
            textField.setBackground(Color.WHITE);
            textField.setHorizontalAlignment(JTextField.LEFT);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.BORDURE, 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            textField.setPreferredSize(new Dimension(150, 32));
        }

        // Styliser le spinner lui-même
        spinner.setFont(Theme.POLICE_NORMALE);
        spinner.setPreferredSize(new Dimension(150, 32));
    }
    // =================================

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
        c.weightx = 1.0; // Permet l'étalement uniforme

        c.gridx = 0; c.gridy = 0;
        champs.add(ComposantsUI.creerLabelChamp("Médecin"), c);
        c.gridx = 1;
        champs.add(ComposantsUI.creerLabelChamp("Patient"), c);
        c.gridx = 2;
        champs.add(ComposantsUI.creerLabelChamp("Date de la visite"), c);

        c.gridy = 1;
        c.gridx = 0;
        champs.add(comboMedecin, c);
        c.gridx = 1;
        champs.add(comboPatient, c);
        c.gridx = 2;
        champs.add(champDate, c);

        carte.add(champs, BorderLayout.CENTER);
        return carte;
    }

    private JPanel construireBoutons() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panneau.setOpaque(false);

        btnEnregistrer.addActionListener(e -> enregistrer());
        btnSupprimer.addActionListener(e -> supprimer());
        btnNouveau.addActionListener(e -> passerEnModeAjout());
        btnActualiser.addActionListener(e -> {
            chargerListesDeroulantes();
            rafraichir();
        });

        panneau.add(btnNouveau);
        panneau.add(btnEnregistrer);
        panneau.add(btnSupprimer);
        panneau.add(btnActualiser);
        return panneau;
    }

    private void chargerListesDeroulantes() {
        try {
            comboMedecin.removeAllItems();
            for (Medecin m : medecinService.listerTous()) {
                comboMedecin.addItem(m);
            }
            comboPatient.removeAllItems();
            for (Patient p : patientService.listerTous()) {
                comboPatient.addItem(p);
            }
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void passerEnModeAjout() {
        enModification = false;
        codemedSelectionne = null;
        codepatSelectionne = null;
        dateSelectionnee = null;
        champDate.setValue(new Date());
        champDate.setEditor(new JSpinner.DateEditor(champDate, "dd/MM/yyyy"));
        champDate.setValue(new Date());
        comboMedecin.setEnabled(true);
        comboPatient.setEnabled(true);
        table.clearSelection();

        labelMode.setText("Nouvelle visite");
        labelMode.setIcon(IconesUI.plus(16, Color.WHITE));
        labelMode.setBackground(Theme.PRIMAIRE);
        btnEnregistrer.setText("Ajouter");
        btnSupprimer.setEnabled(false);
    }

    private void passerEnModeModification(String codemed, String codepat, LocalDate date) {
        enModification = true;
        codemedSelectionne = codemed;
        codepatSelectionne = codepat;
        dateSelectionnee = date;

        selectionnerDansCombo(comboMedecin, codemed);
        selectionnerDansCombo(comboPatient, codepat);
        champDate.setValue(dateVersDate(date));
        comboMedecin.setEnabled(false);
        comboPatient.setEnabled(false);

        labelMode.setText("Modification — " + codemed + " / " + codepat);
        labelMode.setIcon(IconesUI.crayon(16, Color.WHITE));
        labelMode.setBackground(Theme.MODIFICATION);
        btnEnregistrer.setText("Enregistrer les modifications");
        btnSupprimer.setEnabled(true);
    }

    private void enregistrer() {
        Medecin medecin = (Medecin) comboMedecin.getSelectedItem();
        Patient patient = (Patient) comboPatient.getSelectedItem();

        if (medecin == null || patient == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez d'abord créer au moins un médecin et un patient.",
                    "Données manquantes",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date = dateDuChamp();
        boolean modification = enModification;

        // === VALIDATION DE LA DATE (AJOUT ET MODIFICATION) ===
        LocalDate aujourdhui = LocalDate.now();
        if (date.isBefore(aujourdhui)) {
            String typeAction = modification ? "modification" : "ajout";
            JOptionPane.showMessageDialog(this,
                    "❌ Date invalide pour la " + typeAction + " !\n\n" +
                            "La date sélectionnée (" + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ") est dans le passé.\n" +
                            "La visite doit être planifiée aujourd'hui (" + aujourdhui.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ") ou plus tard.",
                    "Date invalide",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (modification) {
                visiterService.modifier(codemedSelectionne, codepatSelectionne, dateSelectionnee, date);
            } else {
                visiterService.creer(medecin.getCodemed(), patient.getCodepat(), date);
            }
            passerEnModeAjout();
            rafraichir();
            afficherSucces(modification ? "Visite modifiée avec succès !" : "Visite ajoutée avec succès !");
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void supprimer() {
        if (!enModification) return;
        int confirmation = JOptionPane.showConfirmDialog(this, "Confirmer la suppression de cette visite ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        try {
            visiterService.supprimer(codemedSelectionne, codepatSelectionne, dateSelectionnee);
            passerEnModeAjout();
            rafraichir();
            afficherSucces("Visite supprimée avec succès !");
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void rafraichir() {
        try {
            List<Visiter> visites = visiterService.listerToutes();
            modeleTable.setRowCount(0);
            for (Visiter v : visites) {
                modeleTable.addRow(new Object[]{
                        v.getCodemed(), v.getNomMedecin(), v.getCodepat(), v.getNomPatient(), v.getDate().toString()
                });
            }
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private LocalDate dateDuChamp() {
        Date date = (Date) champDate.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date dateVersDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void selectionnerDansCombo(JComboBox<?> combo, String code) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            String itemCode = (item instanceof Medecin) ? ((Medecin) item).getCodemed() : ((Patient) item).getCodepat();
            if (itemCode.equals(code)) {
                combo.setSelectedIndex(i);
                return;
            }
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