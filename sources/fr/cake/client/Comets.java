/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1058
 *  net.minecraft.class_1059
 *  net.minecraft.class_10725
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.InsectBatch;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10725;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;

public final class Comets {
    private static Object world;
    private static class_243 anchor;
    private static long born;

    public static void reset() {
        world = null;
        anchor = null;
    }

    private static class_243 at(int i, double t) {
        double phase = (double)i * 2.399963 + t * (0.1 + (double)(i % 4) * 0.018);
        double r = 8.0 + (double)(i % 5) * 2.2;
        return anchor.method_1031(Math.cos(phase) * r, 1.0 + (double)(i % 4) * 2.1 + Math.sin(phase * 0.8 + (double)i) * 2.0, Math.sin(phase) * r);
    }

    public static void render(class_4184 camera, Matrix4f matrix) {
        class_310 mc = class_310.method_1551();
        if (!CakeClient.settings.comets || mc.field_1687 == null) {
            Comets.reset();
            return;
        }
        if (world != mc.field_1687 || anchor == null || anchor.method_1025(camera.method_71156()) > 6400.0) {
            world = mc.field_1687;
            anchor = camera.method_71156();
            born = System.nanoTime();
        }
        class_1059 atlas = mc.method_72703().method_73025(class_10725.field_56389);
        class_1058 sprite = atlas.method_4608(class_2960.method_60655((String)"cakeclient", (String)"mote"));
        double time = (double)(System.nanoTime() - born) / 1.0E9;
        InsectBatch.begin(camera, matrix);
        for (int i = 0; i < 14; ++i) {
            for (int j = 0; j < 14; ++j) {
                double p = (double)j / 14.0;
                double q = (double)(j + 1) / 14.0;
                InsectBatch.ribbon(Comets.at(i, time - (1.0 - p) * 1.7), Comets.at(i, time - (1.0 - q) * 1.7), 0.014 + 0.026 * q, 11823103, (float)(p * p * 0.82), (float)(q * q * 0.82), sprite);
            }
            InsectBatch.add(Comets.at(i, time), 0.075f, 0.0f, 15322879, 0.95f, sprite);
        }
        InsectBatch.end(atlas.method_71659());
    }
}

