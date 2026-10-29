/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_332
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.LyricsService;
import fr.cake.client.Settings;
import fr.cake.client.SmoothTexture;
import fr.cake.client.SpotifyVisualizer;
import fr.cake.client.UiCanvas;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;
import net.minecraft.class_10799;
import net.minecraft.class_332;

public final class SpotifyHud {
    private static final int W = 840;
    private static final int H = 212;
    private static final BufferedImage IMAGE = new BufferedImage(840, 212, 2);
    private static SmoothTexture texture;
    private static String key;
    private static BufferedImage cover;
    private static long revision;
    private static boolean paused;
    private static SmoothTexture timeTexture;
    private static long timeKey;
    private static String durationKey;

    public static Bounds bounds(int width, int height) {
        Settings s = CakeClient.settings;
        double scale = Math.min((double)width / 1920.0, (double)height / 1080.0) * (0.7 + s.musicScale * 0.8);
        double w = 420.0 * scale;
        double h = 106.0 * scale;
        return new Bounds(s.musicX * Math.max(0.0, (double)width - w), s.musicY * Math.max(0.0, (double)height - h), w, h);
    }

    public static void move(double x, double y, int width, int height) {
        Bounds b = SpotifyHud.bounds(width, height);
        Settings s = CakeClient.settings;
        s.musicX = Math.clamp(x / Math.max(1.0, (double)width - b.width), 0.0, 1.0);
        s.musicY = Math.clamp(y / Math.max(1.0, (double)height - b.height), 0.0, 1.0);
    }

