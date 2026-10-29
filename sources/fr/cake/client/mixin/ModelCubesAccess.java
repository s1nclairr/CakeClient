/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_630
 *  net.minecraft.class_630$class_628
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package fr.cake.client.mixin;

import java.util.List;
import net.minecraft.class_630;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={class_630.class})
public interface ModelCubesAccess {
    @Accessor(value="field_3663")
    public List<class_630.class_628> cake$cubes();
}

