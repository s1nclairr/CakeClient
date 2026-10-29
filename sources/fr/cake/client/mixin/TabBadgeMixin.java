/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_355
 *  net.minecraft.class_640
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package fr.cake.client.mixin;

import fr.cake.client.CosmeticNetwork;
import net.minecraft.class_2561;
import net.minecraft.class_355;
import net.minecraft.class_640;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_355.class})
public abstract class TabBadgeMixin {
    @Inject(method={"method_1918"}, at={@At(value="RETURN")}, cancellable=true)
    private void cake$badge(class_640 p, CallbackInfoReturnable<class_2561> ci) {
        if (CosmeticNetwork.marked(p.method_2966().id())) {
            ci.setReturnValue((Object)((class_2561)ci.getReturnValue()).method_27661().method_10852((class_2561)class_2561.method_43470((String)" [Cake Client]").method_27694(s -> s.method_36139(11832052))));
        }
    }
}

