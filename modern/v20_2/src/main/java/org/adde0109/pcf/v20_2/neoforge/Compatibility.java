package org.adde0109.pcf.v20_2.neoforge;

import static org.adde0109.pcf.v20_2.neoforge.forwarding.FWDBootstrap.PLAYER_INFO_CHANNEL;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Platforms;

import io.netty.buffer.ByteBuf;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.custom.payload.SimpleQueryPayload;

import org.adde0109.pcf.PCF;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public final class Compatibility {
    public static final boolean isNeoForge1_20_2;
    private static final boolean shouldApplyFFAPIFix;

    static {
        MetaAPI api = MetaAPI.instance();
        isNeoForge1_20_2 =
                api.isPlatformPresent(Platforms.NEOFORGE)
                        && api.version().is(MinecraftVersions.V20_2);
        shouldApplyFFAPIFix =
                api.isPlatformPresent(Platforms.NEOFORGE)
                        && api.version().isAtLeast(MinecraftVersions.V21_1)
                        && api.isModLoaded(Platforms.NEOFORGE, "fabric_networking_api_v1");
    }

    /**
     * Read the id and ResourceLocation so PCF can continue reading the packet as normal
     *
     * @param buf The ByteBuf from the packet
     */
    public static void neoForgeReadSimpleQueryPayload(FriendlyByteBuf buf) {
        if (isNeoForge1_20_2) {
            buf.readVarInt();
            buf.readResourceLocation();
        }
    }

    /**
     * Set the callback returnable to a SimpleQueryPayload to comply with NeoForge 1.20.2's
     * modification to ServerboundCustomQueryAnswerPacketMixin#readPayload's return type
     *
     * @param buf The ByteBuf to send
     * @param cir The mixin's callback returnable
     */
    public static void neoForgeReturnSimpleQueryPayload(
            ByteBuf buf, int queryId, CallbackInfoReturnable<Object> cir) {
        if (isNeoForge1_20_2) {
            cir.setReturnValue(
                    buf == null
                            ? null
                            : SimpleQueryPayload.outbound(
                                    new FriendlyByteBuf(buf), queryId, PLAYER_INFO_CHANNEL));
        }
    }

    /**
     * Remove PCF's query id from FFAPI's channels map to prevent infinite login screen
     *
     * @param serverLoginPacketListener an instance of ServerLoginPacketListenerImpl
     */
    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    public static void applyFFAPIFix(Object serverLoginPacketListener, int queryId) {
        if (!shouldApplyFFAPIFix) {
            return;
        }
        // spotless:off
        try {
            ServerLoginNetworkAddon addon = (ServerLoginNetworkAddon)
                    ((NetworkHandlerExtensions) serverLoginPacketListener).getAddon();
            MethodHandles.Lookup slnaLookup =
                    MethodHandles.privateLookupIn(ServerLoginNetworkAddon.class, MethodHandles.publicLookup());
            MethodHandle channelsMH =
                    slnaLookup.findGetter(ServerLoginNetworkAddon.class, "channels", Map.class);
            Map<Integer, ?> channels = (Map<Integer, ?>) channelsMH.invokeExact(addon);

            channels.remove(queryId);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            PCF.logger.warn("Lookup Exception applying FFAPI fix", e);
        } catch (Throwable e) {
            PCF.logger.warn("Exception applying FFAPI fix", e);
        }
        // spotless:on
    }
}
