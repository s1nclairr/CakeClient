/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_243
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package fr.cake.client.mixin;

import fr.cake.client.PlayerFollow;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={class_1297.class})
public abstract class FollowMovementMixin {
    @ModifyVariable(method={"method_5724"}, at=@At(value="HEAD"), argsOnly=true)
    private class_243 cake$follow(class_243 input) {
        return PlayerFollow.direction(this, input);
    }
}

