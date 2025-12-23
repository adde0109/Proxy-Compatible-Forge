package org.adde0109.pcf.v19_2.forge.forwarding.modern;

import static org.adde0109.pcf.common.FriendlyByteBuf.Crypt.MAX_KEY_SIGNATURE_SIZE;
import static org.adde0109.pcf.common.FriendlyByteBuf.readByteArray;
import static org.adde0109.pcf.common.FriendlyByteBuf.readInstant;
import static org.adde0109.pcf.common.FriendlyByteBuf.readPublicKey;
import static org.adde0109.pcf.common.FriendlyByteBuf.readUUID;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_DEFAULT;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY_V2;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.FriendlyByteBuf.CryptException;
import org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern.ServerLoginPacketListenerImplAccessor_V1;
import org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern.ServerLoginPacketListenerImplAccessor_V2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Adapted from: <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.1</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/4074d4ee99a75ad005b05bfba8257e55beeb335f/patches/server/0884-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.2</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.4</a>
 */
public final class HandleProfileKey {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerLoginPacketListenerImpl");
    private static final Constraint IS_19_1_2 =
            Constraint.builder().min(MinecraftVersions.V19_1).max(MinecraftVersions.V19_2).build();
    private static final Component MISSING_PROFILE_PUBLIC_KEY =
            Component.translatable("multiplayer.disconnect.missing_public_key");
    private static final Component INVALID_SIGNATURE =
            Component.translatable("multiplayer.disconnect.invalid_public_key_signature");

    public static @Nullable Component handle(
            @NotNull ServerLoginPacketListenerImpl self,
            @NotNull ByteBuf buf,
            int version,
            @NotNull UUID profileId) {
        // Clear key on 1.19.1 - 1.19.2 if using MODERN_DEFAULT
        if (version == MODERN_DEFAULT && IS_19_1_2.result()) {
            ((ServerLoginPacketListenerImplAccessor_V2) self).pcf$setProfilePublicKeyData(null);
        }

        MinecraftServer server = (MinecraftServer) MetaAPI.instance().server();
        boolean enforceSecureProfile = server.enforceSecureProfile();

        // 1.19 forwarding with key v1
        if (version == MODERN_FORWARDING_WITH_KEY) {
            try {
                ProfilePublicKey publicKey = readKey(buf);
                if (enforceSecureProfile && publicKey == null) {
                    return MISSING_PROFILE_PUBLIC_KEY;
                }
                ((ServerLoginPacketListenerImplAccessor_V1) self)
                        .pcf$setPlayerProfilePublicKey(publicKey);
            } catch (CryptException e) {
                PCF.logger.error("Public key read failed.", e);
                if (enforceSecureProfile) {
                    return INVALID_SIGNATURE;
                }
            }
        }

        // 1.19.1 - 1.19.2 forwarding with key v2
        if (version == MODERN_FORWARDING_WITH_KEY_V2) {
            final ProfilePublicKey.Data forwardedKeyData = readForwardedKey(buf);
            final UUID signer = readSignerUuidOrElse(buf, profileId);
            if (((ServerLoginPacketListenerImplAccessor_V2) self).pcf$profilePublicKeyData()
                    == null) {
                try {
                    ServerLoginPacketListenerImplAccessor_V2.pcf$validatePublicKey(
                            forwardedKeyData,
                            signer,
                            server.getServiceSignatureValidator(),
                            enforceSecureProfile);
                    ((ServerLoginPacketListenerImplAccessor_V2) self)
                            .pcf$setProfilePublicKeyData(forwardedKeyData);
                } catch (Exception e) {
                    LOGGER.error("Failed to validate profile key: {}", e.getMessage());
                    return INVALID_SIGNATURE;
                }
            }
        }
        return null;
    }

    /**
     * Modern Forwarding v2 - 1.19 <br>
     * Reads a ProfilePublicKey from the given ByteBuf
     *
     * @param buf The ByteBuf to read from
     * @return The ProfilePublicKey read from the ByteBuf
     * @throws CryptException If there was an error reading the key
     */
    private static @NotNull ProfilePublicKey readKey(final @NotNull ByteBuf buf)
            throws CryptException {
        return new ProfilePublicKey(readForwardedKey(buf));
    }

    /**
     * Modern Forwarding v3 - 1.19.1 - 1.19.2 <br>
     *
     * @param buf The ByteBuf to read from
     * @param orElse The UUID to return if no UUID is present
     * @return The UUID read from the ByteBuf, or the given UUID if none is present
     */
    public static @NotNull UUID readSignerUuidOrElse(
            final @NotNull ByteBuf buf, final @NotNull UUID orElse) {
        return buf.readBoolean() ? readUUID(buf) : orElse;
    }

    /**
     * Modern Forwarding v3 - 1.19.1 - 1.19.2 <br>
     *
     * @param buf The ByteBuf to read from
     * @return The ProfilePublicKey.Data read from the ByteBuf
     */
    private static @NotNull ProfilePublicKey.Data readForwardedKey(final @NotNull ByteBuf buf) {
        return new ProfilePublicKey.Data(
                readInstant(buf), readPublicKey(buf), readByteArray(buf, MAX_KEY_SIGNATURE_SIZE));
    }
}
