package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.common.Component.translatable;
import static org.adde0109.pcf.forwarding.modern.ReflectionUtils.enforceSecureProfile;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_DEFAULT;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY_V2;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_MAX_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PAYLOAD;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.checkIntegrity;

import dev.neuralnexus.taterapi.event.Cancellable;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.mixin.CancellableMixin;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.Mode;
import org.adde0109.pcf.forwarding.compat.ArclightBridge;
import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

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
    public static final Set<Integer> TRANSACTION_IDS = ConcurrentHashMap.newKeySet();

    /**
     * Abstract implementation of the hello packet handler
     *
     * @param slpl The ServerLoginPacketListenerImpl
     * @param ci The callback info
     */
    public static void handleHello(
            final @NotNull ServerLoginPacketListenerBridge slpl, final @NotNull CallbackInfo ci) {
        if (!PCF.instance().forwarding().enabled()
                || !PCF.instance().forwarding().mode().equals(Mode.MODERN)) {
            return;
        }
        try {
            slpl.bridge$setVelocityLoginMessageId(ThreadLocalRandom.current().nextInt());
            TRANSACTION_IDS.add(slpl.bridge$velocityLoginMessageId());
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

    @ApiStatus.Internal
    public static BiConsumer<@NotNull ServerLoginPacketListenerBridge, @NotNull ByteBuf>
            preProcessor = (slpl, buf) -> {};

    private static final @NotNull Object DIRECT_CONNECT_ERR =
            literal("This server requires you to connect with Velocity.");
    private static final @NotNull Object EMPTY_PAYLOAD_ERR =
            literal("Received empty player info payload from the proxy.");
    private static final @NotNull Object PLAYER_INFO_ERR =
            literal("Unable to verify player details.");
    private static final @NotNull Object FAILED_TO_VERIFY =
            translatable("multiplayer.disconnect.unverified_username");
    private static final @NotNull Object MISSING_PROFILE_PUBLIC_KEY =
            translatable("multiplayer.disconnect.missing_public_key");
    private static final @NotNull Object INVALID_SIGNATURE =
            translatable("multiplayer.disconnect.invalid_public_key_signature");

    private static final Constraint IS_19_1_2 =
            Constraint.builder().min(MinecraftVersions.V19_1).max(MinecraftVersions.V19_2).build();

    /**
     * Abstract implementation of the custom query packet handler
     *
     * @param slpl The ServerLoginPacketListenerImpl
     * @param transactionId The transaction ID
     * @param mcPacket The Minecraft packet
     */
    public static void handleCustomQueryPacket(
            final @NotNull ServerLoginPacketListenerBridge slpl,
            final int transactionId,
            final @NotNull Object mcPacket) {
        handleCustomQueryPacket(slpl, transactionId, mcPacket, Cancellable.DUMMY);
    }

    /**
     * Abstract implementation of the custom query packet handler
     *
     * @param slpl The ServerLoginPacketListenerImpl
     * @param transactionId The transaction ID
     * @param mcPacket The Minecraft packet
     * @param ci The callback info
     */
    public static void handleCustomQueryPacket(
            final @NotNull ServerLoginPacketListenerBridge slpl,
            final int transactionId,
            final @NotNull Object mcPacket,
            final @NotNull CallbackInfo ci) {
        handleCustomQueryPacket(slpl, transactionId, mcPacket, new CancellableMixin(ci));
    }

    /**
     * Abstract implementation of the custom query packet handler
     *
     * @param slpl The ServerLoginPacketListenerImpl
     * @param transactionId The transaction ID
     * @param mcPacket The Minecraft packet
     * @param ci The callback info wrapper
     */
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

        if (packet.payload() == null) {
            slpl.bridge$disconnect(DIRECT_CONNECT_ERR);
            ci.cancel();
            return;
        } else if (packet.payload().data().readableBytes() == 0) {
            PCF.logger.error(
                    "Received empty forwarding payload. Has Velocity been configured to use modern forwarding?");
            slpl.bridge$disconnect(EMPTY_PAYLOAD_ERR);
            ci.cancel();
            return;
        }
        final ByteBuf buf = packet.payload().data();

        // Apply fixes
        preProcessor.accept(slpl, buf);

        TRANSACTION_IDS.remove(slpl.bridge$velocityLoginMessageId());

        // Validate data
        try {
            if (!checkIntegrity(buf)) {
                slpl.bridge$disconnect(PLAYER_INFO_ERR);
                ci.cancel();
                return;
            }
        } catch (AssertionError e) {
            if (e.getCause() instanceof InvalidKeyException
                    && PCF.instance().forwarding().secret().isBlank()) {
                PCF.logger.error(
                        "Please configure the `forwarding.secret` setting in PCF's config file!");
            } else {
                PCF.logger.error("An error occurred while validating player details: ", e);
            }
            slpl.bridge$disconnect(PLAYER_INFO_ERR);
            ci.cancel();
            return;
        }
        PCF.logger.debug("Player-data validated!");

        final PlayerInfoQueryAnswerPayload payload =
                packet.payload().as(PlayerInfoQueryAnswerPayload.STREAM_CODEC);

        int version = payload.version();
        if (version > MODERN_MAX_VERSION) {
            throw new IllegalStateException(
                    "Unsupported forwarding version "
                            + version
                            + ", wanted up to "
                            + MODERN_MAX_VERSION);
        }
        PCF.logger.debug("Using modern forwarding version: " + version);

        // Apply IP forwarding
        final int port = ((InetSocketAddress) slpl.bridge$connection().bridge$address()).getPort();
        final InetSocketAddress address = new InetSocketAddress(payload.address(), port);
        slpl.bridge$connection().bridge$address(address);

        // Handle profile key
        // Clear key on 1.19.1 - 1.19.2 if using MODERN_DEFAULT
        if (version == MODERN_DEFAULT && IS_19_1_2.result()) {
            ((ServerLoginPacketListener_KeyV2) slpl).bridge$profilePublicKeyData(null);
        }

        // 1.19 forwarding with key v1
        if (version == MODERN_FORWARDING_WITH_KEY) {
            boolean enforceSecureProfile = enforceSecureProfile();
            try {
                VelocityProxy.ProfilePublicKeyData publicKeyData = payload.key();
                if (enforceSecureProfile && publicKeyData == null) {
                    slpl.bridge$disconnect(MISSING_PROFILE_PUBLIC_KEY);
                    ci.cancel();
                    return;
                }
                ((ServerLoginPacketListener_KeyV1) slpl)
                        .bridge$setPlayerProfilePublicKey(publicKeyData);
            } catch (DecoderException e) {
                PCF.logger.error("Public key read failed.", e);
                if (enforceSecureProfile) {
                    slpl.bridge$disconnect(INVALID_SIGNATURE);
                    ci.cancel();
                    return;
                }
            }
        }

        // 1.19.1 - 1.19.2 forwarding with key v2
        if (version == MODERN_FORWARDING_WITH_KEY_V2) {
            final VelocityProxy.ProfilePublicKeyData forwardedKeyData = payload.key();
            final UUID signer = payload.signer();
            if (((ServerLoginPacketListener_KeyV2) slpl).bridge$profilePublicKeyData() == null) {
                try {
                    ((ServerLoginPacketListener_KeyV2) slpl)
                            .bridge$validatePublicKey(forwardedKeyData, signer);
                    ((ServerLoginPacketListener_KeyV2) slpl)
                            .bridge$profilePublicKeyData(forwardedKeyData);
                } catch (Exception e) {
                    slpl.bridge$logger_error("Failed to validate profile key: {}", e.getMessage());
                    slpl.bridge$disconnect(INVALID_SIGNATURE);
                    ci.cancel();
                    return;
                }
            }
        }

        // Proceed with login
        final NameAndId nameAndId = new NameAndId(payload.profile());
        try {
            // TODO: Pull this into a common compat class when other hybrids are supported
            if (MetaAPI.instance().isPlatformPresent(Platforms.ARCLIGHT)) {
                ((ArclightBridge) slpl).arclight$preLogin();
                ci.cancel();
                return;
            }
            slpl.bridge$logger_info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
            slpl.bridge$startClientVerification(payload.profile());
        } catch (Exception e) {
            slpl.bridge$disconnect(FAILED_TO_VERIFY);
            slpl.bridge$logger_error("Exception while forwarding user {}", nameAndId.name());
            e.printStackTrace();
        }
        ci.cancel();
    }
}
