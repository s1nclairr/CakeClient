/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package fr.cake.client.mixin;

import net.minecraft.class_12247;
import net.minecraft.class_1921;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={class_1921.class})
public interface RenderTypeInvoker {
    @Invoker(value="method_75940")
    public static class_1921 cake$create(String name, class_12247 setup) {
        throw new AssertionError();
    }
}

