/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.CakeOptimize;
import fr.cake.client.KnifeCatalog;
import fr.cake.client.LyricsSky;
import fr.cake.client.Settings;
import fr.cake.client.UiCanvas;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.lang.reflect.Field;

public final class CakeExtras {
    private static int editor = 0;
    private static final String[] KNIVES = KnifeCatalog.LABELS;
    private static String status = "Mode FPS inactif";
    private static final String[] FONTS = new String[]{"CAKE UI", "SANS", "SERIF", "MONO", "DISPLAY"};

    private CakeExtras() {
    }

    public static boolean collectionOpen(int tab) {
        return tab == 6 && editor == 3;
    }

    public static UiCanvas.Hover collectionHover(double x, double y) {
        if (x >= 1640.0 && x <= 1828.0 && y >= 304.0 && y <= 352.0) {
            return new UiCanvas.Hover(1640.0, 304.0, 188.0, 48.0);
        }
        if (x < 396.0 || y < 418.0) {
            return null;
        }
        int col = (int)((x - 396.0) / 356.0);
        int row = (int)((y - 418.0) / 70.0);
        int index = row * 4 + col;
        if (col < 4 && row < 6 && index < KnifeCatalog.COUNT && x <= (double)(722 + col * 356) && y <= (double)(474 + row * 70)) {
            return new UiCanvas.Hover(396 + col * 356, 418 + row * 70, 326.0, 56.0);
        }
        return null;
    }

    private static Settings s() {
        return CakeClient.settings;
    }

    private static double num(String key, double fallback) {
        try {
            Boolean b;
            Object value = Settings.class.getField(key).get(CakeExtras.s());
            return value instanceof Boolean ? (double)((b = (Boolean)value) != false ? 1 : 0) : ((Number)value).doubleValue();
        }
        catch (Exception e) {
            return fallback;
        }
    }

    private static int integer(String key, int fallback) {
        return (int)CakeExtras.num(key, fallback);
    }