    public static void render(class_332 g, boolean editor) {
        boolean pause;
        Settings s = CakeClient.settings;
        if (s == null || !s.visualizer && !editor) {
            return;
        }
        LyricsService.Media media = LyricsService.media();
        if (media == null && !editor) {
            return;
        }
        String next = media == null ? "Aucun morceau en cours" : media.key();
        BufferedImage art = LyricsService.artwork();
        boolean bl = pause = media == null || !media.playing();
        if (texture == null) {
            texture = new SmoothTexture("spotify_card", 840, 212);
            revision = -1L;
        }
        if (!next.equals(key) || art != cover || revision != Settings.revision || paused != pause) {
            key = next;
            cover = art;
            paused = pause;
            revision = Settings.revision;
            SpotifyHud.paint(media, art);
            texture.update(IMAGE);
        }
        Bounds b = SpotifyHud.bounds(g.method_51421(), g.method_51443());
        float scale = (float)(b.width / 840.0);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)b.x, (float)b.y);
        g.method_51448().scale(scale, scale);
        g.method_25290(class_10799.field_56883, SpotifyHud.texture.id, 0, 0, 0.0f, 0.0f, 840, 212, 840, 212);
        int accent = 0xFF000000 | Color.HSBtoRGB((float)s.visualizerHue, 0.75f, 1.0f) & 0xFFFFFF;
        double progress = media == null || media.duration() <= 0L ? 0.0 : Math.clamp((double)media.currentPosition() / (double)media.duration(), 0.0, 1.0);
        g.method_25294(222, 168, 600, 176, -13290950);
        g.method_25294(222, 168, 222 + (int)(378.0 * progress), 176, accent);
        float level = SpotifyVisualizer.level();
        if (s.musicBars) {
            for (int i = 0; i < 4; ++i) {
                int bar = pause ? 4 : 5 + (int)(level * (float)(12 + i * 4));
                g.method_25294(220 + i * 9, 49 - bar, 225 + i * 9, 49, accent);
            }
        }
        g.method_51448().popMatrix();
        SpotifyHud.renderTime(g, b, media);
    }

    private static void renderTime(class_332 g, Bounds b, LyricsService.Media m) {
        long seconds = m == null ? 0L : m.currentPosition() / 1000L;
        String end = SpotifyHud.time(m == null ? 0L : m.duration());
        if (timeTexture == null) {
            timeTexture = new SmoothTexture("spotify_time", 210, 52);
        }
        if (seconds != timeKey || !end.equals(durationKey)) {
            timeKey = seconds;
            durationKey = end;
            BufferedImage img = new BufferedImage(210, 52, 2);
            Graphics2D a = img.createGraphics();
            SpotifyHud.quality(a);
            a.setFont(UiCanvas.font(27.0f, false));
            a.setColor(new Color(190, 189, 199));
            a.drawString(SpotifyHud.time(seconds * 1000L) + " / " + end, 3, 35);
            a.dispose();
            timeTexture.update(img);
        }
        float scale = (float)(b.width / 840.0);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)b.x, (float)b.y);
        g.method_51448().scale(scale, scale);
        g.method_25290(class_10799.field_56883, SpotifyHud.timeTexture.id, 616, 143, 0.0f, 0.0f, 210, 52, 210, 52);
        g.method_51448().popMatrix();
    }

    public static String time(long ms) {
        long t = Math.max(0L, ms) / 1000L;
        return t / 60L + ":" + String.format(Locale.ROOT, "%02d", t % 60L);
    }

    private static void quality(Graphics2D a) {
        a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        a.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        a.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    private static void paint(LyricsService.Media media, BufferedImage artwork) {
        Settings s = CakeClient.settings;
        Graphics2D a = IMAGE.createGraphics();
        a.setComposite(AlphaComposite.Clear);
        a.fillRect(0, 0, 840, 212);
        a.setComposite(AlphaComposite.SrcOver);
        SpotifyHud.quality(a);
        a.setColor(new Color(13, 13, 17, (int)(s.musicOpacity * 255.0)));
        a.fillRoundRect(0, 0, 840, 212, 22, 22);
        a.setColor(new Color(255, 255, 255, 24));
        a.drawRoundRect(1, 1, 837, 209, 22, 22);
        Shape clip = a.getClip();
        a.setClip(new RoundRectangle2D.Float(14.0f, 14.0f, 180.0f, 180.0f, 15.0f, 15.0f));
        if (s.musicArtwork && artwork != null) {
            a.drawImage(artwork, 14, 14, 180, 180, null);
        } else {
            a.setPaint(new GradientPaint(14.0f, 14.0f, new Color(91, 52, 150), 194.0f, 194.0f, new Color(26, 19, 42)));
            a.fillRect(14, 14, 180, 180);
            a.setColor(new Color(239, 227, 255));
            a.setFont(UiCanvas.font(78.0f, true));
            a.drawString("\u266a", 65, 133);
        }
        a.setClip(clip);
        a.setColor(new Color(245, 245, 248));
        a.setFont(UiCanvas.font(35.0f, true));
        a.drawString(SpotifyHud.ellipsis(a, media == null ? "Spotify \u00b7 en attente" : media.title(), 535), 267, 52);
        a.setFont(UiCanvas.font(30.0f, false));
        a.setColor(new Color(177, 177, 190));
        a.drawString(SpotifyHud.ellipsis(a, media == null ? "Lance un morceau sur ton PC" : media.artist(), 570), 222, 111);
        if (media != null && !media.playing()) {
            a.setFont(UiCanvas.font(20.0f, true));
            a.setColor(new Color(159, 130, 210));
            a.drawString("EN PAUSE", 222, 144);
        }
        a.dispose();
    }

    private static String ellipsis(Graphics2D a, String t, int width) {
        if (a.getFontMetrics().stringWidth(t) <= width) {
            return t;
        }
        while (!t.isEmpty() && a.getFontMetrics().stringWidth(t + "\u2026") > width) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "\u2026";
    }

    public static void close() {
        if (texture != null) {
            texture.close();
            texture = null;
        }
        if (timeTexture != null) {
            timeTexture.close();
            timeTexture = null;
        }
    }

    static {
        key = "";
        revision = -1L;
        timeKey = Long.MIN_VALUE;
        durationKey = "";
    }

    public record Bounds(double x, double y, double width, double height) {
        public boolean contains(double px, double py) {
            return px >= this.x && py >= this.y && px <= this.x + this.width && py <= this.y + this.height;
        }
    }
}

