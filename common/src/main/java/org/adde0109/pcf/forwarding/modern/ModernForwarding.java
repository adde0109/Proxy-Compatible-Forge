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

import dev.neuralnexus.taterapi.event.Cancellable;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.Mode;
import org.adde0109.pcf.forwarding.compat.ArclightBridge;
import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.util.Optional;
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
    private static final @NotNull Object player_info_err =
            literal("Unable to verify player details.");
    private static final @NotNull Object failed_to_verify =
            translatable("multiplayer.disconnect.unverified_username");

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T DIRECT_CONNECT_ERR() {
        return (T) direct_conn_err;
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T PLAYER_INFO_ERR() {
        return (T) player_info_err;
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T FAILED_TO_VERIFY() {
        return (T) failed_to_verify;
    }

    public static void handleHello(
            final @NotNull ServerLoginPacketListenerBridge slpl, final @NotNull CallbackInfo ci) {
        if (!PCF.instance().forwarding().enabled()
                || !PCF.instance().forwarding().mode().equals(Mode.MODERN)) {
            return;
        }
        try {
            slpl.bridge$setVelocityLoginMessageId(ThreadLocalRandom.current().nextInt());
            QUERY_IDS.add(slpl.bridge$velocityLoginMessageId());
            slpl.bridge$connection()
                    .bridge$send(
                            new ClientboundCustomQueryPacket(
                                            slpl.bridge$velocityLoginMessageId(),
                                            PLAYER_INFO_PAYLOAD)
                                    .toMC());
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        } catch (Exception e) {
            PCF.logger.error("An error occurred while sending the forwarding request: ", e);
        }
    }

    public static Optional<GameProfile> forward(
            final @NotNull ServerLoginPacketListenerBridge slpl,
            final @NotNull ByteBuf buf,
            final @NotNull Cancellable ci) {
        try {
            if (!checkIntegrity(buf)) {
                slpl.bridge$disconnect(PLAYER_INFO_ERR());
                ci.cancel();
                return Optional.empty();
            }
        } catch (AssertionError e) {
            if (e.getCause() instanceof InvalidKeyException
                    && PCF.instance().forwarding().secret().isBlank()) {
                PCF.logger.error(
                        "Please configure the `forwarding.secret` setting in PCF's config file!");
            } else {
                PCF.logger.error("An error occurred while validating player details: ", e);
            }
            slpl.bridge$disconnect(PLAYER_INFO_ERR());
            ci.cancel();
            return Optional.empty();
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

        final int port = ((InetSocketAddress) slpl.bridge$connection().bridge$address()).getPort();
        final InetSocketAddress address = new InetSocketAddress(readAddress(buf), port);
        slpl.bridge$connection().bridge$address(address);

        final GameProfile profile = createProfile(buf);
        return Optional.of(profile);
    }

    public static void handleCustomQueryPacket(
            final @NotNull ServerLoginPacketListenerBridge slpl,
            final int transactionId,
            final @NotNull Object mcPacket,
            final @NotNull Cancellable ci) {
        if (!PCF.instance().forwarding().enabled()
                || !PCF.instance().forwarding().mode().equals(Mode.MODERN)
                || transactionId != slpl.bridge$velocityLoginMessageId()) {
            return;
        }
        final ServerboundCustomQueryAnswerPacket packet =
                ServerboundCustomQueryAnswerPacket.fromMC(mcPacket);

        QUERY_IDS.remove(slpl.bridge$velocityLoginMessageId());

        if (packet.payload() == null) {
            slpl.bridge$disconnect(DIRECT_CONNECT_ERR());
            ci.cancel();
            return;
        }
        final ByteBuf buf = packet.payload().data();

        // TODO: PreProcessing
        // Compatibility.neoForgeReadSimpleQueryPayload(buf);
        // Compatibility.applyFFAPIFix(slpl, slpl.bridge$velocityLoginMessageId());

        final Optional<GameProfile> profile = forward(slpl, buf, ci);
        if (profile.isEmpty()) {
            return;
        }

        // TODO: PostProcessing
        // Handle profile key
        //        final Object disconnectReason = handle(this, buf, data.version(), nameAndId.id());
        //        if (disconnectReason != null) {
        //            this.bridge$disconnect(disconnectReason);
        //            ci.cancel();
        //            return;
        //        }

        final NameAndId nameAndId = new NameAndId(profile.get());

        // Proceed with login
        try {
            // TODO: Pull this into a common compat class
            if (MetaAPI.instance().isPlatformPresent(Platforms.ARCLIGHT)) {
                ((ArclightBridge) slpl).arclight$preLogin();
                ci.cancel();
                return;
            }
            slpl.bridge$logger_info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
            slpl.bridge$startClientVerification(profile.get());
        } catch (Exception e) {
            slpl.bridge$disconnect(FAILED_TO_VERIFY());
            slpl.bridge$logger_error("Exception while forwarding user {}", nameAndId.name());
            e.printStackTrace();
        }
        ci.cancel();
    }
}
