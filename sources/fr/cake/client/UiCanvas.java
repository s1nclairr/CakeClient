/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_4063
 *  net.minecraft.class_4066
 *  net.minecraft.class_437
 */
package fr.cake.client;

import fr.cake.client.AvatarSkins;
import fr.cake.client.BackCosmetics;
import fr.cake.client.CakeClient;
import fr.cake.client.CakeExtras;
import fr.cake.client.CakeOptimize;
import fr.cake.client.ClientScreen;
import fr.cake.client.CombatEffects;
import fr.cake.client.CosmeticFx;
import fr.cake.client.CrosshairHud;
import fr.cake.client.Edition;
import fr.cake.client.Fireflies;
import fr.cake.client.FogMath;
import fr.cake.client.HotbarHud;
import fr.cake.client.I18n;
import fr.cake.client.KnifeCatalog;
import fr.cake.client.KnifeFinishes;
import fr.cake.client.LyricsService;
import fr.cake.client.MenuKey;
import fr.cake.client.MotionBlur;
import fr.cake.client.PlayerFollow;
import fr.cake.client.PreviewTrial;
import fr.cake.client.PvpCustomizer;
import fr.cake.client.RealisticMode;
import fr.cake.client.Settings;
import fr.cake.client.SkinCatalog;
import fr.cake.client.SmoothTexture;
import fr.cake.client.SpatialPanel;
import fr.cake.client.SpotifyHudEditor;
import fr.cake.client.SpotifyVisualizer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import javax.imageio.ImageIO;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_4063;
import net.minecraft.class_4066;
import net.minecraft.class_437;

public final class UiCanvas {
    public static final int W = 1920;
    public static final int H = 1080;
    private static final Color BG = new Color(10, 12, 21, 250);
    private static final Color CARD = new Color(21, 24, 39, 245);
    private static final Color LINE = new Color(160, 149, 201, 28);
    private static final Color TEXT = new Color(240, 239, 249);
    private static final Color MUTED = new Color(155, 157, 177);
    private static final Color PURPLE = new Color(178, 139, 255);
    private static final String[] TABS = new String[]{"Motion blur", "Lucioles", "Spatial Panel", "Ciel", "Ambiance", "Lyrics in Sky", "Couteaux", "Optique", "FPS", "Fog", "Trail", "Jump Circle", "Silent Aura", "Hitbox", "Hotbar", "Skin", "Spotify Visualizer", "Hit Particles", "Particules", "Pack Customizer", "Wings & Capes", "Realistic Mode", "Crosshair", "Combat Effects", "Knife Materials", "Settings"};
    private static final int[][] CATEGORIES = new int[][]{{21, 3, 7, 0, 9, 22}, {4, 1, 18, 10, 11, 17}, {6, 24, 20, 15}, {12, 13, 23}, {16, 5}, {8, 14, 19, 2, 25}};
    private static final String[] CATEGORY_NAMES = new String[]{"Visuels", "Atmosphere", "Cosm\u00e9tiques", "Combat", "Musique", "System"};
    private static final BufferedImage[] PREVIEWS = new BufferedImage[KnifeCatalog.COUNT];
    private final List<Widget> widgets = new ArrayList<Widget>();
    private final BufferedImage image = new BufferedImage(1920, 1080, 2);
    private static final Font FONT = UiCanvas.loadFont();
    private Graphics2D g;
    private SmoothTexture texture;
    private int tab = Edition.PLAN == Edition.Plan.FREE ? 3 : 21;
    private int drag = -1;
    private int hover = -1;
    private long lastRevision = -1L;
    private long lastStatus = -1L;
    private double pointerX = -1.0;
    private double pointerY = -1.0;
    private boolean dirty = true;
    private boolean spatial;
    private double lastSliderValue = Double.NaN;
    private long paints;
    private static final BufferedImage[] SKIN_PREVIEWS = new BufferedImage[AvatarSkins.IDS.length];
    private int skinPage;
    private int avatarPage = 0;
    private int packItem;
    private int packAngle;
    private static final BufferedImage[] BACK_PREVIEWS = new BufferedImage[13];

    private static int category(int t) {
        for (int i = 0; i < CATEGORIES.length; ++i) {
            for (int n : CATEGORIES[i]) {
                if (n != t) continue;
                return i;
            }
        }
        return 0;
    }

    public void setSpatial(boolean value) {
        if (this.spatial != value) {
            this.spatial = value;
            this.dirty = true;
        }
    }

    public Hover hoverBounds() {
        if (this.hover < 0 || this.hover >= this.widgets.size()) {
            return null;
        }
        Widget w = this.widgets.get(this.hover);
        return new Hover(w.x, w.y, w.w, w.h);
    }

    public long paintCount() {
        return this.paints;
    }

    private static Font loadFont() {
        Font font;
        block8: {
            InputStream in = UiCanvas.class.getResourceAsStream("/assets/cakeclient/font/ui.ttf");
            try {
                font = Font.createFont(0, in);
                if (in == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    return new Font("SansSerif", 0, 24);
                }
            }
            in.close();
        }
        return font;
    }

    public static Font font(float size, boolean bold) {
        return FONT.deriveFont(bold ? 1 : 0, size);
    }

    public int tab() {
        return this.tab;
    }

    private Settings s() {
        return CakeClient.settings;
    }

    public SmoothTexture texture() {
        this.update(this.pointerX, this.pointerY);
        return this.texture;
    }

    public void invalidate() {
        this.dirty = true;
    }

    public void update(double mx, double my) {
        long status;
        this.pointerX = mx;
        this.pointerY = my;
        int next = -1;
        for (int i = 0; i < this.widgets.size(); ++i) {
            Widget w = this.widgets.get(i);
            if (!UiCanvas.inside(w, mx, my)) continue;
            next = i;
            break;
        }
        if (next != this.hover) {
            this.hover = next;
            if (!this.spatial) {
                this.dirty = true;
            }
        }
        long l = status = this.tab == 5 || this.tab == 8 || this.tab == 16 || this.tab == 19 || this.tab == 21 ? System.currentTimeMillis() / 1000L : 0L;
        if (!this.dirty && this.lastRevision == Settings.revision && this.lastStatus == status && this.texture != null) {
            return;
        }
        this.dirty = false;
        this.lastRevision = Settings.revision;
        this.lastStatus = status;
        if (this.texture == null) {
            this.texture = new SmoothTexture("panel", 1920, 1080);
        }
        this.paint();
        this.texture.update(this.image);
    }

    public boolean click(double x, double y) {
        if (!Edition.module(Edition.PLAN, this.tab) && x >= 350.0 && x <= 1874.0 && y >= 130.0 && y < 970.0) {
            return true;
        }
        if (Edition.musicCustomization(Edition.PLAN) && CakeExtras.intercept(this, x, y, this.tab)) {
            return true;
        }
        this.pointerX = x;
        this.pointerY = y;
        for (int i = 0; i < this.widgets.size(); ++i) {
            Widget w = this.widgets.get(i);
            if (!UiCanvas.inside(w, x, y)) continue;
            ClientScreen.tap();
            if (w.slider != null) {
                this.drag = i;
                this.lastSliderValue = Math.clamp((x - w.x) / w.w, 0.0, 1.0);
                w.slider.accept(this.lastSliderValue);
                this.s().changed();
            } else if (w.action != null) {
                w.action.run();
                this.s().changed();
                if (!PreviewTrial.active()) {
                    this.s().save();
                }
                CakeOptimize.flush();
            }
            this.dirty = true;
            return true;
        }
        return x >= 0.0 && x <= 1920.0 && y >= 0.0 && y <= 1080.0;
    }

    public void drag(double x, double y) {
        if (this.drag >= 0 && this.drag < this.widgets.size()) {
            Widget w = this.widgets.get(this.drag);
            if (w.slider != null) {
                double value = Math.clamp((x - w.x) / w.w, 0.0, 1.0);
                if (Math.abs(value - this.lastSliderValue) < 0.001) {
                    return;
                }
                this.lastSliderValue = value;
                w.slider.accept(value);
                this.s().changed();
                this.dirty = true;
            }
        }
    }

    public void release() {
        if (this.drag >= 0) {
            this.drag = -1;
            CakeOptimize.flush();
            this.s().save();
            this.dirty = true;
        }
    }

    private static boolean inside(Widget w, double x, double y) {
        return x >= w.x && x <= w.x + w.w && y >= w.y && y <= w.y + w.h;
    }

