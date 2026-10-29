/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import fr.cake.client.SpriteBatch;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;

public final class HitParticles {
    private static final int MAX_HIT = 84;
    private static final int MAX_AMBIENT = 96;
    private static final ArrayList<Dot> dots = new ArrayList();
    private static final ArrayDeque<Flash> flashes = new ArrayDeque();
    private static final Random random = new Random();
    private static int target = -1;
    private static int previousHurt;
    private static int tick;
    private static long attack;
    private static Object world;

    public static void attack(class_1297 e) {
        int n;
        target = e.method_5628();
        attack = System.nanoTime();
        if (e instanceof class_1309) {
            class_1309 l = (class_1309)e;
            n = l.field_6235;
        } else {
            n = 0;
        }
        previousHurt = n;
    }

    public static void clear() {
        dots.clear();
        flashes.clear();
        world = null;
        target = -1;
    }

    public static void tick(class_310 mc) {
        class_1297 class_12972;
        if (world != mc.field_1687) {
            HitParticles.clear();
            world = mc.field_1687;
        }
        if (mc.field_1687 == null || mc.field_1724 == null || mc.method_1493()) {
            return;
        }
        Settings s = CakeClient.settings;
        if (s.hitParticles && target >= 0 && System.nanoTime() - attack < 900000000L && (class_12972 = mc.field_1687.method_8469(target)) instanceof class_1309) {
            class_1309 entity = (class_1309)class_12972;
            if (entity.field_6235 > previousHurt) {
                HitParticles.burst(entity.method_73189().method_1031(0.0, (double)entity.method_17682() * 0.58, 0.0), s);
                target = -1;
            }
        }
        dots.removeIf(d -> ++d.age >= d.life || (d.hit ? !s.hitParticles : !s.extraParticles));
        for (Dot d2 : dots) {
            d2.previous = d2.p;
            d2.p = d2.p.method_1019(d2.v);
            d2.v = d2.v.method_1021(d2.hit ? 0.95 : 0.99).method_1031(0.0, d2.hit ? -0.002 : 0.0, 0.0);
        }
        long now = System.nanoTime();
        flashes.removeIf(f -> now - f.time > 400000000L || !s.hitParticles);
        if (s.extraParticles && ++tick % 2 == 0 && dots.stream().filter(d -> !d.hit).count() < 96L) {
            class_243 p = mc.field_1724.method_73189().method_1031((random.nextDouble() - 0.5) * 20.0, 0.2 + random.nextDouble() * 7.0, (random.nextDouble() - 0.5) * 20.0);
            int cell = s.extraParticleStyle == 1 ? 6 : (s.extraParticleStyle == 2 ? 3 : (s.extraParticleStyle == 3 ? 22 : 0));
            dots.add(new Dot(p, new class_243(0.002, -0.004 + random.nextDouble() * 0.008, 0.001), 100 + random.nextInt(100), cell, Color.HSBtoRGB((float)s.extraParticleHue, 0.45f, 1.0f) & 0xFFFFFF, 0.025f + (float)s.extraParticleSize * 0.045f, false));
        }
    }

    private static void burst(class_243 p, Settings s) {
        int n = 18 + (int)(s.hitParticleCount * 28.0);
        int cell = s.hitParticleStyle == 1 ? 20 : (s.hitParticleStyle == 2 ? 1 : (s.hitParticleStyle == 3 ? 0 : 2));
        int color = Color.HSBtoRGB((float)s.hitParticleHue, 0.57f, 1.0f) & 0xFFFFFF;
        while (dots.stream().filter(d -> d.hit).count() + (long)n > 84L) {
            int at = -1;
            for (int i = 0; i < dots.size(); ++i) {
                if (!HitParticles.dots.get((int)i).hit) continue;
                at = i;
                break;
            }
            if (at < 0) break;
            dots.remove(at);
        }
        for (int i = 0; i < n; ++i) {
            double a = (double)i * Math.PI * (3.0 - Math.sqrt(5.0));
            double y = 1.0 - 2.0 * ((double)i + 0.5) / (double)n;
            double r = Math.sqrt(1.0 - y * y);
            double speed = 0.055 + random.nextDouble() * 0.13;
            class_243 velocity = new class_243(Math.cos(a) * r, y * 0.7 + 0.2, Math.sin(a) * r).method_1021(speed);
            dots.add(new Dot(p.method_1019(velocity.method_1021(0.15)), velocity, 16 + random.nextInt(15), cell, color, 0.065f + (float)s.hitParticleSize * 0.14f, true));
        }
        flashes.add(new Flash(p, System.nanoTime(), color, 0.45f + (float)s.hitParticleSize * 0.55f));
        while (flashes.size() > 4) {
            flashes.removeFirst();
        }
    }

    public static void render() {
        float partial = class_310.method_1551().method_61966().method_60637(true);
        long now = System.nanoTime();
        for (Flash f : flashes) {
            float t = (float)(now - f.time) / 4.0E8f;
            if (t < 0.0f || t > 1.0f) continue;
            float fade = (1.0f - t) * (1.0f - t);
            SpriteBatch.add(f.position, (double)f.size * (0.6 + (double)t * 0.9), t * 0.8f, f.color, fade * 0.65f, SpriteBatch.cell(21));
            SpriteBatch.add(f.position, (double)f.size * 0.4 * (double)(1.0f + t), 0.0f, 0xFFFFFF, fade * 0.8f, SpriteBatch.cell(0));
        }
        for (Dot d : dots) {
            double age = (float)d.age + partial;
            double life = age / (double)d.life;
            float fade = (float)(Math.min(1.0, age / 2.0) * Math.pow(Math.max(0.0, 1.0 - life), 1.15));
            class_243 at = d.previous.method_35590(d.p, (double)partial);
            double size = (double)d.size * (0.6 + 0.4 * (double)fade);
            SpriteBatch.add(at, size, (float)(age * 0.07), d.color, fade, SpriteBatch.cell(d.cell));
            if (!d.hit) continue;
            class_243 tail = at.method_1020(d.v.method_1021(2.6));
            SpriteBatch.ribbon(tail, at, size * 0.34, d.color, 0.0f, fade * 0.8f, SpriteBatch.cell(7));
            SpriteBatch.add(at, size * 0.23, 0.0f, 0xFFFFFF, fade, SpriteBatch.cell(0));
        }
    }

    private static final class Dot {
        class_243 p;
        class_243 previous;
        class_243 v;
        int age;
        int life;
        int cell;
        int color;
        float size;
        boolean hit;

        Dot(class_243 p, class_243 v, int life, int cell, int color, float size, boolean hit) {
            this.p = this.previous = p;
            this.v = v;
            this.life = life;
            this.cell = cell;
            this.color = color;
            this.size = size;
            this.hit = hit;
        }
    }

    private record Flash(class_243 position, long time, int color, float size) {
    }
}

