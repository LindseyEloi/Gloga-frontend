package com.centremedical.client.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

public final class ComposantsUI {

    private ComposantsUI() {}

    public static JPanel creerBandeau(String titre, String sousTitre) {
        JPanel bandeau = new JPanel();
        bandeau.setLayout(new BoxLayout(bandeau, BoxLayout.Y_AXIS));
        bandeau.setOpaque(false);
        bandeau.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(Theme.POLICE_TITRE);
        labelTitre.setForeground(Theme.TEXTE);

        JLabel labelSousTitre = new JLabel(sousTitre);
        labelSousTitre.setFont(Theme.POLICE_NORMALE);
        labelSousTitre.setForeground(Theme.TEXTE_CLAIR);

        bandeau.add(labelTitre);
        bandeau.add(Box.createVerticalStrut(2));
        bandeau.add(labelSousTitre);
        return bandeau;
    }

    public static JPanel creerCarte() {
        JPanel carte = new JPanel();
        carte.setBackground(Theme.CARTE);
        carte.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDURE, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return carte;
    }

    public static JTextField creerChamp(int colonnes) {
        JTextField champ = new JTextField(colonnes);
        champ.setFont(Theme.POLICE_NORMALE);
        champ.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDURE, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return champ;
    }

    public static JLabel creerLabelChamp(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(Theme.POLICE_LABEL);
        label.setForeground(Theme.TEXTE);
        return label;
    }

    /** Badge de mode (Ajout / Modification) avec une icône dessinée, sans emoji. */
    public static JLabel creerBadgeMode(String texte, Icon icone, Color couleurFond) {
        JLabel badge = new JLabel(texte, icone, SwingConstants.LEFT);
        badge.setIconTextGap(8);
        badge.setOpaque(true);
        badge.setBackground(couleurFond);
        badge.setForeground(Color.WHITE);
        badge.setFont(Theme.POLICE_SOUS_TITRE);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return badge;
    }

    public static void styliserTable(JTable table) {
        table.setFont(Theme.POLICE_TABLE);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 242, 241));
        table.setSelectionForeground(Theme.TEXTE);
        table.setFillsViewportHeight(true);

        JTableHeader entete = table.getTableHeader();
        entete.setFont(Theme.POLICE_TABLE_ENTETE);
        entete.setBackground(new Color(226, 232, 236)); // gris clair au lieu du bleu foncé
        entete.setForeground(Color.BLACK);               // titres bien visibles en noir
        entete.setPreferredSize(new Dimension(entete.getPreferredSize().width, 38));
        entete.setReorderingAllowed(false);

        // Certains Look & Feel (Windows notamment) ignorent setForeground() sur le header par défaut :
        // on force donc aussi un renderer d'en-tête personnalisé pour garantir le texte noir.
        entete.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                label.setFont(Theme.POLICE_TABLE_ENTETE);
                label.setForeground(Color.BLACK);
                label.setBackground(new Color(226, 232, 236));
                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMAIRE),
                        BorderFactory.createEmptyBorder(4, 12, 4, 12)));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
                }
                c.setForeground(Theme.TEXTE);
                setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
                return c;
            }
        });
    }

}