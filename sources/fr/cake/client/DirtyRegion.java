/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import java.util.Arrays;

public record DirtyRegion(int x, int y, int width, int height) {
    public static DirtyRegion between(int[] next, int[] previous, int width, int height) {
        if (next.length != previous.length || next.length != width * height) {
            throw new IllegalArgumentException("Image size mismatch");
        }
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < height; ++y) {
            int last;
            int row = y * width;
            int first = Arrays.mismatch(next, row, row + width, previous, row, row + width);
            if (first < 0) continue;
            for (last = width - 1; last > first && next[row + last] == previous[row + last]; --last) {
            }
            minX = Math.min(minX, first);
            maxX = Math.max(maxX, last);
            minY = Math.min(minY, y);
            maxY = y;
        }
        return maxX < 0 ? null : new DirtyRegion(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}

