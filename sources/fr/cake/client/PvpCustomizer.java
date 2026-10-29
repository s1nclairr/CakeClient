/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_310
 *  net.minecraft.class_3283
 *  net.minecraft.class_3288
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.PvpCatalog;
import fr.cake.client.PvpOverrides;
import fr.cake.client.RealisticMode;
import fr.cake.client.Settings;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_3283;
import net.minecraft.class_3288;

public final class PvpCustomizer {
    private static final String PACK = "file/Cake-PvP-Customizer.zip";
    private static boolean loading;
    private static long due;
    private static Object world;
    private static String status;
    private static final String[][] FILES;
    private static final Map<String, BufferedImage> previews;

    public static BufferedImage preview(int item, int variant, int angle) {
        if (variant == 0) {
            return null;
        }
        String key = variant + "-" + item + "-" + angle;
        if (previews.containsKey(key)) {
            return previews.get(key);
        }
        BufferedImage image = null;
        for (int a : new int[]{angle, 0}) {
            try (InputStream in = PvpCustomizer.class.getResourceAsStream("/assets/cakeclient/pvp/previews/" + variant + "-" + item + "-" + a + ".png");){
                if (in == null) continue;
                image = ImageIO.read(in);
                break;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        previews.put(key, image);
        return image;
    }

    public static boolean loading() {
        return loading;
    }

    public static String status() {
        return status;
    }

    public static String label(int index) {
        return PvpCatalog.get(index).name();
    }

    public static void cycle(int item, int direction) {
        Settings s = CakeClient.settings;
        s.pvpTextures[item] = PvpCatalog.next(item, s.pvpTextures[item], direction);
        s.changed();
        s.save();
        due = System.nanoTime() + 650000000L;
    }

    public static void tick() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            world = null;
            return;
        }
        if (world != mc.method_1562()) {
            world = mc.method_1562();
            if (Arrays.stream(CakeClient.settings.pvpTextures).anyMatch(x -> x > 0) || mc.method_1520().method_29210().stream().anyMatch(x -> x.endsWith("Cake-PvP-Customizer.zip"))) {
                due = System.nanoTime() + 1000000000L;
            }
        }
        if (due != 0L && System.nanoTime() >= due && !loading && !RealisticMode.loading()) {
            due = 0L;
            PvpCustomizer.apply();
        }
    }

