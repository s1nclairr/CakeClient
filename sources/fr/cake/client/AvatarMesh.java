/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package fr.cake.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class AvatarMesh {
    public final int vertices;
    public final int bones;
    public final float[] data;
    public final int[] joints;
    public final int[] indices;
    public final Clip[] clips;
    public final float[] scratch;

    private AvatarMesh(int vertices, int bones, float[] data, int[] joints, int[] indices, Clip[] clips) {
        this.scratch = new float[vertices * 6];
        this.vertices = vertices;
        this.bones = bones;
        this.data = data;
        this.joints = joints;
        this.indices = indices;
        this.clips = clips;
    }

    public static AvatarMesh read(InputStream in) throws IOException {
        if (in == null) {
            throw new IOException("Missing avatar");
        }
        ByteBuffer b = ByteBuffer.wrap(in.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
        try {
            int i;
            if (b.getInt() != 843929153) {
                throw new IOException("Unknown avatar format");
            }
            int n = b.getInt();
            int ni = b.getInt();
            int nb = b.getInt();
            int nc = b.getInt();
            if (n < 1 || n > 60000 || ni < 3 || ni > 150000 || ni % 3 != 0 || nb < 1 || nb > 128 || nc != 2) {
                throw new IOException("Invalid avatar counts");
            }
            float[] data = new float[n * 12];
            for (int i2 = 0; i2 < data.length; ++i2) {
                data[i2] = b.getFloat();
                if (Float.isFinite(data[i2])) continue;
                throw new IOException("Nonfinite mesh");
            }
            int[] joints = new int[n * 4];
            int[] indices = new int[ni];
            for (i = 0; i < joints.length; ++i) {
                joints[i] = Short.toUnsignedInt(b.getShort());
                if (joints[i] < nb) continue;
                throw new IOException("Bad joint");
            }
            for (i = 0; i < ni; ++i) {
                indices[i] = b.getInt();
                if (indices[i] >= 0 && indices[i] < n) continue;
                throw new IOException("Bad index");
            }
            Clip[] clips = new Clip[nc];
            for (int c = 0; c < nc; ++c) {
                float duration = b.getFloat();
                int frames = b.getInt();
                if (!(duration > 0.0f) || !Float.isFinite(duration) || frames < 2 || frames > 3000) {
                    throw new IOException("Invalid clip");
                }
                Matrix4f[] poses = new Matrix4f[frames * nb];
                for (int f = 0; f < poses.length; ++f) {
                    float[] m = new float[16];
                    for (int k = 0; k < 16; ++k) {
                        m[k] = b.getFloat();
                        if (Float.isFinite(m[k])) continue;
                        throw new IOException("Bad matrix");
                    }
                    poses[f] = new Matrix4f().set(m);
                }
                clips[c] = new Clip(duration, frames, poses);
            }
            if (b.hasRemaining()) {
                throw new IOException("Trailing mesh bytes");
            }
            return new AvatarMesh(n, nb, data, joints, indices, clips);
        }
        catch (BufferUnderflowException e) {
            throw new IOException("Truncated avatar", e);
        }
    }

    public Matrix4f[] pose(double idle, double walk, float amount) {
        Matrix4f[] result = new Matrix4f[this.bones];
        for (int i = 0; i < this.bones; ++i) {
            result[i] = this.clips[0].bone(i, this.bones, idle).lerp((Matrix4fc)this.clips[1].bone(i, this.bones, Math.max(0.0, walk)), Math.clamp(amount, 0.0f, 1.0f));
        }
        return result;
    }

    public record Clip(float duration, int frames, Matrix4f[] poses) {
        Matrix4f bone(int bone, int count, double seconds) {
            double f = seconds % (double)this.duration / (double)this.duration * (double)(this.frames - 1);
            int a = Math.min(this.frames - 2, (int)f);
            return new Matrix4f((Matrix4fc)this.poses[a * count + bone]).lerp((Matrix4fc)this.poses[(a + 1) * count + bone], (float)(f - (double)a));
        }
    }
}

