/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.ClientScreen;
import java.util.Locale;
import org.lwjgl.glfw.GLFW;

public final class MenuKey {
    public static boolean waiting;

    public static String name() {
        if (waiting) {
            return "Pressez une touche\u2026";
        }
        int k = CakeClient.settings.menuKey;
        if (k == 346) {
            return "ALT GR";
        }
        if (k >= 290 && k <= 314) {
            return "F" + (k - 289);
        }
        String s = GLFW.glfwGetKeyName((int)k, (int)0);
        if (s != null) {
            return s.toUpperCase(Locale.ROOT);
        }
        return switch (k) {
            case 32 -> "ESPACE";
            case 258 -> "TAB";
            case 257 -> "ENTR\u00c9E";
            case 344 -> "SHIFT DROIT";
            case 340 -> "SHIFT";
            case 341 -> "CTRL";
            case 345 -> "CTRL DROIT";
            case 342 -> "ALT";
            default -> "Touche " + k;
        };
    }

    public static boolean capture(int key, int action) {
        if (!waiting) {
            return false;
        }
        if (action != 1) {
            return true;
        }
        if (key == 256) {
            waiting = false;
        } else if (key >= 32 && key <= 348) {
            CakeClient.settings.menuKey = key;
            waiting = false;
            CakeClient.settings.changed();
            CakeClient.settings.save();
        }
        ClientScreen.canvas.invalidate();
        return true;
    }
}

