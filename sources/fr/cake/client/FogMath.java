/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class FogMath {
    private FogMath() {
    }

    public static float distance(double slider) {
        return 32L + 8L * Math.round(Math.clamp(slider, 0.0, 1.0) * 44.0);
    }

    public static Range range(double distance, double startFraction, double strength, float vanillaEnd) {
        float end = (float)Math.max(1.0, Math.min(distance, (double)vanillaEnd));
        float width = end * (1.0f - (float)Math.clamp(startFraction, 0.0, 0.95)) * (0.2f + 0.8f * (float)Math.clamp(strength, 0.0, 1.0));
        return new Range(Math.max(0.0f, end - width), end);
    }

    public static int chunksFor(float end) {
        return Math.max(2, (int)Math.ceil((double)end / 16.0));
    }

    public record Range(float start, float end) {
    }
}

