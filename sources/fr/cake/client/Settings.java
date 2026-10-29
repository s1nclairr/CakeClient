/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonParser
 *  net.fabricmc.loader.api.FabricLoader
 */
package fr.cake.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import fr.cake.client.CakeClient;
import fr.cake.client.Edition;
import fr.cake.client.KnifeCatalog;
import fr.cake.client.PvpCatalog;
import java.awt.Color;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;

public final class Settings {
    public int shaderPack = 0;
    public boolean backHalo = true;
    public double backLift = 0.5;
    public double realismExposure = 0.5;
    public double realismBloom = 0.4;
    public double realismRain = 0.5;
    public int realismLook = 0;
    public boolean realismSaved = false;
    public boolean previousShadersEnabled = false;
    public String previousShader = "";
    public String language = "en";
    public boolean backEnabled = false;
    public boolean realistic = false;
    public boolean crosshairEnabled = false;
    public boolean knifeTrails = false;
    public boolean hitColor = false;
    public boolean deathEffects = false;
    public int backStyle = 0;
    public int realismQuality = 0;
    public int crosshairStyle = 0;
    public int deathStyle = 0;
    public double backScale = 0.5;
    public double backSpeed = 0.4;
    public double crosshairSize = 0.4;
    public double crosshairGap = 0.3;
    public double crosshairThickness = 0.3;
    public double crosshairHue = 0.77;
    public double knifeTrailHue = 0.77;
    public double knifeTrailLife = 0.4;
    public double hitColorHue = 0.77;
    public double hitColorStrength = 0.65;
    public boolean visualizer = false;
    public boolean hitParticles = false;
    public boolean extraParticles = false;
    public int visualizerStyle = 1;
    public int hitParticleStyle = 0;
    public int extraParticleStyle = 0;
    public double visualizerHue = 0.77;
    public double visualizerSize = 0.5;
    public double hitParticleCount = 0.5;
    public double hitParticleSize = 0.4;
    public double hitParticleHue = 0.77;
    public double extraParticleSize = 0.5;
    public double extraParticleHue = 0.77;
    public double musicX = 0.025;
    public double musicY = 0.08;
    public double musicScale = 0.5;
    public double musicOpacity = 0.88;
    public boolean musicArtwork = true;
    public boolean musicBars = true;
    public int[] pvpTextures = new int[]{0, 0, 0, 0, 0};
    public int menuKey = 346;
    public int avatarSkin = 21;
    public boolean shareCosmetics = true;
    public boolean skinEnabled = false;
    public double skinScale = 0.5;
    public boolean followEnabled = false;
    public boolean followSword = true;
    public boolean hitboxEnabled = false;
    public boolean hitboxPlayers = true;
    public boolean hotbarEnabled = false;
    public boolean knifeOriginalSounds = true;
    public double hotbarScale = 0.4;
    public double hotbarLift = 0.0;
    public int hotbarStyle = 0;
    public double followRange = 0.35;
    public double hitboxRange = 0.35;
    public double hitboxPadding = 0.0;
    public double hitboxHue = 0.77;
    public double hotbarHue = 0.77;
    public double hotbarOpacity = 0.75;
    public double knifeVolume = 0.7;
    public double trailSize = 0.4;
    public double trailLife = 0.4;
    public double trailDensity = 0.4;
    public double jumpSize = 0.4;
    public double jumpLife = 0.4;
    public boolean potatoEnabled = false;
    public boolean trailEnabled = false;
    public boolean jumpEnabled = false;
    public int fpsPreset = 0;
    public int trailStyle = 0;
    public int jumpStyle = 0;
    public double trailHue = 0.77;
    public double jumpHue = 0.77;
    public boolean fogEnabled = false;
    public boolean fogLimitTerrain = false;
    public boolean fogSky = true;
    public boolean fpsReduceEffects = true;
    public int fpsRenderDistance = -1;
    public double fogDistance = 0.35;
    public double fogStart = 0.3;
    public double fogOpacity = 1.0;
    public double fogHue = 0.72;
    public double fogSaturation = 0.3;
    public double fogBrightness = 0.72;
    public double fogTint = 0.45;
    public boolean knives = false;
    public int knifeModel = 0;
    public int knifeFinish = 0;
    public double knifeScale = 0.5;
    public double knifeSpeed = 0.5;
    public boolean motionBlur = false;
    public boolean fireflies = false;
    public boolean sounds = true;
    public boolean rainbow = false;
    public double blur = 0.4;
    public double density = 0.35;
    public double speed = 0.35;
    public double radius = 0.5;
    public double glow = 0.4;
    public double hue = 0.76;
    public boolean skyClouds = false;
    public boolean spatial = false;
    public boolean sky = false;
    public boolean sakura = false;
    public boolean motes = false;
    public boolean embers = false;
    public boolean lyrics = false;
    public double menuScale = 0.62;
    public double panelSize = 0.5;
    public double panelDistance = 0.5;
    public int skyMode = 2;
    public double skyHue = 0.73;
    public double skyIntensity = 0.65;
    public double skySpeed = 0.25;
    public double stars = 0.4;
    public double ambientDensity = 0.3;
    public double wind = 0.3;
    public double lyricSize = 0.5;
    public double lyricDistance = 0.45;
    public double lyricOpacity = 0.85;
    public double lyricOffset = 0.5;
    public double lyricHeight = 0.5;
    public double lyricSpacing = 0.4;
    public double lyricOverlap = 0.7;
    public double lyricHue = 0.76;
    public double ambientHue = 0.72;
    public double fogStrength = 0.0;
    public int lyricFont = 0;
    public Map<String, Long> lyricCalibrations = new LinkedHashMap<String, Long>();
    public boolean comets = false;
    public boolean atmosphere = false;
    public boolean snow = false;
    public boolean pollen = false;
    public int atmosphereStyle = 0;
    public double bloom = 0.3;
    public double occlusion = 0.5;
    public double exposure = 0.5;
    public double vignette = 0.25;
    public static final Path DIR = FabricLoader.getInstance().getConfigDir().resolve("cake-client");
    private static final Path LEGACY = DIR.resolveSibling("cake-client.json");
    private static final Path PATH = Edition.PLAN == Edition.Plan.PREMIUM ? LEGACY : DIR.resolveSibling("cake-client-" + Edition.name().toLowerCase(Locale.ROOT) + ".json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static volatile long revision;

    public static Settings load() {
        try {
            Path input;
            Path path = input = Files.exists(PATH, new LinkOption[0]) ? PATH : LEGACY;
            if (Files.exists(input, new LinkOption[0])) {
                String raw = Files.readString(input);
                Settings s = (Settings)GSON.fromJson(raw, Settings.class);
                if (s != null && !JsonParser.parseString((String)raw).getAsJsonObject().has("fogEnabled") && s.fogStrength > 0.0) {
                    s.fogEnabled = true;
                    s.fogHue = s.ambientHue;
                }
                if (s != null) {
                    s.sanitize();
                    return s;
                }
            }
        }
        catch (Exception e) {
            CakeClient.LOG.warn("Invalid configuration; using defaults", (Throwable)e);
        }
        Settings s = new Settings();
        s.sanitize();
        return s;
    }

    private static double c(double n, double fallback) {
        return Double.isFinite(n) ? Math.clamp(n, 0.0, 1.0) : fallback;
    }

    public void sanitize() {
        this.shaderPack = Math.clamp((long)this.shaderPack, 0, 1);
        this.backLift = Settings.c(this.backLift, 0.5);
        this.realismExposure = Settings.c(this.realismExposure, 0.5);
        this.realismBloom = Settings.c(this.realismBloom, 0.4);
        this.realismRain = Settings.c(this.realismRain, 0.5);
        this.realismLook = Math.clamp((long)this.realismLook, 0, 2);
        if (this.language == null || !Set.of("en", "fr", "ru").contains(this.language)) {
            this.language = "en";
        }
        this.backStyle = Math.clamp((long)this.backStyle, 0, 12);
        this.realismQuality = Math.clamp((long)this.realismQuality, 0, 2);
        this.crosshairStyle = Math.clamp((long)this.crosshairStyle, 0, 7);
        this.deathStyle = Math.clamp((long)this.deathStyle, 0, 4);
        this.backScale = Settings.c(this.backScale, 0.5);
        this.backSpeed = Settings.c(this.backSpeed, 0.4);
        this.crosshairSize = Settings.c(this.crosshairSize, 0.4);
        this.crosshairGap = Settings.c(this.crosshairGap, 0.3);
        this.crosshairThickness = Settings.c(this.crosshairThickness, 0.3);
        this.crosshairHue = Settings.c(this.crosshairHue, 0.77);
        this.knifeTrailHue = Settings.c(this.knifeTrailHue, 0.77);
        this.knifeTrailLife = Settings.c(this.knifeTrailLife, 0.4);
        this.hitColorHue = Settings.c(this.hitColorHue, 0.77);
        this.hitColorStrength = Settings.c(this.hitColorStrength, 0.65);
        this.musicX = Settings.c(this.musicX, 0.025);
        this.musicY = Settings.c(this.musicY, 0.08);
        this.musicScale = Settings.c(this.musicScale, 0.5);
        this.musicOpacity = Settings.c(this.musicOpacity, 0.88);
        this.fpsPreset = Math.clamp((long)this.fpsPreset, 0, 4);
        int n = this.fpsRenderDistance = this.fpsRenderDistance < 2 ? -1 : Math.clamp((long)this.fpsRenderDistance, 2, 32);
        if (this.pvpTextures == null || this.pvpTextures.length != 5) {
            this.pvpTextures = new int[5];
        }
        for (int i = 0; i < 5; ++i) {
            this.pvpTextures[i] = PvpCatalog.valid(i, this.pvpTextures[i]);
        }
        if (this.menuKey < 32 || this.menuKey > 348 || this.menuKey == 256) {
            this.menuKey = 346;
        }
        this.avatarSkin = Math.clamp((long)this.avatarSkin, 1, 32);
        this.skinScale = Settings.c(this.skinScale, 0.5);
        this.trailStyle = Math.clamp((long)this.trailStyle, 0, 7);
        this.jumpStyle = Math.clamp((long)this.jumpStyle, 0, 11);
        this.hotbarStyle = Math.clamp((long)this.hotbarStyle, 0, 3);
        this.trailSize = Settings.c(this.trailSize, 0.4);
        this.trailLife = Settings.c(this.trailLife, 0.4);
        this.trailDensity = Settings.c(this.trailDensity, 0.4);
        this.jumpSize = Settings.c(this.jumpSize, 0.4);
        this.jumpLife = Settings.c(this.jumpLife, 0.4);
        this.followRange = Settings.c(this.followRange, 0.35);
        this.hitboxRange = Settings.c(this.hitboxRange, 0.35);
        this.hitboxPadding = Settings.c(this.hitboxPadding, 0.0);
        this.hitboxHue = Settings.c(this.hitboxHue, 0.77);
        this.hotbarScale = Settings.c(this.hotbarScale, 0.4);
        this.hotbarLift = Settings.c(this.hotbarLift, 0.0);
        this.hotbarHue = Settings.c(this.hotbarHue, 0.77);
        this.hotbarOpacity = Settings.c(this.hotbarOpacity, 0.75);
        this.knifeVolume = Settings.c(this.knifeVolume, 0.7);
        this.fogDistance = Settings.c(this.fogDistance, 0.35);
        this.fogStart = Settings.c(this.fogStart, 0.3);
        this.fogOpacity = Settings.c(this.fogOpacity, 1.0);
        this.fogHue = Settings.c(this.fogHue, 0.72);
        this.fogSaturation = Settings.c(this.fogSaturation, 0.3);
        this.fogBrightness = Settings.c(this.fogBrightness, 0.72);
        this.fogTint = Settings.c(this.fogTint, 0.45);
        this.knifeModel = Math.clamp((long)this.knifeModel, 0, KnifeCatalog.COUNT - 1);
        this.knifeFinish = Math.clamp((long)this.knifeFinish, 0, 13);
        this.knifeScale = Settings.c(this.knifeScale, 0.5);
        this.knifeSpeed = Settings.c(this.knifeSpeed, 0.5);
        this.blur = Settings.c(this.blur, 0.4);
        this.density = Settings.c(this.density, 0.35);
        this.speed = Settings.c(this.speed, 0.35);
        this.radius = Settings.c(this.radius, 0.5);
        this.glow = Settings.c(this.glow, 0.4);
        this.hue = Settings.c(this.hue, 0.76);
        this.menuScale = Math.clamp(Settings.c(this.menuScale, 0.62), 0.42, 0.78);
        this.panelSize = Settings.c(this.panelSize, 0.5);
        this.panelDistance = Settings.c(this.panelDistance, 0.5);
        this.skyMode = Math.clamp((long)this.skyMode, 0, 9);
        this.skyHue = Settings.c(this.skyHue, 0.73);
        this.skyIntensity = Settings.c(this.skyIntensity, 0.65);
        this.skySpeed = Settings.c(this.skySpeed, 0.25);
        this.stars = Settings.c(this.stars, 0.4);
        this.ambientDensity = Settings.c(this.ambientDensity, 0.3);
        this.wind = Settings.c(this.wind, 0.3);
        this.lyricSize = Settings.c(this.lyricSize, 0.5);
        this.lyricDistance = Settings.c(this.lyricDistance, 0.45);
        this.lyricOpacity = Settings.c(this.lyricOpacity, 0.85);
        this.lyricOffset = Settings.c(this.lyricOffset, 0.5);
        this.lyricOverlap = Settings.c(this.lyricOverlap, 0.7);
        if (this.lyricCalibrations == null) {
            this.lyricCalibrations = new LinkedHashMap<String, Long>();
        }
        this.lyricCalibrations.entrySet().removeIf(e -> e.getKey() == null || ((String)e.getKey()).length() > 800 || e.getValue() == null || Math.abs((double)((Long)e.getValue()).longValue()) > 120000.0);
        while (this.lyricCalibrations.size() > 24) {
            this.lyricCalibrations.remove(this.lyricCalibrations.keySet().iterator().next());
        }
        this.lyricHeight = Settings.c(this.lyricHeight, 0.5);
        this.lyricSpacing = Settings.c(this.lyricSpacing, 0.4);
        this.lyricHue = Settings.c(this.lyricHue, 0.76);
        this.ambientHue = Settings.c(this.ambientHue, 0.72);
        this.fogStrength = Settings.c(this.fogStrength, 0.0);
        this.lyricFont = Math.clamp((long)this.lyricFont, 0, 4);
        this.atmosphereStyle = Math.clamp((long)this.atmosphereStyle, 0, 3);
        this.bloom = Settings.c(this.bloom, 0.3);
        this.occlusion = Settings.c(this.occlusion, 0.5);
        this.exposure = Settings.c(this.exposure, 0.5);
        this.vignette = Settings.c(this.vignette, 0.25);
        Edition.enforce(this);
    }

    public void changed() {
        this.sanitize();
        ++revision;
    }

    public void save() {
        this.sanitize();
        ++revision;
        try {
            Files.createDirectories(PATH.getParent(), new FileAttribute[0]);
            Path tmp = PATH.resolveSibling(String.valueOf(PATH.getFileName()) + ".tmp");
            Files.writeString(tmp, (CharSequence)GSON.toJson((Object)this), new OpenOption[0]);
            try {
                Files.move(tmp, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Exception e) {
            CakeClient.LOG.error("Could not save settings", (Throwable)e);
        }
    }

    public int color(double t) {
        return Color.HSBtoRGB((float)(this.rainbow ? t * 0.035 % 1.0 : this.hue), 0.5f, 1.0f) & 0xFFFFFF;
    }
}

