/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10789
 *  net.minecraft.class_11285
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package fr.cake.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import fr.cake.client.ClientScreen;
import fr.cake.client.SmoothTexture;
import fr.cake.client.UiCanvas;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.class_10789;
import net.minecraft.class_11285;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class WorldQuad {
    private static final RenderPipeline PANEL = WorldQuad.pipeline(false);
    private static final RenderPipeline LYRIC = WorldQuad.pipeline(true);
    private static GpuBuffer vertices;
    private static final Map<SmoothTexture, class_11285> UNIFORMS;
    private static final ByteBuffer DATA;

    private static RenderPipeline pipeline(boolean overlay) {
        return RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(WorldQuad.id(overlay ? "pipeline/lyric_panel" : "pipeline/world_panel")).withVertexShader(WorldQuad.id("core/world_panel")).withFragmentShader(WorldQuad.id("core/world_panel")).withSampler("CardSampler").withUniform("CardData", class_10789.field_60031).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27379).withDepthTestFunction(overlay ? DepthTestFunction.NO_DEPTH_TEST : DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withBlend(BlendFunction.TRANSLUCENT).build();
    }

    private static class_2960 id(String path) {
        return class_2960.method_60655((String)"cakeclient", (String)path);
    }

    public static void draw(SmoothTexture texture, class_243 center, class_243 right, class_243 up, double width, double height, class_243 camera, Matrix4f matrix, float opacity, float progress, boolean lyric) {
        UiCanvas.Hover hover;
        if (vertices == null) {
            ByteBuffer v = ByteBuffer.allocateDirect(144).order(ByteOrder.nativeOrder());
            WorldQuad.vertex(v, -0.5f, 0.5f, 0.0f, 0.0f);
            WorldQuad.vertex(v, -0.5f, -0.5f, 0.0f, 1.0f);
            WorldQuad.vertex(v, 0.5f, -0.5f, 1.0f, 1.0f);
            WorldQuad.vertex(v, -0.5f, 0.5f, 0.0f, 0.0f);
            WorldQuad.vertex(v, 0.5f, -0.5f, 1.0f, 1.0f);
            WorldQuad.vertex(v, 0.5f, 0.5f, 1.0f, 0.0f);
            v.flip();
            vertices = RenderSystem.getDevice().createBuffer(() -> "Cake / Immutable panel quad", 32, v);
        }
        class_11285 ring = UNIFORMS.computeIfAbsent(texture, t -> new class_11285(() -> "Cake / Panel transform", 138, 112));
        class_243 c = center.method_1020(camera);
        class_243 r = right.method_1021(width);
        class_243 u = up.method_1021(height);
        Matrix4f local = new Matrix4f((float)r.field_1352, (float)r.field_1351, (float)r.field_1350, 0.0f, (float)u.field_1352, (float)u.field_1351, (float)u.field_1350, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, (float)c.field_1352, (float)c.field_1351, (float)c.field_1350, 1.0f);
        DATA.clear();
        new Matrix4f((Matrix4fc)matrix).mul((Matrix4fc)local).get(0, DATA);
        DATA.position(64);
        DATA.putFloat(opacity).putFloat(progress).putFloat(lyric ? 1.0f : 0.0f).putFloat(0.0f);
        DATA.putFloat(0.73f).putFloat(0.58f).putFloat(1.0f).putFloat(1.0f);
        UiCanvas.Hover hover2 = hover = lyric ? null : ClientScreen.canvas.hoverBounds();
        if (hover == null) {
            DATA.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        } else {
            DATA.putFloat((float)hover.x()).putFloat((float)hover.y()).putFloat((float)hover.w()).putFloat((float)hover.h());
        }
        DATA.flip();
        GpuBuffer uniform = ring.method_71119();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(uniform.slice(), DATA);
        class_276 target = class_310.method_1551().method_1522();
        try (RenderPass pass = encoder.createRenderPass(() -> "Cake / Cached spatial panel", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());){
            pass.setPipeline(lyric ? LYRIC : PANEL);
            pass.setVertexBuffer(0, vertices);
            pass.setUniform("CardData", uniform);
            pass.bindTexture("CardSampler", texture.method_71659(), RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
            pass.draw(0, 6);
        }
        ring.method_71121();
    }

    private static void vertex(ByteBuffer v, float x, float y, float u, float t) {
        v.putFloat(x).putFloat(y).putFloat(0.0f).putFloat(u).putFloat(t).putInt(-1);
    }

    public static void release(SmoothTexture texture) {
        class_11285 ring = UNIFORMS.remove((Object)texture);
        if (ring != null) {
            ring.close();
        }
    }

    public static void close() {
        if (vertices != null) {
            vertices.close();
            vertices = null;
        }
        for (class_11285 ring : UNIFORMS.values()) {
            ring.close();
        }
        UNIFORMS.clear();
    }

    static {
        UNIFORMS = new IdentityHashMap<SmoothTexture, class_11285>();
        DATA = ByteBuffer.allocateDirect(112).order(ByteOrder.nativeOrder());
    }
}

