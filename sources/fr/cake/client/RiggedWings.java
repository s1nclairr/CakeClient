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
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import fr.cake.client.AvatarMesh;
import fr.cake.client.CakeClient;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class RiggedWings {
    private static final Map<Integer, AvatarMesh> CACHE = new HashMap<Integer, AvatarMesh>();
    public static final Map<Integer, String> MODELS = Map.of(1, "dragon", 2, "dragon_evolved", 5, "alpaking");

    private static AvatarMesh load(int style) {
        AvatarMesh avatarMesh;
        block8: {
            InputStream in = RiggedWings.class.getResourceAsStream("/assets/cakeclient/back/hd/" + MODELS.get(style) + ".avm");
            try {
                avatarMesh = AvatarMesh.read(in);
                if (in == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    CakeClient.LOG.error("Imported rigged wing", (Throwable)e);
                    return null;
                }
            }
            in.close();
        }
        return avatarMesh;
    }

    public static void submit(class_4587 stack, class_11659 collector, int light, int style, double time, boolean animate) {
        AvatarMesh mesh = CACHE.computeIfAbsent(style, RiggedWings::load);
        if (mesh == null) {
            return;
        }
        Matrix4f[] bones = mesh.pose(animate ? time : 0.0, 0.0, 0.0f);
        stack.method_22903();
        stack.method_22904(0.0, 0.15, 0.17);
        stack.method_22905(1.0f, -1.0f, 1.0f);
        stack.method_22907((Quaternionfc)new Quaternionf().rotationX(-0.6f));
        class_2960 texture = class_2960.method_60655((String)"cakeclient", (String)"avatars/atlas.png");
        collector.method_73483(stack, class_12249.method_75994((class_2960)texture), (pose, v) -> {
            float[] p = mesh.scratch;
            Vector3f point = new Vector3f();
            Vector3f normal = new Vector3f();
            Vector3f temp = new Vector3f();
            for (int n = 0; n < mesh.vertices; ++n) {
                int i = n * 12;
                float[] d = mesh.data;
                point.zero();
                normal.zero();
                for (int k = 0; k < 4; ++k) {
                    float w = d[i + 8 + k];
                    if (w < 1.0E-5f) continue;
                    Matrix4f bone = bones[mesh.joints[n * 4 + k]];
                    bone.transformPosition(d[i], d[i + 1], d[i + 2], temp);
                    point.fma(w, (Vector3fc)temp);
                    bone.transformDirection(d[i + 3], d[i + 4], d[i + 5], temp);
                    normal.fma(w, (Vector3fc)temp);
                }
                pose.method_23761().transformPosition(point);
                pose.method_56820(normal.x, normal.y, normal.z, normal);
                normal.normalize();
                int o = n * 6;
                p[o] = point.x;
                p[o + 1] = point.y;
                p[o + 2] = point.z;
                p[o + 3] = normal.x;
                p[o + 4] = normal.y;
                p[o + 5] = normal.z;
            }
            for (int q = 0; q < mesh.indices.length / 3 * 4; ++q) {
                int n = mesh.indices[q / 4 * 3 + Math.min(q % 4, 2)];
                int o = n * 6;
                int i = n * 12;
                v.method_22912(p[o], p[o + 1], p[o + 2]).method_1336(255, 255, 255, 255).method_22913(mesh.data[i + 6], mesh.data[i + 7]).method_60803(light).method_22922(class_4608.field_21444).method_22914(p[o + 3], p[o + 4], p[o + 5]);
            }
        });
        stack.method_22909();
    }
}