    private static void set(String key, Object value) {
        try {
            Field f = Settings.class.getField(key);
            if (value instanceof Integer) {
                Integer n = (Integer)value;
                f.setInt(CakeExtras.s(), n);
            } else if (value instanceof Boolean) {
                Boolean b = (Boolean)value;
                f.setBoolean(CakeExtras.s(), b);
            } else {
                f.setDouble(CakeExtras.s(), (Double)value);
            }
            CakeExtras.s().save();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void refresh() {
        try {
            Field field = LyricsSky.class.getDeclaredField("previous");
            field.setAccessible(true);
            field.set(null, "");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static Font lyricFont(float size, boolean bold) {
        int f = CakeExtras.integer("lyricFont", 0);
        if (f == 0) {
            return UiCanvas.font(size, bold);
        }
        String name = switch (f) {
            case 1 -> "SansSerif";
            case 2 -> "Serif";
            case 3 -> "Monospaced";
            default -> "Dialog";
        };
        return new Font(name, bold ? 1 : 0, 1).deriveFont(size);
    }

    public static Color lyricFill() {
        return Color.getHSBColor((float)CakeExtras.num("lyricHue", 0.76), 0.55f, 1.0f);
    }

    public static Color lyricGlow() {
        Color c = CakeExtras.lyricFill();
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 220);
    }

    public static Color lyricOutline() {
        Color c = CakeExtras.lyricFill();
        return new Color((int)((double)c.getRed() * 0.11), (int)((double)c.getGreen() * 0.11), (int)((double)c.getBlue() * 0.11), 230);
    }

    private static void round(Graphics2D g, int x, int y, int w, int h, int radius, Color c) {
        g.setColor(c);
        g.fillRoundRect(x, y, w, h, radius, radius);
    }

    private static void label(Graphics2D g, String v, int x, int y, int size, boolean strong) {
        g.setFont(UiCanvas.font(size, strong));
        g.setColor(new Color(241, 235, 255));
        g.drawString(v, x, y);
    }

    private static void button(Graphics2D g, int x, int y, int w, int h, String title, boolean active) {
        CakeExtras.round(g, x, y, w, h, 14, active ? new Color(134, 86, 202, 180) : new Color(58, 50, 76, 210));
        CakeExtras.label(g, title, x + 16, y + h / 2 + 8, 21, active);
    }

    private static void rainbow(Graphics2D g, int x, int y, int w) {
        for (int i = 0; i < w; ++i) {
            g.setColor(Color.getHSBColor((float)i / (float)w, 0.65f, 1.0f));
            g.fillRect(x + i, y, 1, 25);
        }
    }

    public static void decorate(UiCanvas panel, Graphics2D g, int tab) {
        if (tab != 5) {
            editor = 0;
            return;
        }
        if (tab == 6) {
            CakeExtras.button(g, 1535, 194, 324, 58, "COLLECTION / 22", editor == 3);
            if (editor != 3) {
                return;
            }
            CakeExtras.drawKnives(g);
            return;
        }
        if (tab == 4) {
            CakeExtras.button(g, 1535, 194, 324, 58, "TRAIL / JUMP", editor == 5);
            if (editor != 5) {
                return;
            }
            CakeExtras.drawEffects(g);
            return;
        }
        if (tab == 7) {
            CakeExtras.button(g, 1170, 194, 330, 58, "POTATO / BOOST FPS", editor == 4);
            if (editor == 4) {
                CakeExtras.drawFps(g);
                return;
            }
        }
        CakeExtras.button(g, 1535, 194, 324, 58, tab == 5 ? "POLICE / COULEURS" : "AMBIANCE / FOG", editor == (tab == 5 ? 1 : 2));
        if (editor == 0) {
            return;
        }
        if (editor == 1 && tab != 5 || editor == 2 && tab != 7) {
            editor = 0;
            return;
        }
        CakeExtras.round(g, 350, 282, 1524, 635, 24, new Color(17, 14, 29, 246));
        g.setColor(new Color(134, 96, 212, 180));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(350, 282, 1524, 635, 24, 24);
        CakeExtras.label(g, editor == 1 ? "Typographie des lyrics" : "Ambiance color\u00e9e et brouillard", 395, 345, 35, true);
        CakeExtras.button(g, 1650, 302, 177, 48, "RETOUR", false);
        if (editor == 1) {
            CakeExtras.label(g, "Police", 400, 412, 24, true);
            for (int i = 0; i < FONTS.length; ++i) {
                CakeExtras.button(g, 395 + i * 283, 430, 258, 58, FONTS[i], CakeExtras.integer("lyricFont", 0) == i);
            }
            CakeExtras.label(g, "Couleur des paroles", 400, 563, 24, true);
            CakeExtras.rainbow(g, 395, 589, 1420);
            int x = (int)(395.0 + CakeExtras.num("lyricHue", 0.76) * 1420.0);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(4.0f));
            g.drawRect(x - 5, 583, 10, 37);
            CakeExtras.label(g, "La teinte modifie le texte et sa lueur. Les autres modules restent inchang\u00e9s.", 400, 690, 23, false);
            CakeExtras.label(g, "L'aper\u00e7u prend effet sur les nouvelles paroles sans changer la synchronisation.", 400, 749, 23, false);
        } else {
            CakeExtras.label(g, "Densit\u00e9 du brouillard", 400, 431, 24, true);
            double fog = CakeExtras.num("fogStrength", 0.0);
            CakeExtras.round(g, 395, 469, 1420, 16, 12, new Color(69, 62, 91));
            CakeExtras.round(g, 395, 469, (int)(1420.0 * fog), 16, 12, new Color(177, 134, 247));
            CakeExtras.label(g, Math.round(fog * 100.0) + " %", 1650, 453, 21, true);
            CakeExtras.label(g, "Couleur g\u00e9n\u00e9rale du brouillard", 400, 574, 24, true);
            CakeExtras.rainbow(g, 395, 600, 1420);
            int x = (int)(395.0 + CakeExtras.num("ambientHue", 0.72) * 1420.0);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(4.0f));
            g.drawRect(x - 5, 594, 10, 37);
            CakeExtras.label(g, "Rendu calcul\u00e9 \u00e0 la r\u00e9solution native et appliqu\u00e9 aux pixels du d\u00e9cor.", 400, 721, 22, false);
            CakeExtras.label(g, "Le module Optique est activ\u00e9 automatiquement si vous r\u00e9glez le brouillard.", 400, 765, 22, false);
        }
    }

    public static boolean intercept(UiCanvas panel, double x, double y, int tab) {
        int desired;
        if (tab != 5) {
            return false;
        }
        if (tab == 6) {
            if (x >= 1535.0 && x <= 1859.0 && y >= 194.0 && y <= 252.0) {
                editor = editor == 3 ? 0 : 3;
                panel.invalidate();
                return true;
            }
            if (editor == 3) {
                return CakeExtras.selectKnife(panel, x, y);
            }
            return false;
        }
        if (tab == 4) {
            if (x >= 1535.0 && x <= 1859.0 && y >= 194.0 && y <= 252.0) {
                editor = editor == 5 ? 0 : 5;
                panel.invalidate();
                return true;
            }
            if (editor == 5) {
                return CakeExtras.selectEffects(panel, x, y);
            }
            return false;
        }
        if (tab == 7) {
            if (x >= 1170.0 && x <= 1500.0 && y >= 194.0 && y <= 252.0) {
                editor = editor == 4 ? 0 : 4;
                panel.invalidate();
                return true;
            }
            if (editor == 4) {
                return CakeExtras.selectFps(panel, x, y);
            }
        }
        int n = desired = tab == 5 ? 1 : 2;
        if (x >= 1535.0 && x <= 1859.0 && y >= 194.0 && y <= 252.0) {
            editor = editor == desired ? 0 : desired;
            panel.invalidate();
            return true;
        }
        if (editor != desired) {
            return false;
        }
        if (x >= 1650.0 && x <= 1827.0 && y >= 302.0 && y <= 350.0) {
            editor = 0;
            panel.invalidate();
            return true;
        }
        if (editor == 1) {
            if (y >= 430.0 && y <= 488.0 && x >= 395.0 && x < 1810.0) {
                int f = (int)((x - 395.0) / 283.0);
                if (x - 395.0 - (double)(f * 283) < 258.0) {
                    CakeExtras.set("lyricFont", f);
                    CakeExtras.refresh();
                    panel.invalidate();
                }
                return true;
            }
            if (y >= 572.0 && y <= 632.0 && x >= 395.0 && x <= 1815.0) {
                CakeExtras.set("lyricHue", Math.max(0.0, Math.min(1.0, (x - 395.0) / 1420.0)));
                CakeExtras.refresh();
                panel.invalidate();
                return true;
            }
        } else {
            if (y >= 453.0 && y <= 505.0 && x >= 395.0 && x <= 1815.0) {
                CakeExtras.set("fogStrength", Math.max(0.0, Math.min(1.0, (x - 395.0) / 1420.0)));
                CakeExtras.s().atmosphere = true;
                CakeExtras.s().save();
                panel.invalidate();
                return true;
            }
            if (y >= 587.0 && y <= 647.0 && x >= 395.0 && x <= 1815.0) {
                CakeExtras.set("ambientHue", Math.max(0.0, Math.min(1.0, (x - 395.0) / 1420.0)));
                panel.invalidate();
                return true;
            }
        }
        return x >= 350.0 && x <= 1874.0 && y >= 282.0 && y <= 917.0;
    }

    private static void frame(Graphics2D g, String title) {
        CakeExtras.round(g, 350, 282, 1524, 635, 24, new Color(17, 14, 29, 249));
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(new Color(134, 96, 212, 180));
        g.drawRoundRect(350, 282, 1524, 635, 24, 24);
        CakeExtras.label(g, title, 392, 344, 37, true);
        CakeExtras.button(g, 1640, 304, 188, 48, "RETOUR", false);
    }

    private static void drawKnives(Graphics2D g) {
        CakeExtras.frame(g, "COLLECTION // 22 COUTEAUX CS2");
        CakeExtras.label(g, "Mod\u00e8les et animations CS2 propres \u00e0 chaque couteau", 400, 397, 22, false);
        int selected = CakeExtras.integer("knifeModel", 0);
        for (int i = 0; i < KNIVES.length; ++i) {
            int x = 396 + i % 4 * 356;
            int y = 418 + i / 4 * 70;
            CakeExtras.button(g, x, y, 326, 56, (i == selected ? "\u25cf  " : "") + KNIVES[i], i == selected);
        }
        CakeExtras.label(g, "G : inspection  |  Maintenir G : Butterfly  |  Changement imm\u00e9diat", 400, 883, 21, false);
    }

    private static boolean selectKnife(UiCanvas p, double x, double y) {
        if (x >= 1640.0 && x <= 1828.0 && y >= 304.0 && y <= 352.0) {
            editor = 0;
            p.invalidate();
            return true;
        }
        if (y >= 418.0 && y <= 838.0) {
            int row = (int)((y - 418.0) / 70.0);
            int col = (int)((x - 396.0) / 356.0);
            int i = row * 4 + col;
            if (col >= 0 && col < 4 && row >= 0 && row < 6 && i < KnifeCatalog.COUNT && x >= (double)(396 + col * 356) && x <= (double)(722 + col * 356) && y <= (double)(474 + row * 70)) {
                CakeExtras.set("knifeModel", i);
                p.invalidate();
                return true;
            }
        }
        return x >= 350.0 && x <= 1874.0 && y >= 282.0 && y <= 917.0;
    }

    private static void drawFps(Graphics2D g) {
        CakeExtras.frame(g, "POTATO // FPS BOOST");
        CakeExtras.label(g, "Profil graphique (appliqu\u00e9 apr\u00e8s red\u00e9marrage)", 399, 420, 25, false);
        int mode = CakeExtras.integer("fpsPreset", 0);
        String[] labels = new String[]{"STANDARD", "RAPIDE", "ULTRA FPS"};
        for (int i = 0; i < 3; ++i) {
            CakeExtras.button(g, 396 + i * 475, 457, 447, 70, labels[i], mode == i);
        }
        CakeExtras.button(g, 396, 590, 1397, 72, "Textures Potato 8\u00d78 : " + (CakeExtras.s().getClass() != null && CakeExtras.num("potatoEnabled", 0.0) > 0.0 ? "ACTIV\u00c9ES" : "D\u00c9SACTIV\u00c9ES"), CakeExtras.num("potatoEnabled", 0.0) > 0.0);
        CakeExtras.label(g, "186 textures l\u00e9g\u00e8res. Conservation du pack et des options personnelles.", 399, 727, 23, false);
        CakeExtras.label(g, "Ce profil supprime le superflu mais ne garantit aucun FPS chiffr\u00e9.", 399, 780, 23, false);
        CakeExtras.label(g, status.length() > 100 ? status.substring(0, 100) : status, 399, 850, 21, false);
    }

    private static boolean selectFps(UiCanvas p, double x, double y) {
        if (x >= 1640.0 && x <= 1828.0 && y >= 304.0 && y <= 352.0) {
            editor = 0;
            p.invalidate();
            return true;
        }
        if (y >= 457.0 && y <= 527.0 && x >= 396.0 && x < 1793.0) {
            int i = (int)((x - 396.0) / 475.0);
            if (i <= 2 && x < (double)(396 + i * 475 + 447)) {
                CakeExtras.set("fpsPreset", i);
                if (i > 0) {
                    for (String key : new String[]{"motionBlur", "atmosphere", "sky", "fireflies", "sakura", "motes", "embers", "snow", "pollen"}) {
                        try {
                            Settings.class.getField(key).setBoolean(CakeExtras.s(), false);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    CakeExtras.s().save();
                }
                status = CakeOptimize.apply(CakeExtras.num("potatoEnabled", 0.0) > 0.0, i);
                p.invalidate();
            }
            return true;
        }
        if (y >= 590.0 && y <= 662.0 && x >= 396.0 && x <= 1793.0) {
            boolean v = CakeExtras.num("potatoEnabled", 0.0) <= 0.0;
            CakeExtras.set("potatoEnabled", v);
            status = CakeOptimize.apply(v, CakeExtras.integer("fpsPreset", 0));
            p.invalidate();
            return true;
        }
        return x >= 350.0 && x <= 1874.0 && y >= 282.0 && y <= 917.0;
    }

    private static void drawEffects(Graphics2D g) {
        CakeExtras.frame(g, "TRAILS // JUMP CIRCLES");
        CakeExtras.label(g, "Effets personnalisables - conserv\u00e9s ind\u00e9pendamment des paroles", 399, 401, 24, false);
        CakeExtras.button(g, 395, 439, 681, 73, "Trail : " + (CakeExtras.num("trailEnabled", 0.0) > 0.0 ? "ACTIF" : "INACTIF"), CakeExtras.num("trailEnabled", 0.0) > 0.0);
        CakeExtras.button(g, 1110, 439, 683, 73, "Jump Circle : " + (CakeExtras.num("jumpEnabled", 0.0) > 0.0 ? "ACTIF" : "INACTIF"), CakeExtras.num("jumpEnabled", 0.0) > 0.0);
        CakeExtras.label(g, "Style Trail", 399, 571, 24, true);
        CakeExtras.label(g, "Style Jump Circle", 1110, 571, 24, true);
        String[] styles = new String[]{"NEON", "ORBITAL", "SPARK", "AURA"};
        for (int i = 0; i < 4; ++i) {
            CakeExtras.button(g, 396 + i % 2 * 345, 597 + i / 2 * 77, 326, 60, styles[i], CakeExtras.integer("trailStyle", 0) == i);
            CakeExtras.button(g, 1110 + i % 2 * 345, 597 + i / 2 * 77, 326, 60, styles[i], CakeExtras.integer("jumpStyle", 0) == i);
        }
        CakeExtras.label(g, "Effets l\u00e9gers et param\u00e9trables dans le profil Cake Client.", 399, 870, 22, false);
    }

    private static boolean selectEffects(UiCanvas p, double x, double y) {
        if (x >= 1640.0 && x <= 1828.0 && y >= 304.0 && y <= 352.0) {
            editor = 0;
            p.invalidate();
            return true;
        }
        if (y >= 439.0 && y <= 512.0) {
            if (x >= 395.0 && x <= 1076.0) {
                CakeExtras.set("trailEnabled", CakeExtras.num("trailEnabled", 0.0) <= 0.0);
                p.invalidate();
                return true;
            }
            if (x >= 1110.0 && x <= 1793.0) {
                CakeExtras.set("jumpEnabled", CakeExtras.num("jumpEnabled", 0.0) <= 0.0);
                p.invalidate();
                return true;
            }
        }
        if (y >= 597.0 && y <= 734.0) {
            int row = (int)((y - 597.0) / 77.0);
            int col = x < 1100.0 ? (int)((x - 396.0) / 345.0) : (int)((x - 1110.0) / 345.0);
            int style = row * 2 + col;
            if (col >= 0 && col < 2 && style >= 0 && style < 4 && y <= (double)(657 + row * 77)) {
                if (x >= 396.0 && x <= 1067.0) {
                    CakeExtras.set("trailStyle", style);
                    p.invalidate();
                    return true;
                }
                if (x >= 1110.0 && x <= 1781.0) {
                    CakeExtras.set("jumpStyle", style);
                    p.invalidate();
                    return true;
                }
            }
        }
        return x >= 350.0 && x <= 1874.0 && y >= 282.0 && y <= 917.0;
    }
}

