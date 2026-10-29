/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 */
package fr.cake.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cake.client.CakeClient;
import fr.cake.client.KnifeCatalog;
import fr.cake.client.Settings;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;

public final class KnifeAudio {
    private static JsonObject cues;
    private static long serial;
    private static double last;
    private static int model;
    private static final List<class_1109> active;

    private static String source(int m) {
        return switch (KnifeCatalog.IDS[m]) {
            case "classic" -> "css";
            case "huntsman" -> "tactical";
            case "shadow" -> "push";
            case "nomad" -> "canis";
            case "paracord" -> "cord";
            case "survival" -> "outdoor";
            default -> KnifeCatalog.IDS[m];
        };
    }

    public static void stop() {
        class_310 mc = class_310.method_1551();
        for (class_1109 sound : active) {
            mc.method_1483().method_4870((class_1113)sound);
        }
        active.clear();
        serial = -2L;
        last = -0.001;
    }

    public static void frame(int m, String clip, long sequence, double seconds) {
        JsonObject bank;
        Settings s = CakeClient.settings;
        if (!(s.sounds && s.knifeOriginalSounds && class_310.method_1551().field_1755 == null && class_310.method_1551().method_1569())) {
            if (!active.isEmpty()) {
                KnifeAudio.stop();
            }
            return;
        }
        if (cues == null) {
            try (InputStream in = KnifeAudio.class.getResourceAsStream("/assets/cakeclient/knives/audio-cues.json");){
                cues = JsonParser.parseReader((Reader)new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            }
            catch (Exception e) {
                cues = new JsonObject();
                CakeClient.LOG.warn("Original knife cues unavailable", (Throwable)e);
            }
        }
        if (model != m || serial != sequence) {
            KnifeAudio.stop();
            model = m;
            serial = sequence;
        }
        if ((bank = cues.getAsJsonObject(KnifeAudio.source(m))) == null) {
            return;
        }
        JsonArray list = bank.getAsJsonArray(clip);
        if (list == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        active.removeIf(sound -> !mc.method_1483().method_4877((class_1113)sound));
        for (JsonElement item : list) {
            JsonObject c = item.getAsJsonObject();
            double at = c.get("at").getAsDouble();
            if (!(at > last) || !(at <= seconds) || !(seconds - at < 0.18)) continue;
            class_1109 sound2 = class_1109.method_4757((class_3414)class_3414.method_47908((class_2960)class_2960.method_60655((String)"cakeclient", (String)c.get("sound").getAsString())), (float)1.0f, (float)((float)(s.knifeVolume * 0.7)));
            mc.method_1483().method_4873((class_1113)sound2);
            active.add(sound2);
        }
        last = seconds;
    }

    static {
        serial = -2L;
        last = -0.001;
        model = -1;
        active = new ArrayList<class_1109>();
    }
}

