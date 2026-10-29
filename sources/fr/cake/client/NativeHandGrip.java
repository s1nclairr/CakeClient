/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package fr.cake.client;

import org.joml.Vector3f;

public final class NativeHandGrip {
    public static Vector3f anchor(float pivotX, float pivotY, float pivotZ, float minX, float maxX, float maxY, float minZ, float maxZ, boolean left) {
        float x = (minX + maxX) * 0.5f;
        float y = maxY - 1.0f;
        float z = (minZ + maxZ) * 0.5f;
        float a = left ? -0.1f : 0.1f;
        float c = (float)Math.cos(a);
        float s = (float)Math.sin(a);
        return new Vector3f((pivotX + c * x - s * y) / 16.0f, (pivotY + s * x + c * y) / 16.0f, (pivotZ + z) / 16.0f);
    }
}

