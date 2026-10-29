/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2828$class_2831
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_634
 *  net.minecraft.class_742
 *  net.minecraft.class_746
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Knives;
import fr.cake.client.Settings;
import fr.cake.client.SilentAimMath;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_634;
import net.minecraft.class_742;
import net.minecraft.class_746;

public final class PlayerFollow {
    private static class_1657 target;
    private static Object world;
    private static boolean armed;

    public static String label() {
        return !armed ? "Silent Aura \u00b7 V: activate" : (target == null ? "Silent Aura \u00b7 searching \u00b7 V: stop" : "Silent Aura \u00b7 " + target.method_5477().getString() + " \u00b7 V: stop");
    }

    public static void clear() {
        target = null;
        armed = false;
    }

    public static void select() {
        armed = !armed;
        target = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void tick(class_310 mc) {
        Settings s;
        if (world != mc.field_1687) {
            PlayerFollow.clear();
            world = mc.field_1687;
        }
        if ((s = CakeClient.settings) == null || !s.followEnabled) {
            PlayerFollow.clear();
            return;
        }
        target = null;
        if (!armed || mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null || mc.method_1562() == null || mc.field_1755 != null || mc.method_1493() || !mc.method_1569() || !mc.field_1724.method_5805() || mc.field_1724.method_7325() || mc.field_1724.method_6115() || s.followSword && !mc.field_1724.method_6047().method_31573(class_3489.field_42611)) {
            return;
        }
        class_746 player = mc.field_1724;
        class_243 eye = player.method_33571();
        double distance = SilentAimMath.range(s.followRange, player.method_55755());
        double best = distance * distance;
        class_243 aim = null;
        for (class_742 p : mc.field_1687.method_18456()) {
            class_243 point;
            double d;
            if (p == player || !p.method_5805() || p.method_31481() || p.method_7325() || p.method_5767() || player.method_5722((class_1297)p) || !player.method_6057((class_1297)p) || !((d = eye.method_1025(point = p.method_73189().method_1031(0.0, (double)p.method_17682() * 0.65, 0.0))) < best)) continue;
            best = d;
            target = p;
            aim = point;
        }
        if (target == null || player.method_7261(0.0f) < 0.95f) {
            return;
        }
        SilentAimMath.Rotation rotation = SilentAimMath.rotation(eye, aim);
        float yaw = player.method_36454();
        float pitch = player.method_36455();
        class_634 connection = mc.method_1562();
        connection.method_52787((class_2596)new class_2828.class_2831(rotation.yaw(), rotation.pitch(), player.method_24828(), player.field_5976));
        try {
            mc.field_1761.method_2918((class_1657)player, (class_1297)target);
            player.method_6104(class_1268.field_5808);
            Knives.attack(0);
        }
        finally {
            connection.method_52787((class_2596)new class_2828.class_2831(yaw, pitch, player.method_24828(), player.field_5976));
        }
    }

    static class_243 redirect(class_243 input, double playerYaw, double dx, double dz) {
        if (input.method_37268() < 1.0E-12) {
            return input;
        }
        double yaw = Math.toDegrees(Math.atan2(-dx, dz));
        return input.method_1024((float)(-Math.toRadians(yaw - playerYaw)));
    }

    public static class_243 direction(Object entity, class_243 input) {
        return input;
    }
}

