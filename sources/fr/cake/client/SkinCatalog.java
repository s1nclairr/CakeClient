/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class SkinCatalog {
    public static final int[] IDS = new int[]{28, 30, 31, 32, 24, 25, 26, 27, 21, 22, 20, 23};

    public static boolean available(int id) {
        for (int x : IDS) {
            if (x != id) continue;
            return true;
        }
        return false;
    }
}

