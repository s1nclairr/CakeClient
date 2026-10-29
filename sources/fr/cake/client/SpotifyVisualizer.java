/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_2960
 */
package fr.cake.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import fr.cake.client.CakeClient;
import fr.cake.client.LyricsService;
import fr.cake.client.Settings;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_2960;

public final class SpotifyVisualizer {
    private static volatile Process process;
    private static volatile float peak;
    private static volatile String status;
    private static float smooth;
    private static long retry;
    private static long last;
    private static GpuBuffer uniform;
    private static final ByteBuffer DATA;
    private static final RenderPipeline PIPE;

    public static String status() {
        return LyricsService.media() == null ? LyricsService.status() : "Spotify \u00b7 " + LyricsService.media().title();
    }

    public static void tick() {
        Settings s = CakeClient.settings;
        if (!s.visualizer) {
            if (process != null) {
                SpotifyVisualizer.close();
            }
            return;
        }
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            status = "D\u00e9tection audio : Windows 10 / 11 requis";
            return;
        }
        if (process != null || System.nanoTime() < retry) {
            return;
        }
        retry = System.nanoTime() + 15000000000L;
        Thread worker = new Thread(() -> {
            try (InputStream in = SpotifyVisualizer.class.getResourceAsStream("/assets/cakeclient/bridge/audio-meter.ps1");){
                Process child;
                String source = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                String encoded = Base64.getEncoder().encodeToString(source.getBytes(StandardCharsets.UTF_16LE));
                process = child = new ProcessBuilder("powershell.exe", "-NoLogo", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden", "-EncodedCommand", encoded).redirectError(ProcessBuilder.Redirect.DISCARD).start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));){
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            peak = Math.clamp(Float.parseFloat(line.trim()), 0.0f, 1.0f);
                            status = "Niveau audio Windows actif \u00b7 Spotify d\u00e9tect\u00e9 si en lecture";
                        }
                        catch (NumberFormatException numberFormatException) {}
                    }
                }
                child.destroy();
            }
            catch (Exception e) {
                status = "Sortie audio indisponible";
            }
            finally {
                process = null;
                peak = 0.0f;
            }
        }, "Cake-Audio-Meter");
        worker.setDaemon(true);
        worker.start();
    }

    public static float level() {
        float target = LyricsService.media() != null && LyricsService.media().playing() ? (float)Math.sqrt(peak) : 0.0f;
        long now = System.nanoTime();
        float dt = last == 0L ? 0.016f : Math.min(0.1f, (float)(now - last) / 1.0E9f);
        last = now;
        smooth += (target - smooth) * (1.0f - (float)Math.exp(-dt * 12.0f));
        return smooth;
    }

    public static void render() {
    }

    public static void close() {
        Process p = process;
        if (p != null) {
            p.destroy();
        }
        process = null;
        peak = 0.0f;
        if (uniform != null) {
            uniform.close();
            uniform = null;
        }
    }

    static {
        status = "Module d\u00e9sactiv\u00e9";
        DATA = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        PIPE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56838}).withLocation(class_2960.method_60655((String)"cakeclient", (String)"pipeline/music_viz")).withVertexShader(class_2960.method_60656((String)"core/screenquad")).withFragmentShader(class_2960.method_60655((String)"cakeclient", (String)"core/music_viz")).withUniform("MusicData", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).build();
    }
}

