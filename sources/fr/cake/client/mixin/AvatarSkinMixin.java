/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_1058
 *  net.minecraft.class_11659
 *  net.minecraft.class_11683$class_11792
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_310
 *  net.minecraft.class_3879
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_922
 *  net.minecraft.class_972
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import fr.cake.client.AvatarSkins;
import fr.cake.client.BackCosmetics;
import fr.cake.client.CakeClient;
import fr.cake.client.CombatEffects;
import fr.cake.client.Edition;
import fr.cake.client.Settings;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_310;
import net.minecraft.class_3879;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_922;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_922.class})
public abstract class AvatarSkinMixin {
    @Inject(method={"method_62355(Lnet/minecraft/class_1309;Lnet/minecraft/class_10042;F)V"}, at={@At(value="TAIL")})
    private void cake$ownAvatar(class_1309 entity, class_10042 state, float partial, CallbackInfo ci) {
        CombatEffects.capture(entity, state);
        class_310 mc = class_310.method_1551();
        Settings s = CakeClient.settings;
        if (s != null && s.skinEnabled && entity == mc.field_1724 && Edition.skin(Edition.PLAN, s.avatarSkin)) {
            state.field_53333 = false;
        }
    }

    @Redirect(method={"method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_11659;method_73490(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_1058;ILnet/minecraft/class_11683$class_11792;)V"))
    private void cake$model(class_11659 collector, class_3879 model, Object state, class_4587 stack, class_1921 type, int light, int overlay, int color, class_1058 sprite, int outline, class_11683.class_11792 crumble) {
        stack.method_22903();
        CombatEffects.pose(state, stack);
        BackCosmetics.submit(state, stack, collector, light);
        if (!AvatarSkins.submit(state, stack, collector)) {
            collector.method_73490(model, state, stack, type, light, CombatEffects.tinted(state) ? class_4608.field_21444 : overlay, CombatEffects.tint(state, color), sprite, outline, crumble);
        }
        stack.method_22909();
    }

    @Redirect(method={"method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_3887;method_4199(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10017;FF)V"))
    private void cake$layers(class_3887 layer, class_4587 stack, class_11659 collector, int light, class_10017 state, float yaw, float pitch) {
        if (!(AvatarSkins.state(state) != null || layer instanceof class_972 && BackCosmetics.replacesCape(state))) {
            layer.method_4199(stack, collector, light, state, yaw, pitch);
        }
    }
}

