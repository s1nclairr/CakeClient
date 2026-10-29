/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package fr.cake.client.mixin;

import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={class_312.class})
public interface MouseMotionAccess {
    @Accessor(value="field_1789")
    public void cake$pendingX(double var1);

    @Accessor(value="field_1787")
    public void cake$pendingY(double var1);
}

