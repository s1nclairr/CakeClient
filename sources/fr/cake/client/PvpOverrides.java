/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_3255
 *  net.minecraft.class_3262
 *  net.minecraft.class_3262$class_7664
 *  net.minecraft.class_3264
 *  net.minecraft.class_3298
 *  net.minecraft.class_5352
 *  net.minecraft.class_7367
 *  net.minecraft.class_7368
 *  net.minecraft.class_9224
 */
package fr.cake.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3255;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_3298;
import net.minecraft.class_5352;
import net.minecraft.class_7367;
import net.minecraft.class_7368;
import net.minecraft.class_9224;

public final class PvpOverrides
extends class_3255 {
    private static volatile PvpOverrides active;
    private final Map<String, byte[]> files;

    private PvpOverrides(Map<String, byte[]> files) {
        super(new class_9224("cake-pvp-runtime", (class_2561)class_2561.method_43470((String)"Cake PvP"), class_5352.field_25348, Optional.empty()));
        this.files = Map.copyOf(files);
    }

    public static void install(Map<String, byte[]> files) {
        active = new PvpOverrides(files);
    }

    public static Optional<class_3298> find(class_2960 id) {
        PvpOverrides pack = active;
        if (pack == null) {
            return Optional.empty();
        }
        byte[] data = pack.files.get("assets/" + id.method_12836() + "/" + id.method_12832());
        if (data == null) {
            return Optional.empty();
        }
        return Optional.of(new class_3298((class_3262)pack, () -> new ByteArrayInputStream(data), () -> {
            byte[] meta = pack.files.get("assets/" + id.method_12836() + "/" + id.method_12832() + ".mcmeta");
            return meta == null ? class_7368.field_38688 : class_7368.method_43042((InputStream)new ByteArrayInputStream(meta));
        }));
    }

    public static void append(String path, Predicate<class_2960> filter, Map<class_2960, class_3298> result) {
        PvpOverrides pack = active;
        if (pack == null) {
            return;
        }
        for (Map.Entry<String, byte[]> entry : pack.files.entrySet()) {
            class_2960 id;
            String n = entry.getKey();
            if (!n.startsWith("assets/minecraft/") || n.endsWith(".mcmeta") || !(id = class_2960.method_60656((String)n.substring(17))).method_12832().startsWith(path) || !filter.test(id)) continue;
            result.put(id, PvpOverrides.find(id).orElseThrow());
        }
    }

    public class_7367<InputStream> method_14410(String ... path) {
        byte[] b = this.files.get(String.join((CharSequence)"/", path));
        return b == null ? null : () -> new ByteArrayInputStream(b);
    }

    public class_7367<InputStream> method_14405(class_3264 type, class_2960 id) {
        if (type != class_3264.field_14188) {
            return null;
        }
        byte[] b = this.files.get("assets/" + id.method_12836() + "/" + id.method_12832());
        return b == null ? null : () -> new ByteArrayInputStream(b);
    }

    public Set<String> method_14406(class_3264 type) {
        return Set.of("minecraft");
    }

    public void method_14408(class_3264 type, String namespace, String path, class_3262.class_7664 output) {
        for (Map.Entry<String, byte[]> e : this.files.entrySet()) {
            String prefix = "assets/" + namespace + "/";
            if (!e.getKey().startsWith(prefix + path)) continue;
            class_2960 id = class_2960.method_60655((String)namespace, (String)e.getKey().substring(prefix.length()));
            output.accept((Object)id, () -> new ByteArrayInputStream((byte[])e.getValue()));
        }
    }

    public void close() {
    }
}

