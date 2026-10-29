/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package fr.cake.client;

import fr.cake.client.KnifeMesh;
import org.joml.Vector3f;

public final class KnifeModels {
    public static KnifeMesh hand() {
        KnifeMesh m = new KnifeMesh();
        int glove = 2369328;
        int stitch = 0x444955;
        int skin = 12159077;
        m.tube(new Vector3f(0.015f, -0.1f, 0.065f), new Vector3f(0.13f, -0.51f, 0.4f), 0.047f, 0.078f, skin);
        m.tube(new Vector3f(0.045f, -0.22f, 0.16f), new Vector3f(0.14f, -0.55f, 0.43f), 0.063f, 0.085f, 1909549);
        m.tube(new Vector3f(0.041f, -0.21f, 0.155f), new Vector3f(0.051f, -0.245f, 0.18f), 0.069f, 0.069f, stitch);
        m.ellipsoid(0.0f, -0.012f, 0.063f, 0.065f, 0.105f, 0.043f, glove);
        float[] ys = new float[]{0.089f, 0.035f, -0.021f, -0.074f};
        for (int i = 0; i < 4; ++i) {
            float y = ys[i];
            float r = 0.018f - (float)i * 0.001f;
            m.tube(new Vector3f(0.052f, y, 0.066f), new Vector3f(0.004f, y, 0.097f), r, r, glove);
            m.tube(new Vector3f(0.004f, y, 0.097f), new Vector3f(-0.05f, y, 0.061f), r, r * 0.9f, glove);
            m.tube(new Vector3f(-0.05f, y, 0.061f), new Vector3f(-0.033f, y, 0.01f), r * 0.9f, r * 0.75f, glove);
            m.ellipsoid(0.01f, y, 0.096f, r * 1.12f, r * 0.8f, 0.009f, stitch);
        }
        m.tube(new Vector3f(0.055f, -0.055f, 0.062f), new Vector3f(0.079f, 0.005f, 0.049f), 0.023f, 0.02f, glove);
        m.tube(new Vector3f(0.079f, 0.005f, 0.049f), new Vector3f(0.028f, 0.051f, 0.032f), 0.02f, 0.016f, glove);
        m.ellipsoid(0.003f, -0.025f, 0.1f, 0.045f, 0.055f, 0.008f, 3159618);
        return m;
    }

    public static Model build(int type) {
        KnifeMesh grip = new KnifeMesh();
        KnifeMesh blade = new KnifeMesh();
        KnifeMesh left = new KnifeMesh();
        KnifeMesh right = new KnifeMesh();
        int dark = 2501685;
        int edge = 14804973;
        int steel = 11122116;
        if (type == 0) {
            grip.polygon(new float[][]{{-0.031f, -0.115f}, {0.025f, -0.115f}, {0.033f, -0.04f}, {0.025f, 0.076f}, {0.008f, 0.106f}, {-0.022f, 0.08f}, {-0.036f, -0.04f}}, 0.031f, dark, 5265510);
            grip.ring(0.0f, 0.121f, 0.0f, 0.034f, 0.009f, steel);
            blade.blade(new float[][]{{-0.027f, -0.103f}, {-0.072f, -0.13f}, {-0.13f, -0.124f}, {-0.189f, -0.078f}, {-0.225f, -0.008f}, {-0.229f, 0.066f}, {-0.208f, 0.12f}, {-0.194f, 0.025f}, {-0.158f, -0.031f}, {-0.107f, -0.059f}, {-0.039f, -0.059f}}, 0.012f, steel, edge);
            for (y = -0.075f; y < 0.076f; y += 0.036f) {
                grip.tube(new Vector3f(-0.024f, y, 0.019f), new Vector3f(0.021f, y + 0.004f, 0.019f), 0.003f, 0.003f, 6449783);
            }
        } else if (type == 1) {
            blade.blade(new float[][]{{-0.018f, 0.1f}, {-0.024f, 0.25f}, {-0.008f, 0.392f}, {0.026f, 0.292f}, {0.025f, 0.1f}}, 0.009f, steel, edge);
            KnifeModels.handle(left, -0.022f);
            KnifeModels.handle(right, 0.022f);
            grip.tube(new Vector3f(-0.042f, 0.103f, 0.0f), new Vector3f(0.042f, 0.103f, 0.0f), 0.012f, 0.012f, steel);
        } else {
            grip.polygon(new float[][]{{-0.026f, -0.12f}, {0.026f, -0.12f}, {0.032f, -0.082f}, {0.031f, 0.09f}, {-0.031f, 0.09f}, {-0.033f, -0.082f}}, 0.037f, dark, 4410712);
            for (y = -0.095f; y < 0.08f; y += 0.025f) {
                grip.tube(new Vector3f(-0.025f, y, 0.021f), new Vector3f(0.025f, y, 0.021f), 0.003f, 0.003f, 6910330);
            }
            grip.polygon(new float[][]{{-0.081f, 0.09f}, {0.077f, 0.09f}, {0.073f, 0.112f}, {-0.079f, 0.112f}}, 0.029f, steel, edge);
            if (type == 2) {
                blade.blade(new float[][]{{-0.026f, 0.107f}, {-0.027f, 0.31f}, {0.0f, 0.458f}, {0.031f, 0.325f}, {0.029f, 0.108f}}, 0.011f, steel, edge);
                grip.ring(-0.061f, 0.108f, 0.0f, 0.022f, 0.005f, steel);
            } else {
                blade.blade(new float[][]{{-0.029f, 0.105f}, {-0.048f, 0.323f}, {-0.044f, 0.438f}, {0.015f, 0.392f}, {0.06f, 0.315f}, {0.059f, 0.131f}, {0.027f, 0.105f}}, 0.014f, steel, edge);
            }
        }
        for (float y : new float[]{-0.075f, 0.046f}) {
            grip.ellipsoid(0.0f, y, 0.022f, 0.006f, 0.006f, 0.003f, 12107978);
        }
        return new Model(grip, blade, left, right);
    }

    private static void handle(KnifeMesh m, float x) {
        m.polygon(new float[][]{{x - 0.016f, -0.124f}, {x + 0.016f, -0.124f}, {x + 0.02f, -0.087f}, {x + 0.017f, 0.105f}, {x - 0.017f, 0.105f}, {x - 0.02f, -0.087f}}, 0.026f, 3488841, 8753311);
        for (float y = -0.09f; y < 0.085f; y += 0.037f) {
            m.ellipsoid(x, y, 0.015f, 0.008f, 0.013f, 0.0018f, 1185568);
        }
        m.ellipsoid(x, 0.101f, 0.02f, 0.009f, 0.009f, 0.004f, 13752287);
    }

    public record Model(KnifeMesh grip, KnifeMesh blade, KnifeMesh left, KnifeMesh right) {
    }
}

