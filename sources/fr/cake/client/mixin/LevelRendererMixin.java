/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.minecraft.class_4184
 *  net.minecraft.class_761
 *  net.minecraft.class_9779
 *  net.minecraft.class_9922
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import fr.cake.client.Atmosphere;
import fr.cake.client.Comets;
import fr.cake.client.CosmeticFx;
import fr.cake.client.Fireflies;
import fr.cake.client.LyricsSky;
import fr.cake.client.MotionBlur;
import fr.cake.client.SkyEffect;
import fr.cake.client.SpatialPanel;
import fr.cake.client.SpotifyVisualizer;
import net.minecraft.class_4184;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_761.class})
public abstract class LevelRendererMixin {
    @Inject(method={"method_22710"}, at={@At(value="TAIL")})
    private void cake$render(class_9922 allocator, class_9779 delta, boolean outlines, class_4184 camera, Matrix4f view, Matrix4f projection, Matrix4f culling, GpuBufferSlice fog, Vector4f color, boolean sky, CallbackInfo ci) {
        Matrix4f matrix = new Matrix4f((Matrix4fc)projection).mul((Matrix4fc)view);
        CosmeticFx.render(camera, matrix);
        SkyEffect.render(matrix);
        Atmosphere.render();
        MotionBlur.render();
        Fireflies.render(camera, matrix, delta.method_60637(true));
        Comets.render(camera, matrix);
        SpatialPanel.render(camera, matrix);
        LyricsSky.render(camera, matrix);
        SpotifyVisualizer.render();
    }
}

