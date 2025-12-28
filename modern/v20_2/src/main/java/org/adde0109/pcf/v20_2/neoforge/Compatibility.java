package org.adde0109.pcf.v20_2.neoforge;

import static org.adde0109.pcf.common.FriendlyByteBuf.readResourceLocation;
import static org.adde0109.pcf.common.FriendlyByteBuf.readVarInt;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_CHANNEL;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Platforms;

import io.netty.buffer.ByteBuf;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.custom.payload.SimpleQueryPayload;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public final class Compatibility {
    public static final Constraint NEOFORGE_V20_2 =
            Constraint.builder()
                    .platform(Platforms.NEOFORGE)
                    .version(MinecraftVersions.V20_2)
                    .build();

    public static final Constraint FFAPI_V21_1 =
            Constraint.builder()
                .platform(Platforms.NEOFORGE)
                .version(MinecraftVersions.V21_1)
                .deps("fabric_networking_api_v1")
                .build();

    /**
     * Read the id and ResourceLocation so PCF can continue reading the packet as normal
     *
     * @param buf The ByteBuf from the packet
     */
    public static void neoForgeReadSimpleQueryPayload(final @NotNull ServerLoginPacketListenerBridge ignored, final @NotNull ByteBuf buf) {
        if (NEOFORGE_V20_2.result()) {
            readVarInt(buf);
            readResourceLocation(buf);
        }
    }

    /**
     * Set the callback returnable to a SimpleQueryPayload to comply with NeoForge 1.20.2's
     * modification to ServerboundCustomQueryAnswerPacketMixin#readPayload's return type
     *
     * @param buf The ByteBuf to send
     * @param transactionId The transaction id
     * @param cir The mixin's callback returnable
     */
    public static void neoForgeReturnSimpleQueryPayload(
            final @NotNull ByteBuf buf, final int transactionId, final @NotNull CallbackInfoReturnable<Object> cir) {
        if (NEOFORGE_V20_2.result()) {
            cir.setReturnValue(
                    buf == null
                            ? null
                            : SimpleQueryPayload.outbound(
                                    new FriendlyByteBuf(buf), transactionId, PLAYER_INFO_CHANNEL()));
        }
    }



    private static MethodHandle channelsMH;

    /**
     * Remove PCF's query id from FFAPI's channels map to prevent infinite login screen
     *
     * @param slpl an instance of ServerLoginPacketListenerImpl
     */
    @SuppressWarnings("unchecked")
    public static void applyFFAPIFix(final @NotNull ServerLoginPacketListenerBridge slpl, final @NotNull ByteBuf ignored) {
        if (!FFAPI_V21_1.result()) {
            return;
        }
        // spotless:off
        try {
            if (channelsMH == null) {
                MethodHandles.Lookup slnaLookup =
                        MethodHandles.privateLookupIn(ServerLoginNetworkAddon.class, MethodHandles.publicLookup());
                channelsMH =
                        slnaLookup.findGetter(ServerLoginNetworkAddon.class, "channels", Map.class);
            }
            ServerLoginNetworkAddon addon = (ServerLoginNetworkAddon)
                    ((NetworkHandlerExtensions) slpl).getAddon();
            Map<Integer, ?> channels = (Map<Integer, ?>) channelsMH.invokeExact(addon);

            channels.remove(slpl.bridge$velocityLoginMessageId());
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            PCF.logger.warn("Lookup Exception applying FFAPI fix", e);
        } catch (Throwable e) {
            PCF.logger.warn("Exception applying FFAPI fix", e);
        }
        // spotless:on
    }
}
