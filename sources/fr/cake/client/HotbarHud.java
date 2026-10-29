/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_1306
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_746
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import fr.cake.client.SmoothTexture;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.minecraft.class_10799;
import net.minecraft.class_1306;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_746;

public final class HotbarHud {
    public static final String[] STYLES = new String[]{"Glass", "Cartes", "Terminal", "Obsidian"};
    private static SmoothTexture background;
    private static SmoothTexture selection;
    private static SmoothTexture offhand;
    private static long revision;
    private static long last;
    private static double selected;

    public static boolean active() {
        class_310 mc = class_310.method_1551();
        return CakeClient.settings != null && CakeClient.settings.hotbarEnabled && mc.field_1724 != null && !mc.field_1724.method_7325() && !mc.field_1690.field_1842;
    }

    public static boolean replaces(String path) {
        return HotbarHud.active() && (path.equals("hud/hotbar") || path.equals("hud/hotbar_selection") || path.equals("hud/hotbar_offhand_left") || path.equals("hud/hotbar_offhand_right"));
    }

    public static void begin(class_332 g) {
        if (!HotbarHud.active()) {
            return;
        }
        Settings s = CakeClient.settings;
        g.method_51448().pushMatrix();
        float scale = (float)(0.8 + s.hotbarScale * 0.5);
        g.method_51448().translate((float)g.method_51421() / 2.0f, (float)g.method_51443());
        g.method_51448().scale(scale, scale);
        g.method_51448().translate((float)(-g.method_51421()) / 2.0f, (float)(-g.method_51443()) - (float)(s.hotbarLift * 32.0));
        if (background == null) {
            background = new SmoothTexture("hotbar_background", 784, 128);
            selection = new SmoothTexture("hotbar_selection", 112, 112);
            offhand = new SmoothTexture("hotbar_offhand", 112, 112);
            revision = -1L;
        }
        if (revision != Settings.revision) {
            revision = Settings.revision;
            HotbarHud.repaint();
        }
        long now = System.nanoTime();
        double dt = last == 0L ? 1.0 : Math.min(0.1, (double)(now - last) / 1.0E9);
        last = now;
        int slot = class_310.method_1551().field_1724.method_31548().method_67532();
        selected += ((double)slot - selected) * (1.0 - Math.exp(-dt * 20.0));
        int x = g.method_51421() / 2 - 90;
        int y = g.method_51443() - 22;
        g.method_25302(class_10799.field_56883, HotbarHud.background.id, x - 8, y - 8, 0.0f, 0.0f, 196, 32, 784, 128, 784, 128);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)((double)x + selected * 20.0 - 4.0), (float)(y - 4));
        g.method_25302(class_10799.field_56883, HotbarHud.selection.id, 0, 0, 0.0f, 0.0f, 28, 28, 112, 112, 112, 112);
        g.method_51448().popMatrix();
        class_746 player = class_310.method_1551().field_1724;
        if (!player.method_6079().method_7960()) {
            boolean left = player.method_6068() == class_1306.field_6183;
            g.method_25302(class_10799.field_56883, HotbarHud.offhand.id, left ? x - 31 : x + 183, y - 4, 0.0f, 0.0f, 28, 28, 112, 112, 112, 112);
        }
    }

    public static void end(class_332 g) {
        if (HotbarHud.active()) {
            g.method_51448().popMatrix();
        }
    }

    private static Graphics2D draw(BufferedImage img) {
        Graphics2D a = img.createGraphics();
        a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return a;
    }

    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.clamp((long)a, 0, 255));
    }

    private static void repaint() {
        Settings s = CakeClient.settings;
        Color accent = new Color(Color.HSBtoRGB((float)s.hotbarHue, 0.58f, 1.0f));
        int opacity = (int)(s.hotbarOpacity * 245.0);
        BufferedImage bg = new BufferedImage(784, 128, 2);
        Graphics2D a = HotbarHud.draw(bg);
        a.setColor(new Color(0, 0, 0, 70));
        a.fillRoundRect(9, 12, 766, 110, 35, 35);
        if (s.hotbarStyle == 1) {
            for (i = 0; i < 9; ++i) {
                int x = 31 + i * 80;
                a.setPaint(new GradientPaint(x, 22.0f, new Color(35, 31, 48, opacity), x, 111.0f, new Color(9, 9, 15, opacity)));
                a.fillRoundRect(x, 22, 76, 88, 15, 15);
                a.setColor(HotbarHud.alpha(accent, 65));
                a.drawRoundRect(x, 22, 76, 88, 15, 15);
            }
        } else if (s.hotbarStyle == 2) {
            a.setColor(new Color(4, 16, 14, opacity));
            a.fillRect(23, 19, 737, 94);
            a.setStroke(new BasicStroke(2.0f));
            a.setColor(HotbarHud.alpha(accent, 170));
            a.drawRect(23, 19, 737, 94);
            for (i = 1; i < 9; ++i) {
                a.setColor(HotbarHud.alpha(accent, 45));
                a.drawLine(30 + i * 80, 22, 30 + i * 80, 108);
            }
        } else {
            int arc = s.hotbarStyle == 3 ? 7 : 34;
            a.setPaint(new GradientPaint(0.0f, 18.0f, new Color(36, 32, 47, opacity), 0.0f, 116.0f, new Color(9, 9, 16, opacity)));
            a.fillRoundRect(21, 18, 742, 96, arc, arc);
            a.setColor(HotbarHud.alpha(accent, 85));
            a.setStroke(new BasicStroke(2.0f));
            a.drawRoundRect(21, 18, 742, 96, arc, arc);
            a.setColor(new Color(255, 255, 255, 22));
            a.drawLine(39, 21, 744, 21);
        }
        a.dispose();
        background.update(bg);
        BufferedImage sel = new BufferedImage(112, 112, 2);
        a = HotbarHud.draw(sel);
        for (int i = 6; i > 0; --i) {
            a.setColor(HotbarHud.alpha(accent, 7));
            a.fillRoundRect(9 - i, 8 - i, 94 + i * 2, 97 + i * 2, 20, 20);
        }
        a.setPaint(new GradientPaint(0.0f, 8.0f, HotbarHud.alpha(accent, 90), 0.0f, 101.0f, HotbarHud.alpha(accent, 13)));
        a.fillRoundRect(12, 10, 88, 94, 15, 15);
        a.setStroke(new BasicStroke(3.0f));
        a.setColor(HotbarHud.alpha(accent, 235));
        a.drawRoundRect(12, 10, 88, 94, 15, 15);
        a.setColor(new Color(255, 255, 255, 210));
        a.fillRoundRect(41, 5, 30, 4, 4, 4);
        a.dispose();
        selection.update(sel);
        BufferedImage off = new BufferedImage(112, 112, 2);
        a = HotbarHud.draw(off);
        a.setColor(new Color(16, 14, 24, opacity));
        a.fillRoundRect(6, 7, 100, 100, 21, 21);
        a.setStroke(new BasicStroke(2.0f));
        a.setColor(HotbarHud.alpha(accent, 85));
        a.drawRoundRect(6, 7, 100, 100, 21, 21);
        a.dispose();
        offhand.update(off);
    }

    static {
        revision = -1L;
    }
}

