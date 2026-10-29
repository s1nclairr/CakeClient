/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10042
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import fr.cake.client.SpriteBatch;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.class_10042;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class CombatEffects {
    public static final String[] NAMES = new String[]{"Ascension", "Soul Rift", "Crystal Shatter", "Supernova", "Cyber Fade"};
    private static final Map<Integer, Pending> pending = new HashMap<Integer, Pending>();
    private static final Map<Integer, Long> hits = new HashMap<Integer, Long>();
    private static final WeakHashMap<Object, Integer> states = new WeakHashMap();
    private static final ArrayDeque<Death> deaths = new ArrayDeque();
    private static final Set<Integer> dead = new HashSet<Integer>();
    private static Object world;

    public static void attack(class_1297 e) {
        if (e instanceof class_1309) {
            class_1309 l = (class_1309)e;
            if (pending.size() < 12) {
                pending.put(e.method_5628(), new Pending(l.field_6235, System.nanoTime()));
            }
        }
    }

    public static void capture(class_1309 e, class_10042 state) {
        states.put(state, e.method_5628());
    }

    public static void tick(class_310 mc) {
        if (world != mc.field_1687) {
            pending.clear();
            hits.clear();
            deaths.clear();
            dead.clear();
            states.clear();
            world = mc.field_1687;
        }
        if (mc.field_1687 == null || mc.field_1724 == null || mc.method_1493()) {
            return;
        }
        long now = System.nanoTime();
        pending.entrySet().removeIf(e -> {
            class_1297 entity = mc.field_1687.method_8469(((Integer)e.getKey()).intValue());
            if (entity instanceof class_1309) {
                class_1309 l = (class_1309)entity;
                if (l.field_6235 > ((Pending)e.getValue()).hurt) {
                    hits.put((Integer)e.getKey(), now);
                    return true;
                }
            }
            return now - ((Pending)e.getValue()).time > 900000000L;
        });
        hits.entrySet().removeIf(e -> now - (Long)e.getValue() > 650000000L);
        Settings s = CakeClient.settings;
        if (s.deathEffects) {
            for (class_1297 e2 : mc.field_1687.method_18112()) {
                if (!(e2 instanceof class_1309)) continue;
                class_1309 l = (class_1309)e2;
                if (e2 == mc.field_1724 || e2.method_5767() || e2.method_5858((class_1297)mc.field_1724) > 1024.0) continue;
                if (l.field_6213 > 0 && dead.add(e2.method_5628())) {
                    int color = Color.HSBtoRGB((float)s.hitColorHue, 0.6f, 1.0f) & 0xFFFFFF;
                    deaths.add(new Death(e2.method_73189().method_1031(0.0, 0.2, 0.0), now, s.deathStyle, color, e2.method_5628()));
                    while (deaths.size() > 4) {
                        deaths.removeFirst();
                    }
                }
                if (l.field_6213 != 0) continue;
                dead.remove(e2.method_5628());
            }
            if (dead.size() > 256) {
                dead.clear();
            }
        } else {
            deaths.clear();
        }
        deaths.removeIf(d -> now - d.time > 2200000000L);
    }

    public static boolean tinted(Object state) {
        Integer id = states.get(state);
        return CakeClient.settings.hitColor && id != null && hits.containsKey(id);
    }

    public static int tint(Object state, int argb) {
        Integer id = states.get(state);
        if (id == null || !CombatEffects.tinted(state)) {
            return argb;
        }
        float t = (float)Math.max(0.0, 1.0 - (double)(System.nanoTime() - hits.get(id)) / 6.5E8);
        float strength = (float)CakeClient.settings.hitColorStrength * t;
        int rgb = Color.HSBtoRGB((float)CakeClient.settings.hitColorHue, 0.8f, 1.0f);
        int a = argb >>> 24;
        if (a == 0) {
            a = 255;
        }
        int out = a << 24;
        for (int shift : new int[]{16, 8, 0}) {
            out |= (int)((float)(argb >> shift & 0xFF) * (1.0f - strength) + (float)(rgb >> shift & 0xFF) * strength) << shift;
        }
        return out;
    }

    public static void pose(Object state, class_4587 stack) {
        class_10042 s;
        block10: {
            block9: {
                if (!CakeClient.settings.deathEffects || !(state instanceof class_10042)) break block9;
                s = (class_10042)state;
                if (!(s.field_53449 <= 0.0f)) break block10;
            }
            return;
        }
        float p = Math.clamp(s.field_53449 / 20.0f, 0.0f, 1.0f);
        switch (CakeClient.settings.deathStyle) {
            case 0: {
                stack.method_22904(0.0, (double)(-p) * 0.7, 0.0);
                stack.method_22907((Quaternionfc)new Quaternionf().rotationY(p * 2.0f));
                break;
            }
            case 1: {
                stack.method_22904(0.0, (double)(-p) * 0.2, 0.0);
                stack.method_22905(1.0f - p * 0.65f, 1.0f + p * 0.4f, 1.0f - p * 0.65f);
                break;
            }
            case 2: {
                stack.method_22907((Quaternionfc)new Quaternionf().rotationY(p * 4.0f));
                break;
            }
            case 3: {
                float k = 1.0f + (float)Math.sin((double)p * Math.PI) * 0.3f;
                stack.method_22905(k, k, k);
                break;
            }
            case 4: {
                stack.method_22905(1.0f, Math.max(0.02f, 1.0f - p), 1.0f);
            }
        }
    }

    public static void render() {
        long now = System.nanoTime();
        for (Death d : deaths) {
            double age = (double)(now - d.time) / 1.0E9;
            double p = age / 2.2;
            double fade = Math.pow(Math.max(0.0, 1.0 - p), 1.4);
            double radius = 0.35 + 1.8 * p;
            int cell = switch (d.style) {
                case 1 -> 9;
                case 2 -> 14;
                case 3 -> 13;
                case 4 -> 11;
                default -> 8;
            };
            SpriteBatch.floor(d.at, radius, (float)(age * 0.3), d.color, (float)fade, cell);
            SpriteBatch.floor(d.at.method_1031(0.0, 0.01, 0.0), radius * 0.68, (float)(-age), d.color, (float)(fade * 0.6), 12);
            for (int i = 0; i < 26; ++i) {
                double a = (double)i * 2.399963 + (double)d.seed * 0.13;
                double r = (0.15 + (double)(i % 7) * 0.06) * (1.0 + 2.0 * p);
                double height = age * (0.5 + (double)(i % 5) * 0.15);
                if (d.style == 2) {
                    r *= 2.0;
                }
                if (d.style == 3) {
                    r = age * (0.5 + (double)(i % 5) * 0.16);
                    height = Math.sin((double)i * 2.1) * r + 0.7;
                }
                class_243 at = d.at.method_1031(Math.cos(a + age) * r, height, Math.sin(a + age) * r);
                SpriteBatch.add(at, 0.035 + 0.065 * fade, (float)(a + age), d.color, (float)fade, SpriteBatch.cell(d.style == 2 ? 20 : 0));
                if (i % 3 != 0) continue;
                SpriteBatch.ribbon(at.method_1031(0.0, -0.3 * fade, 0.0), at, 0.022, d.color, 0.0f, (float)fade, SpriteBatch.cell(7));
            }
        }
    }

    private record Pending(int hurt, long time) {
    }

    private record Death(class_243 at, long time, int style, int color, int seed) {
    }
}

