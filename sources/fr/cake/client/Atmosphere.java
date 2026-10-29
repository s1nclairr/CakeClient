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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;

public final class Atmosphere {
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56838}).withLocation(Atmosphere.id("pipeline/atmosphere")).withVertexShader(class_2960.method_60656((String)"core/screenquad")).withFragmentShader(Atmosphere.id("core/atmosphere")).withSampler("SceneSampler").withSampler("DepthSampler").withUniform("AtmosphereData", class_10789.field_60031).build();
    private static class_6367 swap;
    private static GpuBuffer buffer;
    private static boolean failed;
    private static final ByteBuffer DATA;

    private static class_2960 id(String p) {
        return class_2960.method_60655((String)"cakeclient", (String)p);
    }

    public static void render() {
        if (RealisticMode.active()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (!s.atmosphere || mc.field_1687 == null || failed) {
            return;
        }
        try {
            class_276 target = mc.method_1522();
            if (swap == null || Atmosphere.swap.field_1482 != target.field_1482 || Atmosphere.swap.field_1481 != target.field_1481) {
                Atmosphere.close();
                swap = new class_6367("Cake / Atmosphere", target.field_1482, target.field_1481, false);
                buffer = RenderSystem.getDevice().createBuffer(() -> "Cake / Atmosphere uniforms", 136, 32L);
            }
            DATA.clear();
            DATA.putFloat(target.field_1482).putFloat(target.field_1481).putFloat((float)s.bloom).putFloat((float)s.occlusion);
            DATA.putFloat((float)s.exposure).putFloat((float)s.atmosphereStyle + (float)s.ambientHue * 0.1f).putFloat((float)s.vignette).putFloat(0.0f);
            DATA.flip();
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.writeToBuffer(buffer.slice(), DATA);
            try (RenderPass pass = encoder.createRenderPass(() -> "Cake / Full resolution atmosphere", swap.method_71639(), OptionalInt.empty());){
                pass.setPipeline(PIPELINE);
                pass.setUniform("AtmosphereData", buffer);
                pass.bindTexture("SceneSampler", target.method_71639(), RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
                pass.bindTexture("DepthSampler", target.method_71640(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                pass.draw(0, 3);
            }
            encoder.copyTextureToTexture(swap.method_30277(), target.method_30277(), 0, 0, 0, 0, 0, target.field_1482, target.field_1481);
        }
        catch (RuntimeException e) {
            Atmosphere.close();
            failed = true;
            CakeClient.LOG.error("Atmosphere rendering failed", (Throwable)e);
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
        failed = false;
    }

    static {
        DATA = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
    }
}

