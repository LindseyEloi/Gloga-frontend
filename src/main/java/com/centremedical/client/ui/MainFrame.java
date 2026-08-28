package com.centremedical.client.ui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Gestion des Visites - Centre Médical");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setIconImage(IconeApplication.genererIcone(64));

        getContentPane().setBackground(Theme.FOND);
        setLayout(new BorderLayout());

        add(construireEnTete(), BorderLayout.NORTH);
        add(construireOnglets(), BorderLayout.CENTER);
    }

    private JPanel construireEnTete() {
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(Theme.PRIMAIRE);
        entete.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel icone = new JLabel(new ImageIcon(IconeApplication.genererIcone(40)));
        JLabel titre = new JLabel("   Centre Médical — Gestion des Visites");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Color.WHITE);

        JPanel gauche = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gauche.setOpaque(false);
        gauche.add(icone);
        gauche.add(titre);

        entete.add(gauche, BorderLayout.WEST);
        return entete;
    }

    private JTabbedPane construireOnglets() {
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(Theme.POLICE_SOUS_TITRE);
        onglets.setBackground(Theme.FOND);
        onglets.setForeground(Theme.TEXTE);
        // Supprime le rectangle pointillé de focus qui s'affichait sur l'onglet sélectionné
        onglets.setFocusable(false);

        onglets.addTab("Médecins", IconesUI.medecin(18, Theme.PRIMAIRE), new MedecinPanel());
        onglets.addTab("Patients", IconesUI.patient(18, Theme.PRIMAIRE), new PatientPanel());
        onglets.addTab("Visites", IconesUI.calendrier(18, Theme.PRIMAIRE), new VisiterPanel());
        return onglets;
    }
}