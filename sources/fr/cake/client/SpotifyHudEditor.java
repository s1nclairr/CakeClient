/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.SpotifyHud;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public final class SpotifyHudEditor
extends class_437 {
    private final class_437 parent;
    private boolean dragging;
    private double grabX;
    private double grabY;

    public SpotifyHudEditor(class_437 parent) {
        super((class_2561)class_2561.method_43470((String)"D\u00e9placer Spotify"));
        this.parent = parent;
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25420(class_332 g, int x, int y, float dt) {
    }

    public void method_25394(class_332 g, int x, int y, float dt) {
        g.method_25294(0, 0, this.field_22789, this.field_22790, 0x35000000);
        SpotifyHud.render(g, true);
        SpotifyHud.Bounds b = SpotifyHud.bounds(this.field_22789, this.field_22790);
        g.method_73198((int)b.x() - 2, (int)b.y() - 2, (int)b.width() + 4, (int)b.height() + 4, -4943635);
        g.method_25300(this.field_22793, "Glisse la carte \u00b7 \u00c9chap pour enregistrer", this.field_22789 / 2, this.field_22790 - 28, -1120262);
    }

    public boolean method_25402(class_11909 e, boolean twice) {
        SpotifyHud.Bounds b = SpotifyHud.bounds(this.field_22789, this.field_22790);
        if (e.method_74245() == 0 && b.contains(e.comp_4798(), e.comp_4799())) {
            this.dragging = true;
            this.grabX = e.comp_4798() - b.x();
            this.grabY = e.comp_4799() - b.y();
            return true;
        }
        return false;
    }

    public boolean method_25403(class_11909 e, double dx, double dy) {
        if (!this.dragging) {
            return false;
        }
        SpotifyHud.move(e.comp_4798() - this.grabX, e.comp_4799() - this.grabY, this.field_22789, this.field_22790);
        return true;
    }

    public boolean method_25406(class_11909 e) {
        this.dragging = false;
        CakeClient.settings.save();
        return true;
    }

    public void method_25419() {
        CakeClient.settings.save();
        this.field_22787.method_1507(this.parent);
    }
}

