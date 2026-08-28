package com.centremedical.client.ui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoutonArrondi extends JButton {

    private final Color couleurNormale;
    private final Color couleurSurvol;

    public BoutonArrondi(String texte, Color couleurFond, Color couleurTexte) {
        this(texte, null, couleurFond, couleurTexte);
    }

    public BoutonArrondi(String texte, Icon icone, Color couleurFond, Color couleurTexte) {
        super(texte, icone);
        this.couleurNormale = couleurFond;
        this.couleurSurvol = couleurFond.darker();

        setFont(Theme.POLICE_SOUS_TITRE);
        setForeground(couleurTexte);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(8);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean survole = getMousePosition() != null;
        g2.setColor(!isEnabled() ? new Color(200, 200, 200) : (survole ? couleurSurvol : couleurNormale));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}