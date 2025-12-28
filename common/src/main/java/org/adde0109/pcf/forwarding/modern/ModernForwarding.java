package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.common.Component.translatable;
import static org.adde0109.pcf.common.FriendlyByteBuf.readAddress;
import static org.adde0109.pcf.common.FriendlyByteBuf.readVarInt;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_MAX_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PAYLOAD;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.checkIntegrity;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for modern forwarding handling. <br>
 * Adapted from: <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/bef2c9d005bdd039f188ee53094a928e76bd8e59/patches/server/0273-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.2</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/4074d4ee99a75ad005b05bfba8257e55beeb335f/patches/server/0884-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.3</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.4</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/server/network/ServerLoginPacketListenerImpl.java.patch">Paper
 * 1.20.x</a>
 */
public final class ModernForwarding {
    public static final Set<Integer> QUERY_IDS = ConcurrentHashMap.newKeySet();

    private static final @NotNull Object direct_conn_err =
            literal("This server requires you to connect with Velocity.");
    private static final @NotNull Object failed_to_verify =
            translatable("multiplayer.disconnect.unverified_username");

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T DIRECT_CONNECT_ERR() {
        return (T) direct_conn_err;
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T FAILED_TO_VERIFY() {
        return (T) failed_to_verify;
    }

    public static void handleHello(ServerLoginPacketListenerBridge slpl, CallbackInfo ci) {
        if (!PCF.instance().forwarding().enabled()
                || !PCF.instance().forwarding().mode().equals(Mode.MODERN)) {
            return;
        }
        try {
            slpl.pcf$setVelocityLoginMessageId(ThreadLocalRandom.current().nextInt());
            QUERY_IDS.add(slpl.pcf$velocityLoginMessageId());
            slpl.pcf$connection()
                    .pcf$send(
                            new ClientboundCustomQueryPacket(
                                            slpl.pcf$velocityLoginMessageId(), PLAYER_INFO_PAYLOAD)
                                    .toMC());
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        } catch (Exception e) {
            PCF.logger.error("An error occurred while sending the forwarding request: ", e);
        }
    }

    public static @NotNull Data forward(
            final @NotNull ByteBuf buf, final @NotNull SocketAddress remoteAddress) {
        try {
            if (!checkIntegrity(buf)) {
                return new Data("Unable to verify player details");
            }
        } catch (AssertionError e) {
            if (e.getCause() instanceof InvalidKeyException
                    && PCF.instance().forwarding().secret().isBlank()) {
                PCF.logger.error(
                        "Please configure the `forwarding.secret` setting in PCF's config file!");
            } else {
                PCF.logger.error("An error occurred while validating player details: ", e);
            }
            return new Data("Unable to verify player details");
        }
        PCF.logger.debug("Player-data validated!");

        int version = readVarInt(buf);
        if (version > MODERN_MAX_VERSION) {
            throw new IllegalStateException(
                    "Unsupported forwarding version "
                            + version
                            + ", wanted up to "
                            + MODERN_MAX_VERSION);
        }
        PCF.logger.debug("Using modern forwarding version: " + version);

        final InetSocketAddress address =
                new InetSocketAddress(
                        readAddress(buf), ((InetSocketAddress) remoteAddress).getPort());
        final GameProfile profile = createProfile(buf);

        return new Data(version, null, address, profile);
    }

    public record Data(
            int version,
            @Nullable String disconnectMsg,
            @Nullable SocketAddress address,
            @Nullable GameProfile profile) {
        public Data(final @NotNull String disconnectMsg) {
            this(-1, disconnectMsg, null, null);
        }
    }
}
