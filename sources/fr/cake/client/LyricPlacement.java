/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import java.util.ArrayList;
import java.util.List;

public final class LyricPlacement {
    private static double unit(double value) {
        return Double.isFinite(value) ? Math.clamp(value, 0.0, 1.0) : 0.5;
    }

    public static List<Place> arrange(int count, int line, double distance, double size, double spacing, double height, double verticalFov, double aspect) {
        if (count <= 0) {
            return List.of();
        }
        count = Math.min(6, count);
        distance = Math.max(1.0, distance);
        aspect = Math.clamp(aspect, 0.5, 4.0);
        double halfY = distance * Math.tan(Math.toRadians(Math.clamp(verticalFov, 30.0, 110.0) / 2.0)) * 0.64;
        double halfX = halfY * aspect;
        int columns = count == 1 ? 1 : 2;
        int rows = (count + columns - 1) / columns;
        double gap = 0.14 + LyricPlacement.unit(spacing) * 0.7;
        double centerY = (LyricPlacement.unit(height) - 0.5) * halfY * 0.8;
        double wanted = distance * (0.68 + LyricPlacement.unit(size) * 0.85);
        double width = Math.min(wanted, 2.0 * halfX / ((double)columns + gap * (double)(columns - 1)));
        width = Math.min(width, 2.0 * (halfY - Math.abs(centerY)) / ((double)rows * 384.0 / 1920.0 + (double)(rows - 1) * 0.13));
        double cardHeight = (width *= 0.94) * 384.0 / 1920.0;
        double rowStep = cardHeight + width * 0.13;
        double slackX = Math.max(0.0, halfX - width * ((double)columns + gap * (double)(columns - 1)) / 2.0);
        double shiftX = ((double)Math.floorMod(line, 11) / 5.0 - 1.0) * slackX * 0.85;
        double maxCenter = Math.max(0.0, halfY - ((double)(rows - 1) * rowStep + cardHeight) / 2.0 - cardHeight * 0.2);
        centerY = Math.clamp(centerY + ((double)Math.floorMod(line, 7) / 3.0 - 1.0) * cardHeight * 0.65, -maxCenter, maxCenter);
        ArrayList<Place> places = new ArrayList<Place>(count);
        for (int i = 0; i < count; ++i) {
            int row = i / columns;
            int col = i % columns;
            int inRow = Math.min(columns, count - row * columns);
            double x = ((double)col - (double)(inRow - 1) / 2.0) * width * (1.0 + gap);
            if (Math.floorMod(line, 5) == 4) {
                x = -x;
            }
            double y = centerY + ((double)(rows - 1) / 2.0 - (double)row) * rowStep;
            double stagger = count > 1 && Math.floorMod(line, 3) != 0 ? (double)((i % 2 == 0 ? 1 : -1) * (Math.floorMod(line, 3) == 1 ? 1 : -1)) * cardHeight * 0.16 : 0.0;
            y = Math.clamp(y + stagger, -halfY + cardHeight / 2.0, halfY - cardHeight / 2.0);
            places.add(new Place(x += shiftX, y, width, cardHeight));
        }
        return List.copyOf(places);
    }

    public record Place(double x, double y, double width, double height) {
    }
}

