/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_11659$class_11660
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import com.google.gson.Gson;
import fr.cake.client.CakeClient;
import fr.cake.client.CapeCloth;
import fr.cake.client.CosmeticNetwork;
import fr.cake.client.HdWings;
import fr.cake.client.RiggedWings;
import fr.cake.client.Settings;
import fr.cake.client.WingFit;
import fr.cake.protocol.CosmeticState;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class BackCosmetics {
    public static final String[] IDS = new String[]{"angel", "dragon", "bat", "evil", "fire", "parrot", "blue_butterfly", "monarch_butterfly", "fairy", "slime", "astral", "royal", "arctic"};
    public static final String[] NAMES = new String[]{"Celestial", "Dragon", "Drake", "Obsidian", "Golden Angel", "Alpaking", "Blue Butterfly", "Monarch", "Fairy", "Slime", "Account / Astral Cape", "Royal Cape", "Arctic Cape"};
    private static final Map<Boolean, Bone[]> meshes = new HashMap<Boolean, Bone[]>();

    private static Bone[] load(boolean insect) {
        return meshes.computeIfAbsent(insect, k -> {
            Bone[] boneArray;
            block8: {
                InputStream in = BackCosmetics.class.getResourceAsStream("/assets/cakeclient/back/" + (k != false ? "insectoid" : "avian") + ".json");
                try {
                    boneArray = (Bone[])new Gson().fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), Bone[].class);
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
                        CakeClient.LOG.error("Wings import", (Throwable)e);
                        return new Bone[0];
                    }
                }
                in.close();
            }
            return boneArray;
        });
    }

    public static boolean replacesCape(Object raw) {
        class_1297 class_12972;
        block3: {
            block2: {
                class_310 mc = class_310.method_1551();
                if (mc.field_1687 == null || !(raw instanceof class_10055)) break block2;
                class_10055 a = (class_10055)raw;
                class_12972 = mc.field_1687.method_8469(a.field_53528);
                if (class_12972 instanceof class_1657) break block3;
            }
            return false;
        }
        class_1657 p = (class_1657)class_12972;
        CosmeticState state = CosmeticNetwork.get(p.method_5667());
        return state != null && (state.flags() & 0x10) != 0;
    }

    public static void submit(Object raw, class_4587 stack, class_11659 collector, int light) {
        boolean accountCape;
        class_1657 player;
        class_10055 state;
        class_310 mc;
        Settings s;
        block11: {
            block10: {
                s = CakeClient.settings;
                mc = class_310.method_1551();
                if (s == null || mc.field_1724 == null || !(raw instanceof class_10055)) break block10;
                state = (class_10055)raw;
                if (!state.field_53333) break block11;
            }
            return;
        }
        class_1297 entity = mc.field_1687.method_8469(state.field_53528);
        if (!(entity instanceof class_1657) || (player = (class_1657)entity).method_7325() || state.field_53332 > 1600.0) {
            return;
        }
        CosmeticState cosmetic = CosmeticNetwork.get(player.method_5667());
        if (cosmetic == null || (cosmetic.flags() & 0x10) == 0) {
            return;
        }
        int id = Math.clamp((long)cosmetic.back(), 0, 12);
        boolean cape = id >= 10;
        boolean insect = id >= 6 && !cape;
        double time = (double)System.nanoTime() / 1.0E9 * (0.5 + (double)(cosmetic.backSpeed() * 2.0f));
        float scale = (float)(cape ? 0.85 + (double)cosmetic.backScale() * 0.3 : 0.65 + (double)cosmetic.backScale() * 0.35);
        stack.method_22903();
        if (!cape) {
            stack.method_22904(0.0, (double)WingFit.shoulderY(s.backLift, cosmetic.skin(), (cosmetic.flags() & 8) != 0), 0.025);
        }
        stack.method_22905(scale, scale, scale);
        if (player.method_18276()) {
            stack.method_22904(0.0, 0.15, 0.08);
            stack.method_22907((Quaternionfc)new Quaternionf().rotationX(0.3f));
        }
        if (id == 0 || id == 3 || id == 4) {
            HdWings.submit(stack, collector, light, id, time, s.backHalo);
            stack.method_22909();
            return;
        }
        if (RiggedWings.MODELS.containsKey(id)) {
            RiggedWings.submit(stack, collector, light, id, time, s.backHalo);
            stack.method_22909();
            return;
        }
        boolean bl = accountCape = id == 10 && state.field_53520 != null && state.field_53520.comp_1627() != null;
        class_2960 texture = accountCape ? state.field_53520.comp_1627().comp_3627() : class_2960.method_60655((String)"cakeclient", (String)("back/" + IDS[id] + (cape ? ".png" : "_wings.png")));
        class_1921 type = class_12249.method_75994((class_2960)texture);
        Matrix4f source = new Matrix4f((Matrix4fc)stack.method_23760().method_23761());
        class_11659.class_11660 geometry = (pose, v) -> {
            if (cape) {
                CapeCloth.draw(v, pose.method_23761(), player, cosmetic.backSpeed(), accountCape, light);
                return;
            }
            HashMap<String, Matrix4f> transforms = new HashMap<String, Matrix4f>();
            for (Bone b : BackCosmetics.load(insect)) {
                Matrix4f m = b.parent.isEmpty() ? new Matrix4f((Matrix4fc)pose.method_23761()) : new Matrix4f((Matrix4fc)transforms.get(b.parent));
                m.translate(b.pivot[0] / 16.0f, b.pivot[1] / 16.0f, b.pivot[2] / 16.0f);
                float side = b.name.contains("Right") ? -1.0f : 1.0f;
                String n = b.name;
                if (insect && n.startsWith("wing")) {
                    m.rotateY(side * (0.35f + (float)Math.sin(time * 3.8) * 0.32f));
                } else if (n.startsWith("coracoid")) {
                    m.rotateY(side * (-0.4102f + (float)Math.sin(time * 1.5) * 0.12f)).rotateZ(side * (-0.279f + (float)Math.sin(time * 1.5) * 0.06f));
                } else if (n.startsWith("humerus")) {
                    m.rotateY(side * 0.2269f).rotateZ(side * 0.5061f);
                } else if (n.startsWith("ulna")) {
                    m.rotateY(side * 0.2094f).rotateZ(side * (-0.4887f + (float)Math.sin(time * 1.5 - 0.6) * 0.06f));
                } else if (n.startsWith("carpals")) {
                    m.rotateY(side * 0.0698f).rotateZ(side * 0.3194f);
                } else if (n.startsWith("feathersCoracoid")) {
                    m.rotateZ(side * 0.4098f);
                }
                transforms.put(b.name, m);
                for (Shape shape : b.shapes) {
                    BackCosmetics.box(v, m, shape, light);
                }
            }
        };
        collector.method_73483(stack, type, geometry);
        if (!cape && s.backHalo) {
            collector.method_73483(stack, class_12249.method_76014((class_2960)class_2960.method_60655((String)"cakeclient", (String)("back/" + IDS[id] + "-halo.png"))), geometry);
        }
        stack.method_22909();
    }

    private static void box(class_4588 v, Matrix4f m, Shape s, int light) {
        float[] b = s.box;
        float x = b[0] / 16.0f;
        float y = b[1] / 16.0f;
        float z = b[2] / 16.0f;
        float w = b[3] / 16.0f;
        float h = b[4] / 16.0f;
        float d = (s.plane ? 0.14f : b[5]) / 16.0f;
        float u = s.uv[0];
        float t = s.uv[1];
        if (s.plane || d == 0.0f) {
            BackCosmetics.quad(v, m, new float[][]{{x, y, z}, {x + w, y, z}, {x + w, y + h, z}, {x, y + h, z}}, u / 64.0f, t / 64.0f, (u + b[3]) / 64.0f, (t + b[4]) / 64.0f, light);
            if (d > 0.0f) {
                BackCosmetics.quad(v, m, new float[][]{{x + w, y, z + d}, {x, y, z + d}, {x, y + h, z + d}, {x + w, y + h, z + d}}, u / 64.0f, t / 64.0f, (u + b[3]) / 64.0f, (t + b[4]) / 64.0f, light);
            }
            return;
        }
        float[][] p = new float[][]{{x, y, z}, {x + w, y, z}, {x + w, y + h, z}, {x, y + h, z}, {x, y, z + d}, {x + w, y, z + d}, {x + w, y + h, z + d}, {x, y + h, z + d}};
        int[][] faces = new int[][]{{1, 0, 3, 2}, {4, 5, 6, 7}, {0, 4, 7, 3}, {5, 1, 2, 6}, {0, 1, 5, 4}, {3, 7, 6, 2}};
        float a = b[3];
        float c = b[4];
        float e = b[5];
        float[][] uv = new float[][]{{u + e, t + e, u + e + a, t + e + c}, {u + 2.0f * e + a, t + e, u + 2.0f * e + 2.0f * a, t + e + c}, {u, t + e, u + e, t + e + c}, {u + e + a, t + e, u + 2.0f * e + a, t + e + c}, {u + e, t, u + e + a, t + e}, {u + e + a, t, u + e + 2.0f * a, t + e}};
        for (int i = 0; i < 6; ++i) {
            int[] f = faces[i];
            float[] r = uv[i];
            BackCosmetics.quad(v, m, new float[][]{p[f[0]], p[f[1]], p[f[2]], p[f[3]]}, r[0] / 64.0f, r[1] / 64.0f, r[2] / 64.0f, r[3] / 64.0f, light);
        }
    }

    private static void quad(class_4588 v, Matrix4f m, float[][] p, float u, float t, float r, float b, int light) {
        float[][] uv = new float[][]{{u, t}, {r, t}, {r, b}, {u, b}};
        Vector3f normal = new Vector3f(p[1]).sub((Vector3fc)new Vector3f(p[0])).cross((Vector3fc)new Vector3f(p[2]).sub((Vector3fc)new Vector3f(p[0]))).normalize();
        m.transformDirection(normal).normalize();
        for (int i = 0; i < 4; ++i) {
            Vector3f point = m.transformPosition(new Vector3f(p[i]));
            v.method_22912(point.x, point.y, point.z).method_22913(uv[i][0], uv[i][1]).method_1336(255, 255, 255, 255).method_22922(class_4608.field_21444).method_60803(light).method_22914(normal.x, normal.y, normal.z);
        }
    }

    private static class Bone {
        String name;
        String parent;
        float[] pivot;
        Shape[] shapes;

        private Bone() {
        }
    }

    private static class Shape {
        float[] box;
        float[] uv;
        boolean plane;

        private Shape() {
        }
    }
}

