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
import fr.cake.client.PvpCustomizer;
import fr.cake.client.Settings;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_3283;
import net.minecraft.class_3288;

public final class RealisticMode {
    public static String status = "Disabled \u00b7 enable to apply shader and 256x textures";
    private static Boolean last;
    private static boolean busy;
    private static boolean refresh;
    private static String failure;

    public static boolean active() {
        try {
            return (Boolean)Class.forName("net.irisshaders.iris.Iris").getMethod("isPackInUseQuick", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean loading() {
        return busy;
    }

    public static void refresh() {
        refresh = true;
    }

    public static String selectedPack() {
        return CakeClient.settings.shaderPack == 1 ? "MakeUp-UltraFast-9.6.zip" : "Cake-Realis.zip";
    }

    private static Class<?> iris() throws Exception {
        return Class.forName("net.irisshaders.iris.Iris");
    }

    public static void tick() {
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (mc.field_1687 == null || busy || PvpCustomizer.loading()) {
            return;
        }
        if (last == null) {
            last = s.realistic ? false : s.realismSaved || RealisticMode.packName().equals("Cake-Realis.zip") || RealisticMode.packName().equals("MakeUp-UltraFast-9.6.zip");
            String string = status = s.realistic ? "Applying lighting and textures\u2026" : "Disabled \u00b7 enable to apply shader and 256x textures";
        }
        if (last == s.realistic && !refresh) {
            return;
        }
        refresh = false;
        boolean wanted = s.realistic;
        last = wanted;
        busy = true;
        if (wanted) {
            failure = "";
        }
        try {
            Path dir = FabricLoader.getInstance().getGameDir();
            RealisticMode.install(RealisticMode.selectedPack(), dir.resolve("shaderpacks"));
            RealisticMode.install("Cake-Realism-256x.zip", dir.resolve("resourcepacks"));
            Class<?> cls = RealisticMode.iris();
            Object cfg = cls.getMethod("getIrisConfig", new Class[0]).invoke(null, new Object[0]);
            Class<?> c = cfg.getClass();
            if (wanted && !s.realismSaved) {
                s.previousShader = String.valueOf(((Optional)c.getMethod("getShaderPackName", new Class[0]).invoke(cfg, new Object[0])).orElse(null));
                s.previousShadersEnabled = (Boolean)c.getMethod("areShadersEnabled", new Class[0]).invoke(cfg, new Object[0]);
                s.realismSaved = true;
                s.save();
            }
            c.getMethod("setShaderPackName", String.class).invoke(cfg, wanted ? RealisticMode.selectedPack() : (s.previousShader == null || s.previousShader.equals("null") ? null : s.previousShader));
            c.getMethod("setShadersEnabled", Boolean.TYPE).invoke(cfg, wanted || s.previousShadersEnabled);
            c.getMethod("save", new Class[0]).invoke(cfg, new Object[0]);
            if (wanted) {
                Properties props = new Properties();
                try (ZipFile zip = new ZipFile(dir.resolve("shaderpacks").resolve(RealisticMode.selectedPack()).toFile());){
                    ZipEntry entry = zip.getEntry("shaders/shaders.properties");
                    if (entry != null) {
                        String[] stringArray;
                        Properties full = new Properties();
                        full.load(zip.getInputStream(entry));
                        if (s.shaderPack == 0) {
                            String[] stringArray2 = new String[3];
                            stringArray2[0] = "MEDIUM";
                            stringArray2[1] = "HIGH";
                            stringArray = stringArray2;
                            stringArray2[2] = "ULTRA";
                        } else {
                            String[] stringArray3 = new String[3];
                            stringArray3[0] = "low";
                            stringArray3[1] = "medium";
                            stringArray = stringArray3;
                            stringArray3[2] = "high";
                        }
                        String profile = full.getProperty("profile." + stringArray[s.realismQuality], "");
                        for (String token : profile.split("\\s+")) {
                            if (token.isBlank() || token.startsWith("profile.")) continue;
                            String[] p = token.split("=", 2);
                            props.setProperty(p[0].replace("!", ""), p.length == 2 ? p[1] : (p[0].startsWith("!") ? "false" : "true"));
                        }
                    }
                }
                if (s.shaderPack == 0) {
                    props.setProperty("PBR_TEXTURES", "true");
                    props.setProperty("DOF", "false");
                    props.setProperty("MOTION_BLUR", "false");
                    props.setProperty("EXPOSURE", (new String[]{"0.8", "0.9", "1.0", "1.1", "1.2"})[(int)Math.round(s.realismExposure * 4.0)]);
                    props.setProperty("BLOOM_STRENGTH", (new String[]{"0.5", "0.75", "1.0", "1.5", "2.0"})[(int)Math.round(s.realismBloom * 4.0)]);
                    props.setProperty("PUDDLE_AMOUNT", (new String[]{"0.5", "0.75", "1.0", "1.25", "1.5"})[(int)Math.round(s.realismRain * 4.0)]);
                    props.setProperty("RAIN_OPACITY", (new String[]{"0.5", "0.75", "1.0", "1.5", "2.0"})[(int)Math.round(s.realismRain * 4.0)]);
                    props.setProperty("SATURATION", (new String[]{"1.0", "1.1", "1.2"})[s.realismLook]);
                    props.setProperty("TONEMAP", s.realismLook == 1 ? "1" : "0");
                    props.setProperty("SHARPENING", s.realismLook == 2 ? "0.6" : "0.4");
                } else {
                    props.setProperty("DOF", "false");
                    props.setProperty("MOTION_BLUR", "false");
                    props.setProperty("COLOR_SCHEME", (new String[]{"1", "2", "3"})[s.realismLook]);
                }
                cls.getMethod("queueShaderPackOptionsFromProperties", Properties.class).invoke(null, props);
            }
            class_3283 repo = mc.method_1520();
            repo.method_14445();
            ArrayList<String> selected = new ArrayList<String>(repo.method_29210());
            selected.removeIf(x -> x.endsWith("Cake-Realism-256x.zip"));
            if (wanted) {
                String id = repo.method_29206().stream().filter(x -> x.endsWith("Cake-Realism-256x.zip")).findFirst().orElseThrow();
                selected.add(id);
            }
            repo.method_14447(selected);
            mc.field_1690.field_1887.clear();
            for (class_3288 p : repo.method_14444()) {
                if (p.method_14465()) continue;
                mc.field_1690.field_1887.add(p.method_14463());
            }
            mc.field_1690.method_1640();
            status = "Applying lighting and textures\u2026";
            mc.method_1521().whenComplete((v, error) -> mc.execute(() -> {
                try {
                    if (error != null) {
                        throw new RuntimeException((Throwable)error);
                    }
                    RealisticMode.iris().getMethod("reload", new Class[0]).invoke(null, new Object[0]);
                    Optional stored = (Optional)RealisticMode.iris().getMethod("getStoredError", new Class[0]).invoke(null, new Object[0]);
                    if (wanted && stored.isPresent()) {
                        throw new IllegalStateException("Shader compile failed", (Throwable)stored.get());
                    }
                    Object object = wanted ? RealisticMode.selectedPack() + " + XsRealism 256x active" : (status = failure.isEmpty() ? "Previous shader restored" : failure);
                    if (!wanted) {
                        s.realismSaved = false;
                        s.save();
                    }
                }
                catch (Exception e) {
                    failure = "Realism failed: " + RealisticMode.rootMessage(e) + " \u00b7 config/cake-client/realism-error.txt";
                    status = failure;
                    s.realistic = false;
                    last = true;
                    RealisticMode.report(e);
                    CakeClient.LOG.error("Realistic mode", (Throwable)e);
                }
                finally {
                    busy = false;
                    ++Settings.revision;
                }
            }));
        }
        catch (Exception e) {
            busy = false;
            s.realistic = false;
            last = true;
            failure = "Realism failed: " + RealisticMode.rootMessage(e) + " \u00b7 config/cake-client/realism-error.txt";
            status = failure;
            RealisticMode.report(e);
            CakeClient.LOG.error("Realistic mode", (Throwable)e);
        }
    }

    private static String rootMessage(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private static void report(Exception e) {
        try {
            Files.createDirectories(Settings.DIR, new FileAttribute[0]);
            StringWriter text = new StringWriter();
            e.printStackTrace(new PrintWriter(text));
            Files.writeString(Settings.DIR.resolve("realism-error.txt"), (CharSequence)text.toString(), new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static String packName() {
        try {
            return String.valueOf(RealisticMode.iris().getMethod("getCurrentPackName", new Class[0]).invoke(null, new Object[0]));
        }
        catch (Exception e) {
            return "";
        }
    }

    private static void install(String name, Path directory) throws Exception {
        Files.createDirectories(directory, new FileAttribute[0]);
        Path target = directory.resolve(name);
        Path tmp = directory.resolve(name + ".cake.tmp");
        try (InputStream in = RealisticMode.class.getResourceAsStream("/assets/cakeclient/realism/" + name);){
            if (in == null) {
                throw new IOException(name);
            }
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!Files.exists(target, new LinkOption[0]) || Files.mismatch(tmp, target) != -1L) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.delete(tmp);
        }
    }

    static {
        failure = "";
    }
}

