/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_757
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import fr.cake.client.FpsBenchmark;
import fr.cake.client.Knives;
import fr.cake.client.PanelBenchmark;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_757.class})
public abstract class KnifeRenderMixin {
    @Inject(method={"method_3188"}, at={@At(value="TAIL")})
    private void cake$knife(CallbackInfo ci) {
        Knives.render();
        PanelBenchmark.frame();
        FpsBenchmark.frame();
    }
}

