/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_437
 *  net.minecraft.class_5498
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.ClientScreen;
import fr.cake.client.Knives;
import fr.cake.client.LyricsSky;
import fr.cake.client.MotionBlur;
import fr.cake.client.Settings;
import fr.cake.client.SpatialPanel;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_437;
import net.minecraft.class_5498;

public final class PreviewTrial {
    private static int pending = -1;
    private static class_5498 camera;
    private static boolean back;
    private static boolean active;
    private static boolean spatial;
    private static boolean knife;
    private static boolean inspectPending;
    private static boolean blur;
    private static boolean flies;
    private static boolean sky;
    private static boolean sakura;
    private static boolean motes;
    private static boolean embers;
    private static boolean atmosphere;
    private static long until;
    private static long inspectAt;

    public static boolean active() {
        return active;
    }

    public static void start(int module) {
        pending = module;
    }

    private static void begin(int module) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        if (module == 6 && !mc.field_1724.method_6047().method_31573(class_3489.field_42611)) {
            return;
        }
        PreviewTrial.cancel();
        Settings s = CakeClient.settings;
        camera = module == 20 ? mc.field_1690.method_31044() : null;
        back = s.backEnabled;
        atmosphere = s.atmosphere;
        spatial = SpatialPanel.visible();
        knife = s.knives;
        inspectPending = module == 6;
        blur = s.motionBlur;
        flies = s.fireflies;
        sky = s.sky;
        sakura = s.sakura;
        motes = s.motes;
        embers = s.embers;
        if (mc.field_1755 != null) {
            mc.method_1507(null);
        }
        SpatialPanel.hide();
        if (module == 20) {
            s.backEnabled = true;
            mc.field_1690.method_31043(class_5498.field_26665);
        }
        if (module == 7) {
            s.atmosphere = true;
        }
        if (module == 5) {
            LyricsSky.preview();
        }
        if (module == 6) {
            s.knives = true;
        }
        if (module == 0) {
            s.motionBlur = true;
        }
        if (module == 1) {
            s.fireflies = true;
        }
        if (module == 3) {
            s.sky = true;
        }
        if (module == 4) {
            s.sakura = true;
            s.motes = true;
        }
        long now = System.nanoTime();
        double delay = module == 6 ? Knives.drawSeconds() + 0.15 : 1.0;
        inspectAt = now + (long)(delay * 1.0E9);
        active = true;
        until = now + (long)((module == 6 ? delay + Knives.inspectionSeconds() + 1.0 : 6.0) * 1.0E9);
        s.changed();
    }

    public static void tick(class_310 mc) {
        if (pending >= 0) {
            int module = pending;
            pending = -1;
            PreviewTrial.begin(module);
        }
        if (active && inspectPending && System.nanoTime() > inspectAt) {
            inspectPending = false;
            Knives.inspect();
        }
        if (active && (mc.field_1687 == null || mc.field_1755 != null || System.nanoTime() >= until)) {
            boolean returnMenu = mc.field_1687 != null && mc.field_1755 == null;
            PreviewTrial.cancel();
            if (returnMenu) {
                if (spatial && CakeClient.settings.spatial) {
                    SpatialPanel.toggle();
                } else {
                    mc.method_1507((class_437)new ClientScreen(null));
                }
            }
        }
    }

    public static void cancel() {
        pending = -1;
        if (!active) {
            return;
        }
        active = false;
        LyricsSky.stopPreview();
        Settings s = CakeClient.settings;
        s.backEnabled = back;
        if (camera != null) {
            class_310.method_1551().field_1690.method_31043(camera);
            camera = null;
        }
        s.atmosphere = atmosphere;
        s.knives = knife;
        s.motionBlur = blur;
        s.fireflies = flies;
        s.sky = sky;
        s.sakura = sakura;
        s.motes = motes;
        s.embers = embers;
        s.changed();
        MotionBlur.reset();
    }
}

