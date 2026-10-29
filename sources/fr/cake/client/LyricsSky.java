/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.CakeExtras;
import fr.cake.client.LrcTimeline;
import fr.cake.client.LyricChunks;
import fr.cake.client.LyricPlacement;
import fr.cake.client.LyricSequence;
import fr.cake.client.LyricsService;
import fr.cake.client.Settings;
import fr.cake.client.SmoothTexture;
import fr.cake.client.SpatialPanel;
import fr.cake.client.WorldQuad;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class LyricsSky {
    private static final int SLOTS = 12;
    private static final SmoothTexture[] textures = new SmoothTexture[12];
    private static final Card[] cards = new Card[12];
    private static long demoStarted;
    private static final LrcTimeline DEMO;
    private static String previous;
    private static int slot;
    private static Object world;
    private static long lastPosition;
    private static long lastFrame;

    public static void preview() {
        demoStarted = System.nanoTime();
        previous = "";
    }

    public static void stopPreview() {
        demoStarted = 0L;
        previous = "";
    }

    public static void render(class_4184 camera, Matrix4f matrix) {
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (world != mc.field_1687) {
            LyricsSky.clear();
            world = mc.field_1687;
        }
        boolean demo = demoStarted != 0L && System.nanoTime() - demoStarted < 6000000000L;
        LyricsService.Media media = demo ? new LyricsService.Media("local-placement-demo", "Placement", "Cake", 0L, 6000L, true, demoStarted) : LyricsService.media();
        LrcTimeline timeline = demo ? DEMO : LyricsService.timeline();
        long now = System.nanoTime();
        if (!s.lyrics && !demo || mc.field_1755 != null || media == null || timeline == null || !media.playing() || !demo && now - media.sampled() > 5000000000L) {
            LyricsSky.clear();
            return;
        }
        long position = media.currentPosition() + (long)((s.lyricOffset - 0.5) * 30000.0) + (demo ? 0L : LyricsService.calibration());
        int index = timeline.index(position);
        if (index < 0 || media.duration() > 0L && media.currentPosition() >= media.duration()) {
            LyricsSky.clear();
            return;
        }
        if (lastPosition >= 0L && Math.abs(position - lastPosition - (now - lastFrame) / 1000000L) > 1500L) {
            LyricsSky.clear();
        }
        lastPosition = position;
        lastFrame = now;
        String key = media.key() + "\n" + index;
        double progress = timeline.progress(index, position, media.duration());
        if (!key.equals(previous)) {
            previous = key;
            for (Card c : cards) {
                if (c == null || c.retiring != 0L) continue;
                c.retiring = now;
            }
            List<LyricChunks.Chunk> groups = LyricChunks.split(timeline.lines.get(index).text());
            class_243 forward = SpatialPanel.look(camera);
            class_243 r = forward.method_1036(new class_243(0.0, 1.0, 0.0));
            r = r.method_1027() < 0.001 ? new class_243(1.0, 0.0, 0.0) : r.method_1029();
            class_243 u = r.method_1036(forward).method_1029();
            double distance = 5.5 + s.lyricDistance * 8.0;
            double aspect = (double)mc.method_22683().method_4489() / (double)Math.max(1, mc.method_22683().method_4506());
            int layout = media.key().hashCode() ^ Integer.rotateLeft(index * -1640531527, 13);
            List<LyricPlacement.Place> places = LyricPlacement.arrange(groups.size(), layout, distance, s.lyricSize, s.lyricSpacing, s.lyricHeight, ((Integer)mc.field_1690.method_41808().method_41753()).intValue(), aspect);
            int total = groups.stream().mapToInt(LyricChunks.Chunk::characters).sum();
            int consumed = 0;
            for (int i = 0; i < groups.size(); ++i) {
                int n;
                LyricChunks.Chunk group = groups.get(i);
                if (textures[n = slot++ % 12] == null) {
                    LyricsSky.textures[n] = new SmoothTexture("lyrics_" + n, 1920, 384);
                }
                Raster raster = LyricsSky.raster(group.text());
                textures[n].update(raster.image());
                LyricPlacement.Place place = places.get(i);
                class_243 center = camera.method_71156().method_1019(forward.method_1021(distance)).method_1019(r.method_1021(place.x())).method_1019(u.method_1021(place.y()));
                double from = (double)consumed / (double)Math.max(1, total);
                double to = (double)(consumed += group.characters()) / (double)Math.max(1, total);
                double width = place.width();
                LyricsSky.cards[n] = new Card(textures[n], place.x(), place.y(), distance, index, raster.left(), raster.right(), from, to, width, (double)groups.subList(0, Math.min(groups.size(), i + 2 + (int)Math.round(s.lyricOverlap * 2.0))).stream().mapToInt(LyricChunks.Chunk::characters).sum() / (double)Math.max(1, total));
            }
        }
        for (int i = 0; i < 12; ++i) {
            double fadeOut;
            Card c = cards[i];
            if (c == null) continue;
            double d = fadeOut = c.retiring == 0L ? 1.0 : 1.0 - Math.min(1.0, (double)(now - c.retiring) / 8.5E8);
            if (fadeOut <= 0.0) {
                LyricsSky.cards[i] = null;
                continue;
            }
            if (c.retiring == 0L) {
                c.visibility = LyricSequence.layered(progress, c.from, c.hideAt, 0.1);
            }
            if (c.visibility <= 0.001) continue;
            if (c.center == null) {
                class_243 forward = SpatialPanel.look(camera);
                class_243 r = forward.method_1036(new class_243(0.0, 1.0, 0.0));
                r = r.method_1027() < 0.001 ? new class_243(1.0, 0.0, 0.0) : r.method_1029();
                c.right = r;
                c.up = r.method_1036(forward).method_1029();
                c.center = camera.method_71156().method_1019(forward.method_1021(c.distance)).method_1019(r.method_1021(c.shift)).method_1019(c.up.method_1021(c.vertical));
            }
            double groupProgress = c.line == index ? Math.clamp((progress - c.from) / Math.max(0.01, c.to - c.from), 0.0, 1.0) : 1.0;
            Vector3f qr = new Vector3f(1.0f, 0.0f, 0.0f).rotate((Quaternionfc)camera.method_23767());
            Vector3f qu = new Vector3f(0.0f, 1.0f, 0.0f).rotate((Quaternionfc)camera.method_23767());
            class_243 billboardRight = new class_243((double)qr.x, (double)qr.y, (double)qr.z);
            class_243 billboardUp = new class_243((double)qu.x, (double)qu.y, (double)qu.z);
            WorldQuad.draw(c.texture, c.center.method_1019(billboardUp.method_1021((1.0 - c.visibility) * -0.1)), billboardRight, billboardUp, c.width, c.width * 384.0 / 1920.0, camera.method_71156(), matrix, (float)(s.lyricOpacity * c.visibility * fadeOut), c.left + (c.rightEdge - c.left) * (float)groupProgress, true);
        }
    }

    private static Raster raster(String text) {
        int i;
        BufferedImage image = new BufferedImage(1920, 384, 2);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        float size = 254.0f;
        g.setFont(CakeExtras.lyricFont(size, true));
        double measured = g.getFontMetrics().stringWidth(text);
        if (measured > 1780.0) {
            size = (float)((double)(size * 1780.0f) / measured);
        }
        g.setFont(CakeExtras.lyricFont(Math.max(28.0f, size), true));
        FontMetrics fm = g.getFontMetrics();
        int x = (1920 - fm.stringWidth(text)) / 2;
        int y = (384 - fm.getHeight()) / 2 + fm.getAscent();
        Shape outline = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getOutline(x, y);
        BufferedImage glow = new BufferedImage(480, 96, 2);
        Graphics2D gg = glow.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gg.scale(0.25, 0.25);
        gg.setColor(CakeExtras.lyricGlow());
        gg.setStroke(new BasicStroke(22.0f, 1, 1));
        gg.draw(outline);
        gg.fill(outline);
        gg.dispose();
        float[] kernel = new float[17];
        float total = 0.0f;
        for (i = 0; i < 17; ++i) {
            kernel[i] = (float)Math.exp(-Math.pow((double)(i - 8) / 3.4, 2.0) / 2.0);
            total += kernel[i];
        }
        i = 0;
        while (i < 17) {
            int n = i++;
            kernel[n] = kernel[n] / total;
        }
        glow = new ConvolveOp(new Kernel(17, 1, kernel), 1, null).filter(glow, null);
        glow = new ConvolveOp(new Kernel(1, 17, kernel), 1, null).filter(glow, null);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(glow, 0, 0, 1920, 384, null);
        g.setColor(CakeExtras.lyricOutline());
        g.setStroke(new BasicStroke(5.0f, 1, 1));
        g.draw(outline);
        g.setColor(CakeExtras.lyricFill());
        g.fill(outline);
        g.dispose();
        return new Raster(image, (float)x / 1920.0f, (float)(x + fm.stringWidth(text)) / 1920.0f);
    }

    private static void clear() {
        for (int i = 0; i < 12; ++i) {
            LyricsSky.cards[i] = null;
        }
        previous = "";
        lastPosition = -1L;
        lastFrame = 0L;
    }

    public static void close() {
        for (int i = 0; i < 12; ++i) {
            if (textures[i] != null) {
                textures[i].close();
                LyricsSky.textures[i] = null;
            }
            LyricsSky.cards[i] = null;
        }
        previous = "";
    }

    static {
        DEMO = LrcTimeline.parse("[00:00]let it happen\n[00:03]Des mots devant vous");
        previous = "";
        lastPosition = -1L;
    }

    private static final class Card {
        final SmoothTexture texture;
        class_243 center;
        class_243 right;
        class_243 up;
        double visibility;
        final long born;
        final int line;
        final float left;
        final float rightEdge;
        final double from;
        final double to;
        final double width;
        final double shift;
        final double vertical;
        final double distance;
        final double hideAt;
        long retiring;

        Card(SmoothTexture texture, double shift, double vertical, double distance, int line, float left, float edge, double from, double to, double width, double hideAt) {
            this.hideAt = hideAt;
            this.texture = texture;
            this.shift = shift;
            this.vertical = vertical;
            this.distance = distance;
            this.line = line;
            this.left = left;
            this.rightEdge = edge;
            this.from = from;
            this.to = to;
            this.width = width;
            this.born = System.nanoTime();
        }
    }

    private record Raster(BufferedImage image, float left, float right) {
    }
}

