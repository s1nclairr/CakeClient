/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_4063
 *  net.minecraft.class_5365
 */
package fr.cake.client;

import com.google.gson.GsonBuilder;
import fr.cake.client.CakeClient;
import fr.cake.client.CakeOptimize;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_4063;
import net.minecraft.class_5365;

public final class FpsBenchmark {
    private static final String OUTPUT = System.getProperty("cakeclient.fpsBenchmark");
    private static long began;
    private static long last;
    private static int phase;
    private static final List<Double> samples;
    private static final List<Map<String, Object>> result;

    public static void frame() {
        if (OUTPUT == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null || mc.field_1755 != null) {
            return;
        }
        long now = System.nanoTime();
        if (phase < 0) {
            phase = 0;
            began = now;
            class_315 o = mc.field_1690;
            o.method_42503().method_41748((Object)12);
            CakeClient.settings.fpsRenderDistance = 12;
            o.method_42510().method_41748((Object)8);
            o.method_42517().method_41748((Object)1.0);
            o.method_75329().method_41748((Object)class_5365.field_25428);
            o.method_42528().method_41748((Object)class_4063.field_18164);
            o.method_41792().method_41748((Object)true);
            o.method_42435().method_41748((Object)true);
            o.method_42433().method_41748((Object)false);
            o.method_42524().method_41748((Object)260);
            mc.field_1724.field_3944.method_45730("difficulty peaceful");
            mc.field_1724.field_3944.method_45730("time set day");
            mc.field_1724.method_36457(6.0f);
            mc.field_1724.method_36456(0.0f);
            last = now;
            return;
        }
        double elapsed = (double)(now - began) / 1.0E9;
        if (last != 0L && elapsed > 3.0 && (phase == 1 || phase == 3)) {
            samples.add((double)(now - last) / 1000000.0);
        }
        last = now;
        if (elapsed < 12.0) {
            return;
        }
        if (phase == 1 || phase == 3) {
            Collections.sort(samples);
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("phase", phase == 1 ? "baseline" : "maximum");
            row.put("frames", samples.size());
            row.put("median_ms", samples.get(samples.size() / 2));
            row.put("p95_ms", samples.get(Math.min(samples.size() - 1, (int)((double)samples.size() * 0.95))));
            row.put("view_chunks", mc.field_1690.method_42503().method_41753());
            row.put("simulation_chunks", mc.field_1690.method_42510().method_41753());
            row.put("framebuffer", mc.method_1522().field_1482 + "x" + mc.method_1522().field_1481);
            result.add(row);
            samples.clear();
        }
        began = now;
        if (++phase == 2) {
            CakeOptimize.preset(3);
        }
        if (phase == 4) {
            try {
                Files.writeString(Path.of(OUTPUT, new String[0]), (CharSequence)new GsonBuilder().setPrettyPrinting().create().toJson(result), new OpenOption[0]);
            }
            catch (Exception e) {
                CakeClient.LOG.error("FPS benchmark write failed", (Throwable)e);
            }
            mc.method_1592();
        }
    }

    static {
        phase = -1;
        samples = new ArrayList<Double>();
        result = new ArrayList<Map<String, Object>>();
    }
}

