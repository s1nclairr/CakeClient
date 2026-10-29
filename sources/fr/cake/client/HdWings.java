/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class HdWings {
    private static Part[] cached;

    public static Part[] read(InputStream in) throws IOException {
        if (in == null) {
            throw new IOException("Missing wing mesh");
        }
        try {
            ByteBuffer b = ByteBuffer.wrap(in.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
            if (b.getInt() != 826562371 || b.getInt() != 4) {
                throw new IOException("Bad wing header");
            }
            Part[] result = new Part[4];
            for (int p = 0; p < 4; ++p) {
                int side = b.getInt();
                int material = b.getInt();
                int n = b.getInt();
                int ni = b.getInt();
                if (side < 0 || side > 1 || material < 0 || material > 1 || n < 3 || n > 20000 || ni < 3 || ni > 60000 || ni % 3 != 0) {
                    throw new IOException("Bad wing counts");
                }
                float[] v = new float[n * 8];
                for (int k = 0; k < v.length; ++k) {
                    v[k] = b.getFloat();
                    if (Float.isFinite(v[k])) continue;
                    throw new IOException("Bad wing vertex");
                }
                int[] indices = new int[ni];
                for (int k = 0; k < ni; ++k) {
                    indices[k] = b.getInt();
                    if (indices[k] >= 0 && indices[k] < n) continue;
                    throw new IOException("Bad wing index");
                }
                result[p] = new Part(side, material, v, indices);
            }
            if (b.hasRemaining()) {
                throw new IOException("Trailing wing bytes");
            }
            return result;
        }
        catch (BufferUnderflowException e) {
            throw new IOException("Truncated wing", e);
        }
    }

    private static Part[] mesh() {
        if (cached == null) {
            try (InputStream in = HdWings.class.getResourceAsStream("/assets/cakeclient/back/hd/angel.cwd");){
                cached = HdWings.read(in);
            }
            catch (IOException e) {
                CakeClient.LOG.error("HD wings", (Throwable)e);
                cached = new Part[0];
            }
        }
        return cached;
    }

    public static void submit(class_4587 stack, class_11659 collector, int light, int style, double time, boolean animated) {
        for (Part part : HdWings.mesh()) {
            float side = part.side == 0 ? 1.0f : -1.0f;
            stack.method_22903();
            stack.method_46416(side * 0.17f, 0.05f, 0.16f);
            float flap = animated ? (float)Math.sin(time * 1.15) * 0.13f : 0.0f;
            stack.method_22907((Quaternionfc)new Quaternionf().rotationY(side * (0.1f + flap)).rotateZ(side * 0.5f));
            stack.method_22905(side * 0.95f, -0.95f, 0.95f);
            class_2960 texture = class_2960.method_60655((String)"cakeclient", (String)("back/hd/" + (part.material == 0 ? "feather" : "wing") + ".png"));
            collector.method_73483(stack, class_12249.method_75994((class_2960)texture), (pose, v) -> {
                Matrix4f m = pose.method_23761();
                float[] a = part.vertices;
                int[] indices = part.indices;
                for (int i = 0; i < indices.length; i += 3) {
                    for (int q = 0; q < 4; ++q) {
                        int k = indices[i + Math.min(q, 2)] * 8;
                        float x = a[k];
                        float y = a[k + 2];
                        float z = a[k + 1];
                        float nx = a[k + 3];
                        float ny = a[k + 5];
                        float nz = a[k + 4];
                        float px = m.m00() * x + m.m10() * y + m.m20() * z + m.m30();
                        float py = m.m01() * x + m.m11() * y + m.m21() * z + m.m31();
                        float pz = m.m02() * x + m.m12() * y + m.m22() * z + m.m32();
                        float dx = m.m00() * nx + m.m10() * ny + m.m20() * nz;
                        float dy = m.m01() * nx + m.m11() * ny + m.m21() * nz;
                        float dz = m.m02() * nx + m.m12() * ny + m.m22() * nz;
                        float inv = 1.0f / (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                        v.method_22912(px, py, pz).method_1336(style == 3 ? 65 : 255, style == 3 ? 61 : (style == 4 ? 205 : 255), style == 3 ? 92 : (style == 4 ? 128 : 255), 255).method_22913(a[k + 6], a[k + 7]).method_22922(class_4608.field_21444).method_60803(light).method_22914(dx * inv, dy * inv, dz * inv);
                    }
                }
            });
            stack.method_22909();
        }
    }

    public record Part(int side, int material, float[] vertices, int[] indices) {
    }
}

