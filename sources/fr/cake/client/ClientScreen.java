/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_3414
 *  net.minecraft.class_437
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.SmoothTexture;
import fr.cake.client.UiCanvas;
import fr.cake.client.mixin.MouseMotionAccess;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_437;

public final class ClientScreen
extends class_437 {
    public static final UiCanvas canvas = new UiCanvas();
    private final class_437 parent;
    private final long opened = System.nanoTime();
    private long closing;
    private double left;
    private double top;
    private double drawWidth;
    private double drawHeight;

    public ClientScreen(class_437 parent) {
        super((class_2561)class_2561.method_43470((String)"Cake Client"));
        this.parent = parent;
    }

    protected void method_25426() {
        canvas.setSpatial(false);
        canvas.invalidate();
        ClientScreen.sound("open", 0.45f);
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25420(class_332 g, int mx, int my, float dt) {
        if (this.field_22787.field_1687 == null) {
            this.method_57728(g, dt);
        }
        this.method_57734(g);
    }

    public void method_25394(class_332 g, int mx, int my, float dt) {
        long now = System.nanoTime();
        if (this.closing != 0L && now - this.closing > 210000000L) {
            this.field_22787.method_1507(this.parent);
            return;
        }
        double p = Math.min(1.0, (double)(now - this.opened) / 3.6E8);
        double ease = 1.0 - Math.pow(1.0 - p, 4.0);
        if (this.closing != 0L) {
            ease = 1.0 - Math.pow(Math.min(1.0, (double)(now - this.closing) / 2.1E8), 3.0);
        }
        double scale = Math.min((double)this.field_22789 / 1920.0, (double)this.field_22790 / 1080.0) * CakeClient.settings.menuScale;
        this.drawWidth = 1920.0 * scale;
        this.drawHeight = 1080.0 * scale;
        this.left = ((double)this.field_22789 - this.drawWidth) / 2.0;
        this.top = ((double)this.field_22790 - this.drawHeight) / 2.0 - (1.0 - ease) * ((double)this.field_22790 + this.drawHeight) / 2.0;
        canvas.update(((double)mx - this.left) / scale, ((double)my - this.top) / scale);
        SmoothTexture texture = canvas.texture();
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)this.left, (float)this.top);
        g.method_51448().scale((float)scale, (float)scale);
        g.method_25290(class_10799.field_56883, texture.id, 0, 0, 0.0f, 0.0f, 1920, 1080, 1920, 1080);
        g.method_51448().popMatrix();
    }

    private double x(double mouse) {
        return (mouse - this.left) * 1920.0 / Math.max(1.0, this.drawWidth);
    }

    private double y(double mouse) {
        return (mouse - this.top) * 1080.0 / Math.max(1.0, this.drawHeight);
    }

    public boolean method_25402(class_11909 event, boolean twice) {
        return this.closing == 0L && event.method_74245() == 0 && canvas.click(this.x(event.comp_4798()), this.y(event.comp_4799()));
    }

    public boolean method_25403(class_11909 event, double dx, double dy) {
        if (event.method_74245() == 0) {
            canvas.drag(this.x(event.comp_4798()), this.y(event.comp_4799()));
            return true;
        }
        return false;
    }

    public boolean method_25406(class_11909 event) {
        if (event.method_74245() == 0) {
            canvas.release();
            return true;
        }
        return false;
    }

    public void method_25419() {
        this.closeAnimated();
    }

    public void closeAnimated() {
        if (this.closing == 0L) {
            canvas.release();
            this.closing = System.nanoTime();
            CakeClient.settings.save();
            ClientScreen.sound("close", 0.45f);
        }
    }

    public void method_25432() {
        canvas.release();
        CakeClient.settings.save();
        MouseMotionAccess mouse = (MouseMotionAccess)this.field_22787.field_1729;
        mouse.cake$pendingX(0.0);
        mouse.cake$pendingY(0.0);
        this.field_22787.field_1729.method_1599();
    }

    public static void tap() {
        ClientScreen.sound("tap", 0.35f);
    }

    private static void sound(String name, float volume) {
        if (CakeClient.settings != null && CakeClient.settings.sounds) {
            class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3414.method_47908((class_2960)class_2960.method_60655((String)"cakeclient", (String)name)), (float)1.0f, (float)volume));
        }
    }
}

