/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package fr.cake.client;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class PvpCatalog {
    public static final List<Choice> CHOICES = PvpCatalog.load();

    private static List<Choice> load() {
        List<Choice> list;
        block8: {
            InputStream in = PvpCatalog.class.getResourceAsStream("/assets/cakeclient/pvp/catalog.json");
            try {
                list = List.of((Choice[])new Gson().fromJson((Reader)new InputStreamReader(in, StandardCharsets.UTF_8), Choice[].class));
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
                    throw new ExceptionInInitializerError(e);
                }
            }
            in.close();
        }
        return list;
    }

    public static Choice get(int id) {
        return CHOICES.get(Math.clamp((long)id, 0, CHOICES.size() - 1));
    }

    public static int valid(int item, int id) {
        return id >= 0 && id < CHOICES.size() && PvpCatalog.get(id).supports(item) ? id : 0;
    }

    public static int next(int item, int id, int direction) {
        int result = id;
        while (!PvpCatalog.get(result = Math.floorMod(result + direction, CHOICES.size())).supports(item)) {
        }
        return result;
    }

    public record Choice(int id, String name, int[] items, String author, String license, String source) {
        public boolean supports(int item) {
            for (int i : this.items) {
                if (i != item) continue;
                return true;
            }
            return false;
        }
    }
}

