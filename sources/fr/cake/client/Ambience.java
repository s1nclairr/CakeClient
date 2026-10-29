/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10725
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3940
 *  net.minecraft.class_3940$class_11941
 *  net.minecraft.class_638
 *  net.minecraft.class_703
 *  net.minecraft.class_746
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.CakeFxMath;
import fr.cake.client.Settings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_10725;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3940;
import net.minecraft.class_638;
import net.minecraft.class_703;
import net.minecraft.class_746;

public final class Ambience {
    private static final List<AmbientParticle> particles = new ArrayList<AmbientParticle>();
    private static Object world;
    private static int tick;
    private static int fxTick;
    private static int fxCooldown;
    private static class_243 previousFx;

    public static void reset() {
        for (AmbientParticle p : particles) {
            p.method_3085();
        }
        particles.clear();
        world = null;
        previousFx = null;
        fxCooldown = 0;
    }

    public static void tick(class_310 mc) {
        Settings s = CakeClient.settings;
        if (!(mc.field_1724 != null && mc.field_1687 != null && (s.sakura || s.motes || s.embers || s.snow || s.pollen || s.trailEnabled || s.jumpEnabled))) {
            if (!particles.isEmpty()) {
                Ambience.reset();
            }
            return;
        }
        if (world != mc.field_1687) {
            Ambience.reset();
            world = mc.field_1687;
        }
        if (mc.method_1493()) {
            return;
        }
        particles.removeIf(p -> !p.method_3086());
        if (!(s.sakura || s.motes || s.embers || s.snow || s.pollen)) {
            return;
        }
        int cap = 12 + (int)(s.ambientDensity * 44.0);
        if (++tick % 3 != 0 || particles.size() >= cap) {
            return;
        }
        int[] modes = new int[6];
        int n = 0;
        if (s.sakura) {
            modes[n++] = 0;
            modes[n++] = 0;
        }
        if (s.motes) {
            modes[n++] = 1;
        }
        if (s.embers) {
            modes[n++] = 2;
        }
        if (s.snow) {
            modes[n++] = 3;
        }
        if (s.pollen) {
            modes[n++] = 4;
        }
        int mode = modes[tick / 3 % n];
        class_243 player = mc.field_1724.method_73189();
        if (!(mode != 0 && mode != 3 || mc.field_1687.method_8311(class_2338.method_49638((class_2374)player).method_10086(3)))) {
            return;
        }
        AmbientParticle p2 = new AmbientParticle(mc.field_1687, player, mode);
        particles.add(p2);
        mc.field_1713.method_3058((class_703)p2);
    }

    private static void effects(class_310 mc, Settings s) {
        class_243 position = mc.field_1724.method_73189();
        if (fxCooldown > 0) {
            --fxCooldown;
        }
        if (previousFx != null) {
            if (s.trailEnabled && ++fxTick % 2 == 0 && position.method_1025(previousFx) > 0.007) {
                Ambience.spawnFx(mc, position, 5, 2);
            }
            if (s.jumpEnabled && fxCooldown == 0 && position.field_1351 - Ambience.previousFx.field_1351 > 0.12) {
                Ambience.spawnFx(mc, position, 6, 12);
                fxCooldown = 20;
            }
        }
        previousFx = position;
    }

    private static void spawnFx(class_310 mc, class_243 center, int mode, int count) {
        for (int i = 0; i < count && particles.size() < 96; ++i) {
            AmbientParticle p = new AmbientParticle(mc.field_1687, center, mode);
            particles.add(p);
            mc.field_1713.method_3058((class_703)p);
        }
    }

