/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.lwjgl.system.MemoryUtil
 */
package fr.cake.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import fr.cake.client.DirtyRegion;
import fr.cake.client.ImagePixels;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.system.MemoryUtil;

public final class SmoothTexture
extends class_1043 {
    public final class_2960 id;
    private final int[] previous;
    private final int[] row;
    private int[] decodedPixels;
    private final int width;
    private final int height;
    private boolean initialized;
    private long uploadedBytes;
    private long uploads;

    public long uploadedBytes() {
        return this.uploadedBytes;
    }

    public long uploads() {
        return this.uploads;
    }

    public SmoothTexture(String name, int width, int height) {
        super("Cake / " + name, width, height, true);
        this.id = class_2960.method_60655((String)"cakeclient", (String)("dynamic/" + name));
        this.field_63613 = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        this.width = width;
        this.height = height;
        this.previous = new int[width * height];
        this.row = new int[width];
        class_310.method_1551().method_1531().method_4616(this.id, (class_1044)this);
    }

    public void update(BufferedImage image) {
        DirtyRegion dirty;
        int[] source = ImagePixels.packedArgb(image, this.width, this.height);
        if (source == null) {
            if (this.decodedPixels == null) {
                this.decodedPixels = new int[this.width * this.height];
            }
            ImagePixels.copyArgb(image, this.width, this.height, this.decodedPixels);
            source = this.decodedPixels;
        }
        DirtyRegion dirtyRegion = dirty = this.initialized ? DirtyRegion.between(source, this.previous, this.width, this.height) : new DirtyRegion(0, 0, this.width, this.height);
        if (dirty == null) {
            return;
        }
        IntBuffer pixels = MemoryUtil.memIntBuffer((long)this.method_4525().method_67769(), (int)(this.width * this.height));
        for (int y = dirty.y(); y < dirty.y() + dirty.height(); ++y) {
            int offset = y * this.width + dirty.x();
            for (int x = 0; x < dirty.width(); ++x) {
                int p;
                this.previous[offset + x] = p = source[offset + x];
                this.row[x] = p & 0xFF00FF00 | p >>> 16 & 0xFF | (p & 0xFF) << 16;
            }
            pixels.position(offset);
            pixels.put(this.row, 0, dirty.width());
        }
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.method_68004(), this.method_4525(), 0, 0, dirty.x(), dirty.y(), dirty.width(), dirty.height(), dirty.x(), dirty.y());
        this.initialized = true;
        ++this.uploads;
        this.uploadedBytes += (long)dirty.width() * (long)dirty.height() * 4L;
    }
}

