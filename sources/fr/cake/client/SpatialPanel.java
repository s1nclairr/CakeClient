/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.ClientScreen;
import fr.cake.client.WorldQuad;
import net.minecraft.class_1297;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class SpatialPanel {
    private static class_243 anchor;
    private static class_243 right;
    private static class_243 normal;
    private static class_243 up;
    private static Object world;
    private static boolean visible;
    private static boolean dragging;
    private static long appeared;

    public static boolean visible() {
        return visible;
    }

    private static double width() {
        return Math.min(2.7 + CakeClient.settings.panelSize * 1.2, (1.9 + CakeClient.settings.panelDistance * 1.2) * 1.5);
    }

    public static void hide() {
        visible = false;
        dragging = false;
        ClientScreen.canvas.release();
    }

    public static void toggle() {
        if (visible) {
            SpatialPanel.hide();
            return;
        }
        class_310 mc = class_310.method_1551();
        SpatialPanel.pin();
    }

    public static void placeFromMenu() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        CakeClient.settings.spatial = true;
        CakeClient.settings.save();
        SpatialPanel.pin();
        if (mc.field_1755 != null) {
            mc.method_1507(null);
        }
    }

    public static void pin() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        class_4184 camera = mc.field_1773.method_19418();
        class_243 forward = SpatialPanel.look(camera);
        class_243 side = forward.method_1036(new class_243(0.0, 1.0, 0.0));
        if (side.method_1027() < 0.001) {
            side = new class_243(1.0, 0.0, 0.0);
        }
        anchor = camera.method_71156().method_1019(forward.method_1021(1.9 + CakeClient.settings.panelDistance * 1.2));
        normal = forward.method_1021(-1.0);
        right = side.method_1029();
        up = normal.method_1036(right).method_1029();
        world = mc.field_1687;
        visible = true;
        appeared = System.nanoTime();
        ClientScreen.canvas.setSpatial(true);
    }

    public static void tick(class_310 mc) {
        Hit h;
        if (mc.field_1687 == null || mc.field_1724 == null || world != mc.field_1687) {
            if (visible) {
                SpatialPanel.hide();
            }
            anchor = null;
            return;
        }
        if (!CakeClient.settings.spatial && visible) {
            SpatialPanel.hide();
        }
        if (dragging && visible && (h = SpatialPanel.hit(mc.field_1773.method_19418().method_71156(), SpatialPanel.look(mc.field_1773.method_19418()))) != null) {
            ClientScreen.canvas.drag(h.x, h.y);
        }
    }

    public static boolean mouse(int button, int action) {
        class_243 end;
        class_310 mc = class_310.method_1551();
        if (!visible || mc.field_1755 != null || button != 0 || mc.field_1724 == null) {
            return false;
        }
        if (action == 0 && dragging) {
            dragging = false;
            ClientScreen.canvas.release();
            return true;
        }
        if (action != 1) {
            return false;
        }
        Hit h = SpatialPanel.hit(mc.field_1773.method_19418().method_71156(), SpatialPanel.look(mc.field_1773.method_19418()));
        if (h == null) {
            return false;
        }
        class_243 eye = mc.field_1773.method_19418().method_71156();
        class_3965 blocked = mc.field_1687.method_17742(new class_3959(eye, end = eye.method_1019(SpatialPanel.look(mc.field_1773.method_19418()).method_1021(h.distance)), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)mc.field_1724));
        if (blocked.method_17783() != class_239.class_240.field_1333 && blocked.method_17784().method_1025(eye) < h.distance * h.distance - 0.02) {
            return false;
        }
        dragging = true;
        ClientScreen.canvas.click(h.x, h.y);
        return true;
    }

    public static class_243 look(class_4184 camera) {
        Vector3f v = new Vector3f(0.0f, 0.0f, -1.0f).rotate((Quaternionfc)camera.method_23767());
        return new class_243((double)v.x, (double)v.y, (double)v.z).method_1029();
    }

    public static Hit hit(class_243 eye, class_243 direction) {
        if (anchor == null || right == null || normal == null) {
            return null;
        }
        return SpatialPanel.intersect(eye, direction, anchor, right, normal, SpatialPanel.width(), SpatialPanel.width() * 9.0 / 16.0);
    }

    public static Hit intersect(class_243 eye, class_243 direction, class_243 center, class_243 axis, class_243 normal, double width, double height) {
        double denominator = direction.method_1026(normal);
        if (Math.abs(denominator) < 1.0E-7) {
            return null;
        }
        double t = center.method_1020(eye).method_1026(normal) / denominator;
        if (t <= 0.0 || t > 32.0) {
            return null;
        }
        class_243 p = eye.method_1019(direction.method_1021(t)).method_1020(center);
        double u = p.method_1026(axis) / width + 0.5;
        double v = 0.5 - p.method_1026(normal.method_1036(axis).method_1029()) / height;
        if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0) {
            return null;
        }
        if (eye.method_1020(center).method_1026(normal) < 0.0) {
            u = 1.0 - u;
        }
        return new Hit(u * 1920.0, v * 1080.0, t);
    }

    public static void render(class_4184 camera, Matrix4f matrix) {
        class_310 mc = class_310.method_1551();
        if (!visible || mc.field_1755 != null || anchor == null || mc.field_1687 != world) {
            return;
        }
        if (anchor.method_1020(camera.method_71156()).method_1026(SpatialPanel.look(camera)) < -SpatialPanel.width()) {
            return;
        }
        boolean back = camera.method_71156().method_1020(anchor).method_1026(normal) < 0.0;
        Hit h = SpatialPanel.hit(camera.method_71156(), SpatialPanel.look(camera));
        ClientScreen.canvas.setSpatial(true);
        ClientScreen.canvas.update(h == null ? -1.0 : h.x, h == null ? -1.0 : h.y);
        float opacity = (float)(1.0 - Math.pow(1.0 - Math.min(1.0, (double)(System.nanoTime() - appeared) / 3.0E8), 3.0));
        WorldQuad.draw(ClientScreen.canvas.texture(), anchor, back ? right.method_1021(-1.0) : right, up, SpatialPanel.width(), SpatialPanel.width() * 9.0 / 16.0, camera.method_71156(), matrix, opacity, 0.0f, false);
    }

    public record Hit(double x, double y, double distance) {
    }
}

