/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class KnifeAnimation {
    private static Pose p(float y, float z, float rx, float ry, float rz, float spin, float blade, float left, float right) {
        return new Pose(0.0f, y, z, rx, ry, rz, spin, blade, left, right);
    }

    public static Pose sample(int model, int mode, double progress) {
        float t = (float)Math.clamp(progress, 0.0, 1.0);
        if (mode == 0) {
            return Pose.IDLE;
        }
        if (mode == 1) {
            float e = 1.0f - t;
            return new Pose(0.05f * e, -0.42f * e, 0.16f * e, 25.0f * e, 0.0f, -30.0f * e, 0.0f, 0.0f, 0.0f, model == 1 ? -170.0f * e : 0.0f);
        }
        if (mode == 3 || mode == 4) {
            float swing = (float)Math.sin(Math.PI * (double)t);
            float ease = (float)Math.sin(Math.PI * Math.min(1.0, (double)t * 1.6));
            return new Pose(-0.22f * swing, 0.07f * ease, 0.13f * swing, -20.0f * swing, mode == 4 ? 30.0f * swing : -35.0f * swing, -65.0f * swing, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        float[] times = new float[]{0.0f, 0.12f, 0.28f, 0.44f, 0.6f, 0.78f, 1.0f};
        Pose[] keys = model == 0 ? new Pose[]{Pose.IDLE, KnifeAnimation.p(0.06f, 0.06f, -8.0f, -18.0f, -15.0f, 0.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.1f, 0.08f, 5.0f, -28.0f, 7.0f, 350.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.08f, 0.11f, 14.0f, 12.0f, 25.0f, 710.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.07f, 0.08f, 6.0f, 30.0f, -10.0f, 720.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.04f, 0.03f, -5.0f, 8.0f, -5.0f, 720.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 720.0f, 0.0f, 0.0f, 0.0f)} : (model == 1 ? new Pose[]{Pose.IDLE, KnifeAnimation.p(0.08f, 0.05f, -12.0f, -15.0f, -25.0f, 0.0f, 0.0f, 0.0f, -150.0f), KnifeAnimation.p(0.1f, 0.08f, 12.0f, 5.0f, -55.0f, 0.0f, 20.0f, 170.0f, -15.0f), KnifeAnimation.p(0.12f, 0.08f, 4.0f, 25.0f, 30.0f, 0.0f, 180.0f, 185.0f, -170.0f), KnifeAnimation.p(0.1f, 0.06f, -15.0f, -20.0f, 45.0f, 0.0f, 360.0f, 360.0f, -350.0f), KnifeAnimation.p(0.05f, 0.02f, 8.0f, 12.0f, -12.0f, 0.0f, 540.0f, 360.0f, -180.0f), KnifeAnimation.p(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 720.0f, 720.0f, -720.0f)} : new Pose[]{Pose.IDLE, KnifeAnimation.p(0.06f, 0.05f, -10.0f, -20.0f, -10.0f, 0.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.12f, 0.11f, 5.0f, -55.0f, 12.0f, 0.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.13f, 0.13f, 10.0f, 65.0f, 22.0f, 0.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.09f, 0.08f, -8.0f, 110.0f, -15.0f, 0.0f, 0.0f, 0.0f, 0.0f), KnifeAnimation.p(0.03f, 0.03f, 0.0f, 35.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f), Pose.IDLE});
        for (int i = 0; i < times.length - 1; ++i) {
            if (!(t <= times[i + 1])) continue;
            float a = (t - times[i]) / (times[i + 1] - times[i]);
            a = a * a * (3.0f - 2.0f * a);
            return KnifeAnimation.mix(keys[i], keys[i + 1], a);
        }
        return keys[keys.length - 1];
    }

    public static Pose mix(Pose a, Pose b, float t) {
        return new Pose(KnifeAnimation.lerp(a.x, b.x, t), KnifeAnimation.lerp(a.y, b.y, t), KnifeAnimation.lerp(a.z, b.z, t), KnifeAnimation.lerp(a.rx, b.rx, t), KnifeAnimation.lerp(a.ry, b.ry, t), KnifeAnimation.lerp(a.rz, b.rz, t), KnifeAnimation.lerp(a.spin, b.spin, t), KnifeAnimation.lerp(a.blade, b.blade, t), KnifeAnimation.lerp(a.left, b.left, t), KnifeAnimation.lerp(a.right, b.right, t));
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public record Pose(float x, float y, float z, float rx, float ry, float rz, float spin, float blade, float left, float right) {
        public static final Pose IDLE = new Pose(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }
}

