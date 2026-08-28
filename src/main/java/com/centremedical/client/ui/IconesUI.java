package com.centremedical.client.ui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public final class IconesUI {

    private IconesUI() {}

    @FunctionalInterface
    private interface Dessinateur {
        void dessiner(Graphics2D g2, int taille);
    }

    private static Icon icone(int taille, Dessinateur dessinateur) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                dessinateur.dessiner(g2, taille);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return taille;
            }

            @Override
            public int getIconHeight() {
                return taille;
            }
        };
    }

    public static Icon plus(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.setStroke(new BasicStroke(Math.max(2f, t / 7f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int m = t / 4;
            g2.drawLine(t / 2, m, t / 2, t - m);
            g2.drawLine(m, t / 2, t - m, t / 2);
        });
    }

    public static Icon corbeille(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.setStroke(new BasicStroke(Math.max(1.5f, t / 9f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int hautCorps = t / 3;
            g2.drawLine(t / 6, hautCorps, t - t / 6, hautCorps);
            g2.drawLine(t / 3, t / 5, t - t / 3, t / 5);
            g2.drawRect(t / 4, hautCorps, t / 2, t - hautCorps - t / 8);
            g2.drawLine(t / 2, hautCorps + t / 10, t / 2, t - t / 6);
        });
    }

    public static Icon actualiser(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.setStroke(new BasicStroke(Math.max(1.5f, t / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int marge = t / 6;
            g2.drawArc(marge, marge, t - 2 * marge, t - 2 * marge, 40, 270);

            Path2D fleche = new Path2D.Double();
            double angle = Math.toRadians(40);
            int cx = t / 2, cy = t / 2, rayon = (t - 2 * marge) / 2;
            double px = cx + rayon * Math.cos(angle);
            double py = cy - rayon * Math.sin(angle);
            fleche.moveTo(px, py);
            fleche.lineTo(px - t / 4.5, py - t / 10.0);
            fleche.lineTo(px - t / 9.0, py + t / 5.0);
            fleche.closePath();
            g2.fill(fleche);
        });
    }

    public static Icon recherche(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.setStroke(new BasicStroke(Math.max(1.5f, t / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int diametre = (int) (t * 0.62);
            g2.draw(new Ellipse2D.Double(t / 8.0, t / 8.0, diametre, diametre));
            int depart = t / 8 + diametre - 2;
            g2.drawLine(depart, depart, t - t / 8, t - t / 8);
        });
    }

    public static Icon medecin(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.fillOval(0, 0, t, t);
            g2.setColor(Color.WHITE);
            int epaisseur = Math.max(2, t / 6);
            int marge = t / 4;
            g2.fillRoundRect((t - epaisseur) / 2, marge, epaisseur, t - 2 * marge, 3, 3);
            g2.fillRoundRect(marge, (t - epaisseur) / 2, t - 2 * marge, epaisseur, 3, 3);
        });
    }

    public static Icon patient(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            int diametreTete = t / 2;
            g2.fillOval((t - diametreTete) / 2, t / 10, diametreTete, diametreTete);
            Path2D corps = new Path2D.Double();
            corps.moveTo(t / 8.0, t);
            corps.curveTo(t / 8.0, t * 0.62, t * 0.15, t * 0.55, t / 2.0, t * 0.55);
            corps.curveTo(t * 0.85, t * 0.55, t - t / 8.0, t * 0.62, t - t / 8.0, t);
            corps.closePath();
            g2.fill(corps);
        });
    }

    public static Icon calendrier(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            int haut = t / 6;
            g2.fillRoundRect(0, haut, t, t - haut, 4, 4);
            g2.setColor(Color.WHITE);
            g2.fillRect(t / 12, haut, t - t / 6, t / 6);
            g2.setColor(couleur);
            g2.setStroke(new BasicStroke(Math.max(1.5f, t / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(t / 4, 0, t / 4, haut + t / 8);
            g2.drawLine(t - t / 4, 0, t - t / 4, haut + t / 8);
            g2.setColor(Color.WHITE);
            int taillePoint = Math.max(2, t / 10);
            for (int ligne = 0; ligne < 2; ligne++) {
                for (int col = 0; col < 3; col++) {
                    g2.fillRect(t / 6 + col * (t / 4), haut + t / 3 + ligne * (t / 4), taillePoint, taillePoint);
                }
            }
        });
    }

    public static Icon coche(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.fillOval(0, 0, t, t);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(Math.max(2f, t / 9f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D coche = new Path2D.Double();
            coche.moveTo(t * 0.28, t * 0.53);
            coche.lineTo(t * 0.44, t * 0.70);
            coche.lineTo(t * 0.74, t * 0.32);
            g2.draw(coche);
        });
    }
    public static Icon crayon(int taille, Color couleur) {
        return icone(taille, (g2, t) -> {
            g2.setColor(couleur);
            g2.setStroke(new BasicStroke(Math.max(1.5f, t / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double largeurPointe = t / 6.0;

            double x1 = t * 0.22, y1 = t * 0.82;
            double x2 = t * 0.72, y2 = t * 0.28;

            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

            double dx = x2 - x1, dy = y2 - y1;
            double longueur = Math.sqrt(dx * dx + dy * dy);
            double ux = dx / longueur, uy = dy / longueur;
            double nx = -uy, ny = ux;

            Path2D pointe = new Path2D.Double();
            pointe.moveTo(x1, y1);
            pointe.lineTo(x1 + ux * largeurPointe + nx * (largeurPointe / 2), y1 + uy * largeurPointe + ny * (largeurPointe / 2));
            pointe.lineTo(x1 + ux * largeurPointe - nx * (largeurPointe / 2), y1 + uy * largeurPointe - ny * (largeurPointe / 2));
            pointe.closePath();
            g2.fill(pointe);
        });
    }
}