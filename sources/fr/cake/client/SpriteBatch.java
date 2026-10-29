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
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10789
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
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
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.class_10789;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

final class SpriteBatch {
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(SpriteBatch.id("pipeline/insects")).withVertexShader(SpriteBatch.id("core/insects")).withFragmentShader(SpriteBatch.id("core/insects")).withSampler("InsectSampler").withUniform("InsectData", class_10789.field_60031).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27379).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withBlend(BlendFunction.TRANSLUCENT).build();
    private static GpuBuffer vertex;
    private static GpuBuffer uniform;
    private static final ByteBuffer V;
    private static final ByteBuffer U;
    private static class_243 eye;
    private static class_243 right;
    private static class_243 up;
    private static int count;

    SpriteBatch() {
    }

    static Region cell(int n) {
        return new Region((float)(n % 4) / 4.0f, (float)(n % 4 + 1) / 4.0f, (float)(n / 4) / 6.0f, (float)(n / 4 + 1) / 6.0f);
    }

    static void floor(class_243 center, double radius, float angle, int rgb, float alpha, int cell) {
        if (count >= 512) {
            return;
        }
        ++count;
        class_243 p = center.method_1020(eye);
        class_243 r = new class_243(Math.cos(angle) * radius, 0.0, Math.sin(angle) * radius);
        class_243 u = new class_243(-Math.sin(angle) * radius, 0.0, Math.cos(angle) * radius);
        Region sprite = SpriteBatch.cell(cell);
        int c = Math.clamp((long)((int)(alpha * 255.0f)), 0, 255) << 24 | rgb;
        SpriteBatch.put(p.method_1020(r).method_1019(u), sprite.u0, sprite.v0, c);
        SpriteBatch.put(p.method_1020(r).method_1020(u), sprite.u0, sprite.v1, c);
        SpriteBatch.put(p.method_1019(r).method_1020(u), sprite.u1, sprite.v1, c);
        SpriteBatch.put(p.method_1020(r).method_1019(u), sprite.u0, sprite.v0, c);
        SpriteBatch.put(p.method_1019(r).method_1020(u), sprite.u1, sprite.v1, c);
        SpriteBatch.put(p.method_1019(r).method_1019(u), sprite.u1, sprite.v0, c);
    }

    private static class_2960 id(String p) {
        return class_2960.method_60655((String)"cakeclient", (String)p);
    }

    static void begin(class_4184 camera, Matrix4f matrix) {
        eye = camera.method_71156();
        Vector3f r = new Vector3f(1.0f, 0.0f, 0.0f).rotate((Quaternionfc)camera.method_23767());
        Vector3f u = new Vector3f(0.0f, 1.0f, 0.0f).rotate((Quaternionfc)camera.method_23767());
        right = new class_243((double)r.x, (double)r.y, (double)r.z);
        up = new class_243((double)u.x, (double)u.y, (double)u.z);
        count = 0;
        V.clear();
        U.clear();
        matrix.get(0, U);
        U.limit(64);
        U.position(0);
    }

    static void add(class_243 position, double size, float angle, int rgb, float alpha, Region sprite) {
        if (count >= 512) {
            return;
        }
        ++count;
        class_243 p = position.method_1020(eye);
        class_243 r = right.method_1021(Math.cos(angle) * size).method_1019(up.method_1021(Math.sin(angle) * size));
        class_243 u = up.method_1021(Math.cos(angle) * size).method_1020(right.method_1021(Math.sin(angle) * size));
        int color = Math.clamp((long)((int)(alpha * 255.0f)), 0, 255) << 24 | rgb;
        SpriteBatch.put(p.method_1020(r).method_1019(u), sprite.u0, sprite.v0, color);
        SpriteBatch.put(p.method_1020(r).method_1020(u), sprite.u0, sprite.v1, color);
        SpriteBatch.put(p.method_1019(r).method_1020(u), sprite.u1, sprite.v1, color);
        SpriteBatch.put(p.method_1020(r).method_1019(u), sprite.u0, sprite.v0, color);
        SpriteBatch.put(p.method_1019(r).method_1020(u), sprite.u1, sprite.v1, color);
        SpriteBatch.put(p.method_1019(r).method_1019(u), sprite.u1, sprite.v0, color);
    }

    static void ribbon(class_243 start, class_243 end, double width, int rgb, float a, float b, Region sprite) {
        class_243 toward;
        if (count >= 512) {
            return;
        }
        class_243 direction = end.method_1020(start);
        class_243 side = direction.method_1036(toward = eye.method_1020(start));
        if (side.method_1027() < 1.0E-6) {
            return;
        }
        side = side.method_1029().method_1021(width);
        ++count;
        class_243 p = start.method_1020(eye);
        class_243 q = end.method_1020(eye);
        int ca = Math.clamp((long)((int)(a * 255.0f)), 0, 255) << 24 | rgb;
        int cb = Math.clamp((long)((int)(b * 255.0f)), 0, 255) << 24 | rgb;
        float u0 = sprite.u0;
        float u1 = sprite.u1;
        float v = (sprite.v0 + sprite.v1) / 2.0f;
        SpriteBatch.put(p.method_1020(side), u0, v, ca);
        SpriteBatch.put(p.method_1019(side), u1, v, ca);
        SpriteBatch.put(q.method_1019(side), u1, v, cb);
        SpriteBatch.put(p.method_1020(side), u0, v, ca);
        SpriteBatch.put(q.method_1019(side), u1, v, cb);
        SpriteBatch.put(q.method_1020(side), u0, v, cb);
    }

    private static void put(class_243 p, float u, float v, int c) {
        V.putFloat((float)p.field_1352).putFloat((float)p.field_1351).putFloat((float)p.field_1350).putFloat(u).putFloat(v).put((byte)(c >> 16)).put((byte)(c >> 8)).put((byte)c).put((byte)(c >>> 24));
    }

    static void end(GpuTextureView atlas) {
        if (count == 0) {
            return;
        }
        V.flip();
        if (vertex == null) {
            vertex = RenderSystem.getDevice().createBuffer(() -> "Cake / Cosmetic batch", 40, 73728L);
            uniform = RenderSystem.getDevice().createBuffer(() -> "Cake / Cosmetic matrix", 136, 64L);
        }
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(vertex.slice(0L, (long)V.remaining()), V);
        encoder.writeToBuffer(uniform.slice(), U);
        class_276 target = class_310.method_1551().method_1522();
        try (RenderPass pass = encoder.createRenderPass(() -> "Cake / Batched cosmetics", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());){
            pass.setPipeline(PIPELINE);
            pass.setVertexBuffer(0, vertex);
            pass.setUniform("InsectData", uniform);
            pass.bindTexture("InsectSampler", atlas, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
            pass.draw(0, count * 6);
        }
    }

    static void close() {
        if (vertex != null) {
            vertex.close();
            vertex = null;
        }
        if (uniform != null) {
            uniform.close();
            uniform = null;
        }
    }

    static {
        V = ByteBuffer.allocateDirect(73728).order(ByteOrder.nativeOrder());
        U = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder());
    }

    record Region(float u0, float u1, float v0, float v1) {
    }
}