    private static final class AmbientParticle
    extends class_3940 {
        final int mode;
        final double phase;

        AmbientParticle(class_638 level, class_243 center, int mode) {
            super(level, center.field_1352, center.field_1351, center.field_1350, class_310.method_1551().method_72703().method_73025(class_10725.field_56389).method_4608(class_2960.method_60655((String)"cakeclient", (String)(mode == 0 ? "sakura" : (mode == 1 ? "mote" : (mode == 2 ? "ember" : (mode == 3 ? "snow" : "pollen")))))));
            this.mode = mode;
            this.phase = this.field_3840.method_43058() * 6.28;
            this.field_3862 = false;
            this.field_3847 = mode == 0 ? 230 + this.field_3840.method_43048(100) : 100 + this.field_3840.method_43048(100);
            this.method_3063(center.field_1352 + (this.field_3840.method_43058() - 0.5) * 10.0, center.field_1351 + (mode == 0 || mode == 3 ? 2.5 + this.field_3840.method_43058() * 4.0 : 0.4 + this.field_3840.method_43058() * 4.0), center.field_1350 + (this.field_3840.method_43058() - 0.5) * 10.0);
            if (mode >= 5) {
                double[] at = CakeFxMath.locate(center.field_1352, center.field_1351, center.field_1350, mode, this.phase);
                this.method_3063(at[0], at[1], at[2]);
                this.field_3847 = 24;
            }
            this.field_3858 = this.field_3874;
            this.field_3838 = this.field_3854;
            this.field_3856 = this.field_3871;
            this.field_17867 = mode == 0 ? 0.14f : (mode == 3 ? 0.035f : (mode == 4 ? 0.03f : 0.025f));
            this.field_62637 = (float)this.phase;
            if (mode >= 5) {
                this.field_17867 = 0.075f;
            }
            if (mode == 0) {
                this.method_74305(1.0f, 0.66f, 0.82f);
            }
            if (mode == 1) {
                this.method_74305(0.83f, 0.79f, 1.0f);
            }
            if (mode == 2) {
                this.method_74305(1.0f, 0.63f, 0.34f);
            }
            if (mode == 3) {
                this.method_74305(0.88f, 0.94f, 1.0f);
            }
            if (mode == 4) {
                this.method_74305(1.0f, 0.86f, 0.42f);
            }
            if (mode >= 5) {
                float[] rgb = CakeFxMath.rgb(mode == 5 ? CakeClient.settings.trailStyle : CakeClient.settings.jumpStyle);
                this.method_74305(rgb[0], rgb[1], rgb[2]);
            }
        }

        public void method_3070() {
            block7: {
                block6: {
                    class_746 player;
                    Settings s = CakeClient.settings;
                    if (++this.field_3866 > this.field_3847 || !this.enabled(s)) {
                        this.method_3085();
                        return;
                    }
                    this.field_3858 = this.field_3874;
                    this.field_3838 = this.field_3854;
                    this.field_3856 = this.field_3871;
                    this.field_62638 = this.field_62637;
                    double t = (double)this.field_3866 * 0.055 + this.phase;
                    double wind = 0.002 + s.wind * 0.025;
                    this.method_3063(this.field_3874 + wind + Math.sin(t) * 0.004, this.field_3854 + (this.mode == 0 ? -0.037 - s.wind * 0.02 : (this.mode == 3 ? -0.012 : (this.mode == 1 || this.mode == 4 ? Math.sin(t * 0.5) * 0.0015 : 0.009))), this.field_3871 + Math.cos(t * 0.7) * 0.005);
                    this.field_62637 += this.mode == 0 ? 0.035f : 0.008f;
                    this.field_62636 = (float)(Math.min(1.0, (double)this.field_3866 / 20.0) * Math.min(1.0, (double)(this.field_3847 - this.field_3866) / 35.0) * (this.mode == 0 ? 0.95 : 0.45));
                    if (this.mode == 0) {
                        this.field_17867 = 0.14f * (0.72f + 0.28f * (float)Math.abs(Math.cos(t)));
                    }
                    if (this.field_3866 % 10 == 0 && this.field_3851.method_8320(class_2338.method_49637((double)this.field_3874, (double)this.field_3854, (double)this.field_3871)).method_26216()) {
                        this.method_3085();
                    }
                    if ((player = class_310.method_1551().field_1724) == null) break block6;
                    class_243 class_2432 = new class_243(this.field_3874, this.field_3854, this.field_3871);
                    if (!(player.method_73189().method_1025(class_2432) > 400.0)) break block7;
                }
                this.method_3085();
            }
        }

        private boolean enabled(Settings s) {
            return this.mode == 5 ? s.trailEnabled : (this.mode == 6 ? s.jumpEnabled : (this.mode == 0 ? s.sakura : (this.mode == 1 ? s.motes : (this.mode == 2 ? s.embers : (this.mode == 3 ? s.snow : s.pollen)))));
        }

        protected class_3940.class_11941 method_74255() {
            return class_3940.class_11941.field_62641;
        }

        protected int method_3068(float partial) {
            return this.mode == 0 ? 0xC000C0 : 0xF000F0;
        }
    }
}

