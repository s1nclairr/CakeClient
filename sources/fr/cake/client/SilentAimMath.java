/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 */
package fr.cake.client;

import net.minecraft.class_243;

public final class SilentAimMath {
    public static Rotation rotation(class_243 eye, class_243 point) {
        class_243 d = point.method_1020(eye);
        return new Rotation((float)Math.toDegrees(Math.atan2(-d.field_1352, d.field_1350)), (float)(-Math.toDegrees(Math.atan2(d.field_1351, Math.hypot(d.field_1352, d.field_1350)))));
    }

    public static double range(double slider, double vanilla) {
        return Math.min(Math.max(0.0, vanilla), 2.0 + Math.clamp(slider, 0.0, 1.0));
    }

    public record Rotation(float yaw, float pitch) {
    }
}

