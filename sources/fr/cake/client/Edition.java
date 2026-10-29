/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import fr.cake.client.Settings;
import fr.cake.client.SkinCatalog;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public final class Edition {
    public static final Plan PLAN = Edition.load();

    private static Plan load() {
        Plan plan;
        block8: {
            InputStream in = Edition.class.getResourceAsStream("/assets/cakeclient/edition.txt");
            try {
                Plan plan2 = plan = in == null ? Plan.PREMIUM : Plan.valueOf(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim().toUpperCase(Locale.ROOT));
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
        return plan;
    }

    public static boolean knife(Plan p, int id) {
        return id >= 0 && id < 22 && (p == Plan.PREMIUM || (p == Plan.FREE ? id == 2 : id <= 3));
    }

    public static boolean sky(Plan p, int id) {
        return id >= 0 && id <= 9 && (p == Plan.PREMIUM || (p == Plan.FREE ? id == 2 : id == 2 || id == 4 || id == 5));
    }

    public static boolean skin(Plan p, int id) {
        return SkinCatalog.available(id) && p == Plan.PREMIUM;
    }

    public static boolean musicCustomization(Plan p) {
        return p == Plan.PREMIUM;
    }

    public static boolean module(Plan p, int tab) {
        if (tab < 0 || tab > 25) {
            return false;
        }
        if (p == Plan.PREMIUM) {
            return true;
        }
        if (p == Plan.PRO) {
            return tab != 15 && tab != 20;
        }
        return Set.of(Integer.valueOf(0), Integer.valueOf(3), Integer.valueOf(6), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(22), Integer.valueOf(25)).contains(tab);
    }

    public static String name() {
        return switch (PLAN.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "Free";
            case 1 -> "Pro";
            case 2 -> "Premium";
        };
    }

    public static void enforce(Settings s) {
        if (!Edition.knife(PLAN, s.knifeModel)) {
            int n = s.knifeModel = PLAN == Plan.FREE ? 2 : 0;
        }
        if (!Edition.sky(PLAN, s.skyMode)) {
            s.skyMode = 2;
        }
        if (!Edition.skin(PLAN, s.avatarSkin)) {
            s.avatarSkin = 21;
        }
        if (!Edition.module(PLAN, 0)) {
            s.motionBlur = false;
        }
        if (!Edition.module(PLAN, 1)) {
            s.fireflies = false;
        }
        if (!Edition.module(PLAN, 2)) {
            s.spatial = false;
        }
        if (!Edition.module(PLAN, 4)) {
            s.sakura = false;
            s.motes = false;
            s.embers = false;
            s.comets = false;
            s.snow = false;
            s.pollen = false;
        }
        if (!Edition.module(PLAN, 5)) {
            s.lyrics = false;
        }
        if (!Edition.module(PLAN, 7)) {
            s.atmosphere = false;
        }
        if (!Edition.module(PLAN, 10)) {
            s.trailEnabled = false;
        }
        if (!Edition.module(PLAN, 11)) {
            s.jumpEnabled = false;
        }
        if (!Edition.module(PLAN, 12)) {
            s.followEnabled = false;
        }
        if (!Edition.module(PLAN, 13)) {
            s.hitboxEnabled = false;
        }
        if (!Edition.module(PLAN, 14)) {
            s.hotbarEnabled = false;
        }
        if (!Edition.module(PLAN, 15)) {
            s.skinEnabled = false;
        }
        if (!Edition.module(PLAN, 16)) {
            s.visualizer = false;
        }
        if (!Edition.module(PLAN, 17)) {
            s.hitParticles = false;
        }
        if (!Edition.module(PLAN, 18)) {
            s.extraParticles = false;
        }
        if (!Edition.module(PLAN, 19)) {
            Arrays.fill(s.pvpTextures, 0);
        }
        if (!Edition.module(PLAN, 20)) {
            s.backEnabled = false;
        }
        if (!Edition.module(PLAN, 21)) {
            s.realistic = false;
        }
        if (!Edition.module(PLAN, 23)) {
            s.hitColor = false;
            s.deathEffects = false;
        }
        if (!Edition.module(PLAN, 24)) {
            s.knifeFinish = 0;
            s.knifeTrails = false;
        }
        if (!Edition.musicCustomization(PLAN)) {
            s.musicX = 0.025;
            s.musicY = 0.08;
            s.musicScale = 0.5;
            s.musicOpacity = 0.88;
            s.musicArtwork = true;
            s.musicBars = true;
            s.visualizerStyle = 1;
            s.visualizerHue = 0.77;
            s.visualizerSize = 0.5;
            s.lyricSize = 0.5;
            s.lyricDistance = 0.45;
            s.lyricOpacity = 0.85;
            s.lyricHeight = 0.5;
            s.lyricSpacing = 0.4;
            s.lyricOverlap = 0.7;
            s.lyricHue = 0.76;
            s.lyricFont = 0;
        }
    }

    public static enum Plan {
        FREE,
        PRO,
        PREMIUM;

    }
}

