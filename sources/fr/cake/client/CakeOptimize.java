/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_3283
 *  net.minecraft.class_4063
 *  net.minecraft.class_4066
 *  net.minecraft.class_5365
 */
package fr.cake.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.cake.client.CakeClient;
import fr.cake.client.FogMath;
import fr.cake.client.FpsPlan;
import fr.cake.client.MotionBlur;
import fr.cake.client.Settings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_3283;
import net.minecraft.class_4063;
import net.minecraft.class_4066;
import net.minecraft.class_5365;

public final class CakeOptimize {
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String PACK = "file/CakeClient-Potato-1.21.11.zip";
    private static final String[] EFFECTS = new String[]{"motionBlur", "atmosphere", "sky", "fireflies", "sakura", "motes", "embers", "snow", "pollen", "comets", "trailEnabled", "jumpEnabled"};
    private static State before;
    private static boolean initialized;
    private static boolean dirty;
    private static boolean loading;
    private static int lastDistance;
    private static String status;

    private CakeOptimize() {
    }

    public static String status() {
        return status;
    }

    private static Path backup() {
        return Settings.DIR.resolveSibling("cake-client-fps-before.json");
    }

    private static State capture() {
        class_315 o = class_310.method_1551().field_1690;
        State a = new State();
        a.view = CakeOptimize.requestedDistance();
        a.simulation = (Integer)o.method_42510().method_41753();
        a.entities = (Double)o.method_42517().method_41753();
        a.particles = ((class_4066)o.method_42475().method_41753()).ordinal();
        a.weather = (Integer)o.method_75333().method_41753();
        a.biome = (Integer)o.method_41805().method_41753();
        a.cap = (Integer)o.method_42524().method_41753();
        a.clouds = ((class_4063)o.method_42528().method_41753()).name();
        a.preset = ((class_5365)o.method_75329().method_41753()).name();
        a.leaves = (Boolean)o.method_75334().method_41753();
        a.transparency = (Boolean)o.method_75337().method_41753();
        a.ao = (Boolean)o.method_41792().method_41753();
        a.shadows = (Boolean)o.method_42435().method_41753();
        a.vsync = (Boolean)o.method_42433().method_41753();
        for (String key : EFFECTS) {
            try {
                a.effects.put(key, Settings.class.getField(key).getBoolean(CakeClient.settings));
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
        return a;
    }

    private static void remember() {
        if (before != null) {
            return;
        }
        try {
            if (Files.exists(CakeOptimize.backup(), new LinkOption[0])) {
                before = (State)JSON.fromJson(Files.readString(CakeOptimize.backup()), State.class);
            }
        }
        catch (Exception e) {
            CakeClient.LOG.warn("Could not read FPS restore point", (Throwable)e);
        }
        if (before == null) {
            before = CakeOptimize.capture();
            try {
                Files.createDirectories(CakeOptimize.backup().getParent(), new FileAttribute[0]);
                Path t = CakeOptimize.backup().resolveSibling(String.valueOf(CakeOptimize.backup().getFileName()) + ".tmp");
                Files.writeString(t, (CharSequence)JSON.toJson((Object)before), new OpenOption[0]);
                Files.move(t, CakeOptimize.backup(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (Exception e) {
                CakeClient.LOG.warn("Could not save FPS restore point", (Throwable)e);
            }
        }
    }

    public static int requestedDistance() {
        Settings s = CakeClient.settings;
        return s.fpsRenderDistance >= 2 ? s.fpsRenderDistance : (Integer)class_310.method_1551().field_1690.method_42503().method_41753();
    }

    public static void tick() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1690 == null) {
            return;
        }
        Settings s = CakeClient.settings;
        if (!initialized) {
            initialized = true;
            if (s.fpsRenderDistance < 2) {
                s.fpsRenderDistance = (Integer)mc.field_1690.method_42503().method_41753();
            }
            CakeOptimize.applyFogLimit();
        }
        int actual = (Integer)mc.field_1690.method_42503().method_41753();
        if (lastDistance >= 0 && actual != lastDistance) {
            s.fpsRenderDistance = actual;
            lastDistance = actual;
        }
        if (s.fogEnabled && s.fogLimitTerrain) {
            CakeOptimize.applyFogLimit();
        }
    }

    public static void applyFogLimit() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1690 == null) {
            return;
        }
        Settings s = CakeClient.settings;
        int value = CakeOptimize.requestedDistance();
        if (s.fogEnabled && s.fogLimitTerrain) {
            value = Math.min(value, FogMath.chunksFor(FogMath.distance(s.fogDistance)));
        }
        if ((Integer)mc.field_1690.method_42503().method_41753() != value) {
            mc.field_1690.method_42503().method_41748((Object)value);
            dirty = true;
        }
        lastDistance = value;
    }

    public static void preset(int level) {
        CakeOptimize.remember();
        class_310 mc = class_310.method_1551();
        class_315 o = mc.field_1690;
        Settings s = CakeClient.settings;
        FpsPlan p = FpsPlan.preset(level, CakeOptimize.before.view, CakeOptimize.before.simulation, CakeOptimize.before.entities);
        o.method_75329().method_41748((Object)class_5365.field_25427);
        s.fpsRenderDistance = p.view();
        o.method_42510().method_41748((Object)p.simulation());
        o.method_42517().method_41748((Object)p.entities());
        o.method_42475().method_41748((Object)class_4066.values()[p.particles()]);
        o.method_75333().method_41748((Object)p.weather());
        o.method_42528().method_41748((Object)class_4063.field_18162);
        o.method_75334().method_41748((Object)p.fastLeaves());
        o.method_75337().method_41748((Object)p.improvedTransparency());
        o.method_41792().method_41748((Object)p.ao());
        o.method_42435().method_41748((Object)false);
        o.method_41805().method_41748((Object)0);
        o.method_42433().method_41748((Object)false);
        o.method_42524().method_41748((Object)260);
        if (s.fpsReduceEffects) {
            CakeOptimize.reduceEffects();
        }
        s.fpsPreset = level;
        dirty = true;
        CakeOptimize.applyFogLimit();
        CakeOptimize.flush();
        s.save();
        status = "Profil actif imm\u00e9diatement \u00b7 " + String.valueOf(o.method_42503().method_41753()) + " chunks";
    }

    public static void reduceEffects() {
        CakeOptimize.remember();
        for (String key : EFFECTS) {
            try {
                Settings.class.getField(key).setBoolean(CakeClient.settings, false);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
        MotionBlur.reset();
        CakeClient.settings.changed();
    }

    public static void edit(Consumer<class_315> action) {
        CakeOptimize.remember();
        action.accept(class_310.method_1551().field_1690);
        CakeClient.settings.fpsPreset = 4;
        dirty = true;
    }

    public static void view(int chunks) {
        CakeOptimize.remember();
        CakeClient.settings.fpsPreset = 4;
        CakeClient.settings.fpsRenderDistance = Math.clamp((long)chunks, 2, 32);
        CakeOptimize.applyFogLimit();
    }

    public static void flush() {
        if (dirty) {
            class_310.method_1551().field_1690.method_1640();
            dirty = false;
        }
    }

    public static void restore() {
        CakeOptimize.remember();
        if (before == null) {
            return;
        }
        State a = before;
        class_315 o = class_310.method_1551().field_1690;
        Settings s = CakeClient.settings;
        o.method_75329().method_41748((Object)class_5365.valueOf((String)a.preset));
        s.fpsRenderDistance = a.view;
        o.method_42510().method_41748((Object)a.simulation);
        o.method_42517().method_41748((Object)a.entities);
        o.method_42475().method_41748((Object)class_4066.values()[Math.clamp((long)a.particles, 0, 2)]);
        o.method_75333().method_41748((Object)a.weather);
        o.method_42528().method_41748((Object)class_4063.valueOf((String)a.clouds));
        o.method_75334().method_41748((Object)a.leaves);
        o.method_75337().method_41748((Object)a.transparency);
        o.method_41792().method_41748((Object)a.ao);
        o.method_42435().method_41748((Object)a.shadows);
        o.method_41805().method_41748((Object)a.biome);
        o.method_42433().method_41748((Object)a.vsync);
        o.method_42524().method_41748((Object)a.cap);
        if (a.effects != null) {
            for (Map.Entry<String, Boolean> e : a.effects.entrySet()) {
                try {
                    Settings.class.getField(e.getKey()).setBoolean(s, e.getValue());
                }
                catch (ReflectiveOperationException reflectiveOperationException) {}
            }
        }
        s.fpsPreset = 0;
        dirty = true;
        CakeOptimize.applyFogLimit();
        CakeOptimize.flush();
        s.save();
        before = null;
        try {
            Files.deleteIfExists(CakeOptimize.backup());
        }
        catch (Exception exception) {
            // empty catch block
        }
        status = "Vos r\u00e9glages pr\u00e9c\u00e9dents ont \u00e9t\u00e9 r\u00e9tablis";
    }

    public static void potato(boolean enabled) {
        if (loading) {
            return;
        }
        loading = true;
        class_310 mc = class_310.method_1551();
        try {
            Path directory = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
            Files.createDirectories(directory, new FileAttribute[0]);
            if (enabled) {
                try (InputStream in = CakeOptimize.class.getResourceAsStream("/assets/cakeclient/potato/CakeClient-Potato-1.21.11.zip");){
                    if (in == null) {
                        throw new IOException("Pack absent");
                    }
                    Path temp = directory.resolve("cake-potato.tmp");
                    Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
                    Files.move(temp, directory.resolve("CakeClient-Potato-1.21.11.zip"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            class_3283 repo = mc.method_1520();
            repo.method_14445();
            ArrayList<String> selected = new ArrayList<String>(repo.method_29210());
            selected.remove(PACK);
            if (enabled) {
                selected.add(PACK);
            }
            repo.method_14447(selected);
            mc.field_1690.method_49598(repo);
            mc.field_1690.method_1640();
            CakeClient.settings.potatoEnabled = enabled;
            CakeClient.settings.save();
            status = "Chargement des textures\u2026";
            mc.method_1521().whenComplete((ok, error) -> mc.execute(() -> {
                loading = false;
                status = error == null ? "Textures " + (enabled ? "Potato" : "personnelles") + " actives" : "\u00c9chec du chargement du pack";
                CakeClient.settings.changed();
            }));
        }
        catch (Exception e) {
            loading = false;
            status = "Chargement impossible : " + e.getClass().getSimpleName();
            CakeClient.LOG.warn("Potato pack failed", (Throwable)e);
        }
    }

    public static String apply(boolean potato, int level) {
        if (level == 0) {
            CakeOptimize.restore();
        } else {
            CakeOptimize.preset(level);
        }
        if (CakeClient.settings.potatoEnabled != potato) {
            CakeOptimize.potato(potato);
        }
        return status;
    }

    static {
        lastDistance = -1;
        status = "R\u00e9glages appliqu\u00e9s en direct";
    }

    static final class State {
        int view;
        int simulation;
        int particles;
        int weather;
        int biome;
        int cap;
        double entities;
        String clouds;
        String preset;
        boolean leaves;
        boolean transparency;
        boolean ao;
        boolean shadows;
        boolean vsync;
        Map<String, Boolean> effects = new LinkedHashMap<String, Boolean>();

        State() {
        }
    }
}

