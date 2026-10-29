/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class WingFit {
    public static final float FEATHER_OPENING = 0.5f;

    public static float shoulderY(double lift) {
        return WingFit.shoulderY(lift, 0, false);
    }

    public static float shoulderY(double lift, int skin, boolean avatar) {
        float f;
        if (avatar) {
            switch (skin) {
                case 28: {
                    f = 0.45f;
                    break;
                }
                case 26: {
                    f = 0.5f;
                    break;
                }
                case 27: {
                    f = 0.6f;
                    break;
                }
                case 22: 
                case 30: {
                    f = 0.35f;
                    break;
                }
                case 31: 
                case 32: {
                    f = 0.38f;
                    break;
                }
                case 20: 
                case 23: {
                    f = 0.25f;
                    break;
                }
                default: {
                    f = 0.18f;
                    break;
                }
            }
        } else {
            f = 0.14f;
        }
        float base = f;
        return base - 0.28f * ((float)Math.clamp(Double.isFinite(lift) ? lift : 0.5, 0.0, 1.0) - 0.5f);
    }
}

