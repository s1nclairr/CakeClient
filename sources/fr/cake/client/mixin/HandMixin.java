/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1007
 *  net.minecraft.class_11659
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1664
 *  net.minecraft.class_1799
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_4587
 *  net.minecraft.class_742
 *  net.minecraft.class_759
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package fr.cake.client.mixin;

import fr.cake.client.Knives;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1664;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_759;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_759.class})
public abstract class HandMixin {
    @Inject(method={"method_3228"}, at={@At(value="HEAD")}, cancellable=true)
    private void cake$hand(class_742 player, float partial, float pitch, class_1268 hand, float swing, class_1799 stack, float equip, class_4587 poses, class_11659 collector, int light, CallbackInfo ci) {
        if (hand != class_1268.field_5808 || !Knives.eligible() || !stack.method_31573(class_3489.field_42611)) {
            return;
        }
        Matrix4f armPose = Knives.prepare();
        poses.method_22903();
        poses.method_34425((Matrix4fc)armPose);
        class_1007 renderer = class_310.method_1551().method_1561().method_74405(player);
        class_2960 skin = player.method_52814().comp_1626().comp_3627();
        if (player.method_6068() == class_1306.field_6183) {
            renderer.method_4220(poses, collector, light, skin, player.method_74091(class_1664.field_7570));
        } else {
            renderer.method_4221(poses, collector, light, skin, player.method_74091(class_1664.field_7568));
        }
        poses.method_22909();
        if (Knives.supportHand() && player.method_6079().method_7960()) {
            poses.method_22903();
            poses.method_34425((Matrix4fc)Knives.supportArmPose());
            if (player.method_6068() == class_1306.field_6183) {
                renderer.method_4221(poses, collector, light, skin, player.method_74091(class_1664.field_7568));
            } else {
                renderer.method_4220(poses, collector, light, skin, player.method_74091(class_1664.field_7570));
            }
            poses.method_22909();
        }
        Knives.submit(poses, collector);
        ci.cancel();
    }
}

