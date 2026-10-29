/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11910
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import fr.cake.client.Knives;
import fr.cake.client.SpatialPanel;
import net.minecraft.class_11910;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_312.class})
public abstract class MouseMixin {
    @Inject(method={"method_1601"}, at={@At(value="HEAD")}, cancellable=true)
    private void cake$button(long window, class_11910 info, int action, CallbackInfo ci) {
        if (SpatialPanel.mouse(info.comp_4801(), action)) {
            ci.cancel();
        } else if (action == 1) {
            Knives.attack(info.comp_4801());
        }
    }
}

