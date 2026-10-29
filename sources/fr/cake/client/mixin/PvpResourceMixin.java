/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 *  net.minecraft.class_3264
 *  net.minecraft.class_3294
 *  net.minecraft.class_3298
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package fr.cake.client.mixin;

import fr.cake.client.PvpOverrides;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_2960;
import net.minecraft.class_3264;
import net.minecraft.class_3294;
import net.minecraft.class_3298;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_3294.class})
public class PvpResourceMixin {
    @Shadow
    @Final
    private class_3264 field_14284;
    @Shadow
    @Final
    private String field_21561;

    @Inject(method={"method_14486"}, at={@At(value="HEAD")}, cancellable=true)
    private void cake$override(class_2960 id, CallbackInfoReturnable<Optional<class_3298>> ci) {
        if (this.field_14284 == class_3264.field_14188) {
            PvpOverrides.find(id).ifPresent(r -> ci.setReturnValue(Optional.of(r)));
        }
    }

    @Inject(method={"method_14489"}, at={@At(value="RETURN")}, cancellable=true)
    private void cake$stack(class_2960 id, CallbackInfoReturnable<List<class_3298>> ci) {
        if (this.field_14284 != class_3264.field_14188) {
            return;
        }
        PvpOverrides.find(id).ifPresent(r -> {
            ArrayList<class_3298> out = new ArrayList<class_3298>((Collection)ci.getReturnValue());
            out.add((class_3298)r);
            ci.setReturnValue(out);
        });
    }

    @Inject(method={"method_41265"}, at={@At(value="RETURN")}, cancellable=true)
    private void cake$stacks(String path, Predicate<class_2960> filter, CallbackInfoReturnable<Map<class_2960, List<class_3298>>> ci) {
        if (this.field_14284 != class_3264.field_14188 || !this.field_21561.equals("minecraft")) {
            return;
        }
        HashMap out = new HashMap((Map)ci.getReturnValue());
        HashMap<class_2960, class_3298> extra = new HashMap<class_2960, class_3298>();
        PvpOverrides.append(path, filter, extra);
        for (Map.Entry e : extra.entrySet()) {
            ArrayList<class_3298> list = new ArrayList<class_3298>(out.getOrDefault(e.getKey(), List.of()));
            list.add((class_3298)e.getValue());
            out.put((class_2960)e.getKey(), list);
        }
        ci.setReturnValue(out);
    }

    @Inject(method={"method_14488"}, at={@At(value="RETURN")}, cancellable=true)
    private void cake$list(String path, Predicate<class_2960> filter, CallbackInfoReturnable<Map<class_2960, class_3298>> ci) {
        if (this.field_14284 != class_3264.field_14188 || !this.field_21561.equals("minecraft")) {
            return;
        }
        HashMap<class_2960, class_3298> out = new HashMap<class_2960, class_3298>((Map)ci.getReturnValue());
        PvpOverrides.append(path, filter, out);
        ci.setReturnValue(out);
    }
}

