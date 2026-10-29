/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LyricChunks {
    public static List<Chunk> split(String text) {
        String[] words = text.strip().split("\\s+");
        if (words.length == 0 || text.isBlank()) {
            return List.of();
        }
        int groups = Math.max(2, (int)Math.ceil((double)words.length / 3.0));
        if (words.length == 1) {
            groups = 1;
        }
        groups = Math.min(groups, 6);
        ArrayList<Chunk> result = new ArrayList<Chunk>();
        int at = 0;
        int cumulative = 0;
        for (int i = 0; i < groups; ++i) {
            int take = (int)Math.ceil((double)(words.length - at) / (double)(groups - i));
            String part = String.join((CharSequence)" ", Arrays.copyOfRange(words, at, at + take));
            at += take;
            result.add(new Chunk(part, part.length(), cumulative += part.length()));
        }
        return List.copyOf(result);
    }

    public record Chunk(String text, int characters, int cumulative) {
    }
}

