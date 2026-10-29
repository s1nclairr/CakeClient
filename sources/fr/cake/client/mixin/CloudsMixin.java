/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_315
 *  net.minecraft.class_4063
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package fr.cake.client.mixin;

import fr.cake.client.CakeClient;
import fr.cake.client.RealisticMode;
import net.minecraft.class_315;
import net.minecraft.class_4063;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_315.class})
public abstract class CloudsMixin {
    @Inject(method={"method_1632"}, at={@At(value="HEAD")}, cancellable=true)
    private void cake$clouds(CallbackInfoReturnable<class_4063> ci) {
        if (CakeClient.settings != null && (CakeClient.settings.sky && !CakeClient.settings.skyClouds || RealisticMode.active())) {
            ci.setReturnValue((Object)class_4063.field_18162);
        }
    }
}

