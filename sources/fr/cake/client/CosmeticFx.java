/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_742
 *  org.joml.Matrix4f
 */
package fr.cake.client;

import fr.cake.client.CombatEffects;
import fr.cake.client.CosmeticNetwork;
import fr.cake.client.HitParticles;
import fr.cake.client.SmoothTexture;
import fr.cake.client.SpriteBatch;
import fr.cake.protocol.CosmeticState;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_742;
import org.joml.Matrix4f;

public final class CosmeticFx {
    public static final String[] TRAILS = new String[]{"Orbes", "Diamants", "\u00c9toiles", "Sakura", "Flammes", "Neige", "\u00c9tincelles", "Ruban"};
    public static final String[] RINGS = new String[]{"Halo", "Double anneau", "Onde", "Hexagone", "Rune", "Soleil", "Orbites", "Pointill\u00e9s", "Fleur", "\u00c9toile", "\u00c9clipse", "Radar"};
    private static final Map<UUID, Track> tracks = new LinkedHashMap<UUID, Track>();
    private static Object world;
    private static SmoothTexture atlas;

    public static void tick(class_310 mc) {
        if (world != mc.field_1687) {
            tracks.clear();
            world = mc.field_1687;
        }
        if (mc.field_1724 == null || mc.field_1687 == null || mc.method_1493()) {
            return;
        }
        long now = System.nanoTime();
        HashSet<UUID> valid = new HashSet<UUID>();
        ArrayList<class_742> players = new ArrayList<class_742>(mc.field_1687.method_18456());
        players.sort(Comparator.comparingDouble(p -> p.method_5858((class_1297)mc.field_1724)));
        int n = 0;
        for (class_742 p2 : players) {
            CosmeticState s = CosmeticNetwork.get(p2.method_5667());
            if (s == null || !p2.method_5805() || p2.method_5767() || p2.method_7325() || p2.method_5739((class_1297)mc.field_1724) > 40.0f) continue;
            if (++n > 8) break;
            valid.add(p2.method_5667());
            Track t = tracks.computeIfAbsent(p2.method_5667(), id -> new Track());
            t.state = s;
            class_243 at = p2.method_73189();
            if (t.last != null && at.method_1025(t.last) > 64.0) {
                t.points.clear();
                t.rings.clear();
            }
            if (t.last != null && (s.flags() & 2) != 0 && at.method_1025(t.last) > 0.003) {
                int steps = Math.min(5, Math.max(1, (int)(at.method_1022(t.last) / (0.08 + (double)(1.0f - s.trailDensity()) * 0.12))));
                for (int k = 1; k <= steps; ++k) {
                    t.points.add(new Mark(t.last.method_35590(at, (double)k / (double)steps).method_1031(0.0, 0.11, 0.0), now));
                    while (t.points.size() > (p2 == mc.field_1724 ? 48 : 24)) {
                        t.points.removeFirst();
                    }
                }
            }
            if (t.last != null && (s.flags() & 4) != 0 && t.ground && !p2.method_24828() && at.field_1351 - t.last.field_1351 > 0.08) {
                t.rings.add(new Mark(t.last.method_1031(0.0, 0.035, 0.0), now));
                while (t.rings.size() > 3) {
                    t.rings.removeFirst();
                }
            }
            t.last = at;
            t.ground = p2.method_24828();
            t.points.removeIf(m -> (s.flags() & 2) == 0 || (double)(now - m.time) > (0.45 + (double)s.trailLife() * 1.75) * 1.0E9);
            t.rings.removeIf(m -> (s.flags() & 4) == 0 || (double)(now - m.time) > (0.7 + (double)(s.jumpLife() * 2.0f)) * 1.0E9);
        }
        tracks.keySet().retainAll(valid);
        HitParticles.tick(mc);
        CombatEffects.tick(mc);
    }

    private static SmoothTexture atlas() {
        if (atlas == null) {
            try (InputStream in = CosmeticFx.class.getResourceAsStream("/assets/cakeclient/fx/atlas.png");){
                BufferedImage image = ImageIO.read(in);
                atlas = new SmoothTexture("cosmetic_atlas", image.getWidth(), image.getHeight());
                atlas.update(image);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return atlas;
    }

    public static void render(class_4184 camera, Matrix4f matrix) {
        if (class_310.method_1551().field_1724 == null) {
            return;
        }
        SpriteBatch.begin(camera, matrix);
        long now = System.nanoTime();
        int budget = 240;
        block0: for (Track track : tracks.values()) {
            CosmeticState s = track.state;
            int color = Color.HSBtoRGB(s.trailHue(), 0.6f, 1.0f) & 0xFFFFFF;
            Mark previous = null;
            for (Mark m : track.points) {
                if (--budget < 0) break;
                double age = (double)(now - m.time) / 1.0E9;
                double life = 0.45 + (double)s.trailLife() * 1.75;
                double fade = Math.max(0.0, 1.0 - age / life);
                double size = (0.055 + (double)s.trailSize() * 0.16) * (0.5 + 0.5 * fade);
                SpriteBatch.Region region = SpriteBatch.cell(s.trail());
                if (s.trail() == 7 && previous != null) {
                    SpriteBatch.ribbon(previous.at, m.at, size * 0.65, color, (float)fade, (float)fade, region);
                } else {
                    SpriteBatch.add(m.at, size, (float)(age * 0.7), color, (float)(fade * 0.9), region);
                }
                previous = m;
            }
            int ringColor = Color.HSBtoRGB(s.jumpHue(), 0.55f, 1.0f) & 0xFFFFFF;
            for (Mark m : track.rings) {
                if (--budget < 0) continue block0;
                double progress = (double)(now - m.time) / 1.0E9 / (0.7 + (double)(s.jumpLife() * 2.0f));
                float alpha = (float)Math.pow(Math.max(0.0, Math.sin(Math.PI * Math.clamp(progress, 0.0, 1.0))), 0.55);
                double radius = (0.9 + (double)s.jumpSize() * 1.7) * (0.55 + 0.65 * (1.0 - Math.pow(1.0 - progress, 3.0)));
                SpriteBatch.floor(m.at, radius, (float)(progress * 0.25), ringColor, alpha, 8 + s.jump());
                if (!(progress > 0.12)) continue;
                SpriteBatch.floor(m.at.method_1031(0.0, 0.007, 0.0), radius * 0.77, (float)(-progress * 0.35), ringColor, alpha * 0.55f, 8 + s.jump());
            }
        }
        HitParticles.render();
        CombatEffects.render();
        SpriteBatch.end(CosmeticFx.atlas().method_71659());
    }

    public static void close() {
        tracks.clear();
        SpriteBatch.close();
        if (atlas != null) {
            atlas.close();
            atlas = null;
        }
        HitParticles.clear();
    }

    private static final class Track {
        class_243 last;
        boolean ground;
        CosmeticState state;
        final ArrayDeque<Mark> points = new ArrayDeque();
        final ArrayDeque<Mark> rings = new ArrayDeque();

        private Track() {
        }
    }

    private record Mark(class_243 at, long time) {
    }
}

