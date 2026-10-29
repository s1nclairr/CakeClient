/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_6367
 */
package fr.cake.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;

public final class MotionBlur {
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56838}).withLocation(class_2960.method_60655((String)"cakeclient", (String)"pipeline/motion")).withVertexShader(class_2960.method_60656((String)"core/screenquad")).withFragmentShader(class_2960.method_60655((String)"cakeclient", (String)"core/motion")).withSampler("CurrentSampler").withSampler("HistorySampler").withUniform("CakeConfig", class_10789.field_60031).build();
    private static class_6367 history;
    private static class_6367 output;
    private static GpuBuffer config;
    private static final ByteBuffer DATA;
    private static boolean primed;
    private static boolean failed;
    private static Object world;
    private static class_243 position;
    private static long last;

    public static void reset() {
        primed = false;
        last = 0L;
        failed = false;
    }

    public static void render() {
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (s == null || !s.motionBlur || s.blur <= 0.001 || mc.field_1687 == null || mc.field_1724 == null || mc.field_1755 != null || mc.method_1493()) {
            primed = false;
            last = 0L;
            return;
        }
        if (failed) {
            return;
        }
        try {
            double dt;
            class_276 main = mc.method_1522();
            if (main.field_1482 <= 0 || main.field_1481 <= 0) {
                return;
            }
            if (history == null || MotionBlur.history.field_1482 != main.field_1482 || MotionBlur.history.field_1481 != main.field_1481) {
                MotionBlur.close();
                history = new class_6367("Cake / History", main.field_1482, main.field_1481, false);
                output = new class_6367("Cake / Composite", main.field_1482, main.field_1481, false);
                config = RenderSystem.getDevice().createBuffer(() -> "Cake / Temporal config", 136, 16L);
            }
            class_243 now = mc.field_1724.method_73189();
            if (world != mc.field_1687 || position == null || position.method_1025(now) > 256.0) {
                primed = false;
            }
            world = mc.field_1687;
            position = now;
            long nanos = System.nanoTime();
            double d = dt = last == 0L ? 0.016666666666666666 : Math.clamp((double)(nanos - last) / 1.0E9, 0.001, 0.25);
            if (last != 0L && nanos - last > 250000000L) {
                primed = false;
            }
            last = nanos;
            float weight = primed ? (float)Math.pow(0.05 + s.blur * 0.85, dt * 60.0) : 0.0f;
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            if (!primed) {
                encoder.copyTextureToTexture(main.method_30277(), history.method_30277(), 0, 0, 0, 0, 0, main.field_1482, main.field_1481);
            }
            DATA.clear();
            DATA.putFloat(weight).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).flip();
            encoder.writeToBuffer(config.slice(), DATA);
            try (RenderPass pass = encoder.createRenderPass(() -> "Cake / Motion Blur", output.method_71639(), OptionalInt.empty());){
                pass.setPipeline(PIPELINE);
                class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
                pass.bindTexture("CurrentSampler", main.method_71639(), sampler);
                pass.bindTexture("HistorySampler", history.method_71639(), sampler);
                pass.setUniform("CakeConfig", config);
                pass.draw(0, 3);
            }
            encoder.copyTextureToTexture(output.method_30277(), history.method_30277(), 0, 0, 0, 0, 0, main.field_1482, main.field_1481);
            encoder.copyTextureToTexture(output.method_30277(), main.method_30277(), 0, 0, 0, 0, 0, main.field_1482, main.field_1481);
            primed = true;
            CakeClient.effectError = "";
        }
        catch (RuntimeException e) {
            CakeClient.LOG.error("Motion Blur disabled after a GPU error", (Throwable)e);
            MotionBlur.close();
            failed = true;
            CakeClient.effectError = e.getClass().getSimpleName();
        }
    }

    public static void close() {
        if (history != null) {
            history.method_1238();
            history = null;
        }
        if (output != null) {
            output.method_1238();
            output = null;
        }
        if (config != null) {
            config.close();
            config = null;
        }
        primed = false;
        last = 0L;
        world = null;
        position = null;
    }

    static {
        DATA = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder());
    }
}

