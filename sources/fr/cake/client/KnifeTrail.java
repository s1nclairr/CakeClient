/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Knives;
import fr.cake.client.Settings;
import java.awt.Color;
import java.util.ArrayDeque;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class KnifeTrail {
    private static final ArrayDeque<Sample> samples = new ArrayDeque();
    private static int tipPart = -1;
    private static int tipVertex;
    private static Knives.NativeMesh[] cached;
    private static long last;

    public static void clear() {
        samples.clear();
        tipPart = -1;
        cached = null;
    }

    public static void submit(class_4587 stack, class_11659 collector, Knives.NativeMesh[] meshes, Matrix4f[] pose, Matrix4f base, Matrix4f hand, boolean active) {
        Settings s = CakeClient.settings;
        if (!s.knifeTrails) {
            samples.clear();
            return;
        }
        long now = System.nanoTime();
        long life = (long)((0.06 + s.knifeTrailLife * 0.28) * 1.0E9);
        samples.removeIf(p -> now - p.at > life);
        if (cached != meshes) {
            KnifeTrail.clear();
            cached = meshes;
            float far = -1.0f;
            Vector3f grip = hand.getTranslation(new Vector3f());
            for (int part = 0; part < meshes.length; ++part) {
                float[] d = meshes[part].data();
                for (int v2 = 0; v2 < meshes[part].count(); ++v2) {
                    Vector3f p2 = pose[part].transformPosition(new Vector3f(d[v2 * 8], d[v2 * 8 + 1], d[v2 * 8 + 2]));
                    float dist = p2.distanceSquared((Vector3fc)grip);
                    if (!(dist > far)) continue;
                    far = dist;
                    tipPart = part;
                    tipVertex = v2;
                }
            }
        }
        if (active && tipPart >= 0 && now - last > 5000000L) {
            last = now;
            float[] d = meshes[tipPart].data();
            int i = tipVertex * 8;
            Vector3f tip = new Matrix4f((Matrix4fc)base).mul((Matrix4fc)pose[tipPart]).transformPosition(new Vector3f(d[i], d[i + 1], d[i + 2]));
            Vector3f grip = new Matrix4f((Matrix4fc)base).mul((Matrix4fc)hand).getTranslation(new Vector3f());
            samples.add(new Sample(tip, new Vector3f((Vector3fc)grip).lerp((Vector3fc)tip, 0.55f), now));
            while (samples.size() > 28) {
                samples.removeFirst();
            }
        }
        if (samples.size() < 2) {
            return;
        }
        Sample[] points = (Sample[])samples.toArray(Sample[]::new);
        int rgb = Color.HSBtoRGB((float)s.knifeTrailHue, 0.45f, 1.0f);
        class_1921 type = class_12249.method_76000((class_2960)class_2960.method_60655((String)"cakeclient", (String)"textures/knife-trail.png"));
        collector.method_73483(stack, type, (transform, v) -> {
            for (int j = 1; j < points.length; ++j) {
                Sample a = points[j - 1];
                Sample b = points[j];
                Vector3f[] ps = new Vector3f[]{a.root, a.tip, b.tip, b.root};
                for (int k = 0; k < 4; ++k) {
                    Vector3f p = transform.method_23761().transformPosition(new Vector3f((Vector3fc)ps[k]));
                    float fade = (float)Math.max(0.0, 1.0 - (double)(now - (k < 2 ? a.at : b.at)) / (double)life);
                    v.method_22912(p.x, p.y, p.z).method_22913(k == 0 || k == 3 ? 0.0f : 1.0f, k < 2 ? 0.0f : 1.0f).method_1336(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, (int)(fade * 140.0f)).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                }
            }
        });
    }

    private record Sample(Vector3f tip, Vector3f root, long at) {
    }
}

