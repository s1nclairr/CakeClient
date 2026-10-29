/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class CsKnifeAsset {
    public final List<Part> parts;
    public final Clip[] clips;

    public int find(String name) {
        for (int i = 0; i < this.clips.length; ++i) {
            if (!this.clips[i].name.equals(name)) continue;
            return i;
        }
        return -1;
    }

    private CsKnifeAsset(List<Part> p, Clip[] c) {
        this.parts = List.copyOf(p);
        this.clips = c;
    }

    public static CsKnifeAsset load(String name) throws IOException {
        CsKnifeAsset csKnifeAsset;
        block19: {
            InputStream in = CsKnifeAsset.class.getResourceAsStream("/assets/cakeclient/knives/" + name + ".ckm");
            try {
                int hands;
                boolean full;
                if (in == null) {
                    throw new FileNotFoundException(name);
                }
                ByteBuffer b = ByteBuffer.wrap(in.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
                int magic = b.getInt();
                boolean version7 = magic == 927812419;
                boolean version6 = magic == 911035203 || version7;
                boolean bl = full = magic == 894257987 || version6;
                int n = version6 ? 32 : (hands = full ? 16 : 3);
                if (magic != 877480771 && !full) {
                    throw new IOException("Invalid knife mesh");
                }
                int n2 = b.getInt();
                if (n2 < 1 || n2 > 32) {
                    throw new IOException("Invalid mesh parts");
                }
                ArrayList<Part> parts = new ArrayList<Part>();
                for (int i = 0; i < n2; ++i) {
                    int joint = b.getInt();
                    int count = b.getInt();
                    if (count < 3 || count > 1000000) {
                        throw new IOException("Invalid vertices");
                    }
                    ByteBuffer vertices = ByteBuffer.allocateDirect(count * 28).order(ByteOrder.nativeOrder());
                    ByteBuffer src = b.slice();
                    src.limit(count * 28);
                    vertices.put(src).flip();
                    b.position(b.position() + count * 28);
                    parts.add(new Part(joint, count, vertices));
                }
                Clip[] clips = new Clip[b.getInt()];
                for (int i = 0; i < clips.length; ++i) {
                    String label;
                    if (version6) {
                        int length = Short.toUnsignedInt(b.getShort());
                        byte[] raw = new byte[length];
                        b.get(raw);
                        label = new String(raw, StandardCharsets.UTF_8);
                    } else {
                        label = (new String[]{"idle", "inspect", "deploy", "slash1", "slash2", "stab"})[Math.min(i, 5)];
                    }
                    float d = b.getFloat();
                    float holdStart = version7 ? b.getFloat() : -1.0f;
                    float holdEnd = version7 ? b.getFloat() : -1.0f;
                    int frames = b.getInt();
                    if (d <= 0.0f || frames < 1 || frames > 2000) {
                        throw new IOException("Invalid clip");
                    }
                    float[] data = new float[frames * (n2 * 16 + hands)];
                    for (int k = 0; k < data.length; ++k) {
                        data[k] = b.getFloat();
                    }
                    clips[i] = new Clip(label, d, frames, n2, data, hands, holdStart, holdEnd);
                }
                if (b.hasRemaining()) {
                    throw new IOException("Unexpected knife data");
                }
                csKnifeAsset = new CsKnifeAsset(parts, clips);
                if (in == null) break block19;
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
                catch (BufferUnderflowException e) {
                    throw new IOException("Truncated knife asset", e);
                }
            }
            in.close();
        }
        return csKnifeAsset;
    }

    public static final class Clip {
        public final float duration;
        public final float holdStart;
        public final float holdEnd;
        public final int frames;
        private final float[] data;
        private final int stride;
        private final int parts;
        private final boolean fullHand;
        private final int handFloats;
        public final String name;

        Clip(String name, float d, int f, int p, float[] a, int handFloats, float holdStart, float holdEnd) {
            this.holdStart = holdStart;
            this.holdEnd = holdEnd;
            this.name = name;
            this.duration = d;
            this.frames = f;
            this.parts = p;
            this.data = a;
            this.handFloats = handFloats;
            this.fullHand = handFloats >= 16;
            this.stride = p * 16 + handFloats;
        }

        public Matrix4f matrix(int part, double seconds) {
            double frame = Math.clamp(seconds / (double)this.duration, 0.0, 1.0) * (double)(this.frames - 1);
            int a = (int)frame;
            int b = Math.min(this.frames - 1, a + 1);
            float t = (float)(frame - (double)a);
            Matrix4f first = new Matrix4f().set(Arrays.copyOfRange(this.data, a * this.stride + part * 16, a * this.stride + part * 16 + 16));
            if (a == b || t == 0.0f) {
                return first;
            }
            Matrix4f second = new Matrix4f().set(Arrays.copyOfRange(this.data, b * this.stride + part * 16, b * this.stride + part * 16 + 16));
            Vector3f position = first.getTranslation(new Vector3f()).lerp((Vector3fc)second.getTranslation(new Vector3f()), t);
            Quaternionf rotation = first.getNormalizedRotation(new Quaternionf()).slerp((Quaternionfc)second.getNormalizedRotation(new Quaternionf()), t);
            return new Matrix4f().translationRotate(position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, rotation.w);
        }

        public Matrix4f handMatrix(double seconds) {
            return this.fullHand ? this.matrix(this.parts, seconds) : new Matrix4f().translation((Vector3fc)this.hand(seconds));
        }

        public boolean hasLeftArm() {
            return this.handFloats == 32;
        }

        public Matrix4f leftArmMatrix(double seconds) {
            return this.hasLeftArm() ? this.matrix(this.parts + 1, seconds) : new Matrix4f();
        }

        public Vector3f hand(double seconds) {
            if (this.fullHand) {
                return this.matrix(this.parts, seconds).getTranslation(new Vector3f());
            }
            double frame = Math.clamp(seconds / (double)this.duration, 0.0, 1.0) * (double)(this.frames - 1);
            int a = (int)frame;
            int b = Math.min(this.frames - 1, a + 1);
            int x = a * this.stride + this.parts * 16;
            int y = b * this.stride + this.parts * 16;
            return new Vector3f(this.data[x], this.data[x + 1], this.data[x + 2]).lerp((Vector3fc)new Vector3f(this.data[y], this.data[y + 1], this.data[y + 2]), (float)(frame - (double)a));
        }
    }

    public record Part(int joint, int count, ByteBuffer vertices) {
    }
}

