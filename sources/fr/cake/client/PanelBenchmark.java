/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  net.minecraft.class_310
 */
package fr.cake.client;

import com.google.gson.GsonBuilder;
import fr.cake.client.CakeClient;
import fr.cake.client.ClientScreen;
import fr.cake.client.SpatialPanel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_310;

public final class PanelBenchmark {
    private static final String PATH = System.getProperty("cakeclient.panelBenchmark", "");
    private static final String[] NAMES = new String[]{"warmup", "hidden", "panel_still", "panel_scan", "hidden_scan", "hidden_repeat"};
    private static final List<List<Double>> FRAMES = new ArrayList<List<Double>>();
    private static long start;
    private static long last;
    private static long paintBase;
    private static int phase;
    private static float yaw;
    private static float pitch;
    private static final List<Map<String, Object>> RESULTS;

    public static void frame() {
        int next;
        if (PATH.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        long now = System.nanoTime();
        if (start == 0L) {
            start = now;
            yaw = mc.field_1724.method_36454();
            pitch = mc.field_1724.method_36455();
            for (int i = 0; i < NAMES.length; ++i) {
                FRAMES.add(new ArrayList());
            }
        }
        if ((next = (int)((now - start) / 8000000000L)) != phase) {
            if (phase >= 0) {
                LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
                List<Double> values = FRAMES.get(phase);
                Collections.sort(values);
                row.put("phase", NAMES[phase]);
                row.put("frames", values.size());
                row.put("median_ms", values.isEmpty() ? 0.0 : values.get(values.size() / 2));
                row.put("p95_ms", values.isEmpty() ? 0.0 : values.get((int)((double)(values.size() - 1) * 0.95)));
                row.put("canvas_paints", ClientScreen.canvas.paintCount() - paintBase);
                RESULTS.add(row);
            }
            if (next >= NAMES.length) {
                try {
                    Files.writeString(Path.of(PATH, new String[0]), (CharSequence)new GsonBuilder().setPrettyPrinting().create().toJson(RESULTS), new OpenOption[0]);
                }
                catch (Exception e) {
                    CakeClient.LOG.error("Benchmark output", (Throwable)e);
                }
                mc.method_1592();
                return;
            }
            phase = next;
            paintBase = ClientScreen.canvas.paintCount();
            if (phase == 2) {
                SpatialPanel.pin();
            }
            if (phase == 4) {
                SpatialPanel.hide();
            }
        }
        double elapsed = (double)(now - start) / 1.0E9 - (double)(phase * 8);
        if (last != 0L && elapsed > 2.0) {
            FRAMES.get(phase).add((double)(now - last) / 1000000.0);
        }
        last = now;
        if (phase == 3 || phase == 4) {
            double t = elapsed;
            mc.field_1724.method_36456(yaw + (float)Math.sin(t * 2.2) * 24.0f);
            mc.field_1724.method_36457(pitch + (float)Math.cos(t * 1.7) * 16.0f);
        } else {
            mc.field_1724.method_36456(yaw);
            mc.field_1724.method_36457(pitch);
        }
    }

    static {
        phase = -1;
        RESULTS = new ArrayList<Map<String, Object>>();
    }
}

