/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class CapeCloth {
    public static final int SEGMENTS = 16;
    private static final Map<UUID, Cloth> states = new LinkedHashMap<UUID, Cloth>();
    private static Object world;

    static float integrate(float position, float velocity, float target, float dt) {
        return velocity + ((target - position) * 52.0f - velocity * 13.0f) * dt;
    }

    private static float[] pose(class_1657 player, double speed) {
        class_310 mc = class_310.method_1551();
        if (world != mc.field_1687) {
            states.clear();
            world = mc.field_1687;
        }
        if (states.size() > 32) {
            states.clear();
        }
        Cloth cloth = states.computeIfAbsent(player.method_5667(), k -> new Cloth());
        long now = System.nanoTime();
        float dt = cloth.time == 0L ? 0.016f : (float)Math.clamp((double)(now - cloth.time) / 1.0E9, 0.0, 0.05);
        cloth.time = now;
        if (mc.method_1493()) {
            return cloth.angle;
        }
        double motion = player.method_18798().method_37267();
        double bend = Math.min(0.9, motion * 3.5) + Math.min(0.35, Math.max(0.0, -player.method_18798().field_1351) * 0.4);
        for (int j = 0; j < 16; ++j) {
            float weight = ((float)j + 1.0f) / 16.0f;
            float target = (float)(0.1 + bend * (double)weight + Math.sin((double)now / 1.0E9 * (1.1 + speed) - (double)j * 0.38) * 0.045 * (double)weight);
            cloth.velocity[j] = CapeCloth.integrate(cloth.angle[j], cloth.velocity[j], target, dt);
            cloth.angle[j] = Math.clamp(cloth.angle[j] + cloth.velocity[j] * dt, -0.03f, 1.25f);
        }
        return cloth.angle;
    }

    public static void draw(class_4588 v, Matrix4f matrix, class_1657 player, double speed, boolean nativeUv, int light) {
        float[] angles = CapeCloth.pose(player, speed);
        float y = 0.08f;
        float z = 0.19f;
        float w = 0.3125f;
        float t = 0.012f;
        for (int i = 0; i < 16; ++i) {
            float nextY = y + (float)Math.cos(angles[i]) * 1.02f / 16.0f;
            float nextZ = z + (float)Math.sin(angles[i]) * 1.02f / 16.0f;
            float a = (float)i / 16.0f;
            float b = ((float)i + 1.0f) / 16.0f;
            float u0 = nativeUv ? 0.015625f : 0.0f;
            float u1 = nativeUv ? 0.171875f : 1.0f;
            float v0 = nativeUv ? (1.0f + a * 16.0f) / 32.0f : a;
            float v1 = nativeUv ? (1.0f + b * 16.0f) / 32.0f : b;
            CapeCloth.face(v, matrix, new float[][]{{-w, y, z}, {w, y, z}, {w, nextY, nextZ}, {-w, nextY, nextZ}}, u0, v0, u1, v1, light);
            CapeCloth.face(v, matrix, new float[][]{{w, y, z + t}, {-w, y, z + t}, {-w, nextY, nextZ + t}, {w, nextY, nextZ + t}}, nativeUv ? 0.1875f : 0.0f, v0, nativeUv ? 0.34375f : 1.0f, v1, light);
            CapeCloth.face(v, matrix, new float[][]{{-w, y, z + t}, {-w, y, z}, {-w, nextY, nextZ}, {-w, nextY, nextZ + t}}, 0.0f, v0, nativeUv ? 0.015625f : 0.02f, v1, light);
            CapeCloth.face(v, matrix, new float[][]{{w, y, z}, {w, y, z + t}, {w, nextY, nextZ + t}, {w, nextY, nextZ}}, nativeUv ? 0.171875f : 0.98f, v0, nativeUv ? 0.1875f : 1.0f, v1, light);
            if (i == 0) {
                CapeCloth.face(v, matrix, new float[][]{{-w, y, z + t}, {w, y, z + t}, {w, y, z}, {-w, y, z}}, u0, 0.0f, u1, nativeUv ? 0.03125f : 0.02f, light);
            }
            if (i == 15) {
                CapeCloth.face(v, matrix, new float[][]{{-w, nextY, nextZ}, {w, nextY, nextZ}, {w, nextY, nextZ + t}, {-w, nextY, nextZ + t}}, u0, nativeUv ? 0.53125f : 0.98f, u1, nativeUv ? 0.5625f : 1.0f, light);
            }
            y = nextY;
            z = nextZ;
        }
    }

    private static void face(class_4588 v, Matrix4f m, float[][] p, float u, float t, float r, float b, int light) {
        float[][] uv = new float[][]{{u, t}, {r, t}, {r, b}, {u, b}};
        Vector3f n = new Vector3f(p[1]).sub((Vector3fc)new Vector3f(p[0])).cross((Vector3fc)new Vector3f(p[2]).sub((Vector3fc)new Vector3f(p[0]))).normalize();
        m.transformDirection(n).normalize();
        for (int i = 0; i < 4; ++i) {
            Vector3f q = m.transformPosition(new Vector3f(p[i]));
            v.method_22912(q.x, q.y, q.z).method_22913(uv[i][0], uv[i][1]).method_1336(255, 255, 255, 255).method_22922(class_4608.field_21444).method_60803(light).method_22914(n.x, n.y, n.z);
        }
    }

    private static class Cloth {
        final float[] angle = new float[16];
        final float[] velocity = new float[16];
        long time;

        private Cloth() {
        }
    }
}