    public static void apply() {
        if (loading) {
            return;
        }
        loading = true;
        class_310 mc = class_310.method_1551();
        try {
            Path dir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
            Files.createDirectories(dir, new FileAttribute[0]);
            Path temp = dir.resolve("Cake-PvP-Customizer.tmp");
            boolean enabled = false;
            LinkedHashMap<String, byte[]> entries = new LinkedHashMap<String, byte[]>();
            entries.put("pack.mcmeta", "{\"pack\":{\"description\":\"Cake PvP Customizer \u00b7 credits inside\",\"min_format\":[75,0],\"max_format\":[75,0],\"pack_format\":75}}".getBytes(StandardCharsets.UTF_8));
            StringBuilder credits = new StringBuilder("Cake PvP Customizer\nFaithful \u2014 https://faithfulpack.net/\nOriginal independent artist packs below; licenses included.\n");
            for (int item = 0; item < 5; ++item) {
                int v2 = CakeClient.settings.pvpTextures[item];
                if (v2 == 0) continue;
                enabled = true;
                PvpCatalog.Choice choice = PvpCatalog.get(v2);
                credits.append(choice.name()).append(" \u2014 ").append(choice.author()).append(" \u2014 ").append(choice.license()).append('\n').append(choice.source()).append('\n');
                String resource = "/assets/cakeclient/pvp/" + v2 + (String)(v2 <= 8 ? "" : "-" + item) + ".zip";
                try (InputStream in = PvpCustomizer.class.getResourceAsStream(resource);){
                    if (in == null) {
                        throw new IOException("Pack absent : " + choice.name());
                    }
                    try (ZipInputStream zip = new ZipInputStream(in);){
                        ZipEntry e;
                        while ((e = zip.getNextEntry()) != null) {
                            boolean accept;
                            String path = e.getName();
                            if (e.isDirectory() || path.contains("..") || path.startsWith("/")) continue;
                            boolean bl = accept = v2 > 8 || !path.startsWith("assets/");
                            if (v2 <= 8) {
                                for (String file : FILES[item]) {
                                    if (!path.equals("assets/minecraft/textures/" + file) && !path.equals("assets/minecraft/textures/" + file + ".mcmeta")) continue;
                                    accept = true;
                                }
                            }
                            if (!accept) continue;
                            entries.put(path, zip.readAllBytes());
                        }
                        continue;
                    }
                }
            }
            entries.put("CREDITS.txt", credits.toString().getBytes(StandardCharsets.UTF_8));
            for (int i = 1; i <= 2; ++i) {
                try (InputStream in = PvpCustomizer.class.getResourceAsStream("/assets/cakeclient/pvp/LICENSE-" + i + ".txt");){
                    if (in == null) continue;
                    entries.put("LICENSE-Faithful-" + i + ".txt", in.readAllBytes());
                    continue;
                }
            }
            PvpOverrides.install(entries);
            try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(temp, new OpenOption[0]));){
                for (Map.Entry e : entries.entrySet()) {
                    zip.putNextEntry(new ZipEntry((String)e.getKey()));
                    zip.write((byte[])e.getValue());
                    zip.closeEntry();
                }
            }
            Files.move(temp, dir.resolve("Cake-PvP-Customizer.zip"), StandardCopyOption.REPLACE_EXISTING);
            class_3283 repo = mc.method_1520();
            repo.method_14445();
            ArrayList<String> selected = new ArrayList<String>(repo.method_29210());
            selected.removeIf(x -> x.endsWith("Cake-PvP-Customizer.zip"));
            String actual = repo.method_29206().stream().filter(x -> x.endsWith("Cake-PvP-Customizer.zip")).findFirst().orElse(PACK);
            if (enabled) {
                if (!repo.method_29206().contains(actual)) {
                    throw new IOException("Le pack g\u00e9n\u00e9r\u00e9 est refus\u00e9 par Minecraft");
                }
                selected.add(actual);
            }
            repo.method_14447(selected);
            if (enabled && !repo.method_29210().contains(actual)) {
                throw new IOException("Le pack g\u00e9n\u00e9r\u00e9 n\u2019est pas s\u00e9lectionn\u00e9");
            }
            mc.field_1690.field_1887.clear();
            for (class_3288 pack : repo.method_14444()) {
                if (pack.method_14465()) continue;
                mc.field_1690.field_1887.add(pack.method_14463());
            }
            mc.field_1690.field_1846.removeIf(x -> x.endsWith("Cake-PvP-Customizer.zip"));
            mc.field_1690.method_1640();
            status = "Chargement des textures\u2026";
            mc.method_1521().whenComplete((v, error) -> mc.execute(() -> {
                loading = false;
                status = error == null ? "Textures et mod\u00e8les appliqu\u00e9s" : "Chargement impossible \u00b7 voir latest.log";
                CakeClient.settings.changed();
            }));
        }
        catch (Exception e) {
            loading = false;
            status = "Pack indisponible \u00b7 voir latest.log";
            CakeClient.LOG.warn("PvP customizer failed", (Throwable)e);
        }
    }

    static {
        status = "Choisis un objet, inspecte-le puis applique le pack.";
        FILES = new String[][]{{"entity/end_crystal/end_crystal.png"}, {"item/mace.png"}, {"item/totem_of_undying.png"}, {"block/obsidian.png"}, {"block/respawn_anchor_top.png", "block/respawn_anchor_bottom.png", "block/respawn_anchor_side0.png", "block/respawn_anchor_side1.png", "block/respawn_anchor_side2.png", "block/respawn_anchor_side3.png", "block/respawn_anchor_side4.png"}};
        previews = new LinkedHashMap<String, BufferedImage>(16, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> e) {
                return this.size() > 8;
            }
        };
    }
}

