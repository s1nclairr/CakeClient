/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package fr.cake.client;

import fr.cake.client.Ambience;
import fr.cake.client.Atmosphere;
import fr.cake.client.CakeOptimize;
import fr.cake.client.ClientScreen;
import fr.cake.client.Comets;
import fr.cake.client.CosmeticFx;
import fr.cake.client.CosmeticNetwork;
import fr.cake.client.Edition;
import fr.cake.client.Fireflies;
import fr.cake.client.InsectBatch;
import fr.cake.client.Knives;
import fr.cake.client.LyricsService;
import fr.cake.client.LyricsSky;
import fr.cake.client.MotionBlur;
import fr.cake.client.PlayerFollow;
import fr.cake.client.PreviewTrial;
import fr.cake.client.PvpCustomizer;
import fr.cake.client.RealisticMode;
import fr.cake.client.Settings;
import fr.cake.client.SkyEffect;
import fr.cake.client.SpatialPanel;
import fr.cake.client.SpotifyHud;
import fr.cake.client.SpotifyVisualizer;
import fr.cake.client.WorldQuad;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CakeClient
implements ClientModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger((String)"CakeClient");
    public static Settings settings;
    public static String effectError;

    public void onInitializeClient() {
        settings = Settings.load();
        CosmeticNetwork.init();
        LOG.info("Cake Client 6.9.4 / Minecraft 1.21.11");
    }

    public static boolean toggleMenu() {
        class_310 mc = class_310.method_1551();
        if (settings == null) {
            return false;
        }
        PreviewTrial.cancel();
        class_437 class_4372 = mc.field_1755;
        if (class_4372 instanceof ClientScreen) {
            ClientScreen screen = (ClientScreen)class_4372;
            screen.closeAnimated();
            return true;
        }
        if (CakeClient.settings.spatial && mc.field_1724 != null && mc.field_1687 != null) {
            if (mc.field_1755 != null) {
                mc.method_1507(null);
            }
            SpatialPanel.toggle();
            return true;
        }
        mc.method_1507((class_437)new ClientScreen(mc.field_1755));
        return true;
    }

    public static void tick() {
        if (settings == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Edition.enforce(settings);
        PvpCustomizer.tick();
        RealisticMode.tick();
        SpotifyVisualizer.tick();
        CosmeticNetwork.tick();
        CakeOptimize.tick();
        CosmeticFx.tick(mc);
        PlayerFollow.tick(mc);
        PreviewTrial.tick(mc);
        Fireflies.tick(mc);
        Ambience.tick(mc);
        SpatialPanel.tick(mc);
        LyricsService.tick();
    }

    public static void shutdown() {
        CakeOptimize.flush();
        PreviewTrial.cancel();
        LyricsService.close();
        MotionBlur.close();
        SkyEffect.close();
        Atmosphere.close();
        SpotifyVisualizer.close();
        SpotifyHud.close();
        CosmeticFx.close();
        WorldQuad.close();
        InsectBatch.close();
        Comets.reset();
        Knives.close();
        ClientScreen.canvas.close();
        LyricsSky.close();
    }

    static {
        effectError = "";
    }
}

