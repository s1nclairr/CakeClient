/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_4184
 *  net.minecraft.class_5636
 *  net.minecraft.class_638
 *  net.minecraft.class_758
 *  net.minecraft.class_9779
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package fr.cake.client.mixin;

import fr.cake.client.CakeClient;
import fr.cake.client.FogMath;
import fr.cake.client.Settings;
import java.awt.Color;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import net.minecraft.class_638;
import net.minecraft.class_758;
import net.minecraft.class_9779;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={class_758.class})
public abstract class FogMixin {
    @Unique
    private class_4184 cake$camera;

    @Inject(method={"method_3211"}, at={@At(value="HEAD")})
    private void cake$camera(class_4184 camera, int distance, class_9779 delta, float sky, class_638 level, CallbackInfoReturnable<Vector4f> ci) {
        this.cake$camera = camera;
    }

    @ModifyArgs(method={"method_3211"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_758;method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"))
    private void cake$fog(Args args) {
        class_1309 living;
        Settings s = CakeClient.settings;
        if (s == null || !s.fogEnabled || this.cake$camera == null) {
            return;
        }
        class_5636 fluid = this.cake$camera.method_19334();
        if (fluid != class_5636.field_27888 && fluid != class_5636.field_60563) {
            return;
        }
        class_1297 class_12972 = this.cake$camera.method_19331();
        if (class_12972 instanceof class_1309 && ((living = (class_1309)class_12972).method_6059(class_1294.field_5919) || living.method_6059(class_1294.field_38092))) {
            return;
        }
        float vanillaEnd = ((Float)args.get(6)).floatValue();
        FogMath.Range range = FogMath.range(FogMath.distance(s.fogDistance), s.fogStart, s.fogStrength, vanillaEnd);
        args.set(3, (Object)Float.valueOf(Math.min(((Float)args.get(3)).floatValue(), range.start())));
        args.set(4, (Object)Float.valueOf(Math.min(((Float)args.get(4)).floatValue(), range.end())));
        args.set(5, (Object)Float.valueOf(range.start()));
        args.set(6, (Object)Float.valueOf(range.end()));
        if (s.fogSky) {
            args.set(7, (Object)Float.valueOf(Math.min(((Float)args.get(7)).floatValue(), range.end())));
            args.set(8, (Object)Float.valueOf(Math.min(((Float)args.get(8)).floatValue(), range.end())));
        }
        Vector4f original = (Vector4f)args.get(2);
        Vector4f color = new Vector4f((Vector4fc)original);
        int rgb = Color.HSBtoRGB((float)s.fogHue, (float)s.fogSaturation, (float)s.fogBrightness);
        float mix = (float)s.fogTint;
        color.x += ((float)(rgb >> 16 & 0xFF) / 255.0f - color.x) * mix;
        color.y += ((float)(rgb >> 8 & 0xFF) / 255.0f - color.y) * mix;
        color.z += ((float)(rgb & 0xFF) / 255.0f - color.z) * mix;
        color.w *= (float)s.fogOpacity;
        if (s.fogSky) {
            original.set((Vector4fc)color);
        }
        args.set(2, (Object)color);
    }
}

