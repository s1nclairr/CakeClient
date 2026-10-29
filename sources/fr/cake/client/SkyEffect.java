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
 *  net.minecraft.class_276
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_6367
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package fr.cake.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import fr.cake.client.CakeClient;
import fr.cake.client.RealisticMode;
import fr.cake.client.Settings;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class SkyEffect {
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56838}).withLocation(SkyEffect.id("pipeline/sky")).withVertexShader(class_2960.method_60656((String)"core/screenquad")).withFragmentShader(SkyEffect.id("core/sky")).withSampler("SceneSampler").withSampler("DepthSampler").withUniform("SkyData", class_10789.field_60031).build();
    private static class_6367 swap;
    private static GpuBuffer buffer;
    private static final ByteBuffer DATA;
    private static long retry;
    private static final long START;

    private static class_2960 id(String p) {
        return class_2960.method_60655((String)"cakeclient", (String)p);
    }

    public static void render(Matrix4f matrix) {
        if (RealisticMode.active()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (!s.sky || s.skyIntensity <= 0.0 || mc.field_1687 == null) {
            return;
        }
        if (System.nanoTime() < retry) {
            return;
        }
        try {
            class_276 target = mc.method_1522();
            if (swap == null || SkyEffect.swap.field_1482 != target.field_1482 || SkyEffect.swap.field_1481 != target.field_1481) {
                SkyEffect.close();
                swap = new class_6367("Cake / Sky composite", target.field_1482, target.field_1481, false);
                buffer = RenderSystem.getDevice().createBuffer(() -> "Cake / Sky uniforms", 136, 96L);
            }
            DATA.clear();
            new Matrix4f((Matrix4fc)matrix).invert().get(0, DATA);
            DATA.position(64);
            int col = Color.HSBtoRGB((float)s.skyHue, 0.6f, 0.85f);
            DATA.putFloat((float)(col >> 16 & 0xFF) / 255.0f).putFloat((float)(col >> 8 & 0xFF) / 255.0f).putFloat((float)(col & 0xFF) / 255.0f).putFloat((float)s.skyIntensity);
            DATA.putFloat(s.skyMode).putFloat((float)(System.nanoTime() - START) / 1.0E9f).putFloat((float)s.skySpeed).putFloat((float)s.stars);
            DATA.flip();
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.writeToBuffer(buffer.slice(), DATA);
            try (RenderPass pass = encoder.createRenderPass(() -> "Cake / Procedural sky", swap.method_71639(), OptionalInt.empty());){
                pass.setPipeline(PIPELINE);
                pass.setUniform("SkyData", buffer);
                pass.bindTexture("SceneSampler", target.method_71639(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                pass.bindTexture("DepthSampler", target.method_71640(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                pass.draw(0, 3);
            }
            encoder.copyTextureToTexture(swap.method_30277(), target.method_30277(), 0, 0, 0, 0, 0, target.field_1482, target.field_1481);
        }
        catch (RuntimeException e) {
            SkyEffect.close();
            retry = System.nanoTime() + 3000000000L;
            CakeClient.LOG.error("Could not render the custom sky", (Throwable)e);
        }
    }

    public static void close() {
        if (swap != null) {
            swap.method_1238();
            swap = null;
        }
        if (buffer != null) {
            buffer.close();
            buffer = null;
        }
        retry = 0L;
    }

    static {
        DATA = ByteBuffer.allocateDirect(96).order(ByteOrder.nativeOrder());
        START = System.nanoTime();
    }
}