    private void paint() {
        ++this.paints;
        this.g = this.image.createGraphics();
        this.g.setComposite(AlphaComposite.Clear);
        this.g.fillRect(0, 0, 1920, 1080);
        this.g.setComposite(AlphaComposite.SrcOver);
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        this.widgets.clear();
        this.round(10.0, 10.0, 1900.0, 1060.0, 26.0, BG);
        this.g.setPaint(new GradientPaint(10.0f, 10.0f, new Color(125, 79, 220, 24), 1150.0f, 600.0f, new Color(25, 27, 42, 0)));
        this.g.fill(new RoundRectangle2D.Double(10.0, 10.0, 1900.0, 1060.0, 52.0, 52.0));
        this.g.setStroke(new BasicStroke(1.5f));
        this.g.setColor(LINE);
        this.g.draw(new RoundRectangle2D.Double(10.0, 10.0, 1900.0, 1060.0, 26.0, 26.0));
        this.g.setPaint(new GradientPaint(10.0f, 120.0f, new Color(91, 65, 145, 35), 300.0f, 950.0f, new Color(17, 20, 33, 10)));
        this.g.fill(new RoundRectangle2D.Double(18.0, 113.0, 283.0, 853.0, 24.0, 24.0));
        this.line(10.0, 102.0, 1910.0, 102.0);
        this.line(310.0, 103.0, 310.0, 995.0);
        this.label("CAKE", 46.0, 60.0, 31.0f, TEXT, true);
        this.label("CLIENT", 146.0, 60.0, 24.0f, MUTED, false);
        this.label(Edition.name().toUpperCase(Locale.ROOT), 240.0, 60.0, 18.0f, PURPLE, true);
        for (int i = 0; i < CATEGORIES.length; ++i) {
            this.navItem(i, 350 + i * 242, 27.0, 226.0, 52.0);
        }
        this.button(1815.0, 30.0, 60.0, 48.0, "\u00d7", () -> {
            class_310 mc = class_310.method_1551();
            class_437 patt0$temp = mc.field_1755;
            if (patt0$temp instanceof ClientScreen) {
                ClientScreen screen = (ClientScreen)patt0$temp;
                screen.closeAnimated();
            } else {
                SpatialPanel.hide();
            }
        }, false);
        this.label(I18n.tr(CATEGORY_NAMES[UiCanvas.category(this.tab)]).toUpperCase(Locale.ROOT), 51.0, 163.0, 18.0f, MUTED, true);
        int[] visible = CATEGORIES[UiCanvas.category(this.tab)];
        for (int i = 0; i < visible.length; ++i) {
            int t = visible[i];
            double y = 198 + i * 86;
            if (this.tab == t) {
                this.round(29.0, y - 2.0, 262.0, 64.0, 10.0, new Color(148, 104, 242, 24));
                this.round(29.0, y + 12.0, 3.0, 34.0, 1.0, PURPLE);
            }
            this.g.setColor(this.enabled(t) ? new Color(155, 225, 189) : new Color(70, 72, 91));
            this.g.fill(new Ellipse2D.Double(269.0, y + 23.0, 6.0, 6.0));
            this.label(TABS[t], 51.0, y + 38.0, 23.0f, this.tab == t ? TEXT : MUTED, this.tab == t);
            if (!Edition.module(Edition.PLAN, t)) {
                this.g.setColor(MUTED);
                this.g.setStroke(new BasicStroke(2.0f));
                this.g.draw(new Line2D.Double(49.0, y + 29.0, 49 + this.g.getFontMetrics().stringWidth(TABS[t]), y + 29.0));
            }
            this.widgets.add(new Widget(30.0, y - 2.0, 260.0, 64.0, () -> {
                this.tab = t;
                this.drag = -1;
                this.dirty = true;
            }, null));
        }
        this.profileCard();
        this.line(30.0, 976.0, 1890.0, 976.0);
        this.label("Menu \u00b7 " + MenuKey.name(), 46.0, 1026.0, 21.0f, MUTED, false);
        this.label("Changes save automatically", 390.0, 1026.0, 20.0f, MUTED, false);
        this.button(1180.0, 995.0, 310.0, 48.0, "Settings", () -> {
            this.tab = 25;
        }, false);
        this.button(1520.0, 995.0, 354.0, 48.0, "Enregistrer et fermer", () -> {
            class_310 mc = class_310.method_1551();
            class_437 patt0$temp = mc.field_1755;
            if (patt0$temp instanceof ClientScreen) {
                ClientScreen screen = (ClientScreen)patt0$temp;
                screen.closeAnimated();
            } else {
                SpatialPanel.hide();
            }
        }, true);
        this.label(TABS[this.tab], 350.0, 188.0, 49.0f, TEXT, true);
        if (this.tab != 25) {
            this.label(this.enabled(this.tab) ? "ACTIVE" : "OFF", 1560.0, 184.0, 18.0f, this.enabled(this.tab) ? new Color(155, 225, 189) : MUTED, true);
        }
        String[] sub = new String[]{"Un flou temporel r\u00e9glable, appliqu\u00e9 avant l'interface.", "Un vol libre dans le d\u00e9cor. Aucun d\u00e9placement attach\u00e9 au joueur.", "Posez le panneau dans le monde, puis d\u00e9placez-vous autour.", "Une ambiance de ciel calcul\u00e9e directement sur la carte graphique.", "Quelques particules l\u00e9g\u00e8res, avec un budget limit\u00e9.", "Des groupes de mots r\u00e9partis devant vous, \u00e0 gauche et \u00e0 droite.", "Mod\u00e8les CS textur\u00e9s, uniquement avec une \u00e9p\u00e9e.", "Bloom, ombres de contact et couleurs photographiques.", "R\u00e9duisez les distances, les particules et les effets co\u00fbteux.", "Distance, densit\u00e9 et couleur du brouillard.", "Une trace l\u00e9g\u00e8re derri\u00e8re vos d\u00e9placements.", "Des cercles au sol \u00e0 chaque saut.", "Aims and hits for you. Your view stays yours.", "Contours visuels des entit\u00e9s visibles.", "Fonds, cases, s\u00e9lection et dimensions personnalisables.", "Sonic, Patrick, Mario, Luigi and eight more recognizable characters.", "Ta musique, sa pochette et sa progression dans une carte d\u00e9pla\u00e7able.", "\u00c9clats lumineux apr\u00e8s un coup confirm\u00e9.", "Une ambiance de particules discr\u00e8te dans le d\u00e9cor.", "Textures PvP ind\u00e9pendantes, appliqu\u00e9es sans red\u00e9marrer."};
        this.label((String)(this.tab == 8 ? class_310.method_1551().method_47599() + " i/s \u00b7 R\u00e9glages appliqu\u00e9s en jeu" : (this.tab < sub.length ? sub[this.tab] : "Select a look. Tune the details. Make it yours.")), 351.0, 234.0, 23.0f, MUTED, false);
        int moduleStart = this.widgets.size();
        switch (this.tab) {
            case 0: {
                this.motion();
                break;
            }
            case 1: {
                this.flies();
                break;
            }
            case 2: {
                this.spatial();
                break;
            }
            case 3: {
                this.sky();
                break;
            }
            case 4: {
                this.ambience();
                break;
            }
            case 5: {
                this.lyrics();
                break;
            }
            case 6: {
                this.knives();
                break;
            }
            case 7: {
                this.optics();
                break;
            }
            case 8: {
                this.fps();
                break;
            }
            case 9: {
                this.fog();
                break;
            }
            case 10: {
                this.effects(false);
                break;
            }
            case 11: {
                this.effects(true);
                break;
            }
            case 12: {
                this.follow();
                break;
            }
            case 13: {
                this.hitboxes();
                break;
            }
            case 14: {
                this.hotbar();
                break;
            }
            case 15: {
                this.skins();
                break;
            }
            case 16: {
                this.musicViz();
                break;
            }
            case 17: {
                this.particles(false);
                break;
            }
            case 18: {
                this.particles(true);
                break;
            }
            case 19: {
                this.packCustomizer();
                break;
            }
            case 20: {
                this.backs();
                break;
            }
            case 21: {
                this.realism();
                break;
            }
            case 22: {
                this.crosshairs();
                break;
            }
            case 23: {
                this.combat();
                break;
            }
            case 24: {
                this.materials();
                break;
            }
            case 25: {
                this.preferences();
            }
        }
        if (Edition.musicCustomization(Edition.PLAN)) {
            CakeExtras.decorate(this, this.g, this.tab);
        }
        if (!Edition.module(Edition.PLAN, this.tab)) {
            this.widgets.subList(moduleStart, this.widgets.size()).clear();
            this.round(350.0, 282.0, 1524.0, 635.0, 21.0, new Color(10, 8, 16, 145));
            this.label("LOCKED \u00b7 " + (Edition.module(Edition.Plan.PRO, this.tab) ? "PRO / PREMIUM" : "PREMIUM"), 390.0, 344.0, 29.0f, PURPLE, true);
            this.g.setColor(MUTED);
            this.g.setStroke(new BasicStroke(3.0f));
            this.g.draw(new Line2D.Double(350.0, 173.0, 350 + this.g.getFontMetrics(UiCanvas.font(49.0f, true)).stringWidth(TABS[this.tab]), 173.0));
        }
        this.g.dispose();
        this.g = null;
    }

