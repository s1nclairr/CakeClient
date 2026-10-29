/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import fr.cake.client.SmoothTexture;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import net.minecraft.class_10799;
import net.minecraft.class_310;
import net.minecraft.class_332;

public final class CrosshairHud {
    public static final String[] NAMES = new String[]{"Classic", "Dot", "Ring", "Chevron", "Diamond", "Brackets", "T-Cross", "Orbit"};
    private static SmoothTexture texture;
    private static long revision;

    public static boolean visible() {
        class_310 mc = class_310.method_1551();
        return CakeClient.settings != null && CakeClient.settings.crosshairEnabled && mc.field_1724 != null && !mc.field_1724.method_7325() && mc.field_1690.method_31044().method_31034() && !mc.field_1690.field_1842;
    }

    public static void render(class_332 g) {
        if (!CrosshairHud.visible()) {
            return;
        }
        if (texture == null) {
            texture = new SmoothTexture("crosshair69", 256, 256);
        }
        if (revision != Settings.revision) {
            Settings s = CakeClient.settings;
            BufferedImage im = new BufferedImage(256, 256, 2);
            Graphics2D d = im.createGraphics();
            d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float r = (float)(18.0 + s.crosshairSize * 68.0);
            float gap = (float)(4.0 + s.crosshairGap * 36.0);
            float w = (float)(2.0 + s.crosshairThickness * 9.0);
            Path2D.Float p = new Path2D.Float();
            int style = s.crosshairStyle;
            if (style == 0 || style == 6) {
                for (int a = 0; a < 4; ++a) {
                    if (style == 6 && a == 3) continue;
                    double t = (double)a * Math.PI / 2.0;
                    p.moveTo(128.0 + Math.cos(t) * (double)gap, 128.0 + Math.sin(t) * (double)gap);
                    p.lineTo(128.0 + Math.cos(t) * ((double)gap + (double)r * 0.5), 128.0 + Math.sin(t) * ((double)gap + (double)r * 0.5));
                }
            } else if (style == 3) {
                p.moveTo(128.0f - r / 2.0f, 128.0f + r / 3.0f);
                p.lineTo(128.0f, 128.0f - r / 3.0f);
                p.lineTo(128.0f + r / 2.0f, 128.0f + r / 3.0f);
            } else if (style == 4) {
                p.moveTo(128.0f, 128.0f - r / 2.0f);
                p.lineTo(128.0f + r / 2.0f, 128.0f);
                p.lineTo(128.0f, 128.0f + r / 2.0f);
                p.lineTo(128.0f - r / 2.0f, 128.0f);
                p.closePath();
            } else if (style == 5) {
                for (int side : new int[]{-1, 1}) {
                    p.moveTo(128.0f + (float)side * (gap + 10.0f), 128.0f - r / 3.0f);
                    p.lineTo(128.0f + (float)side * gap, 128.0f - r / 3.0f);
                    p.lineTo(128.0f + (float)side * gap, 128.0f + r / 3.0f);
                    p.lineTo(128.0f + (float)side * (gap + 10.0f), 128.0f + r / 3.0f);
                }
            }
            Shape shape = style == 1 ? new Ellipse2D.Float(124.0f, 124.0f, 8.0f, 8.0f) : (style == 2 || style == 7 ? new Ellipse2D.Float(128.0f - r / 2.0f, 128.0f - r / 2.0f, r, r) : p);
            d.setColor(new Color(0, 0, 0, 210));
            d.setStroke(new BasicStroke(w + 4.0f, 1, 1));
            d.draw(shape);
            d.setColor(Color.getHSBColor((float)s.crosshairHue, 0.5f, 1.0f));
            d.setStroke(new BasicStroke(w, 1, 1));
            d.draw(shape);
            if (style == 1 || style == 7) {
                d.fill(new Ellipse2D.Float(124.0f, 124.0f, 8.0f, 8.0f));
            }
            d.dispose();
            texture.update(im);
            revision = Settings.revision;
        }
        g.method_25302(class_10799.field_56883, CrosshairHud.texture.id, g.method_51421() / 2 - 32, g.method_51443() / 2 - 32, 0.0f, 0.0f, 64, 64, 256, 256, 256, 256);
    }

    static {
        revision = -1L;
    }
}

