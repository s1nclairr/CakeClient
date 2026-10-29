/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1306
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3489
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_5603
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 *  net.minecraft.class_630$class_628
 *  net.minecraft.class_742
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.CsKnifeAsset;
import fr.cake.client.KnifeAudio;
import fr.cake.client.KnifeCatalog;
import fr.cake.client.KnifePlayback;
import fr.cake.client.KnifeTrail;
import fr.cake.client.NativeHandGrip;
import fr.cake.client.mixin.ModelCubesAccess;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1306;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3489;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_5603;
import net.minecraft.class_591;
import net.minecraft.class_630;
import net.minecraft.class_742;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Knives {
    private static final String[] NAMES = KnifeCatalog.IDS;
    private static CsKnifeAsset asset;
    private static NativeMesh[] meshes;
    private static final class_1921[][] TYPES;
    private static int loaded;
    private static int inspectVariant;
    private static int drawVariant;
    private static boolean wasEligible;
    private static boolean failed;
    private static KnifePlayback playback;
    private static long lastSerial;
    private static long transition;
    private static Matrix4f[] pose;
    private static Matrix4f[] previousPose;
    private static Matrix4f arm;
    private static Matrix4f previousArm;
    private static Matrix4f leftArm;
    private static Matrix4f previousLeft;
    private static boolean supportHand;
    private static boolean cake$inspectHeld;

    private static class_2960 id(String p) {
        return class_2960.method_60655((String)"cakeclient", (String)p);
    }

    public static boolean eligible() {
        class_310 mc = class_310.method_1551();
        return CakeClient.settings != null && CakeClient.settings.knives && mc.field_1724 != null && mc.field_1687 != null && !mc.field_1724.method_7325() && !mc.field_1724.method_6113() && !mc.field_1690.field_1842 && mc.field_1690.method_31044().method_31034() && mc.field_1724.method_6047().method_31573(class_3489.field_42611);
    }

    private static void ensure() {
        int model = CakeClient.settings.knifeModel;
        if (model == loaded) {
            return;
        }
        Knives.close();
        try {
            asset = CsKnifeAsset.load(NAMES[model]);
            meshes = new NativeMesh[Knives.asset.parts.size()];
            for (int i = 0; i < meshes.length; ++i) {
                CsKnifeAsset.Part part = Knives.asset.parts.get(i);
                ByteBuffer b = part.vertices().duplicate().order(ByteOrder.nativeOrder());
                float[] data = new float[part.count() * 8];
                for (int v = 0; v < part.count(); ++v) {
                    int k;
                    int offset = v * 8;
                    for (k = 0; k < 5; ++k) {
                        data[offset + k] = b.getFloat();
                    }
                    b.getInt();
                    for (k = 0; k < 3; ++k) {
                        data[offset + 5 + k] = (float)b.get() / 127.0f;
                    }
                    b.get();
                }
                Knives.meshes[i] = new NativeMesh(part.count(), data);
            }
            loaded = model;
        }
        catch (Exception e) {
            failed = true;
            loaded = model;
            CakeClient.LOG.error("CS knife asset could not load", (Throwable)e);
        }
    }

    private static double speed() {
        return 0.5 + CakeClient.settings.knifeSpeed;
    }

    private static double[] durations() {
        double[] d = new double[Knives.asset.clips.length];
        for (int i = 0; i < d.length; ++i) {
            d[i] = (double)Knives.asset.clips[i].duration / Knives.speed();
        }
        return d;
    }

    public static boolean inspect() {
        int count;
        if (class_310.method_1551().field_1755 != null || !Knives.eligible()) {
            return false;
        }
        Knives.ensure();
        if (asset == null) {
            return false;
        }
        int[] variants = new int[]{1, asset.find("inspect2"), asset.find("inspect3")};
        for (count = 1; count < variants.length && variants[count] >= 0; ++count) {
        }
        playback.play(variants[Math.floorMod(inspectVariant++, count)], System.nanoTime());
        Knives.sound("knife_flip", 0.4f);
        return true;
    }

    public static void attack(int button) {
        if (class_310.method_1551().field_1755 != null || !Knives.eligible() || button != 0 && button != 1) {
            return;
        }
        Knives.ensure();
        if (asset == null) {
            return;
        }
        playback.attack(button == 1, System.nanoTime(), Knives.durations());
        Knives.sound("knife_swing", 0.4f);
    }

    private static Matrix4f base() {
        float y;
        float side = class_310.method_1551().field_1724.method_6068() == class_1306.field_6182 ? -1.0f : 1.0f;
        float scale = (float)(0.85 + CakeClient.settings.knifeScale * 0.3);
        float f = y = loaded == 3 ? -0.055f : -0.045f;
        float z = loaded == 3 ? -0.16f : (loaded == 0 ? -0.1f : -0.13f);
        return new Matrix4f().translation(0.015f * side, y, z).scale(side * scale, scale, scale);
    }

    public static boolean keyEvent(int key, int action) {
        if (key != 71) {
            return false;
        }
        if (action == 0) {
            boolean used = cake$inspectHeld;
            cake$inspectHeld = false;
            return used;
        }
        if (class_310.method_1551().field_1755 != null || !Knives.eligible()) {
            return false;
        }
        if (action == 1) {
            if (!Knives.inspect()) {
                return false;
            }
            cake$inspectHeld = true;
            return true;
        }
        return cake$inspectHeld;
    }

    private static void holdInspection(long now, double[] d) {
        int index;
        int mode = playback.mode();
        if (!cake$inspectHeld || loaded != 1 || !Knives.asset.clips[mode].name.startsWith("inspect")) {
            return;
        }
        CsKnifeAsset.Clip c = Knives.asset.clips[mode];
        playback.skipInspectionHold(now, (double)c.holdStart / Knives.speed(), (double)c.holdEnd / Knives.speed());
        int[] variants = new int[]{1, asset.find("inspect2"), asset.find("inspect3")};
        for (index = 0; index < variants.length - 1 && variants[index] != mode; ++index) {
        }
        int next = variants[(index + 1) % variants.length];
        if (next < 0) {
            next = 1;
        }
        if (playback.loopIfFinished(next, now, d)) {
            Knives.sound("knife_flip", 0.25f);
        }
    }

    private static Matrix4f blend(Matrix4f a, Matrix4f b, float p) {
        Vector3f pos = a.getTranslation(new Vector3f()).lerp((Vector3fc)b.getTranslation(new Vector3f()), p);
        Vector3f scale = a.getScale(new Vector3f()).lerp((Vector3fc)b.getScale(new Vector3f()), p);
        Quaternionf q = a.getUnnormalizedRotation(new Quaternionf()).slerp((Quaternionfc)b.getUnnormalizedRotation(new Quaternionf()), p);
        return new Matrix4f().translationRotateScale((Vector3fc)pos, (Quaternionfc)q, (Vector3fc)scale);
    }

    public static Matrix4f prepare() {
        Knives.ensure();
        if (asset == null) {
            return new Matrix4f();
        }
        long now = System.nanoTime();
        if (!wasEligible) {
            wasEligible = true;
            int other = asset.find("deploy2");
            playback.play(other >= 0 && (drawVariant++ & 1) == 1 ? other : 2, now);
            Knives.sound("knife_draw", 0.4f);
        }
        double[] d = Knives.durations();
        Knives.holdInspection(now, d);
        playback.advance(now, d);
        int mode = playback.mode();
        if (lastSerial != playback.serial()) {
            previousPose = pose;
            previousArm = new Matrix4f((Matrix4fc)arm);
            previousLeft = new Matrix4f((Matrix4fc)leftArm);
            transition = now;
            lastSerial = playback.serial();
        }
        CsKnifeAsset.Clip clip = Knives.asset.clips[mode];
        double t = playback.seconds(now, d, Knives.speed());
        KnifeAudio.frame(loaded, clip.name, playback.serial(), t);
        float mix = (float)Math.clamp((double)(now - transition) / 8.0E7, 0.0, 1.0);
        mix = mix * mix * (3.0f - 2.0f * mix);
        supportHand = clip.hasLeftArm() && (clip.name.startsWith("inspect") || clip.name.startsWith("deploy"));
        leftArm = clip.leftArmMatrix(t);
        if (previousPose != null && mix < 1.0f) {
            leftArm = Knives.blend(previousLeft, leftArm, mix);
        }
        arm = clip.handMatrix(t);
        if (previousPose != null && mix < 1.0f) {
            arm = Knives.blend(previousArm, arm, mix);
        }
        pose = new Matrix4f[meshes.length];
        for (int i = 0; i < pose.length; ++i) {
            Knives.pose[i] = clip.matrix(i, t);
            if (previousPose == null || !(mix < 1.0f)) continue;
            Knives.pose[i] = Knives.blend(previousPose[i], pose[i], mix);
        }
        boolean left = class_310.method_1551().field_1724.method_6068() == class_1306.field_6182;
        return Knives.nativeArm(Knives.base().mul((Matrix4fc)arm), left);
    }

    public static Matrix4f supportArmPose() {
        boolean left = class_310.method_1551().field_1724.method_6068() != class_1306.field_6182;
        return Knives.nativeArm(Knives.base().mul((Matrix4fc)leftArm), left);
    }

    private static Matrix4f nativeArm(Matrix4f matrix, boolean left) {
        class_310 mc = class_310.method_1551();
        class_591 model = (class_591)mc.method_1561().method_74405((class_742)mc.field_1724).method_4038();
        class_630 part = left ? model.field_27433 : model.field_3401;
        class_5603 pose = part.method_41921();
        List<class_630.class_628> cubes = ((ModelCubesAccess)part).cake$cubes();
        if (cubes.isEmpty()) {
            return matrix;
        }
        class_630.class_628 cube = cubes.getFirst();
        Vector3f grip = NativeHandGrip.anchor(pose.comp_2997(), pose.comp_2998(), pose.comp_2999(), cube.field_3645, cube.field_3648, cube.field_3647, cube.field_3643, cube.field_3646, left);
        return matrix.scale(0.32f, 0.58f, 0.32f).translate(-grip.x, -grip.y, -grip.z);
    }

    public static double drawSeconds() {
        Knives.ensure();
        return asset == null ? 1.2 : (double)Knives.asset.clips[2].duration / Knives.speed();
    }

    public static double inspectionSeconds() {
        Knives.ensure();
        return asset == null ? 6.2 : (double)Knives.asset.clips[1].duration / Knives.speed();
    }

    public static boolean supportHand() {
        return asset != null && Knives.asset.clips[playback.mode()].hasLeftArm();
    }

    private static class_1921 type() {
        int f = CakeClient.settings.knifeFinish;
        return class_12249.method_75994((class_2960)Knives.id(f == 0 ? "knives/" + NAMES[loaded] + "-color.png" : "knives/finishes/" + NAMES[loaded] + "-" + f + ".png"));
    }

    public static void submit(class_4587 stack, class_11659 collector) {
        if (asset == null || failed || pose == null) {
            return;
        }
        class_1921 type = Knives.type();
        KnifeTrail.submit(stack, collector, meshes, pose, Knives.base(), arm, playback.mode() != 0);
        for (int part = 0; part < meshes.length; ++part) {
            if (loaded == 10 && part == 0 && !class_310.method_1551().field_1724.method_6079().method_7960()) continue;
            NativeMesh mesh = meshes[part];
            stack.method_22903();
            stack.method_34425((Matrix4fc)Knives.base().mul((Matrix4fc)pose[part]));
            collector.method_73483(stack, type, (transform, vertices) -> {
                float[] d = mesh.data;
                Vector3f point = new Vector3f();
                Vector3f normal = new Vector3f();
                for (int q = 0; q < mesh.count / 3 * 4; ++q) {
                    int v = q / 4 * 3 + Math.min(q % 4, 2);
                    int i = v * 8;
                    transform.method_23761().transformPosition(d[i], d[i + 1], d[i + 2], point);
                    transform.method_56820(d[i + 5], d[i + 6], d[i + 7], normal);
                    vertices.method_22912(point.x, point.y, point.z).method_22913(d[i + 3], d[i + 4]).method_1336(255, 255, 255, 255).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
                }
            });
            stack.method_22909();
        }
    }

    public static void render() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1755 != null || !mc.method_1569()) {
            cake$inspectHeld = false;
        }
        if (!Knives.eligible() || mc.field_1755 != null || !mc.method_1569()) {
            if (!Knives.eligible()) {
                wasEligible = false;
            }
            cake$inspectHeld = false;
            KnifeAudio.stop();
        }
    }

    private static void sound(String n, float v) {
        if (CakeClient.settings.sounds && !CakeClient.settings.knifeOriginalSounds) {
            class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3414.method_47908((class_2960)Knives.id(n)), (float)1.0f, (float)v));
        }
    }

    public static void close() {
        KnifeTrail.clear();
        KnifeAudio.stop();
        meshes = null;
        asset = null;
        loaded = -1;
        failed = false;
        wasEligible = false;
        pose = null;
        previousPose = null;
        lastSerial = -1L;
        arm.identity();
        leftArm.identity();
        cake$inspectHeld = false;
        playback = new KnifePlayback();
    }

    static {
        TYPES = new class_1921[KnifeCatalog.COUNT][14];
        loaded = -1;
        playback = new KnifePlayback();
        lastSerial = -1L;
        arm = new Matrix4f();
        previousArm = new Matrix4f();
        leftArm = new Matrix4f();
        previousLeft = new Matrix4f();
    }

    record NativeMesh(int count, float[] data) {
    }
}

