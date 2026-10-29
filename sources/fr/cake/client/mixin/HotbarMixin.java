/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_329
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import fr.cake.client.CakeClient;
import fr.cake.client.HotbarHud;
import fr.cake.client.PlayerFollow;
import fr.cake.client.Settings;
import fr.cake.client.SpotifyHud;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_329.class})
public abstract class HotbarMixin {
    @Inject(method={"method_1753"}, at={@At(value="TAIL")})
    private void cake$hud(class_332 g, class_9779 delta, CallbackInfo ci) {
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (mc.field_1690.field_1842) {
            return;
        }
        SpotifyHud.render(g, false);
        if (s != null && s.followEnabled && mc.field_1724 != null) {
            g.method_51433(mc.field_1772, PlayerFollow.label(), 8, g.method_51443() - 40, -2704385, true);
        }
    }

    @Inject(method={"method_1759"}, at={@At(value="HEAD")})
    private void cake$start(class_332 g, class_9779 delta, CallbackInfo ci) {
        HotbarHud.begin(g);
    }

    @Inject(method={"method_1759"}, at={@At(value="RETURN")})
    private void cake$end(class_332 g, class_9779 delta, CallbackInfo ci) {
        HotbarHud.end(g);
    }

    @Redirect(method={"method_1759"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_332;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V"))
    private void cake$background(class_332 g, RenderPipeline p, class_2960 sprite, int x, int y, int w, int h) {
        if (!HotbarHud.replaces(sprite.method_12832())) {
            g.method_52706(p, sprite, x, y, w, h);
        }
    }
}