    private void motion() {
        this.toggle(1724.0, 148.0, this.s().motionBlur, () -> {
            this.s().motionBlur = !this.s().motionBlur;
            MotionBlur.reset();
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.label("Intensit\u00e9", 388.0, 343.0, 28.0f, TEXT, true);
        this.slider(390.0, 402.0, 840.0, this.s().blur, (int)(this.s().blur * 100.0) + " %", v -> {
            this.s().blur = v;
        });
        this.label("Faible", 390.0, 457.0, 21.0f, MUTED, false);
        this.right("Marqu\u00e9", 1230.0, 457.0, 21.0f, MUTED);
        this.button(390.0, 505.0, 250.0, 61.0, "L\u00e9ger \u00b7 25 %", () -> {
            this.s().blur = 0.25;
        }, false);
        this.button(660.0, 505.0, 250.0, 61.0, "\u00c9quilibr\u00e9 \u00b7 45 %", () -> {
            this.s().blur = 0.45;
        }, false);
        this.button(930.0, 505.0, 300.0, 61.0, "Cin\u00e9matique \u00b7 70 %", () -> {
            this.s().blur = 0.7;
        }, false);
        this.line(390.0, 620.0, 1230.0, 620.0);
        this.label("Les menus et le HUD restent nets.", 390.0, 680.0, 25.0f, TEXT, false);
        this.label("L'exposition est ajust\u00e9e selon la dur\u00e9e de chaque image.", 390.0, 725.0, 22.0f, MUTED, false);
        if (!CakeClient.effectError.isEmpty()) {
            this.label("Le shader a rencontr\u00e9 une erreur. Voir latest.log.", 390.0, 825.0, 22.0f, new Color(239, 145, 150), false);
        }
        this.trialCard(0, "Essayer le v\u00e9ritable effet", "Bougez la cam\u00e9ra pendant 6 secondes.");
    }

    private void flies() {
        this.toggle(1724.0, 148.0, this.s().fireflies, () -> {
            this.s().fireflies = !this.s().fireflies;
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.rowSlider("Quantit\u00e9", 390.0, 328.0, 840.0, this.s().density, "" + Fireflies.count(), v -> {
            this.s().density = v;
        });
        this.rowSlider("Vitesse de vol", 390.0, 454.0, 840.0, this.s().speed, UiCanvas.pct(this.s().speed), v -> {
            this.s().speed = v;
        });
        this.rowSlider("Rayon d'apparition", 390.0, 580.0, 840.0, this.s().radius, String.format("%.1f m", 4.0 + this.s().radius * 7.0), v -> {
            this.s().radius = v;
        });
        this.rowSlider("\u00c9clat", 390.0, 706.0, 840.0, this.s().glow, UiCanvas.pct(this.s().glow), v -> {
            this.s().glow = v;
        });
        this.hue(390.0, 849.0, 840.0, this.s().hue, v -> {
            this.s().hue = v;
            this.s().rainbow = false;
        });
        this.card(1304.0, 282.0, 570.0, 635.0);
        this.label("Trajectoires naturelles", 1344.0, 341.0, 28.0f, TEXT, true);
        this.wrap("Les lucioles conservent leur place dans le monde. Vous pouvez les d\u00e9passer sans les tirer derri\u00e8re vous.", 1344.0, 397.0, 485.0, 24.0f, MUTED);
        this.button(1344.0, 567.0, 490.0, 60.0, this.s().rainbow ? "Arc-en-ciel activ\u00e9" : "Couleur fixe", () -> {
            this.s().rainbow = !this.s().rainbow;
        }, false);
        this.button(1344.0, 660.0, 490.0, 65.0, UiCanvas.trialLabel(), () -> PreviewTrial.start(1), true);
        this.wrap("24 insectes maximum. Les plus \u00e9loign\u00e9s s'effacent progressivement.", 1344.0, 785.0, 490.0, 22.0f, MUTED);
    }

    private void spatial() {
        this.toggle(1724.0, 148.0, this.s().spatial, () -> {
            boolean bl = this.s().spatial = !this.s().spatial;
            if (!this.s().spatial) {
                SpatialPanel.hide();
            }
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.rowSlider("Taille du panneau 3D", 390.0, 335.0, 840.0, this.s().panelSize, UiCanvas.pct(this.s().panelSize), v -> {
            this.s().panelSize = v;
        });
        this.rowSlider("Distance de pose", 390.0, 485.0, 840.0, this.s().panelDistance, String.format("%.1f m", 1.9 + this.s().panelDistance * 1.2), v -> {
            this.s().panelDistance = v;
        });
        this.rowSlider("Taille du menu classique", 390.0, 635.0, 840.0, (this.s().menuScale - 0.42) / 0.36, (int)(this.s().menuScale * 100.0) + " % de l'\u00e9cran", v -> {
            this.s().menuScale = 0.42 + v * 0.36;
        });
        this.button(390.0, 803.0, 840.0, 66.0, class_310.method_1551().field_1687 == null ? "Disponible dans un monde" : "Poser le panneau devant moi", () -> SpatialPanel.placeFromMenu(), true);
        this.card(1304.0, 282.0, 570.0, 635.0);
        this.label("Une position fixe", 1344.0, 341.0, 28.0f, TEXT, true);
        this.wrap("Marchez, tournez autour du panneau, puis visez un bouton et cliquez. Les curseurs se r\u00e8glent en maintenant le clic.", 1344.0, 400.0, 485.0, 24.0f, MUTED);
        this.wrap("Alt Gr masque ou r\u00e9affiche le panneau. Maj + Alt Gr le replace devant vous. Le panneau reste lisible des deux c\u00f4t\u00e9s.", 1344.0, 620.0, 485.0, 24.0f, MUTED);
    }

    private void sky() {
        this.toggle(1724.0, 148.0, this.s().sky, () -> {
            this.s().sky = !this.s().sky;
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        String[] modes = new String[]{"Colour", "Nova", "Galaxy", "Waves", "Aurora", "Black Hole", "Solar Eclipse", "Nebula", "Meteor Shower", "Polar Veil"};
        for (int i = 0; i < 10; ++i) {
            int n = i;
            this.choice(390 + i % 5 * 290, 310 + i / 5 * 65, 274.0, 52.0, modes[i], () -> {
                this.s().skyMode = n;
            }, this.s().skyMode == i, Edition.sky(Edition.PLAN, i));
        }
        this.rowSlider("Intensit\u00e9", 390.0, 445.0, 665.0, this.s().skyIntensity, UiCanvas.pct(this.s().skyIntensity), v -> {
            this.s().skyIntensity = v;
        });
        this.rowSlider("Vitesse", 1150.0, 445.0, 665.0, this.s().skySpeed, UiCanvas.pct(this.s().skySpeed), v -> {
            this.s().skySpeed = v;
        });
        this.rowSlider("\u00c9toiles", 390.0, 605.0, 665.0, this.s().stars, UiCanvas.pct(this.s().stars), v -> {
            this.s().stars = v;
        });
        this.label("Teinte", 1150.0, 605.0, 25.0f, TEXT, false);
        this.hue(1150.0, 652.0, 665.0, this.s().skyHue, v -> {
            this.s().skyHue = v;
        });
        this.button(390.0, 793.0, 665.0, 66.0, UiCanvas.trialLabel(), () -> PreviewTrial.start(3), true);
        this.button(1150.0, 793.0, 665.0, 66.0, this.s().skyClouds ? "Nuages Minecraft : oui" : "Nuages Minecraft : non", () -> {
            this.s().skyClouds = !this.s().skyClouds;
        }, false);
    }

    private void ambience() {
        this.card(350.0, 282.0, 725.0, 635.0);
        this.card(1104.0, 282.0, 770.0, 635.0);
        this.option(390.0, 350.0, "P\u00e9tales de sakura", this.s().sakura, () -> {
            this.s().sakura = !this.s().sakura;
        });
        this.option(390.0, 440.0, "Points de lumi\u00e8re", this.s().motes, () -> {
            this.s().motes = !this.s().motes;
        });
        this.option(390.0, 530.0, "Braises discr\u00e8tes", this.s().embers, () -> {
            this.s().embers = !this.s().embers;
        });
        this.option(390.0, 620.0, "Neige l\u00e9g\u00e8re", this.s().snow, () -> {
            this.s().snow = !this.s().snow;
        });
        this.option(390.0, 710.0, "Pollen dor\u00e9", this.s().pollen, () -> {
            this.s().pollen = !this.s().pollen;
        });
        this.option(1144.0, 710.0, "Com\u00e8tes violettes", this.s().comets, () -> {
            this.s().comets = !this.s().comets;
        });
        this.rowSlider("Densit\u00e9 globale", 1144.0, 355.0, 685.0, this.s().ambientDensity, UiCanvas.pct(this.s().ambientDensity), v -> {
            this.s().ambientDensity = v;
        });
        this.rowSlider("Vent", 1144.0, 528.0, 685.0, this.s().wind, UiCanvas.pct(this.s().wind), v -> {
            this.s().wind = v;
        });
        this.wrap("Des arcs lumineux dans le d\u00e9cor, sans entit\u00e9s suppl\u00e9mentaires.", 1144.0, 800.0, 680.0, 22.0f, MUTED);
        this.button(390.0, 803.0, 645.0, 66.0, UiCanvas.trialLabel(), () -> PreviewTrial.start(4), true);
    }

    private void lyrics() {
        this.toggle(1724.0, 148.0, this.s().lyrics, () -> {
            this.s().lyrics = !this.s().lyrics;
            LyricsService.settingsChanged();
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        if (Edition.musicCustomization(Edition.PLAN)) {
            this.rowSlider("Taille", 390.0, 334.0, 390.0, this.s().lyricSize, UiCanvas.pct(this.s().lyricSize), v -> {
                this.s().lyricSize = v;
            });
            this.rowSlider("Distance", 840.0, 334.0, 390.0, this.s().lyricDistance, String.format("%.1f m", 5.5 + this.s().lyricDistance * 8.0), v -> {
                this.s().lyricDistance = v;
            });
            this.rowSlider("Hauteur", 390.0, 492.0, 390.0, this.s().lyricHeight, this.s().lyricHeight < 0.4 ? "Plus bas" : (this.s().lyricHeight > 0.6 ? "Plus haut" : "Devant vous"), v -> {
                this.s().lyricHeight = v;
            });
            this.rowSlider("\u00c9cartement", 840.0, 492.0, 390.0, this.s().lyricSpacing, UiCanvas.pct(this.s().lyricSpacing), v -> {
                this.s().lyricSpacing = v;
            });
            this.rowSlider("Opacit\u00e9", 390.0, 650.0, 390.0, this.s().lyricOpacity, UiCanvas.pct(this.s().lyricOpacity), v -> {
                this.s().lyricOpacity = v;
            });
        } else {
            this.wrap("Lyrics in Sky \u00b7 automatic placement. Appearance customization is included in Premium.", 390.0, 410.0, 800.0, 27.0f, MUTED);
        }
        this.rowSlider("Synchro globale", 840.0, 650.0, 390.0, this.s().lyricOffset, String.format("%+.1f s", (this.s().lyricOffset - 0.5) * 30.0), v -> {
            this.s().lyricOffset = v;
        });
        if (Edition.musicCustomization(Edition.PLAN)) {
            this.rowSlider("Groupes simultan\u00e9s", 390.0, 765.0, 840.0, this.s().lyricOverlap, 2 + (int)Math.round(this.s().lyricOverlap * 2.0) + " groupes", v -> {
                this.s().lyricOverlap = v;
            });
        }
        if (Edition.musicCustomization(Edition.PLAN)) {
            this.button(390.0, 842.0, 840.0, 53.0, "D\u00e9mo du placement \u00b7 6 s", () -> PreviewTrial.start(5), true);
        }
        this.label("Spotify sur le PC", 1344.0, 341.0, 28.0f, TEXT, true);
        this.wrap(LyricsService.status(), 1344.0, 397.0, 480.0, 24.0f, this.s().lyrics ? TEXT : MUTED);
        this.label(LyricsService.playbackLabel(), 1344.0, 511.0, 20.0f, PURPLE, false);
        String line = LyricsService.currentLine();
        this.wrap((String)(line.length() > 100 ? line.substring(0, 97) + "\u2026" : line), 1344.0, 558.0, 480.0, 23.0f, TEXT);
        this.button(1344.0, 650.0, 235.0, 49.0, "Phrase pr\u00e9c\u00e9dente", () -> LyricsService.calibrateLine(-1), false);
        this.button(1595.0, 650.0, 239.0, 49.0, "Phrase suivante", () -> LyricsService.calibrateLine(1), false);
        this.button(1344.0, 716.0, 490.0, 53.0, "Caler cette phrase maintenant", () -> LyricsService.calibrateLine(0), true);
        this.button(1344.0, 782.0, 490.0, 43.0, "R\u00e9initialiser la correction", LyricsService::resetCalibration, false);
        this.button(1344.0, 842.0, 490.0, 43.0, "Relancer la d\u00e9tection", LyricsService::retry, false);
    }

    private void knives() {
        this.toggle(1724.0, 148.0, this.s().knives, () -> {
            this.s().knives = !this.s().knives;
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        this.label("Collection \u00b7 22 couteaux", 390.0, 330.0, 27.0f, TEXT, true);
        for (int i = 0; i < KnifeCatalog.COUNT; ++i) {
            int n = i;
            this.choice(390 + i % 4 * 220, 355 + i / 4 * 69, 205.0, 53.0, KnifeCatalog.LABELS[i], () -> {
                this.s().knifeModel = n;
            }, this.s().knifeModel == i, Edition.knife(Edition.PLAN, i));
        }
        this.rowSlider("Taille de la vue", 390.0, 825.0, 390.0, this.s().knifeScale, UiCanvas.pct(this.s().knifeScale), v -> {
            this.s().knifeScale = v;
        });
        this.rowSlider("Vitesse des gestes", 840.0, 825.0, 390.0, this.s().knifeSpeed, String.format("%.2f \u00d7", 0.5 + this.s().knifeSpeed), v -> {
            this.s().knifeSpeed = v;
        });
        this.label(KnifeCatalog.LABELS[this.s().knifeModel], 1344.0, 341.0, 27.0f, TEXT, true);
        int model = this.s().knifeModel;
        if (PREVIEWS[model] == null) {
            try (InputStream in = UiCanvas.class.getResourceAsStream("/assets/cakeclient/knives/preview-" + model + ".png");){
                if (in != null) {
                    UiCanvas.PREVIEWS[model] = ImageIO.read(in);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (PREVIEWS[model] != null) {
            this.g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            this.g.drawImage(PREVIEWS[model], 1324, 358, 530, 283, null);
        }
        this.label(this.s().knifeModel == 1 ? "Maintenir G : rotations" : "G : inspection", 1344.0, 668.0, 25.0f, TEXT, true);
        this.label("Clic gauche : coupes altern\u00e9es", 1344.0, 710.0, 21.0f, TEXT, false);
        this.label("Clic droit : estoc", 1344.0, 745.0, 21.0f, MUTED, false);
        this.button(1344.0, 768.0, 490.0, 48.0, "Knife Materials \u2192", () -> {
            this.tab = 24;
        }, true);
        this.button(1344.0, 835.0, 238.0, 56.0, "Sons CS : " + (this.s().knifeOriginalSounds ? "oui" : "non"), () -> {
            this.s().knifeOriginalSounds = !this.s().knifeOriginalSounds;
        }, this.s().knifeOriginalSounds);
        this.button(1594.0, 835.0, 240.0, 56.0, "Inspection", () -> PreviewTrial.start(6), true);
    }

    private void effects(boolean jump) {
        boolean active = jump ? this.s().jumpEnabled : this.s().trailEnabled;
        this.toggle(1724.0, 148.0, active, () -> {
            if (jump) {
                this.s().jumpEnabled = !this.s().jumpEnabled;
            } else {
                this.s().trailEnabled = !this.s().trailEnabled;
            }
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        String[] names = jump ? CosmeticFx.RINGS : CosmeticFx.TRAILS;
        for (int i = 0; i < names.length; ++i) {
            int n = i;
            this.button(390 + i % 3 * 286, 330 + i / 3 * 102, 268.0, 74.0, names[i], () -> {
                if (jump) {
                    this.s().jumpStyle = n;
                } else {
                    this.s().trailStyle = n;
                }
            }, (jump ? this.s().jumpStyle : this.s().trailStyle) == i);
        }
        this.label("Couleur", 1344.0, 340.0, 27.0f, TEXT, true);
        this.hue(1344.0, 378.0, 490.0, jump ? this.s().jumpHue : this.s().trailHue, v -> {
            if (jump) {
                this.s().jumpHue = v;
            } else {
                this.s().trailHue = v;
            }
        });
        this.rowSlider("Taille", 1344.0, 490.0, 490.0, jump ? this.s().jumpSize : this.s().trailSize, UiCanvas.pct(jump ? this.s().jumpSize : this.s().trailSize), v -> {
            if (jump) {
                this.s().jumpSize = v;
            } else {
                this.s().trailSize = v;
            }
        });
        this.rowSlider("Dur\u00e9e", 1344.0, 640.0, 490.0, jump ? this.s().jumpLife : this.s().trailLife, String.format("%.1f s", jump ? 0.4 + this.s().jumpLife * 2.0 : 0.25 + this.s().trailLife * 1.75), v -> {
            if (jump) {
                this.s().jumpLife = v;
            } else {
                this.s().trailLife = v;
            }
        });
        if (!jump) {
            this.rowSlider("Densit\u00e9", 1344.0, 790.0, 490.0, this.s().trailDensity, UiCanvas.pct(this.s().trailDensity), v -> {
                this.s().trailDensity = v;
            });
        } else {
            this.wrap("D\u00e9clench\u00e9 au d\u00e9collage. Six cercles maximum, avec disparition progressive.", 1344.0, 800.0, 470.0, 23.0f, MUTED);
        }
    }

    private void follow() {
        this.toggle(1724.0, 148.0, this.s().followEnabled, () -> {
            this.s().followEnabled = !this.s().followEnabled;
            PlayerFollow.clear();
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.label("Silent Aura", 390.0, 350.0, 32.0f, TEXT, true);
        this.button(390.0, 400.0, 620.0, 65.0, "V: activate / stop", PlayerFollow::select, true);
        this.button(1050.0, 400.0, 760.0, 65.0, "\u00c9p\u00e9e requise : " + (this.s().followSword ? "oui" : "non"), () -> {
            this.s().followSword = !this.s().followSword;
        }, this.s().followSword);
        this.rowSlider("Attack range", 390.0, 560.0, 880.0, this.s().followRange, String.format("%.2f m", 2.0 + this.s().followRange), v -> {
            this.s().followRange = v;
        });
        this.wrap("Aims and hits for you. Your view stays yours. Press V to activate or stop. Selects the nearest visible player in range and attacks at full cooldown.", 390.0, 710.0, 1370.0, 27.0f, TEXT);
    }

    private void hitboxes() {
        this.toggle(1724.0, 148.0, this.s().hitboxEnabled, () -> {
            this.s().hitboxEnabled = !this.s().hitboxEnabled;
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.label("Contours visibles", 390.0, 350.0, 32.0f, TEXT, true);
        this.hue(390.0, 410.0, 1350.0, this.s().hitboxHue, v -> {
            this.s().hitboxHue = v;
        });
        this.rowSlider("Marge visuelle", 390.0, 540.0, 620.0, this.s().hitboxPadding, String.format("%.2f bloc", this.s().hitboxPadding * 0.3), v -> {
            this.s().hitboxPadding = v;
        });
        this.rowSlider("Distance", 1100.0, 540.0, 620.0, this.s().hitboxRange, (int)(4.0 + this.s().hitboxRange * 28.0) + " blocs", v -> {
            this.s().hitboxRange = v;
        });
        this.button(390.0, 720.0, 620.0, 65.0, this.s().hitboxPlayers ? "Joueurs uniquement" : "Entit\u00e9s vivantes", () -> {
            this.s().hitboxPlayers = !this.s().hitboxPlayers;
        }, this.s().hitboxPlayers);
        this.wrap("La marge r\u00e8gle le contour affich\u00e9. Les collisions et la port\u00e9e des coups gardent leurs dimensions Minecraft.", 390.0, 840.0, 1400.0, 24.0f, MUTED);
    }

    private void hotbar() {
        this.toggle(1724.0, 148.0, this.s().hotbarEnabled, () -> {
            this.s().hotbarEnabled = !this.s().hotbarEnabled;
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        for (int i = 0; i < 4; ++i) {
            int n = i;
            this.button(390 + i * 355, 322.0, 330.0, 66.0, HotbarHud.STYLES[i], () -> {
                this.s().hotbarStyle = n;
            }, this.s().hotbarStyle == i);
        }
        this.label("Couleur de la s\u00e9lection", 390.0, 466.0, 27.0f, TEXT, true);
        this.hue(390.0, 505.0, 1350.0, this.s().hotbarHue, v -> {
            this.s().hotbarHue = v;
        });
        this.rowSlider("Opacit\u00e9 du fond", 390.0, 610.0, 650.0, this.s().hotbarOpacity, UiCanvas.pct(this.s().hotbarOpacity), v -> {
            this.s().hotbarOpacity = v;
        });
        this.rowSlider("Taille de la hotbar", 1130.0, 610.0, 650.0, this.s().hotbarScale, String.format("%.0f %%", 80.0 + this.s().hotbarScale * 50.0), v -> {
            this.s().hotbarScale = v;
        });
        this.rowSlider("Hauteur", 390.0, 770.0, 1390.0, this.s().hotbarLift, UiCanvas.pct(this.s().hotbarLift), v -> {
            this.s().hotbarLift = v;
        });
        this.label("Fonds, cases et s\u00e9lection remplac\u00e9s \u00b7 objets, quantit\u00e9s et main secondaire conserv\u00e9s.", 390.0, 892.0, 24.0f, MUTED, false);
    }

    private BufferedImage avatarPreview(int model) {
        if (SKIN_PREVIEWS[model] == null) {
            try (InputStream in = UiCanvas.class.getResourceAsStream("/assets/cakeclient/avatars/preview-" + model + ".png");){
                if (in != null) {
                    UiCanvas.SKIN_PREVIEWS[model] = ImageIO.read(in);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return SKIN_PREVIEWS[model];
    }

    private void profileCard() {
        this.card(30.0, 785.0, 260.0, 175.0);
        class_310 mc = class_310.method_1551();
        String name = mc.field_1724 == null ? "Cake Player" : mc.field_1724.method_5477().getString();
        this.round(48.0, 804.0, 64.0, 64.0, 14.0, new Color(87, 61, 130));
        if (this.s().skinEnabled) {
            BufferedImage preview = this.avatarPreview(this.s().avatarSkin - 1);
            if (preview != null) {
                this.g.drawImage(preview, 48, 804, 64, 64, null);
            }
        } else {
            this.label(name.substring(0, 1).toUpperCase(), 69.0, 848.0, 31.0f, TEXT, true);
        }
        String shown = name.length() > 13 ? name.substring(0, 12) + "\u2026" : name;
        this.label(shown, 126.0, 834.0, 20.0f, TEXT, true);
        this.label(Edition.name().toUpperCase(Locale.ROOT), 126.0, 862.0, 16.0f, PURPLE, true);
        this.label("Minecraft 1.21.11", 48.0, 905.0, 18.0f, MUTED, false);
        this.label("Avatar & cosmetics \u2192", 48.0, 938.0, 17.0f, PURPLE, false);
        this.widgets.add(new Widget(30.0, 785.0, 260.0, 175.0, () -> {
            this.tab = 15;
        }, null));
    }

    private void skins() {
        this.toggle(1724.0, 148.0, this.s().skinEnabled, () -> {
            this.s().skinEnabled = !this.s().skinEnabled;
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        int pages = (SkinCatalog.IDS.length + 5) / 6;
        this.avatarPage = Math.clamp((long)this.avatarPage, 0, pages - 1);
        for (int k = 0; k < 6 && this.avatarPage * 6 + k < SkinCatalog.IDS.length; ++k) {
            int n = SkinCatalog.IDS[this.avatarPage * 6 + k];
            double x = 390 + k % 3 * 285;
            double y = 312 + k / 3 * 225;
            this.button(x, y, 267.0, 210.0, "", () -> {
                this.s().avatarSkin = n;
            }, this.s().avatarSkin == n);
            BufferedImage preview = this.avatarPreview(n - 1);
            if (preview != null) {
                this.g.drawImage(preview, (int)x + 45, (int)y + 4, 176, 176, null);
            }
            this.label(AvatarSkins.NAMES[n - 1], x + 15.0, y + 197.0, 19.0f, this.s().avatarSkin == n ? PURPLE : TEXT, true);
        }
        this.button(390.0, 770.0, 220.0, 46.0, "\u2190 Previous", () -> {
            this.avatarPage = (this.avatarPage + pages - 1) % pages;
        }, false);
        this.label(this.avatarPage + 1 + " / " + pages, 760.0, 803.0, 23.0f, MUTED, false);
        this.button(1000.0, 770.0, 220.0, 46.0, "Next \u2192", () -> {
            this.avatarPage = (this.avatarPage + 1) % pages;
        }, false);
        this.rowSlider("Taille visuelle", 390.0, 860.0, 820.0, this.s().skinScale, String.format("%.2f \u00d7", 0.85 + this.s().skinScale * 0.3), v -> {
            this.s().skinScale = v;
        });
        BufferedImage preview = this.avatarPreview(this.s().avatarSkin - 1);
        if (preview != null) {
            this.g.drawImage(preview, 1324, 295, 530, 530, null);
        }
        this.label(AvatarSkins.NAMES[this.s().avatarSkin - 1], 1344.0, 867.0, 25.0f, TEXT, true);
        this.label("Imported 3D models \u00b7 author credits included", 1344.0, 901.0, 18.0f, MUTED, false);
    }

    private void musicViz() {
        this.toggle(1724.0, 148.0, this.s().visualizer, () -> {
            this.s().visualizer = !this.s().visualizer;
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.label(SpotifyVisualizer.status(), 390.0, 344.0, 26.0f, TEXT, true);
        if (!Edition.musicCustomization(Edition.PLAN)) {
            this.wrap("Spotify playback card, cover art and progress. Appearance customization is included in Premium.", 390.0, 460.0, 1300.0, 27.0f, MUTED);
            return;
        }
        this.button(390.0, 395.0, 660.0, 70.0, "D\u00e9placer la carte \u00e0 la souris", () -> {
            class_310 mc = class_310.method_1551();
            mc.method_1507((class_437)new SpotifyHudEditor(mc.field_1755));
        }, true);
        this.button(1100.0, 395.0, 700.0, 70.0, "R\u00e9initialiser la position", () -> {
            this.s().musicX = 0.025;
            this.s().musicY = 0.08;
        }, false);
        this.button(390.0, 495.0, 660.0, 62.0, "Pochette : " + (this.s().musicArtwork ? "affich\u00e9e" : "masqu\u00e9e"), () -> {
            this.s().musicArtwork = !this.s().musicArtwork;
        }, this.s().musicArtwork);
        this.button(1100.0, 495.0, 700.0, 62.0, "Mini VU-m\u00e8tre : " + (this.s().musicBars ? "oui" : "non"), () -> {
            this.s().musicBars = !this.s().musicBars;
        }, this.s().musicBars);
        this.rowSlider("Taille de la carte", 390.0, 635.0, 650.0, this.s().musicScale, UiCanvas.pct(this.s().musicScale), v -> {
            this.s().musicScale = v;
        });
        this.rowSlider("Opacit\u00e9", 1130.0, 635.0, 650.0, this.s().musicOpacity, UiCanvas.pct(this.s().musicOpacity), v -> {
            this.s().musicOpacity = v;
        });
        this.label("Accent", 390.0, 771.0, 26.0f, TEXT, true);
        this.hue(580.0, 760.0, 1200.0, this.s().visualizerHue, v -> {
            this.s().visualizerHue = v;
        });
        this.wrap("Pochette Spotify, titre, artiste, progression et dur\u00e9e totale. D\u00e9tection locale via les contr\u00f4les multim\u00e9dias Windows ; la carte reste visible en pause.", 390.0, 866.0, 1390.0, 24.0f, MUTED);
    }

    private void particles(boolean extra) {
        String[] stringArray;
        this.toggle(1724.0, 148.0, extra ? this.s().extraParticles : this.s().hitParticles, () -> {
            if (extra) {
                this.s().extraParticles = !this.s().extraParticles;
            } else {
                this.s().hitParticles = !this.s().hitParticles;
            }
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        if (extra) {
            String[] stringArray2 = new String[4];
            stringArray2[0] = "Poussi\u00e8re lumineuse";
            stringArray2[1] = "\u00c9toiles";
            stringArray2[2] = "P\u00e9tales";
            stringArray = stringArray2;
            stringArray2[3] = "Bulles";
        } else {
            String[] stringArray3 = new String[4];
            stringArray3[0] = "\u00c9toiles";
            stringArray3[1] = "C\u0153urs";
            stringArray3[2] = "Prismes";
            stringArray = stringArray3;
            stringArray3[3] = "Orbes";
        }
        String[] styles = stringArray;
        for (int i = 0; i < 4; ++i) {
            int n = i;
            this.button(390 + i % 2 * 710, 330 + i / 2 * 90, 675.0, 65.0, styles[i], () -> {
                if (extra) {
                    this.s().extraParticleStyle = n;
                } else {
                    this.s().hitParticleStyle = n;
                }
            }, (extra ? this.s().extraParticleStyle : this.s().hitParticleStyle) == i);
        }
        this.hue(390.0, 590.0, 1350.0, extra ? this.s().extraParticleHue : this.s().hitParticleHue, v -> {
            if (extra) {
                this.s().extraParticleHue = v;
            } else {
                this.s().hitParticleHue = v;
            }
        });
        this.rowSlider("Taille", 390.0, 700.0, 1100.0, extra ? this.s().extraParticleSize : this.s().hitParticleSize, "", v -> {
            if (extra) {
                this.s().extraParticleSize = v;
            } else {
                this.s().hitParticleSize = v;
            }
        });
        if (!extra) {
            this.rowSlider("Quantit\u00e9", 390.0, 825.0, 1100.0, this.s().hitParticleCount, UiCanvas.pct(this.s().hitParticleCount), v -> {
                this.s().hitParticleCount = v;
            });
        } else {
            this.wrap("Les particules restent dans le d\u00e9cor. Rendu group\u00e9 avec les autres effets et nombre limit\u00e9 pour pr\u00e9server la fluidit\u00e9.", 390.0, 850.0, 1400.0, 24.0f, MUTED);
        }
    }

    private void packCustomizer() {
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        String[] labels = new String[]{"Cristal", "Mace", "Totem", "Obsidienne", "Anchor"};
        for (int i = 0; i < 5; ++i) {
            int n = i;
            int y = 320 + i * 90;
            this.button(380.0, y, 200.0, 64.0, labels[i], () -> {
                this.packItem = n;
                this.packAngle = 0;
            }, this.packItem == i);
            this.button(600.0, y, 70.0, 64.0, "\u2039", () -> {
                this.packItem = n;
                PvpCustomizer.cycle(n, -1);
            }, false);
            Object text = PvpCustomizer.label(this.s().pvpTextures[i]);
            if (((String)text).length() > 28) {
                text = ((String)text).substring(0, 27) + "\u2026";
            }
            this.button(690.0, y, 455.0, 64.0, (String)text, () -> {
                this.packItem = n;
                this.packAngle = 0;
                PvpCustomizer.cycle(n, 1);
            }, this.s().pvpTextures[i] > 0);
            this.button(1160.0, y, 70.0, 64.0, "\u203a", () -> {
                this.packItem = n;
                this.packAngle = 0;
                PvpCustomizer.cycle(n, 1);
            }, false);
        }
        this.button(390.0, 825.0, 420.0, 56.0, "Appliquer la s\u00e9lection", PvpCustomizer::apply, true);
        this.wrap(PvpCustomizer.status(), 840.0, 850.0, 390.0, 21.0f, MUTED);
        BufferedImage preview = PvpCustomizer.preview(this.packItem, this.s().pvpTextures[this.packItem], this.packAngle);
        if (preview != null) {
            this.g.drawImage(preview, 1330, 300, 520, 520, null);
        } else {
            this.wrap("Le pack actuel reste actif pour cet objet.", 1360.0, 510.0, 440.0, 29.0f, TEXT);
        }
        this.button(1340.0, 825.0, 95.0, 48.0, "\u21b6", () -> {
            this.packAngle = Math.floorMod(this.packAngle - 1, 4);
        }, false);
        this.button(1735.0, 825.0, 95.0, 48.0, "\u21b7", () -> {
            this.packAngle = (this.packAngle + 1) % 4;
        }, false);
        String name = PvpCustomizer.label(this.s().pvpTextures[this.packItem]);
        this.wrap(name, 1455.0, 845.0, 250.0, 20.0f, TEXT);
        this.label("Aper\u00e7u 1080p \u00b7 textures et mod\u00e8les r\u00e9els", 1344.0, 901.0, 20.0f, MUTED, false);
    }

    private void fps() {
        class_315 o = class_310.method_1551().field_1690;
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        String[] modes = new String[]{"\u00c9quilibr\u00e9", "Performance", "Maximum"};
        for (int i = 0; i < 3; ++i) {
            int n = i + 1;
            this.button(390 + i * 286, 320.0, 268.0, 58.0, modes[i], () -> CakeOptimize.preset(n), this.s().fpsPreset == n);
        }
        this.rowSlider("Distance de rendu", 390.0, 440.0, 390.0, (double)(CakeOptimize.requestedDistance() - 2) / 22.0, String.valueOf(o.method_42503().method_41753()) + " chunks", v -> CakeOptimize.view(2 + (int)Math.round(v * 22.0)));
        this.rowSlider("Simulation", 840.0, 440.0, 390.0, (double)((Integer)o.method_42510().method_41753() - 5) / 7.0, String.valueOf(o.method_42510().method_41753()) + " chunks", v -> CakeOptimize.edit(a -> a.method_42510().method_41748((Object)(5 + (int)Math.round(v * 7.0)))));
        this.rowSlider("Distance des entit\u00e9s", 390.0, 610.0, 390.0, ((Double)o.method_42517().method_41753() - 0.5) / 0.5, Math.round((Double)o.method_42517().method_41753() * 100.0) + " %", v -> CakeOptimize.edit(a -> a.method_42517().method_41748((Object)(0.5 + v * 0.5))));
        this.rowSlider("Limite d\u2019images", 840.0, 610.0, 390.0, (double)((Integer)o.method_42524().method_41753() - 30) / 230.0, (String)((Integer)o.method_42524().method_41753() >= 260 ? "Illimit\u00e9e" : String.valueOf(o.method_42524().method_41753()) + " i/s"), v -> CakeOptimize.edit(a -> a.method_42524().method_41748((Object)(30 + (int)Math.round(v * 23.0) * 10))));
        this.label("Particules Minecraft", 390.0, 782.0, 25.0f, TEXT, false);
        String[] particle = new String[]{"Toutes", "R\u00e9duites", "Minimales"};
        for (int i = 0; i < 3; ++i) {
            int n = i;
            this.button(390 + i * 282, 817.0, 262.0, 57.0, particle[i], () -> CakeOptimize.edit(a -> a.method_42475().method_41748((Object)class_4066.values()[n])), ((class_4066)o.method_42475().method_41753()).ordinal() == i);
        }
        this.button(1344.0, 320.0, 490.0, 51.0, "VSync : " + ((Boolean)o.method_42433().method_41753() != false ? "oui" : "non"), () -> CakeOptimize.edit(a -> a.method_42433().method_41748((Object)((Boolean)a.method_42433().method_41753() == false ? 1 : 0))), (Boolean)o.method_42433().method_41753());
        this.button(1344.0, 390.0, 490.0, 51.0, "Ombres des entit\u00e9s : " + ((Boolean)o.method_42435().method_41753() != false ? "oui" : "non"), () -> CakeOptimize.edit(a -> a.method_42435().method_41748((Object)((Boolean)a.method_42435().method_41753() == false ? 1 : 0))), (Boolean)o.method_42435().method_41753());
        this.button(1344.0, 460.0, 490.0, 51.0, "Nuages : " + (o.method_42528().method_41753() == class_4063.field_18162 ? "non" : "oui"), () -> CakeOptimize.edit(a -> a.method_42528().method_41748((Object)(a.method_42528().method_41753() == class_4063.field_18162 ? class_4063.field_18163 : class_4063.field_18162))), o.method_42528().method_41753() != class_4063.field_18162);
        this.button(1344.0, 530.0, 490.0, 51.0, "Feuilles rapides : " + ((Boolean)o.method_75334().method_41753() != false ? "oui" : "non"), () -> CakeOptimize.edit(a -> a.method_75334().method_41748((Object)((Boolean)a.method_75334().method_41753() == false ? 1 : 0))), (Boolean)o.method_75334().method_41753());
        this.button(1344.0, 600.0, 490.0, 51.0, "Occlusion native : " + ((Boolean)o.method_41792().method_41753() != false ? "oui" : "non"), () -> CakeOptimize.edit(a -> a.method_41792().method_41748((Object)((Boolean)a.method_41792().method_41753() == false ? 1 : 0))), (Boolean)o.method_41792().method_41753());
        this.button(1344.0, 670.0, 490.0, 51.0, "All\u00e9ger les effets Cake : " + (this.s().fpsReduceEffects ? "oui" : "non"), () -> {
            boolean bl = this.s().fpsReduceEffects = !this.s().fpsReduceEffects;
            if (this.s().fpsReduceEffects) {
                CakeOptimize.reduceEffects();
            }
        }, this.s().fpsReduceEffects);
        this.button(1344.0, 740.0, 490.0, 51.0, "Textures Potato 8\u00d78 : " + (this.s().potatoEnabled ? "oui" : "non"), () -> CakeOptimize.potato(!this.s().potatoEnabled), this.s().potatoEnabled);
        this.button(1344.0, 835.0, 490.0, 56.0, "R\u00e9tablir mes r\u00e9glages", CakeOptimize::restore, false);
    }

    private void fog() {
        this.toggle(1724.0, 148.0, this.s().fogEnabled, () -> {
            boolean bl = this.s().fogEnabled = !this.s().fogEnabled;
            if (this.s().fogEnabled && this.s().fogStrength == 0.0) {
                this.s().fogStrength = 0.65;
            }
            CakeOptimize.applyFogLimit();
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        this.rowSlider("Distance", 390.0, 345.0, 390.0, this.s().fogDistance, (int)FogMath.distance(this.s().fogDistance) + " blocs", v -> {
            this.s().fogDistance = v;
            CakeOptimize.applyFogLimit();
        });
        this.rowSlider("D\u00e9but de la brume", 840.0, 345.0, 390.0, this.s().fogStart, Math.round(this.s().fogStart * 100.0) + " %", v -> {
            this.s().fogStart = v;
        });
        this.rowSlider("Densit\u00e9", 390.0, 525.0, 390.0, this.s().fogStrength, UiCanvas.pct(this.s().fogStrength), v -> {
            this.s().fogStrength = v;
        });
        this.rowSlider("Opacit\u00e9", 840.0, 525.0, 390.0, this.s().fogOpacity, UiCanvas.pct(this.s().fogOpacity), v -> {
            this.s().fogOpacity = v;
        });
        String[] presets = new String[]{"Brume", "Naturel", "Violet"};
        for (int i = 0; i < 3; ++i) {
            int n = i;
            this.button(390 + i * 286, 690.0, 268.0, 58.0, presets[i], () -> {
                this.s().fogEnabled = true;
                this.s().fogStrength = 0.7;
                this.s().fogDistance = n == 0 ? 0.09 : 0.3;
                this.s().fogStart = n == 0 ? 0.08 : 0.35;
                this.s().fogTint = n == 1 ? 0.12 : 0.8;
                this.s().fogSaturation = n == 2 ? 0.45 : 0.18;
                this.s().fogBrightness = 0.76;
                this.s().fogHue = n == 2 ? 0.76 : 0.56;
                this.s().fogOpacity = 1.0;
                CakeOptimize.applyFogLimit();
            }, false);
        }
        float visible = Math.min(FogMath.distance(this.s().fogDistance), (float)((Integer)class_310.method_1551().field_1690.method_42503().method_41753()).intValue() * 16.0f);
        this.label("Visibilit\u00e9 effective : " + (int)visible + " blocs", 390.0, 811.0, 24.0f, TEXT, true);
        this.wrap("Activez la limite de rendu pour dessiner moins de chunks au-del\u00e0 du brouillard. La r\u00e9solution reste inchang\u00e9e.", 390.0, 855.0, 840.0, 21.0f, MUTED);
        this.label("Couleur", 1344.0, 340.0, 25.0f, TEXT, false);
        this.hue(1344.0, 374.0, 490.0, this.s().fogHue, v -> {
            this.s().fogHue = v;
        });
        this.rowSlider("Saturation", 1344.0, 460.0, 490.0, this.s().fogSaturation, UiCanvas.pct(this.s().fogSaturation), v -> {
            this.s().fogSaturation = v;
        });
        this.rowSlider("Luminosit\u00e9", 1344.0, 565.0, 490.0, this.s().fogBrightness, UiCanvas.pct(this.s().fogBrightness), v -> {
            this.s().fogBrightness = v;
        });
        this.rowSlider("M\u00e9lange de la teinte", 1344.0, 670.0, 490.0, this.s().fogTint, UiCanvas.pct(this.s().fogTint), v -> {
            this.s().fogTint = v;
        });
        this.button(1344.0, 770.0, 490.0, 49.0, "Brume sur le ciel : " + (this.s().fogSky ? "oui" : "non"), () -> {
            this.s().fogSky = !this.s().fogSky;
        }, this.s().fogSky);
        this.button(1344.0, 835.0, 490.0, 56.0, "Limiter le rendu : " + (this.s().fogLimitTerrain ? "oui" : "non"), () -> {
            this.s().fogLimitTerrain = !this.s().fogLimitTerrain;
            CakeOptimize.applyFogLimit();
        }, this.s().fogLimitTerrain);
    }

    private void optics() {
        this.toggle(1724.0, 148.0, this.s().atmosphere, () -> {
            this.s().atmosphere = !this.s().atmosphere;
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        String[] names = new String[]{"Naturel", "Cin\u00e9ma", "Dor\u00e9", "Clair de lune"};
        for (int i = 0; i < 4; ++i) {
            int n = i;
            this.button(390 + i % 2 * 430, 322 + i / 2 * 78, 410.0, 58.0, names[i], () -> {
                this.s().atmosphereStyle = n;
            }, this.s().atmosphereStyle == i);
        }
        this.rowSlider("Bloom lumineux", 390.0, 530.0, 390.0, this.s().bloom, UiCanvas.pct(this.s().bloom), v -> {
            this.s().bloom = v;
        });
        this.rowSlider("Ombres de contact", 840.0, 530.0, 390.0, this.s().occlusion, UiCanvas.pct(this.s().occlusion), v -> {
            this.s().occlusion = v;
        });
        this.rowSlider("Exposition", 390.0, 700.0, 390.0, this.s().exposure, UiCanvas.pct(this.s().exposure), v -> {
            this.s().exposure = v;
        });
        this.rowSlider("Vignette douce", 840.0, 700.0, 390.0, this.s().vignette, UiCanvas.pct(this.s().vignette), v -> {
            this.s().vignette = v;
        });
        this.label("Rendu \u00e0 la r\u00e9solution du jeu", 1344.0, 341.0, 26.0f, TEXT, true);
        this.wrap("Bloom des zones lumineuses, occlusion locale \u00e0 partir de la profondeur et \u00e9talonnage des couleurs. Interface et paroles restent nettes.", 1344.0, 410.0, 480.0, 24.0f, MUTED);
        this.wrap("1080p si le jeu est en 1920 \u00d7 1080. Aucun sous-\u00e9chantillonnage. Le co\u00fbt d\u00e9pend de votre carte graphique.", 1344.0, 644.0, 480.0, 22.0f, MUTED);
        this.button(1344.0, 803.0, 490.0, 66.0, UiCanvas.trialLabel(), () -> PreviewTrial.start(7), true);
    }

    private void trialCard(int module, String title, String description) {
        this.card(1304.0, 282.0, 570.0, 635.0);
        this.label("Aper\u00e7u en jeu", 1344.0, 341.0, 28.0f, TEXT, true);
        this.wrap(title, 1344.0, 409.0, 480.0, 26.0f, TEXT);
        this.wrap(description, 1344.0, 508.0, 480.0, 24.0f, MUTED);
        this.wrap("\u00c0 la fin, vos r\u00e9glages pr\u00e9c\u00e9dents sont r\u00e9tablis et le menu revient.", 1344.0, 642.0, 480.0, 23.0f, MUTED);
        this.button(1344.0, 803.0, 490.0, 66.0, UiCanvas.trialLabel(), () -> PreviewTrial.start(module), true);
    }

    private boolean enabled(int t) {
        Settings a = this.s();
        return switch (t) {
            case 0 -> a.motionBlur;
            case 1 -> a.fireflies;
            case 2 -> a.spatial;
            case 3 -> a.sky;
            case 4 -> {
                if (a.sakura || a.motes || a.embers) {
                    yield true;
                }
                yield false;
            }
            case 5 -> a.lyrics;
            case 6, 24 -> a.knives;
            case 7 -> a.atmosphere;
            case 8 -> {
                if (a.fpsPreset > 0) {
                    yield true;
                }
                yield false;
            }
            case 9 -> a.fogEnabled;
            case 10 -> a.trailEnabled;
            case 11 -> a.jumpEnabled;
            case 12 -> a.followEnabled;
            case 13 -> a.hitboxEnabled;
            case 14 -> a.hotbarEnabled;
            case 15 -> a.skinEnabled;
            case 16 -> a.visualizer;
            case 17 -> a.hitParticles;
            case 18 -> a.extraParticles;
            case 19 -> Arrays.stream(a.pvpTextures).anyMatch(n -> n > 0);
            case 20 -> a.backEnabled;
            case 21 -> a.realistic;
            case 22 -> a.crosshairEnabled;
            case 23 -> {
                if (a.hitColor || a.deathEffects) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    private void preferences() {
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.label("General settings", 390.0, 340.0, 30.0f, TEXT, true);
        this.button(390.0, 400.0, 660.0, 65.0, "Menu : " + MenuKey.name() + " \u00b7 modifier", () -> {
            MenuKey.waiting = true;
        }, MenuKey.waiting);
        this.button(1100.0, 400.0, 690.0, 65.0, "Language \u00b7 " + this.s().language.toUpperCase(), () -> {
            this.s().language = this.s().language.equals("en") ? "fr" : (this.s().language.equals("fr") ? "ru" : "en");
        }, false);
        this.option(390.0, 590.0, "Partage cosm\u00e9tiques : " + (this.s().shareCosmetics ? "oui" : "non"), this.s().shareCosmetics, () -> {
            this.s().shareCosmetics = !this.s().shareCosmetics;
        });
        this.option(390.0, 710.0, "Sons activ\u00e9s", this.s().sounds, () -> {
            this.s().sounds = !this.s().sounds;
        });
    }

    private BufferedImage backPreview(int model) {
        if (BACK_PREVIEWS[model] == null) {
            try (InputStream in = UiCanvas.class.getResourceAsStream("/assets/cakeclient/back/preview-" + model + ".png");){
                if (in != null) {
                    UiCanvas.BACK_PREVIEWS[model] = ImageIO.read(in);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return BACK_PREVIEWS[model];
    }

    private void backs() {
        this.toggle(1724.0, 148.0, this.s().backEnabled, () -> {
            this.s().backEnabled = !this.s().backEnabled;
        });
        this.card(350.0, 282.0, 925.0, 635.0);
        this.card(1304.0, 282.0, 570.0, 635.0);
        this.label("Choose your wings", 390.0, 337.0, 29.0f, TEXT, true);
        for (int i = 0; i < BackCosmetics.NAMES.length; ++i) {
            int n = i;
            this.button(390 + i % 3 * 285, 370 + i / 3 * 68, 267.0, 55.0, BackCosmetics.NAMES[i], () -> {
                this.s().backStyle = n;
            }, this.s().backStyle == i);
        }
        this.rowSlider("Scale", 390.0, 785.0, 390.0, this.s().backScale, UiCanvas.pct(this.s().backScale), v -> {
            this.s().backScale = v;
        });
        this.rowSlider("Animation speed", 840.0, 785.0, 390.0, this.s().backSpeed, UiCanvas.pct(this.s().backSpeed), v -> {
            this.s().backSpeed = v;
        });
        this.label(BackCosmetics.NAMES[this.s().backStyle], 1344.0, 337.0, 28.0f, TEXT, true);
        BufferedImage backImage = this.backPreview(this.s().backStyle);
        if (backImage != null) {
            this.g.drawImage(backImage, 1324, 350, 530, 298, null);
        } else {
            this.wrap("Choose a style, adjust its fit, then preview it on your character.", 1344.0, 425.0, 470.0, 24.0f, MUTED);
        }
        this.rowSlider("Shoulder height", 1344.0, 680.0, 490.0, this.s().backLift, UiCanvas.pct(this.s().backLift), v -> {
            this.s().backLift = v;
        });
        this.label(this.s().backStyle <= 5 ? "Smooth wingbeat" : "Luminous contour", 1344.0, 791.0, 22.0f, TEXT, false);
        this.toggle(1724.0, 758.0, this.s().backHalo, () -> {
            this.s().backHalo = !this.s().backHalo;
        });
        this.button(1344.0, 835.0, 490.0, 50.0, "Preview on my character \u00b7 F5", () -> PreviewTrial.start(20), true);
        this.label("Imported model collection \u00b7 credits included", 390.0, 895.0, 18.0f, MUTED, false);
    }

    private void realism() {
        int n;
        int i;
        this.toggle(1724.0, 148.0, this.s().realistic, () -> {
            this.s().realistic = !this.s().realistic;
        });
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.button(390.0, 310.0, 690.0, 52.0, "Realis \u00b7 atmospheric", () -> {
            this.s().shaderPack = 0;
        }, this.s().shaderPack == 0);
        this.button(1110.0, 310.0, 690.0, 52.0, "MakeUp \u00b7 cinematic", () -> {
            this.s().shaderPack = 1;
        }, this.s().shaderPack == 1);
        this.label("Visual mood", 390.0, 414.0, 24.0f, TEXT, true);
        for (i = 0; i < 3; ++i) {
            n = i;
            this.button(390 + i * 475, 440.0, 450.0, 52.0, (new String[]{"Natural light", "Cinema", "Dreamscape"})[i], () -> {
                this.s().realismLook = n;
            }, this.s().realismLook == i);
        }
        this.label("Render quality", 390.0, 553.0, 25.0f, TEXT, true);
        for (i = 0; i < 3; ++i) {
            n = i;
            this.button(390 + i * 475, 580.0, 450.0, 50.0, (new String[]{"Balanced", "High", "Ultra"})[i], () -> {
                this.s().realismQuality = n;
            }, this.s().realismQuality == i);
        }
        if (this.s().shaderPack == 0) {
            this.rowSlider("Exposure", 390.0, 690.0, 420.0, this.s().realismExposure, UiCanvas.pct(this.s().realismExposure), v -> {
                this.s().realismExposure = v;
            });
            this.rowSlider("Bloom", 870.0, 690.0, 420.0, this.s().realismBloom, UiCanvas.pct(this.s().realismBloom), v -> {
                this.s().realismBloom = v;
            });
            this.rowSlider("Rain & puddles", 1350.0, 690.0, 420.0, this.s().realismRain, UiCanvas.pct(this.s().realismRain), v -> {
                this.s().realismRain = v;
            });
        } else {
            this.wrap("MakeUp uses the author\u2019s quality profiles. Advanced shader controls are available in Iris settings.", 390.0, 713.0, 1370.0, 24.0f, MUTED);
        }
        this.wrap("Photographic 256x materials, surface relief, animated water and wet reflections. Select a mood, then apply. Rain follows the world's weather.", 390.0, 800.0, 900.0, 22.0f, MUTED);
        this.button(1344.0, 775.0, 450.0, 64.0, "Apply look", () -> {
            this.s().realistic = true;
            RealisticMode.refresh();
        }, true);
        this.wrap(RealisticMode.status, 390.0, 895.0, 1380.0, 18.0f, PURPLE);
    }

    private void crosshairs() {
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.option(390.0, 340.0, "Custom crosshair", this.s().crosshairEnabled, () -> {
            this.s().crosshairEnabled = !this.s().crosshairEnabled;
        });
        for (int i = 0; i < 8; ++i) {
            int n = i;
            this.button(390 + i % 4 * 350, 400 + i / 4 * 65, 330.0, 52.0, CrosshairHud.NAMES[i], () -> {
                this.s().crosshairStyle = n;
            }, this.s().crosshairStyle == i);
        }
        this.rowSlider("Size", 390.0, 600.0, 640.0, this.s().crosshairSize, UiCanvas.pct(this.s().crosshairSize), v -> {
            this.s().crosshairSize = v;
        });
        this.rowSlider("Gap", 1110.0, 600.0, 640.0, this.s().crosshairGap, UiCanvas.pct(this.s().crosshairGap), v -> {
            this.s().crosshairGap = v;
        });
        this.rowSlider("Thickness", 390.0, 750.0, 640.0, this.s().crosshairThickness, UiCanvas.pct(this.s().crosshairThickness), v -> {
            this.s().crosshairThickness = v;
        });
        this.hue(1110.0, 800.0, 640.0, this.s().crosshairHue, v -> {
            this.s().crosshairHue = v;
        });
    }

    private void combat() {
        this.card(350.0, 282.0, 1524.0, 635.0);
        this.option(390.0, 340.0, "Hit color", this.s().hitColor, () -> {
            this.s().hitColor = !this.s().hitColor;
        });
        this.hue(1110.0, 315.0, 640.0, this.s().hitColorHue, v -> {
            this.s().hitColorHue = v;
        });
        this.rowSlider("Tint strength", 390.0, 425.0, 640.0, this.s().hitColorStrength, UiCanvas.pct(this.s().hitColorStrength), v -> {
            this.s().hitColorStrength = v;
        });
        this.option(390.0, 620.0, "Death effects", this.s().deathEffects, () -> {
            this.s().deathEffects = !this.s().deathEffects;
        });
        for (int i = 0; i < 5; ++i) {
            int n = i;
            this.button(390 + i % 3 * 475, 680 + i / 3 * 78, 450.0, 62.0, CombatEffects.NAMES[i], () -> {
                this.s().deathStyle = n;
            }, this.s().deathStyle == i);
        }
    }

    private void materials() {
        this.card(350.0, 282.0, 1524.0, 635.0);
        for (int i = 0; i < KnifeFinishes.NAMES.length; ++i) {
            int n = i;
            this.button(390 + i % 4 * 350, 320 + i / 4 * 72, 330.0, 58.0, KnifeFinishes.NAMES[i], () -> {
                this.s().knifeFinish = n;
            }, this.s().knifeFinish == i);
        }
        this.option(390.0, 680.0, "Blade trails", this.s().knifeTrails, () -> {
            this.s().knifeTrails = !this.s().knifeTrails;
        });
        this.hue(1110.0, 660.0, 640.0, this.s().knifeTrailHue, v -> {
            this.s().knifeTrailHue = v;
        });
        this.rowSlider("Trail lifetime", 390.0, 790.0, 640.0, this.s().knifeTrailLife, UiCanvas.pct(this.s().knifeTrailLife), v -> {
            this.s().knifeTrailLife = v;
        });
        this.wrap("Factory uses the original CS asset. The other materials are original Cake designs, available on every knife.", 1110.0, 780.0, 640.0, 22.0f, MUTED);
    }

    private void option(double x, double y, String label, boolean on, Runnable action) {
        this.label(label, x, y, 27.0f, TEXT, false);
        this.toggle(x + 540.0, y - 32.0, on, action);
    }

    private void rowSlider(String label, double x, double y, double w, double value, String display, DoubleConsumer consumer) {
        this.label(label, x, y, 25.0f, TEXT, false);
        this.slider(x, y + 50.0, w, value, display, consumer);
    }

    private void slider(double x, double y, double w, double value, String display, DoubleConsumer consumer) {
        this.right(display, x + w, y - 27.0, 22.0f, PURPLE);
        this.round(x, y, w, 8.0, 4.0, new Color(55, 52, 65));
        this.round(x, y, Math.max(8.0, w * Math.clamp(value, 0.0, 1.0)), 8.0, 4.0, PURPLE);
        this.g.setColor(TEXT);
        this.g.fill(new Ellipse2D.Double(x + w * Math.clamp(value, 0.0, 1.0) - 10.0, y - 6.0, 20.0, 20.0));
        this.widgets.add(new Widget(x, y - 20.0, w, 48.0, null, consumer));
    }

    private void hue(double x, double y, double w, double value, DoubleConsumer consumer) {
        for (int i = 0; i < (int)w; ++i) {
            this.g.setColor(Color.getHSBColor((float)((double)i / w), 0.5f, 1.0f));
            this.g.fillRect((int)x + i, (int)y, 1, 18);
        }
        this.g.setStroke(new BasicStroke(3.0f));
        this.g.setColor(TEXT);
        this.g.draw(new RoundRectangle2D.Double(x + w * value - 8.0, y - 5.0, 16.0, 28.0, 6.0, 6.0));
        this.widgets.add(new Widget(x, y - 14.0, w, 48.0, null, consumer));
    }

    private void toggle(double x, double y, boolean on, Runnable action) {
        this.round(x, y, 110.0, 51.0, 25.0, on ? PURPLE : new Color(66, 63, 74));
        this.g.setColor(on ? TEXT : new Color(170, 167, 181));
        this.g.fill(new Ellipse2D.Double(x + (double)(on ? 66 : 8), y + 8.0, 35.0, 35.0));
        this.widgets.add(new Widget(x, y, 110.0, 51.0, action, null));
    }

    private void choice(double x, double y, double w, double h, String text, Runnable action, boolean primary, boolean allowed) {
        text = I18n.tr(text);
        if (allowed) {
            this.button(x, y, w, h, text, action, primary);
            return;
        }
        this.round(x, y, w, h, 18.0, new Color(55, 49, 65, 100));
        this.g.setFont(UiCanvas.font(h < 55.0 ? 21 : 24, false));
        FontMetrics fm = this.g.getFontMetrics();
        this.g.setColor(new Color(127, 119, 140));
        float tx = (float)(x + (w - (double)fm.stringWidth(text)) / 2.0);
        float ty = (float)(y + (h - (double)fm.getHeight()) / 2.0 + (double)fm.getAscent());
        this.g.drawString(text, tx, ty);
        this.g.setStroke(new BasicStroke(2.0f));
        this.g.draw(new Line2D.Double(tx, (double)ty - h * 0.14, tx + (float)fm.stringWidth(text), (double)ty - h * 0.14));
    }

    private void button(double x, double y, double w, double h, String text, Runnable action, boolean primary) {
        Color color;
        text = I18n.tr(text);
        boolean hover = !this.spatial && this.pointerX >= x && this.pointerX <= x + w && this.pointerY >= y && this.pointerY <= y + h;
        Color color2 = color = primary ? new Color(128, 85, 210, 55) : new Color(40, 41, 59, 240);
        if (hover) {
            color = new Color(92, 73, 134, 210);
        }
        this.round(x, y, w, h, 12.0, color);
        if (primary) {
            this.g.setStroke(new BasicStroke(1.0f));
            this.g.setColor(new Color(178, 139, 255, 125));
            this.g.draw(new RoundRectangle2D.Double(x, y, w, h, 24.0, 24.0));
        }
        float fs = h < 55.0 ? 21 : 24;
        this.g.setFont(UiCanvas.font(fs, primary));
        while ((double)this.g.getFontMetrics().stringWidth(text) > w - 24.0 && fs > 12.0f) {
            this.g.setFont(UiCanvas.font(fs -= 1.0f, primary));
        }
        FontMetrics fm = this.g.getFontMetrics();
        this.g.setColor(primary ? new Color(225, 204, 255) : TEXT);
        this.g.drawString(text, (float)(x + (w - (double)fm.stringWidth(text)) / 2.0), (float)(y + (h - (double)fm.getHeight()) / 2.0 + (double)fm.getAscent()));
        this.widgets.add(new Widget(x, y, w, h, action, null));
    }

    private void navItem(int c, double x, double y, double w, double h) {
        boolean selected = UiCanvas.category(this.tab) == c;
        this.round(x, y, w, h, 13.0, selected ? new Color(120, 86, 190, 70) : new Color(31, 35, 51, 140));
        this.g.setColor(selected ? PURPLE : MUTED);
        this.g.setStroke(new BasicStroke(1.8f));
        double cx = x + 27.0;
        double cy = y + h / 2.0;
        if (c == 0) {
            this.g.draw(new Ellipse2D.Double(cx - 7.0, cy - 7.0, 14.0, 14.0));
            for (int k = 0; k < 8; ++k) {
                double a = (double)k * Math.PI / 4.0;
                this.g.draw(new Line2D.Double(cx + Math.cos(a) * 10.0, cy + Math.sin(a) * 10.0, cx + Math.cos(a) * 13.0, cy + Math.sin(a) * 13.0));
            }
        } else if (c == 1) {
            this.g.draw(new Arc2D.Double(cx - 12.0, cy - 7.0, 16.0, 14.0, 0.0, 180.0, 0));
            this.g.draw(new Arc2D.Double(cx - 3.0, cy - 4.0, 14.0, 10.0, 0.0, 180.0, 0));
            this.g.draw(new Line2D.Double(cx - 12.0, cy + 3.0, cx + 11.0, cy + 3.0));
        } else if (c == 2) {
            Path2D.Double p = new Path2D.Double();
            ((Path2D)p).moveTo(cx, cy - 11.0);
            ((Path2D)p).lineTo(cx + 11.0, cy);
            ((Path2D)p).lineTo(cx, cy + 11.0);
            ((Path2D)p).lineTo(cx - 11.0, cy);
            p.closePath();
            this.g.draw(p);
        } else if (c == 3) {
            this.g.draw(new Ellipse2D.Double(cx - 9.0, cy - 9.0, 18.0, 18.0));
            this.g.draw(new Line2D.Double(cx - 13.0, cy, cx + 13.0, cy));
            this.g.draw(new Line2D.Double(cx, cy - 13.0, cx, cy + 13.0));
        } else if (c == 4) {
            this.g.draw(new Line2D.Double(cx - 5.0, cy + 6.0, cx - 5.0, cy - 9.0));
            this.g.draw(new Line2D.Double(cx - 5.0, cy - 9.0, cx + 7.0, cy - 12.0));
            this.g.draw(new Line2D.Double(cx + 7.0, cy - 12.0, cx + 7.0, cy + 3.0));
            this.g.fill(new Ellipse2D.Double(cx - 11.0, cy + 3.0, 7.0, 6.0));
            this.g.fill(new Ellipse2D.Double(cx + 1.0, cy, 7.0, 6.0));
        } else {
            this.g.draw(new RoundRectangle2D.Double(cx - 11.0, cy - 9.0, 22.0, 18.0, 5.0, 5.0));
            this.g.draw(new Line2D.Double(cx - 6.0, cy - 3.0, cx + 6.0, cy - 3.0));
            this.g.draw(new Line2D.Double(cx - 6.0, cy + 3.0, cx + 3.0, cy + 3.0));
        }
        this.label(CATEGORY_NAMES[c], x + 52.0, y + h / 2.0 + 7.0, 20.0f, selected ? TEXT : MUTED, selected);
        if (selected) {
            this.round(x + 52.0, y + h - 5.0, 40.0, 2.0, 1.0, PURPLE);
        }
        this.widgets.add(new Widget(x, y, w, h, () -> {
            this.tab = CATEGORIES[c][0];
            this.drag = -1;
            this.dirty = true;
        }, null));
    }

    private void card(double x, double y, double w, double h) {
        this.round(x + 2.0, y + 5.0, w, h, 18.0, new Color(0, 0, 0, 35));
        this.round(x, y, w, h, 18.0, CARD);
        this.g.setPaint(new GradientPaint((float)x, (float)y, new Color(121, 96, 172, 13), (float)(x + w), (float)(y + h), new Color(15, 18, 30, 0)));
        this.g.fill(new RoundRectangle2D.Double(x, y, w, h, 36.0, 36.0));
        this.g.setStroke(new BasicStroke(1.0f));
        this.g.setColor(LINE);
        this.g.draw(new RoundRectangle2D.Double(x, y, w, h, 28.0, 28.0));
    }

    private void line(double x, double y, double x2, double y2) {
        this.g.setColor(LINE);
        this.g.setStroke(new BasicStroke(1.2f));
        this.g.draw(new Line2D.Double(x, y, x2, y2));
    }

    private void round(double x, double y, double w, double h, double r, Color color) {
        this.g.setColor(color);
        this.g.fill(new RoundRectangle2D.Double(x, y, w, h, r * 2.0, r * 2.0));
    }

    private void label(String text, double x, double y, float size, Color color, boolean bold) {
        text = I18n.tr(text);
        if (x == 51.0 && size == 23.0f) {
            while (this.g.getFontMetrics(UiCanvas.font(size, bold)).stringWidth(text) > 230 && size > 13.0f) {
                size -= 1.0f;
            }
        }
        this.g.setFont(UiCanvas.font(size, bold));
        this.g.setColor(color);
        this.g.drawString(text, (float)x, (float)y);
    }

    private void right(String text, double x, double y, float size, Color color) {
        text = I18n.tr(text);
        this.g.setFont(UiCanvas.font(size, false));
        this.label(text, x - (double)this.g.getFontMetrics().stringWidth(text), y, size, color, false);
    }

    private void wrap(String text, double x, double y, double w, float size, Color color) {
        text = I18n.tr(text);
        this.g.setFont(UiCanvas.font(size, false));
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String next;
            String string = next = line.isEmpty() ? word : String.valueOf(line) + " " + word;
            if ((double)this.g.getFontMetrics().stringWidth(next) > w && !line.isEmpty()) {
                this.label(line.toString(), x, y, size, color, false);
                line.setLength(0);
                y += (double)size * 1.6;
            }
            if (!line.isEmpty()) {
                line.append(' ');
            }
            line.append(word);
        }
        if (!line.isEmpty()) {
            this.label(line.toString(), x, y, size, color, false);
        }
    }

    private static String trialLabel() {
        return class_310.method_1551().field_1687 == null ? "Ouvrez un monde pour essayer" : "Essayer en jeu \u00b7 6 s";
    }

    private static String pct(double v) {
        return Math.round(v * 100.0) + " %";
    }

    public void close() {
        if (this.texture != null) {
            this.texture.close();
            this.texture = null;
        }
    }

    private record Widget(double x, double y, double w, double h, Runnable action, DoubleConsumer slider) {
    }

    public record Hover(double x, double y, double w, double h) {
    }
}

