/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class LyricSequence {
    private static double smooth(double n) {
        n = Math.clamp(n, 0.0, 1.0);
        return n * n * (3.0 - 2.0 * n);
    }

    public static double opacity(double progress, double from, double to) {
        if (progress < from) {
            return 0.0;
        }
        double span = Math.max(0.02, to - from);
        double enter = Math.min(0.04, span * 0.18);
        double fadeIn = LyricSequence.smooth((progress - from) / enter);
        double fadeOut = to >= 0.999 ? 1.0 : 1.0 - LyricSequence.smooth((progress - to) / Math.min(0.12, span * 0.4));
        return fadeIn * fadeOut;
    }

    public static double layered(double progress, double from, double hideAt, double fade) {
        if (progress < from) {
            return 0.0;
        }
        return LyricSequence.smooth((progress - from) / 0.035) * (hideAt >= 1.0 ? 1.0 : 1.0 - LyricSequence.smooth((progress - hideAt) / Math.max(0.02, fade)));
    }
}

