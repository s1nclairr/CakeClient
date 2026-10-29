/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_309
 *  net.minecraft.class_310
 *  net.minecraft.class_433
 *  net.minecraft.class_442
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import fr.cake.client.CakeClient;
import fr.cake.client.ClientScreen;
import fr.cake.client.Knives;
import fr.cake.client.MenuKey;
import fr.cake.client.PlayerFollow;
import fr.cake.client.SpatialPanel;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import net.minecraft.class_433;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_309.class})
public abstract class KeyboardMixin {
    @Inject(method={"method_1466"}, at={@At(value="HEAD")}, cancellable=true)
    private void cake$key(long window, int action, class_11908 key, CallbackInfo ci) {
        if (MenuKey.capture(key.comp_4795(), action)) {
            ci.cancel();
            return;
        }
        class_310 mc = class_310.method_1551();
        if (CakeClient.settings != null && key.comp_4795() == CakeClient.settings.menuKey && action == 1 && (mc.field_1755 == null || mc.field_1755 instanceof ClientScreen || mc.field_1755 instanceof class_442 || mc.field_1755 instanceof class_433)) {
            if ((key.comp_4797() & 1) != 0 && CakeClient.settings.spatial && (mc.field_1755 == null || mc.field_1755 instanceof ClientScreen)) {
                SpatialPanel.placeFromMenu();
                ci.cancel();
            } else if (CakeClient.toggleMenu()) {
                ci.cancel();
            }
            return;
        }
        if (key.comp_4795() == 86 && action == 1 && CakeClient.settings != null && CakeClient.settings.followEnabled && mc.field_1755 == null) {
            PlayerFollow.select();
            ci.cancel();
            return;
        }
        if (Knives.keyEvent(key.comp_4795(), action)) {
            ci.cancel();
        }
    }
}

