/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

final class ImagePixels {
    private ImagePixels() {
    }

    private static void dimensions(BufferedImage image, int width, int height) {
        if (image == null || image.getWidth() != width || image.getHeight() != height) {
            throw new IllegalArgumentException("Texture image dimensions do not match the upload target");
        }
    }

    static int[] packedArgb(BufferedImage image, int width, int height) {
        Object object;
        ImagePixels.dimensions(image, width, height);
        WritableRaster raster = image.getRaster();
        if (image.getType() == 2 && (object = raster.getDataBuffer()) instanceof DataBufferInt) {
            DataBufferInt pixels = (DataBufferInt)object;
            object = raster.getSampleModel();
            if (object instanceof SinglePixelPackedSampleModel) {
                SinglePixelPackedSampleModel layout = (SinglePixelPackedSampleModel)object;
                if (pixels.getNumBanks() == 1 && pixels.getOffset() == 0 && raster.getSampleModelTranslateX() == 0 && raster.getSampleModelTranslateY() == 0 && layout.getScanlineStride() == width && pixels.getData().length == width * height) {
                    return pixels.getData();
                }
            }
        }
        return null;
    }

    static void copyArgb(BufferedImage image, int width, int height, int[] destination) {
        ImagePixels.dimensions(image, width, height);
        if (destination == null || destination.length < width * height) {
            throw new IllegalArgumentException("Pixel conversion buffer is too small");
        }
        image.getRGB(0, 0, width, height, destination, 0, width);
    }
}

