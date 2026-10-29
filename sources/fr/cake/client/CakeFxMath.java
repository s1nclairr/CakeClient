/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import java.awt.Color;

public final class CakeFxMath {
    private CakeFxMath() {
    }

    public static double[] locate(double x, double y, double z, int type, double phase) {
        double radius = type == 6 ? 1.05 : 0.17;
        return new double[]{x + Math.cos(phase) * radius, y + (type == 6 ? 0.12 : 0.13), z + Math.sin(phase) * radius};
    }

    public static float[] rgb(int style) {
        float h = switch (style) {
            case 1 -> 0.68f;
            case 2 -> 0.02f;
            case 3 -> 0.5f;
            default -> 0.77f;
        };
        int color = Color.HSBtoRGB(h, 0.75f, 1.0f);
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f};
    }
}

