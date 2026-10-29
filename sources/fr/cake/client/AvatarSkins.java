/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import fr.cake.client.AvatarMesh;
import fr.cake.client.CakeClient;
import fr.cake.client.CombatEffects;
import fr.cake.client.CosmeticNetwork;
import fr.cake.client.Edition;
import fr.cake.client.SkinCatalog;
import fr.cake.protocol.CosmeticState;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class AvatarSkins {
    public static final String[] IDS = new String[]{"frog", "chicken", "pigeon", "alpaking", "fish", "glub", "mushnub", "mushroom_king", "cactoro", "ghost", "yeti", "green_blob", "pink_blob", "squidle", "birb", "dino", "bunny", "cat", "alien", "ninja", "sahur", "amogus", "lego", "neighbor", "shrek", "knuckles", "donkey", "sonic", "reserved29", "patrick", "mario", "luigi"};
    public static final String[] NAMES = new String[]{"Grenouille", "Poulet", "Pigeon", "Alpaking", "Poisson pattes", "Glub", "Mini champignon", "Roi champignon", "Cactus", "Fant\u00f4me", "Y\u00e9ti", "Blob vert", "Slime rose", "Pieuvre", "Birb", "Dinosaure", "Lapin costaud", "Chat", "Alien", "Ninja", "Tung Tung Sahur", "Amogus", "Lego \u00b7 LDraw", "Hello Neighbor", "Shrek", "Ugandan Knuckles", "Donkey \u00b7 Shrek", "Sonic", "Reserved", "Patrick", "Mario 64", "Luigi"};
    private static final Map<Integer, AvatarMesh> models = new HashMap<Integer, AvatarMesh>();
    private static final class_1921[] types = new class_1921[4];

    public static CosmeticState state(class_10017 state) {
        class_746 p;
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || !(state instanceof class_10055)) {
            return null;
        }
        class_10055 avatar = (class_10055)state;
        if (mc.field_1724 != null && avatar.field_53528 == mc.field_1724.method_5628()) {
            v0 = mc.field_1724;
        } else {
            class_1297 class_12972 = mc.field_1687.method_8469(avatar.field_53528);
            if (class_12972 instanceof class_1657) {
                class_1657 player = (class_1657)class_12972;
                v0 = player;
            } else {
                v0 = p = null;
            }
        }
        if (p == null || p.method_7325() || p != mc.field_1724 && (p.method_5767() || state.field_53332 > 1600.0)) {
            return null;
        }
        CosmeticState s = p == mc.field_1724 ? CosmeticNetwork.local() : CosmeticNetwork.get(p.method_5667());
        return s != null && (s.flags() & 8) != 0 && SkinCatalog.available(s.skin()) && (p != mc.field_1724 || Edition.skin(Edition.PLAN, s.skin())) ? s : null;
    }

    private static AvatarMesh load(int id) {
        AvatarMesh avatarMesh;
        block8: {
            InputStream in = AvatarSkins.class.getResourceAsStream("/assets/cakeclient/avatars/" + IDS[id - 1] + ".avm");
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
                    CakeClient.LOG.error("Avatar model unavailable", (Throwable)e);
                    return null;
                }
            }
            in.close();
        }
        return avatarMesh;
    }

    private static class_1921 type(int skin) {
        return class_12249.method_75994((class_2960)class_2960.method_60655((String)"cakeclient", (String)(skin <= 20 ? "avatars/atlas.png" : "avatars/" + IDS[skin - 1] + "-atlas.png")));
    }

    public static boolean submit(Object raw, class_4587 stack, class_11659 collector) {
        if (!(raw instanceof class_10042)) {
            return false;
        }
        class_10042 state = (class_10042)raw;
        CosmeticState s = AvatarSkins.state((class_10017)state);
        if (s == null) {
            return false;
        }
        AvatarMesh mesh = models.computeIfAbsent(s.skin(), AvatarSkins::load);
        if (mesh == null) {
            return false;
        }
        double seconds = (double)System.nanoTime() / 1.0E9;
        Matrix4f[] bones = mesh.pose(seconds + (double)((class_10055)state).field_53528 * 0.137, state.field_53450 * 0.13f, Math.min(1.0f, state.field_53451 * 3.0f));
        stack.method_22903();
        stack.method_22904(0.0, 1.501, 0.0);
        float scale = 0.85f + s.skinScale() * 0.3f;
        stack.method_22905(-scale, -scale, scale);
        collector.method_73483(stack, AvatarSkins.type(s.skin()), (pose, vertices) -> {
            float[] p = mesh.scratch;
            Vector3f point = new Vector3f();
            Vector3f normal = new Vector3f();
            Vector3f temp = new Vector3f();
            for (int v = 0; v < mesh.vertices; ++v) {
                int i = v * 12;
                float[] d = mesh.data;
                point.zero();
                normal.zero();
                for (int k = 0; k < 4; ++k) {
                    float w = d[i + 8 + k];
                    if (w < 1.0E-5f) continue;
                    Matrix4f bone = bones[mesh.joints[v * 4 + k]];
                    bone.transformPosition(d[i], d[i + 1], d[i + 2], temp);
                    point.fma(w, (Vector3fc)temp);
                    bone.transformDirection(d[i + 3], d[i + 4], d[i + 5], temp);
                    normal.fma(w, (Vector3fc)temp);
                }
                pose.method_23761().transformPosition(point);
                pose.method_56820(normal.x, normal.y, normal.z, normal);
                normal.normalize();
                int o = v * 6;
                p[o] = point.x;
                p[o + 1] = point.y;
                p[o + 2] = point.z;
                p[o + 3] = normal.x;
                p[o + 4] = normal.y;
                p[o + 5] = normal.z;
            }
            for (int q = 0; q < mesh.indices.length / 3 * 4; ++q) {
                int v = mesh.indices[q / 4 * 3 + Math.min(q % 4, 2)];
                int o = v * 6;
                int i = v * 12;
                vertices.method_22912(p[o], p[o + 1], p[o + 2]).method_22913(mesh.data[i + 6], mesh.data[i + 7]).method_39415(CombatEffects.tint(state, -1)).method_60803(state.field_61820).method_22922(class_4608.field_21444).method_22914(p[o + 3], p[o + 4], p[o + 5]);
            }
        });
        stack.method_22909();
        return true;
    }
}

