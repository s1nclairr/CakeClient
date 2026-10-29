/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1058
 *  net.minecraft.class_1059
 *  net.minecraft.class_10725
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.InsectBatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10725;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;

public final class Fireflies {
    private static Object world;
    private static class_243 previousPlayer;
    private static int sequence;
    private static final Random RANDOM;
    private static final List<Fly> flies;
    private static class_1058[] sprites;

    public static int count() {
        return 6 + (int)Math.round(CakeClient.settings.density * 18.0);
    }

    public static void reset() {
        flies.clear();
        world = null;
        previousPlayer = null;
        sprites = null;
    }

    public static void tick(class_310 mc) {
        if (mc.field_1687 == null || mc.field_1724 == null || !CakeClient.settings.fireflies) {
            if (!flies.isEmpty()) {
                Fireflies.reset();
            }
            return;
        }
        class_243 p = mc.field_1724.method_73189();
        if (world != mc.field_1687 || previousPlayer == null || p.method_1025(previousPlayer) > 4096.0) {
            Fireflies.reset();
            world = mc.field_1687;
        }
        previousPlayer = p;
        if (mc.method_1493()) {
            return;
        }
        double far = 8.0 + CakeClient.settings.radius * 10.0;
        for (Fly f2 : flies) {
            ++f2.age;
            f2.previous = f2.position;
            f2.time += (0.18 + CakeClient.settings.speed * 1.6) / 20.0;
            f2.position = f2.at();
            if (!(f2.anchor.method_1025(p) > far * far)) continue;
            ++f2.retiring;
        }
        flies.removeIf(f -> f.age >= f.lifetime || f.retiring > 25);
        while (flies.size() > Fireflies.count()) {
            flies.removeLast();
        }
        for (int i = 0; i < 2 && flies.size() < Fireflies.count(); ++i) {
            flies.add(new Fly(p));
        }
    }

    public static void render(class_4184 camera, Matrix4f matrix, float partial) {
        if (!CakeClient.settings.fireflies || flies.isEmpty() || world != class_310.method_1551().field_1687) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_1059 atlas = mc.method_72703().method_73025(class_10725.field_56389);
        if (sprites == null) {
            sprites = new class_1058[4];
            for (int i = 0; i < 4; ++i) {
                Fireflies.sprites[i] = atlas.method_4608(class_2960.method_60655((String)"cakeclient", (String)("firefly_" + i)));
            }
        }
        InsectBatch.begin(camera, matrix);
        for (Fly f : flies) {
            class_243 pos = f.previous.method_35590(f.position, (double)partial);
            if (mc.field_1687.method_8320(class_2338.method_49638((class_2374)pos)).method_26216()) continue;
            double fade = Math.min(1.0, (double)f.age / 20.0) * Math.min(1.0, (double)(f.lifetime - f.age) / 30.0) * (1.0 - (double)f.retiring / 26.0);
            float alpha = (float)(fade * (0.75 + CakeClient.settings.glow * 0.25));
            double size = 0.1 + CakeClient.settings.glow * 0.06;
            InsectBatch.add(pos, size, (float)(Math.sin(f.time * 1.4 + f.phase) * 0.18), CakeClient.settings.color(f.time), alpha, sprites[Math.floorMod((int)(f.time * 30.0 + f.phase), 4)]);
        }
        InsectBatch.end(atlas.method_71659());
    }

    static {
        RANDOM = new Random();
        flies = new ArrayList<Fly>();
    }

    static final class Fly {
        final class_243 anchor;
        final double phase = (double)sequence++ * 2.399963;
        final int lifetime;
        int age;
        int retiring;
        double time;
        class_243 position;
        class_243 previous;

        Fly(class_243 player) {
            double r = 2.2 + RANDOM.nextDouble() * (3.0 + CakeClient.settings.radius * 7.0);
            double a = RANDOM.nextDouble() * 6.283;
            this.anchor = player.method_1031(Math.cos(a) * r, 0.5 + RANDOM.nextDouble() * 2.3, Math.sin(a) * r);
            this.time = RANDOM.nextDouble() * 80.0;
            this.lifetime = 500 + RANDOM.nextInt(500);
            this.previous = this.position = this.at();
        }

        class_243 at() {
            return this.anchor.method_1031(Math.sin(this.time + this.phase) * 0.6 + Math.sin(this.time * 0.37) * 0.2, Math.sin(this.time * 0.8 + this.phase) * 0.3, Math.cos(this.time * 0.71 + this.phase) * 0.6);
        }
    }
}

