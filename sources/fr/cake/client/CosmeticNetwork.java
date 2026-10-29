/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_8710
 *  net.minecraft.class_8710$class_9154
 *  net.minecraft.class_9129
 *  net.minecraft.class_9139
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import fr.cake.protocol.CosmeticState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_8710;
import net.minecraft.class_9129;
import net.minecraft.class_9139;

public final class CosmeticNetwork {
    private static final Map<UUID, CosmeticState> peers = new HashMap<UUID, CosmeticState>();
    private static int ticks;
    private static Object connection;
    private static CosmeticState last;
    private static boolean shared;

    public static CosmeticState local() {
        Settings s = CakeClient.settings;
        return new CosmeticState((s.knives ? 1 : 0) | (s.trailEnabled ? 2 : 0) | (s.jumpEnabled ? 4 : 0) | (s.skinEnabled ? 8 : 0) | (s.backEnabled ? 16 : 0), s.knifeModel, s.knifeFinish, s.trailStyle, s.jumpStyle, s.avatarSkin, (float)s.trailHue, (float)s.jumpHue, (float)s.trailSize, (float)s.trailLife, (float)s.trailDensity, (float)s.jumpSize, (float)s.jumpLife, (float)s.skinScale, s.backStyle, (float)s.backScale, (float)s.backSpeed);
    }

    public static void init() {
        PayloadTypeRegistry.playC2S().register(Packet.TYPE, Packet.CODEC);
        PayloadTypeRegistry.playS2C().register(Packet.TYPE, Packet.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(Packet.TYPE, (packet, context) -> {
            try {
                CosmeticState.Message m = CosmeticState.decode(packet.data);
                context.client().execute(() -> {
                    if (m.state() == null) {
                        peers.remove(m.player());
                    } else {
                        peers.put(m.player(), m.state());
                    }
                });
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
            peers.clear();
            last = null;
            connection = null;
        });
    }

    public static CosmeticState get(UUID id) {
        class_310 mc = class_310.method_1551();
        return mc.field_1724 != null && id.equals(mc.field_1724.method_5667()) ? CosmeticNetwork.local() : peers.get(id);
    }

    public static boolean marked(UUID id) {
        class_310 mc = class_310.method_1551();
        return mc.field_1724 != null && id.equals(mc.field_1724.method_5667()) || peers.containsKey(id);
    }

    public static void tick() {
        class_310 mc = class_310.method_1551();
        if (connection != mc.method_1562()) {
            peers.clear();
            last = null;
            shared = false;
            connection = mc.method_1562();
        }
        if (mc.field_1724 == null || connection == null || ++ticks % 20 != 0 || !ClientPlayNetworking.canSend(Packet.TYPE)) {
            return;
        }
        boolean share = CakeClient.settings.shareCosmetics;
        CosmeticState state = CosmeticNetwork.local();
        if (share) {
            if (!shared || !state.equals(last) || ticks % 200 == 0) {
                ClientPlayNetworking.send((class_8710)new Packet(state.packet(mc.field_1724.method_5667())));
                last = state;
                shared = true;
            }
        } else if (shared) {
            ClientPlayNetworking.send((class_8710)new Packet(CosmeticState.encode(mc.field_1724.method_5667(), null)));
            shared = false;
            last = null;
        }
    }

    public record Packet(byte[] data) implements class_8710
    {
        public static final class_8710.class_9154<Packet> TYPE = new class_8710.class_9154(class_2960.method_60655((String)"cakeclient", (String)"cosmetics"));
        public static final class_9139<class_9129, Packet> CODEC = class_9139.method_56437((b, p) -> b.method_52983(p.data), b -> {
            if (b.readableBytes() > 96) {
                throw new IllegalArgumentException("Oversized state");
            }
            byte[] a = new byte[b.readableBytes()];
            b.method_52979(a);
            return new Packet(a);
        });

        public class_8710.class_9154<? extends class_8710> method_56479() {
            return TYPE;
        }
    }
}

