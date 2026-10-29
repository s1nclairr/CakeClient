/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public record CosmeticState(int flags, int knife, int finish, int trail, int jump, int skin, float trailHue, float jumpHue, float trailSize, float trailLife, float trailDensity, float jumpSize, float jumpLife, float skinScale, int back, float backScale, float backSpeed) {
    public static final int MAGIC = 1128352581;
    public static final int VERSION = 3;
    public static final int MAX_BYTES = 96;

    public CosmeticState(int flags, int knife, int finish, int trail, int jump, int skin, float trailHue, float jumpHue, float trailSize, float trailLife, float trailDensity, float jumpSize, float jumpLife, float skinScale) {
        this(flags, knife, finish, trail, jump, skin, trailHue, jumpHue, trailSize, trailLife, trailDensity, jumpSize, jumpLife, skinScale, 0, 0.5f, 0.4f);
    }

    public CosmeticState {
        if ((flags & 0xFFFFFFE0) != 0 || knife < 0 || knife > 21 || finish < 0 || finish > 13 || trail < 0 || trail > 7 || jump < 0 || jump > 11 || skin < 1 || skin > 32 || back < 0 || back > 12) {
            throw new IllegalArgumentException("Invalid cosmetic enum");
        }
        for (float f : new float[]{trailHue, jumpHue, trailSize, trailLife, trailDensity, jumpSize, jumpLife, skinScale, backScale, backSpeed}) {
            if (Float.isFinite(f) && !(f < 0.0f) && !(f > 1.0f)) continue;
            throw new IllegalArgumentException("Invalid cosmetic value");
        }
    }

    public byte[] packet(UUID player) {
        return CosmeticState.encode(player, this);
    }

    public static byte[] encode(UUID player, CosmeticState s) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream o = new DataOutputStream(bytes);
            o.writeInt(1128352581);
            o.writeByte(3);
            o.writeBoolean(s != null);
            o.writeLong(player.getMostSignificantBits());
            o.writeLong(player.getLeastSignificantBits());
            if (s != null) {
                for (int n : new int[]{s.flags, s.knife, s.finish, s.trail, s.jump, s.skin}) {
                    o.writeByte(n);
                }
                for (float f : new float[]{s.trailHue, s.jumpHue, s.trailSize, s.trailLife, s.trailDensity, s.jumpSize, s.jumpLife, s.skinScale}) {
                    o.writeFloat(f);
                }
                o.writeByte(s.back);
                o.writeFloat(s.backScale);
                o.writeFloat(s.backSpeed);
            }
            return bytes.toByteArray();
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
    }

    public static Message decode(byte[] b) throws IOException {
        if (b.length > 96) {
            throw new IOException("Oversized payload");
        }
        try {
            CosmeticState state;
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
            if (in.readInt() != 1128352581) {
                throw new IOException("Unknown protocol");
            }
            int version = in.readUnsignedByte();
            if (version != 2 && version != 3) {
                throw new IOException("Unknown protocol");
            }
            boolean present = in.readBoolean();
            UUID id = new UUID(in.readLong(), in.readLong());
            CosmeticState cosmeticState = present ? new CosmeticState(in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), version == 3 ? in.readUnsignedByte() : 0, version == 3 ? in.readFloat() : 0.5f, version == 3 ? in.readFloat() : 0.4f) : (state = null);
            if (in.available() != 0) {
                throw new IOException("Trailing data");
            }
            return new Message(id, state);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Invalid state", e);
        }
    }

    public record Message(UUID player, CosmeticState state) {
    }
}

