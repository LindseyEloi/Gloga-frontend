package com.centremedical.client.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class IconeApplication {

    private IconeApplication() {}

    /** Icône "croix médicale" (cercle + croix blanche), dessinée en code. */
    public static Image genererIcone(int taille) {
        BufferedImage image = new BufferedImage(taille, taille, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Theme.PRIMAIRE);
        g2.fillOval(0, 0, taille, taille);

        g2.setColor(Color.WHITE);
        int epaisseur = Math.max(2, taille / 4);
        int marge = taille / 5;
        g2.fillRoundRect((taille - epaisseur) / 2, marge, epaisseur, taille - 2 * marge, 4, 4);
        g2.fillRoundRect(marge, (taille - epaisseur) / 2, taille - 2 * marge, epaisseur, 4, 4);

        g2.dispose();
        return image;
    }
}